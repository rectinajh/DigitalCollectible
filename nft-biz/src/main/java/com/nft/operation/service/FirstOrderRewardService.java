package com.nft.operation.service;

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

import com.nft.collection.domain.PayOrder;
import com.nft.collection.repo.PayOrderRepo;
import com.nft.collection.service.AirDropService;
import com.nft.collection.vo.AirDropResultVO;
import com.nft.common.utils.ThreadPoolUtils;
import com.nft.common.vo.PageResult;
import com.nft.constants.Constant;
import com.nft.operation.domain.FirstOrderRewardRecord;
import com.nft.operation.param.FirstOrderRewardRecordQueryCondParam;
import com.nft.operation.repo.FirstOrderRewardRecordRepo;
import com.nft.operation.vo.FirstOrderRewardRecordVO;
import com.nft.setting.domain.FirstOrderRewardSetting;
import com.nft.setting.repo.FirstOrderRewardSettingRepo;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.util.StrUtil;

@Service
public class FirstOrderRewardService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private AirDropService airDropService;

	@Autowired
	private FirstOrderRewardSettingRepo firstOrderRewardSettingRepo;

	@Autowired
	private PayOrderRepo payOrderRepo;

	@Autowired
	private FirstOrderRewardRecordRepo firstOrderRewardRecordRepo;

	@Transactional(readOnly = true)
	public PageResult<FirstOrderRewardRecordVO> findByPage(@Valid FirstOrderRewardRecordQueryCondParam param) {
		Page<FirstOrderRewardRecord> result = firstOrderRewardRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<FirstOrderRewardRecordVO> pageResult = new PageResult<>(
				FirstOrderRewardRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Lock(keys = "'createFirstOrderRewardRecord' + #memberId")
	@Transactional
	public void createFirstOrderRewardRecord(@NotBlank String memberId) {
		FirstOrderRewardSetting firstOrderRewardSetting = firstOrderRewardSettingRepo
				.findTopByOrderByLatelyUpdateTime();
		if (!firstOrderRewardSetting.getRewardFun()) {
			return;
		}
		if (StrUtil.isBlank(firstOrderRewardSetting.getRewardArtworkId())) {
			return;
		}
		FirstOrderRewardRecord oldRecord = firstOrderRewardRecordRepo.findTopByMemberId(memberId);
		if (oldRecord != null) {
			return;
		}
		PayOrder payOrder = payOrderRepo.findTopByMemberIdAndStateOrderByPaidTimeAsc(memberId, Constant.支付订单状态_已付款);
		if (payOrder == null) {
			return;
		}

		FirstOrderRewardRecord firstOrderRewardRecord = FirstOrderRewardRecord.build(memberId,
				firstOrderRewardSetting.getRewardArtworkId(), payOrder.getId());
		firstOrderRewardRecordRepo.save(firstOrderRewardRecord);

		ThreadPoolUtils.getRewardPool().schedule(() -> {
			redissonClient.getTopic(Constant.首单奖励发放).publish(firstOrderRewardRecord.getId());
		}, 1, TimeUnit.SECONDS);
	}

	@Transactional
	public void firstOrderRewardGrant() {
		List<FirstOrderRewardRecord> rewardRecords = firstOrderRewardRecordRepo.findByState(Constant.奖励发放状态_未发放);
		for (FirstOrderRewardRecord rewardRecord : rewardRecords) {
			redissonClient.getTopic(Constant.首单奖励发放).publish(rewardRecord.getId());
		}
	}

	@Lock(keys = "'firstOrderRewardGrant' + #id")
	@Transactional
	public void firstOrderRewardGrant(@NotBlank String id) {
		FirstOrderRewardRecord rewardRecord = firstOrderRewardRecordRepo.getOne(id);
		if (!Constant.奖励发放状态_未发放.equals(rewardRecord.getState())) {
			return;
		}

		AirDropResultVO airDropResult = airDropService.airDrop(rewardRecord.getMemberId(),
				rewardRecord.getCollectionId(), Constant.空投业务类型_首单奖励);
		if (airDropResult.getSuccessFlag()) {
			rewardRecord.success();
		} else {
			rewardRecord.fail();
		}
		firstOrderRewardRecordRepo.save(rewardRecord);
	}

}
