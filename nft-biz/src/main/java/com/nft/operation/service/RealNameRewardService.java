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
import com.nft.operation.domain.RealNameRewardRecord;
import com.nft.operation.param.RealNameRewardRecordQueryCondParam;
import com.nft.operation.repo.RealNameRewardRecordRepo;
import com.nft.operation.vo.RealNameRewardRecordVO;
import com.nft.setting.domain.RealNameRewardSetting;
import com.nft.setting.repo.RealNameRewardSettingRepo;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.util.StrUtil;

@Service
public class RealNameRewardService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private AirDropService airDropService;

	@Autowired
	private RealNameRewardRecordRepo realNameRewardRecordRepo;

	@Autowired
	private RealNameRewardSettingRepo realNameRewardSettingRepo;

	@Transactional(readOnly = true)
	public PageResult<RealNameRewardRecordVO> findByPage(@Valid RealNameRewardRecordQueryCondParam param) {
		Page<RealNameRewardRecord> result = realNameRewardRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<RealNameRewardRecordVO> pageResult = new PageResult<>(
				RealNameRewardRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Lock(keys = "'createRealNameRewardRecord' + #memberId")
	@Transactional
	public void createRealNameRewardRecord(@NotBlank String memberId) {
		RealNameRewardSetting realNameRewardSetting = realNameRewardSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (!realNameRewardSetting.getRewardFun()) {
			return;
		}
		if (StrUtil.isBlank(realNameRewardSetting.getRewardArtworkId())) {
			return;
		}
		RealNameRewardRecord oldRecord = realNameRewardRecordRepo.findTopByMemberId(memberId);
		if (oldRecord != null) {
			return;
		}

		RealNameRewardRecord realNameRewardRecord = RealNameRewardRecord.build(memberId,
				realNameRewardSetting.getRewardArtworkId());
		realNameRewardRecordRepo.save(realNameRewardRecord);

		ThreadPoolUtils.getRewardPool().schedule(() -> {
			redissonClient.getTopic(Constant.实名认证奖励发放).publish(realNameRewardRecord.getId());
		}, 1, TimeUnit.SECONDS);
	}

	@Transactional
	public void realNameRewardGrant() {
		List<RealNameRewardRecord> rewardRecords = realNameRewardRecordRepo.findByState(Constant.奖励发放状态_未发放);
		for (RealNameRewardRecord rewardRecord : rewardRecords) {
			redissonClient.getTopic(Constant.实名认证奖励发放).publish(rewardRecord.getId());
		}
	}

	@Lock(keys = "'realNameRewardGrant' + #id")
	@Transactional
	public void realNameRewardGrant(@NotBlank String id) {
		RealNameRewardRecord rewardRecord = realNameRewardRecordRepo.getOne(id);
		if (!Constant.奖励发放状态_未发放.equals(rewardRecord.getState())) {
			return;
		}

		AirDropResultVO airDropResult = airDropService.airDrop(rewardRecord.getMemberId(),
				rewardRecord.getCollectionId(), Constant.空投业务类型_新手奖励);
		if (airDropResult.getSuccessFlag()) {
			rewardRecord.success();
		} else {
			rewardRecord.fail();
		}
		realNameRewardRecordRepo.save(rewardRecord);
	}

}
