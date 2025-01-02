package com.nft.operation.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.operation.domain.FirstOrderRewardRecord;

public interface FirstOrderRewardRecordRepo
		extends JpaRepository<FirstOrderRewardRecord, String>, JpaSpecificationExecutor<FirstOrderRewardRecord> {

	FirstOrderRewardRecord findTopByMemberId(String memberId);

	List<FirstOrderRewardRecord> findByState(String state);

}
