package com.nft.collection.service;

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

import com.nft.collection.domain.AirDropCondition;
import com.nft.collection.domain.AirDropRecord;
import com.nft.collection.domain.AirDropTask;
import com.nft.collection.domain.Collection;
import com.nft.collection.param.AddAirDropTaskParam;
import com.nft.collection.param.AirDropConditionParam;
import com.nft.collection.param.AirDropRecordQueryCondParam;
import com.nft.collection.param.AirDropTaskQueryCondParam;
import com.nft.collection.repo.AirDropConditionRepo;
import com.nft.collection.repo.AirDropRecordRepo;
import com.nft.collection.repo.AirDropTaskRepo;
import com.nft.collection.repo.CollectionRepo;
import com.nft.collection.vo.AirDropRecordVO;
import com.nft.collection.vo.AirDropResultVO;
import com.nft.collection.vo.AirDropTaskVO;
import com.nft.collection.vo.IssueResultVO;
import com.nft.common.utils.ThreadPoolUtils;
import com.nft.common.vo.PageResult;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;
import com.nft.member.param.MemberSnapshotQueryParam;
import com.nft.member.repo.MemberRepo;
import com.nft.member.service.MemberService;
import com.nft.member.vo.MemberSnapshotVO;
import com.zengtengpeng.annotation.Lock;

@Validated
@Service
public class AirDropService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private MemberService memberService;

	@Autowired
	private IssuedCollectionService issuedCollectionService;

	@Autowired
	private CollectionRepo collectionRepo;

	@Autowired
	private AirDropRecordRepo airDropRecordRepo;

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private AirDropTaskRepo airDropTaskRepo;

	@Autowired
	private AirDropConditionRepo airDropConditionRepo;

	@Transactional(readOnly = true)
	public PageResult<AirDropRecordVO> findAirDropRecordByPage(@Valid AirDropRecordQueryCondParam param) {
		Page<AirDropRecord> result = airDropRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<AirDropRecordVO> pageResult = new PageResult<>(AirDropRecordVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public List<AirDropTaskVO> findAllAirDropTask() {
		AirDropTaskQueryCondParam param = new AirDropTaskQueryCondParam();
		List<AirDropTask> result = airDropTaskRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("createTime")));
		return AirDropTaskVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<AirDropTaskVO> findAirDropTaskByPage(@Valid AirDropTaskQueryCondParam param) {
		Page<AirDropTask> result = airDropTaskRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<AirDropTaskVO> pageResult = new PageResult<>(AirDropTaskVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void addAirDropTask(@Valid AddAirDropTaskParam param) {
		AirDropTask airDropTask = param.convertToPo();
		airDropTaskRepo.save(airDropTask);

		double orderNo = 1;
		for (AirDropConditionParam airDropConditionParam : param.getAirDropConditions()) {
			AirDropCondition airDropCondition = airDropConditionParam.convertToPo(airDropTask.getId(), orderNo);
			airDropConditionRepo.save(airDropCondition);

			orderNo++;
		}

		if (param.getExecuteTime() == null) {
			ThreadPoolUtils.getAirDropPool().schedule(() -> {
				redissonClient.getTopic(Constant.执行空投任务).publish(airDropTask.getId());
			}, 1, TimeUnit.SECONDS);
		}
	}

	@Transactional
	public void executeAirDropTask() {
		Date now = new Date();
		List<AirDropTask> airDropTasks = airDropTaskRepo.findByStateAndExecuteTimeLessThan(Constant.空投任务状态_未执行, now);
		for (AirDropTask airDropTask : airDropTasks) {
			redissonClient.getTopic(Constant.执行空投任务).publish(airDropTask.getId());
		}
	}

	@Lock(keys = "'executeAirDropTask' + #id")
	@Transactional
	public void executeAirDropTask(String id) {
		AirDropTask airDropTask = airDropTaskRepo.getOne(id);
		if (!Constant.空投任务状态_未执行.equals(airDropTask.getState())) {
			return;
		}

		Date now = new Date();
		List<MemberSnapshotVO> memberSnapshots = memberService
				.findMemberSnapshot(MemberSnapshotQueryParam.buildWithAirDrop(airDropTask.getAirDropConditions()));
		for (MemberSnapshotVO memberSnapshot : memberSnapshots) {
			AirDropRecord airDropRecord = AirDropRecord.buildWithAirDropTask(memberSnapshot.getId(),
					airDropTask.getCollectionId(), airDropTask.getId());
			airDropRecord.setCreateTime(now);
			airDropRecordRepo.save(airDropRecord);
		}

		airDropTask.setState(Constant.空投任务状态_已执行);
		airDropTaskRepo.save(airDropTask);
	}

	@Transactional
	public void airDropGrant() {
		List<AirDropRecord> airDropRecords = airDropRecordRepo.findByState(Constant.空投记录状态_未执行);
		for (AirDropRecord airDropRecord : airDropRecords) {
			redissonClient.getTopic(Constant.空投发放).publish(airDropRecord.getId());
		}
	}

	@Lock(keys = "'airDropGrant' + #id")
	@Transactional
	public void airDropGrant(@NotBlank String id) {
		AirDropRecord airDropRecord = airDropRecordRepo.getOne(id);
		if (!Constant.空投记录状态_未执行.equals(airDropRecord.getState())) {
			return;
		}

		AirDropResultVO airDropResult = airDropInner(airDropRecord.getMemberId(), airDropRecord.getCollectionId());
		if (airDropResult.getSuccessFlag()) {
			airDropRecord.success(airDropResult.getIssuedCollectionId());
		} else {
			airDropRecord.fail();
		}
		airDropRecordRepo.save(airDropRecord);
	}

	@Transactional
	public AirDropResultVO airDrop(@NotBlank String memberId, @NotBlank String collectionId, @NotBlank String bizType) {
		AirDropResultVO airDropResult = airDropInner(memberId, collectionId);
		if (airDropResult.getSuccessFlag()) {
			airDropRecordRepo.save(AirDropRecord.buildWithSuccess(memberId, collectionId,
					airDropResult.getIssuedCollectionId(), bizType));
		} else {
			airDropRecordRepo.save(AirDropRecord.buildWithFail(memberId, collectionId, bizType));
		}
		return airDropResult;
	}

	public AirDropResultVO airDropInner(@NotBlank String memberId, @NotBlank String collectionId) {
		Member member = memberRepo.getOne(memberId);
		member.validBasicRisk();
		Collection collection = collectionRepo.getOne(collectionId);
		if (collection.getStock() <= 0) {
			return AirDropResultVO.fail();
		}

		IssueResultVO firstIssueResultVO = issuedCollectionService.issue(collection, memberId,
				Constant.藏品获取方式_空投);

		return AirDropResultVO.success(firstIssueResultVO.getIssuedCollectionId());
	}

}
