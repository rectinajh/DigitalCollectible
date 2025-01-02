package com.nft.operation.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.operation.domain.RealNameRewardRecord;

public interface RealNameRewardRecordRepo
		extends JpaRepository<RealNameRewardRecord, String>, JpaSpecificationExecutor<RealNameRewardRecord> {

	RealNameRewardRecord findTopByMemberId(String memberId);

	List<RealNameRewardRecord> findByState(String state);

}
