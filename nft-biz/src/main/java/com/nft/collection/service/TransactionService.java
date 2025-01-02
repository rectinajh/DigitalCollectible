package com.nft.collection.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.collection.domain.Collection;
import com.nft.collection.domain.IssuedCollectionActionLog;
import com.nft.collection.domain.MemberHoldCollection;
import com.nft.collection.domain.MemberResaleCollection;
import com.nft.collection.domain.MysteryBoxCommodity;
import com.nft.collection.domain.PayOrder;
import com.nft.collection.domain.PreSaleQualify;
import com.nft.collection.param.CancelPayParam;
import com.nft.collection.param.CollectionCancelResaleParam;
import com.nft.collection.param.CollectionResaleParam;
import com.nft.collection.param.ConfirmPayParam;
import com.nft.collection.param.LatestCollectionCreateOrderParam;
import com.nft.collection.param.OpenMysteryBoxParam;
import com.nft.collection.param.PayOrderQueryCondParam;
import com.nft.collection.param.ResaleCollectionCreateOrderParam;
import com.nft.collection.repo.CollectionRepo;
import com.nft.collection.repo.IssuedCollectionActionLogRepo;
import com.nft.collection.repo.MemberHoldCollectionRepo;
import com.nft.collection.repo.MemberResaleCollectionRepo;
import com.nft.collection.repo.PayOrderRepo;
import com.nft.collection.repo.PreSaleQualifyRepo;
import com.nft.collection.vo.IssueResultVO;
import com.nft.collection.vo.MyPayOrderDetailVO;
import com.nft.collection.vo.MyPayOrderVO;
import com.nft.collection.vo.OpenMysteryBoxResultVO;
import com.nft.collection.vo.PayOrderVO;
import com.nft.common.exception.BizException;
import com.nft.common.utils.ThreadPoolUtils;
import com.nft.common.vo.PageResult;
import com.nft.constants.Constant;
import com.nft.log.domain.MemberBalanceChangeLog;
import com.nft.log.repo.MemberBalanceChangeLogRepo;
import com.nft.member.domain.Member;
import com.nft.member.repo.MemberRepo;
import com.nft.setting.domain.OperationSetting;
import com.nft.setting.repo.OperationSettingRepo;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.WeightRandom.WeightObj;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class TransactionService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private IssuedCollectionService issuedCollectionService;

	@Autowired
	private CollectionRepo collectionRepo;

	@Autowired
	private MemberHoldCollectionRepo memberHoldCollectionRepo;

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private PayOrderRepo payOrderRepo;

	@Autowired
	private MemberBalanceChangeLogRepo memberBalanceChangeLogRepo;

	@Autowired
	private MemberResaleCollectionRepo memberResaleCollectionRepo;

	@Autowired
	private IssuedCollectionActionLogRepo issuedCollectionActionLogRepo;

	@Autowired
	private OperationSettingRepo operationSettingRepo;

	@Autowired
	private PreSaleQualifyRepo preSaleQualifyRepo;

	@Transactional(readOnly = true)
	public MyPayOrderDetailVO findMyPayOrderDetail(@NotBlank String id) {
		PayOrder order = payOrderRepo.getOne(id);
		return MyPayOrderDetailVO.convertFor(order);
	}

	@Transactional(readOnly = true)
	public PageResult<PayOrderVO> findPayOrderByPage(@Valid PayOrderQueryCondParam param) {
		Page<PayOrder> result = payOrderRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<PayOrderVO> pageResult = new PageResult<>(PayOrderVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<MyPayOrderVO> findMyPayOrderByPage(@Valid PayOrderQueryCondParam param) {
		Page<PayOrder> result = payOrderRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MyPayOrderVO> pageResult = new PageResult<>(MyPayOrderVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void payOrderTimeoutCancel() {
		Date now = new Date();
		List<PayOrder> payOrders = payOrderRepo.findByStateAndOrderDeadlineLessThan(Constant.支付订单状态_待付款, now);
		for (PayOrder payOrder : payOrders) {
			redissonClient.getTopic(Constant.支付订单超时取消).publish(payOrder.getId());
		}
	}

	@Transactional
	public void cancelPay(@Valid CancelPayParam param) {
		PayOrder payOrder = payOrderRepo.getOne(param.getOrderId());
		if (!payOrder.getMemberId().equals(param.getMemberId())) {
			throw new BizException("订单归属异常");
		}
		cancelPay(param.getOrderId());
	}

	@Transactional
	public void cancelPay(String orderId) {
		PayOrder payOrder = payOrderRepo.getOne(orderId);
		if (!Constant.支付订单状态_待付款.equals(payOrder.getState())) {
			throw new BizException("订单状态异常");
		}
		if (Constant.支付订单业务模式_平台自营.equals(payOrder.getBizMode())) {
			cancelPayWithLatestCollection(payOrder);
		} else if (Constant.支付订单业务类型_二级市场.equals(payOrder.getBizMode())) {
			cancelPayWithResaleCollection(payOrder);
		}

		ThreadPoolUtils.getRiskPool().schedule(() -> {
			redissonClient.getTopic(Constant.风控原因_下单未付款).publish(payOrder.getMemberId());
		}, 1, TimeUnit.SECONDS);
	}

	public void cancelPayWithLatestCollection(PayOrder payOrder) {
		payOrder.cancelOrder();
		payOrderRepo.save(payOrder);
	}

	public void cancelPayWithResaleCollection(PayOrder payOrder) {
		payOrder.cancelOrder();
		payOrderRepo.save(payOrder);

		MemberResaleCollection memberResaleCollection = memberResaleCollectionRepo.getOne(payOrder.getBizOrderNo());
		if (StrUtil.isNotBlank(memberResaleCollection.getLockPayMemberId())) {
			memberResaleCollection.setLockPayMemberId(null);
			memberResaleCollectionRepo.save(memberResaleCollection);
		}
	}

	@Transactional
	public void confirmPay(@Valid ConfirmPayParam param) {
		PayOrder payOrder = payOrderRepo.getOne(param.getOrderId());
		if (!Constant.支付订单状态_待付款.equals(payOrder.getState())) {
			throw new BizException("订单状态异常");
		}
		if (!payOrder.getMemberId().equals(param.getMemberId())) {
			throw new BizException("订单归属异常");
		}
		if (Constant.支付订单业务模式_平台自营.equals(payOrder.getBizMode())) {
			confirmPayWithLatestCollection(payOrder);
		} else if (Constant.支付订单业务类型_二级市场.equals(payOrder.getBizMode())) {
			confirmPayWithResaleCollection(payOrder);
		}
	}

	public void confirmPayWithResaleCollection(PayOrder payOrder) {
		Member buyer = payOrder.getMember();
		buyer.validBasicRisk();
		double balance = NumberUtil.round(buyer.getBalance() - payOrder.getAmount(), 2).doubleValue();
		if (balance < 0) {
			throw new BizException("余额不足");
		}

		MemberResaleCollection memberResaleCollection = memberResaleCollectionRepo.getOne(payOrder.getBizOrderNo());
		memberResaleCollection.sold();
		memberResaleCollectionRepo.save(memberResaleCollection);

		issuedCollectionService.resale(memberResaleCollection.getMemberHoldCollection(), buyer, payOrder);

		Member seller = memberResaleCollection.getMember();
		seller.setBalance(NumberUtil.round(seller.getBalance() + payOrder.getAmount(), 2).doubleValue());
		memberRepo.save(seller);

		Boolean boughtFlag = buyer.getBoughtFlag();
		if (!boughtFlag) {
			buyer.setBoughtFlag(true);
		}
		buyer.setBalance(balance);
		memberRepo.save(buyer);

		payOrder.paid();
		payOrderRepo.save(payOrder);

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithSellCollection(seller, payOrder));
		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithBuyResaleCollection(buyer, payOrder));

		if (!boughtFlag) {
			ThreadPoolUtils.getRewardPool().schedule(() -> {
				redissonClient.getTopic(Constant.首单奖励).publish(buyer.getId());
			}, 1, TimeUnit.SECONDS);
		}
	}

	public void confirmPayWithLatestCollection(PayOrder payOrder) {
		Member member = payOrder.getMember();
		member.validBasicRisk();
		Collection collection = payOrder.getCollection();
		if (collection.getStock() <= 0) {
			throw new BizException("已售罄");
		}
		double balance = NumberUtil.round(member.getBalance() - collection.getPrice(), 2).doubleValue();
		if (balance < 0) {
			throw new BizException("余额不足");
		}
		Boolean preSaleFlag = payOrder.getCreateTime().getTime() < collection.getSaleTime().getTime();
		PreSaleQualify preSaleQualify = null;
		if (preSaleFlag) {
			PreSaleQualify tmpPreSaleQualify = preSaleQualifyRepo
					.findTopByMemberIdAndCollectionIdAndStateOrderByPreMinuteDesc(member.getId(), collection.getId(),
							Constant.优先购资格状态_未使用);
			if (tmpPreSaleQualify == null) {
				throw new BizException("没有优先购的资格");
			}
			Date preMinuteAfterTime = DateUtil
					.offset(collection.getSaleTime(), DateField.MINUTE, -tmpPreSaleQualify.getPreMinute()).toJdkDate();
			if (DateUtil.compare(new Date(), preMinuteAfterTime) < 0) {
				throw new BizException(DateUtil.format(preMinuteAfterTime, "MM.dd HH:mm" + "之后才能抢购"));
			}
			preSaleQualify = tmpPreSaleQualify;
		}

		IssueResultVO firstIssueResultVO = issuedCollectionService.issue(collection, member.getId(),
				Constant.藏品获取方式_购买);

		Boolean boughtFlag = member.getBoughtFlag();
		if (!boughtFlag) {
			member.setBoughtFlag(true);
		}
		member.setBalance(balance);
		memberRepo.save(member);

		payOrder.paid();
		payOrderRepo.save(payOrder);

		if (preSaleFlag) {
			preSaleQualify.used(firstIssueResultVO.getIssuedCollectionId());
			preSaleQualifyRepo.save(preSaleQualify);
		}

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithBuyLatestCollection(member, payOrder));

		if (!boughtFlag) {
			ThreadPoolUtils.getRewardPool().schedule(() -> {
				redissonClient.getTopic(Constant.首单奖励).publish(member.getId());
			}, 1, TimeUnit.SECONDS);
		}
	}

	@Lock(keys = "'resaleCollectionCreateOrder' + #param.resaleCollectionId")
	@Transactional
	public String resaleCollectionCreateOrder(@Valid ResaleCollectionCreateOrderParam param) {
		MemberResaleCollection resaleCollection = memberResaleCollectionRepo.getOne(param.getResaleCollectionId());
		if (!Constant.转售的藏品状态_已发布.equals(resaleCollection.getState())) {
			throw new BizException("藏品状态异常");
		}
		if (StrUtil.isNotBlank(resaleCollection.getLockPayMemberId())
				&& !resaleCollection.getLockPayMemberId().equals(param.getMemberId())) {
			throw new BizException("商品已被锁定");
		}
		PayOrder pendingOrder = payOrderRepo.findTopByMemberIdAndCollectionIdAndBizOrderNoAndState(param.getMemberId(),
				resaleCollection.getCollectionId(), resaleCollection.getId(), Constant.支付订单状态_待付款);
		if (pendingOrder != null) {
			return pendingOrder.getId();
		}
		Member buyer = memberRepo.getOne(param.getMemberId());
		buyer.validPlaceOrderRisk();
		if (buyer.getId().equals(resaleCollection.getMemberId())) {
			throw new BizException("不能购买自己的藏品");
		}

		if (StrUtil.isBlank(resaleCollection.getLockPayMemberId())) {
			resaleCollection.setLockPayMemberId(param.getMemberId());
			memberResaleCollectionRepo.save(resaleCollection);
		}

		Integer payOrderDeadline = operationSettingRepo.findTopByOrderByLatelyUpdateTime().getPayOrderDeadline();
		PayOrder payOrder = PayOrder.buildWithResaleCollection(resaleCollection, buyer, payOrderDeadline);
		payOrderRepo.save(payOrder);
		return payOrder.getId();
	}

	@Lock(keys = "'latestCollectionCreateOrder' + #param.memberId + #param.collectionId")
	@Transactional
	public String latestCollectionCreateOrder(@Valid LatestCollectionCreateOrderParam param) {
		OperationSetting operationSetting = operationSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (!operationSetting.getPrimaryMarketFun()) {
			throw new BizException("未开放一级市场");
		}
		PayOrder pendingOrder = payOrderRepo.findTopByMemberIdAndCollectionIdAndState(param.getMemberId(),
				param.getCollectionId(), Constant.支付订单状态_待付款);
		if (pendingOrder != null) {
			return pendingOrder.getId();
		}
		Member member = memberRepo.getOne(param.getMemberId());
		member.validPlaceOrderRisk();
		Collection collection = collectionRepo.getOne(param.getCollectionId());
		if (!collection.getExternalSaleFlag()) {
			throw new BizException("不对外发售");
		}
		Date now = new Date();
		if (now.getTime() - collection.getSaleTime().getTime() < 0) {
			if (collection.getPreSaleFlag()) {
				PreSaleQualify preSaleQualify = preSaleQualifyRepo
						.findTopByMemberIdAndCollectionIdAndStateOrderByPreMinuteDesc(param.getMemberId(),
								param.getCollectionId(), Constant.优先购资格状态_未使用);
				if (preSaleQualify == null) {
					throw new BizException("没有优先购的资格");
				}
				Date preMinuteAfterTime = DateUtil
						.offset(collection.getSaleTime(), DateField.MINUTE, -preSaleQualify.getPreMinute()).toJdkDate();
				if (DateUtil.compare(now, preMinuteAfterTime) < 0) {
					throw new BizException(DateUtil.format(preMinuteAfterTime, "MM.dd HH:mm" + "之后才能抢购"));
				}
			} else {
				throw new BizException("未到发售时间");
			}
		}
		if (collection.getStock() <= 0) {
			throw new BizException("已售罄");
		}

		Integer payOrderDeadline = operationSetting.getPayOrderDeadline();
		PayOrder payOrder = PayOrder.buildWithLatestCollection(collection, member, payOrderDeadline);
		payOrderRepo.save(payOrder);
		return payOrder.getId();
	}

	@Transactional
	public void cancelResale(@Valid CollectionCancelResaleParam param) {
		MemberResaleCollection resaleCollection = memberResaleCollectionRepo.getOne(param.getResaleCollectionId());
		if (!param.getMemberId().equals(resaleCollection.getMemberId())) {
			throw new BizException("无权操作该藏品");
		}
		cancelResaleInner(param.getResaleCollectionId());
	}

	@Transactional
	public void cancelResaleInner(@NotBlank String resaleCollectionId) {
		MemberResaleCollection resaleCollection = memberResaleCollectionRepo.getOne(resaleCollectionId);
		if (!Constant.转售的藏品状态_已发布.equals(resaleCollection.getState())) {
			throw new BizException("藏品归属异常");
		}
		if (StrUtil.isNotBlank(resaleCollection.getLockPayMemberId())) {
			throw new BizException("锁定支付中,不能取消");
		}

		resaleCollection.cancelResale();
		memberResaleCollectionRepo.save(resaleCollection);

		MemberHoldCollection memberHoldCollection = resaleCollection.getMemberHoldCollection();
		memberHoldCollection.setState(Constant.持有藏品状态_持有中);
		memberHoldCollectionRepo.save(memberHoldCollection);

		issuedCollectionActionLogRepo.save(IssuedCollectionActionLog.buildWithCancelResale(
				memberHoldCollection.getMemberId(), memberHoldCollection.getIssuedCollectionId()));
	}

	@Transactional
	public OpenMysteryBoxResultVO openMysteryBox(OpenMysteryBoxParam param) {
		MemberHoldCollection mysteryBox = memberHoldCollectionRepo.getOne(param.getHoldCollectionId());
		if (!param.getMemberId().equals(mysteryBox.getMemberId())) {
			throw new BizException("无权操作该商品");
		}
		if (!Constant.持有藏品状态_持有中.equals(mysteryBox.getState())) {
			throw new BizException("商品归属异常");
		}
		if (!Constant.商品类型_盲盒.equals(mysteryBox.getCollection().getCommodityType())) {
			throw new BizException("该商品不属于盲盒");
		}
		List<WeightObj<MysteryBoxCommodity>> weightCommoditys = new ArrayList<>();
		for (MysteryBoxCommodity mysteryBoxCommodity : mysteryBox.getCollection().getSubCommoditys()) {
			weightCommoditys.add(new WeightObj<>(mysteryBoxCommodity, mysteryBoxCommodity.getProbability()));
		}

		MysteryBoxCommodity randomCommodity = RandomUtil.weightRandom(weightCommoditys).next();
		Collection collection = randomCommodity.getCommodity();
		if (collection.getStock() <= 0) {
			throw new BizException("盲盒太火了，请稍后再打开");
		}

		issuedCollectionService.openMysteryBoxDestroy(mysteryBox);

		issuedCollectionService.issue(collection, param.getMemberId(), Constant.藏品获取方式_盲盒);
		return OpenMysteryBoxResultVO.build(collection.getName(), collection.getCover());
	}

	@Transactional
	public void collectionResale(@Valid CollectionResaleParam param) {
		OperationSetting operationSetting = operationSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (!operationSetting.getConsignmentFun()) {
			throw new BizException("未开放寄售功能");
		}
		if (param.getResalePrice() < operationSetting.getConsignmentMinAmount()) {
			throw new BizException("价格不能少于" + operationSetting.getConsignmentMinAmount());
		}
		if (param.getResalePrice() > operationSetting.getConsignmentMaxAmount()) {
			throw new BizException("价格不能大于" + operationSetting.getConsignmentMaxAmount());
		}
		MemberHoldCollection memberHoldCollection = memberHoldCollectionRepo.getOne(param.getHoldCollectionId());
		if (!param.getMemberId().equals(memberHoldCollection.getMemberId())) {
			throw new BizException("无权操作该藏品");
		}
		Date date = DateUtil.offset(memberHoldCollection.getHoldTime(), DateField.DAY_OF_YEAR,
				operationSetting.getConsignmentLimitHoldDay()).toJdkDate();
		if (date.getTime() > new Date().getTime()) {
			throw new BizException("持有满" + operationSetting.getGiveLimitHoldDay() + "天才可以寄售");
		}
		if (!Constant.持有藏品状态_持有中.equals(memberHoldCollection.getState())) {
			throw new BizException("藏品归属异常");
		}
		memberHoldCollection.setState(Constant.持有藏品状态_转售中);
		memberHoldCollectionRepo.save(memberHoldCollection);

		MemberResaleCollection resaleCollection = memberHoldCollection
				.buildResaleCollectionReocrd(param.getResalePrice());
		memberResaleCollectionRepo.save(resaleCollection);

		issuedCollectionActionLogRepo.save(IssuedCollectionActionLog.buildWithResale(resaleCollection.getResalePrice(),
				memberHoldCollection.getMemberId(), memberHoldCollection.getIssuedCollectionId()));
	}

}
