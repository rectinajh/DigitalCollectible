package com.nft.operation.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.operation.domain.InvitedRewardRecord;

public interface InvitedRewardRecordRepo
		extends JpaRepository<InvitedRewardRecord, String>, JpaSpecificationExecutor<InvitedRewardRecord> {

	InvitedRewardRecord findTopByMemberIdAndLevel(String memberId, Integer level);

	List<InvitedRewardRecord> findByState(String state);

}
