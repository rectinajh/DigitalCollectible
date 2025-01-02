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

import com.nft.collection.service.AirDropService;
import com.nft.collection.vo.AirDropResultVO;
import com.nft.common.utils.ThreadPoolUtils;
import com.nft.common.vo.PageResult;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;
import com.nft.member.domain.MemberInvitedCount;
import com.nft.member.repo.MemberRepo;
import com.nft.operation.domain.InvitedRewardRecord;
import com.nft.operation.param.InvitedRewardRecordQueryCondParam;
import com.nft.operation.repo.InvitedRewardRecordRepo;
import com.nft.operation.vo.InvitedRewardRecordVO;
import com.nft.setting.domain.InvitedRewardSetting;
import com.nft.setting.repo.InvitedRewardSettingRepo;
import com.nft.setting.vo.InvitedRequireVO;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.json.JSONUtil;

@Service
public class InvitedRewardService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private AirDropService airDropService;

	@Autowired
	private InvitedRewardSettingRepo invitedRewardSettingRepo;

	@Autowired
	private InvitedRewardRecordRepo invitedRewardRecordRepo;

	@Autowired
	private MemberRepo memberRepo;

	@Transactional(readOnly = true)
	public PageResult<InvitedRewardRecordVO> findByPage(@Valid InvitedRewardRecordQueryCondParam param) {
		Page<InvitedRewardRecord> result = invitedRewardRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<InvitedRewardRecordVO> pageResult = new PageResult<>(
				InvitedRewardRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Lock(keys = "'createInvitedRewardRecord' + #memberId")
	@Transactional
	public void createInvitedRewardRecord(@NotBlank String memberId) {
		InvitedRewardSetting invitedRewardSetting = invitedRewardSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (!invitedRewardSetting.getRewardFun()) {
			return;
		}
		Member member = memberRepo.getOne(memberId);
		MemberInvitedCount memberInvitedCount = member.getMemberInvitedCount();
		if (memberInvitedCount == null) {
			return;
		}
		InvitedRequireVO currentInvitedRequire = null;
		List<InvitedRequireVO> invitedRequires = JSONUtil.toList(invitedRewardSetting.getInvitedRequire(),
				InvitedRequireVO.class);
		for (InvitedRequireVO invitedRequire : invitedRequires) {
			if (memberInvitedCount.getRealNameCount() >= invitedRequire.getInvitedCount()) {
				currentInvitedRequire = invitedRequire;
			}
		}
		if (currentInvitedRequire == null) {
			return;
		}
		InvitedRewardRecord oldRecord = invitedRewardRecordRepo.findTopByMemberIdAndLevel(memberId,
				currentInvitedRequire.getLevel());
		if (oldRecord != null) {
			return;
		}

		InvitedRewardRecord invitedRewardRecord = InvitedRewardRecord.build(memberId,
				currentInvitedRequire.getRewardArtworkId(), currentInvitedRequire.getLevel(),
				currentInvitedRequire.getInvitedCount());
		invitedRewardRecordRepo.save(invitedRewardRecord);

		ThreadPoolUtils.getRewardPool().schedule(() -> {
			redissonClient.getTopic(Constant.邀请奖励发放).publish(invitedRewardRecord.getId());
		}, 1, TimeUnit.SECONDS);
	}

	@Transactional
	public void invitedRewardGrant() {
		List<InvitedRewardRecord> rewardRecords = invitedRewardRecordRepo.findByState(Constant.奖励发放状态_未发放);
		for (InvitedRewardRecord rewardRecord : rewardRecords) {
			redissonClient.getTopic(Constant.邀请奖励发放).publish(rewardRecord.getId());
		}
	}

	@Lock(keys = "'invitedRewardGrant' + #id")
	@Transactional
	public void invitedRewardGrant(@NotBlank String id) {
		InvitedRewardRecord rewardRecord = invitedRewardRecordRepo.getOne(id);
		if (!Constant.奖励发放状态_未发放.equals(rewardRecord.getState())) {
			return;
		}

		AirDropResultVO airDropResult = airDropService.airDrop(rewardRecord.getMemberId(),
				rewardRecord.getCollectionId(), Constant.空投业务类型_邀请奖励);
		if (airDropResult.getSuccessFlag()) {
			rewardRecord.success();
		} else {
			rewardRecord.fail();
		}
		invitedRewardRecordRepo.save(rewardRecord);
	}

}
