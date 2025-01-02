package com.nft.collection.service;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.collection.domain.CollectionGiveRecord;
import com.nft.collection.domain.MemberHoldCollection;
import com.nft.collection.param.CollectionGiveParam;
import com.nft.collection.param.CollectionGiveRecordQueryCondParam;
import com.nft.collection.repo.CollectionGiveRecordRepo;
import com.nft.collection.repo.MemberHoldCollectionRepo;
import com.nft.collection.vo.CollectionGiveRecordVO;
import com.nft.collection.vo.CollectionReceiverInfoVO;
import com.nft.collection.vo.MyGiveRecordVO;
import com.nft.common.exception.BizException;
import com.nft.common.vo.PageResult;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;
import com.nft.member.repo.MemberRepo;
import com.nft.setting.domain.OperationSetting;
import com.nft.setting.repo.OperationSettingRepo;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

@Validated
@Service
public class CollectionGiveService {

	@Autowired
	private IssuedCollectionService issuedCollectionService;

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private CollectionGiveRecordRepo collectionGiveRecordRepo;

	@Autowired
	private MemberHoldCollectionRepo memberHoldCollectionRepo;

	@Autowired
	private OperationSettingRepo operationSettingRepo;

	@Transactional
	public void collectionGive(@Valid CollectionGiveParam param) {
		OperationSetting operationSetting = operationSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (!operationSetting.getGiveFun()) {
			throw new BizException("未开放转赠功能");
		}
		MemberHoldCollection holdCollection = memberHoldCollectionRepo.getOne(param.getHoldCollectionId());
		if (!param.getMemberId().equals(holdCollection.getMemberId())) {
			throw new BizException("无权操作该藏品");
		}
		Date date = DateUtil
				.offset(holdCollection.getHoldTime(), DateField.DAY_OF_YEAR, operationSetting.getGiveLimitHoldDay())
				.toJdkDate();
		if (date.getTime() > new Date().getTime()) {
			throw new BizException("持有满" + operationSetting.getGiveLimitHoldDay() + "天才可以转赠");
		}
		if (!Constant.持有藏品状态_持有中.equals(holdCollection.getState())) {
			throw new BizException("藏品归属异常");
		}

		Member giveTo = memberRepo.findByMobileAndDeletedFlagIsFalse(param.getGiveToAccount());
		if (giveTo == null) {
			giveTo = memberRepo.findByBlockChainAddrAndDeletedFlagIsFalse(param.getGiveToAccount());
		}
		if (giveTo == null) {
			throw new BizException("账号不存在");
		}
		if (giveTo.getId().equals(param.getMemberId())) {
			throw new BizException("不能转给自己");
		}
		giveTo.validBasicRisk();

		CollectionGiveRecord collectionGiveRecord = CollectionGiveRecord.build(holdCollection.getId(),
				holdCollection.getMemberId(), giveTo.getId());
		collectionGiveRecordRepo.save(collectionGiveRecord);

		issuedCollectionService.give(holdCollection, giveTo);
	}

	@Transactional(readOnly = true)
	public PageResult<MyGiveRecordVO> findMyGiveRecordByPage(@Valid CollectionGiveRecordQueryCondParam param) {
		Page<CollectionGiveRecord> result = collectionGiveRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("giveTime"))));
		PageResult<MyGiveRecordVO> pageResult = new PageResult<>(
				MyGiveRecordVO.convertFor(result.getContent(), param.getMemberId()), param.getPageNum(),
				param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<CollectionGiveRecordVO> findCollectionGiveRecordByPage(
			@Valid CollectionGiveRecordQueryCondParam param) {
		Page<CollectionGiveRecord> result = collectionGiveRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("giveTime"))));
		PageResult<CollectionGiveRecordVO> pageResult = new PageResult<>(
				CollectionGiveRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public CollectionReceiverInfoVO getCollectionReceiverInfo(@NotBlank String giveToAccount,
			@NotBlank String giveFromId) {
		Member giveTo = memberRepo.findByMobileAndDeletedFlagIsFalse(giveToAccount);
		if (giveTo == null) {
			giveTo = memberRepo.findByBlockChainAddrAndDeletedFlagIsFalse(giveToAccount);
		}
		if (giveTo == null) {
			throw new BizException("请检查账号是否正确");
		}
		if (giveTo.getId().equals(giveFromId)) {
			throw new BizException("无法转赠给自己");
		}
		giveTo.validBasicRisk();
		return CollectionReceiverInfoVO.build(giveTo.getMobile(), giveTo.getBlockChainAddr());
	}

}
