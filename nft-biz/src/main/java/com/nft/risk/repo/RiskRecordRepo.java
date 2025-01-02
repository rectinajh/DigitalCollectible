package com.nft.risk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.risk.domain.RiskRecord;

public interface RiskRecordRepo extends JpaRepository<RiskRecord, String>, JpaSpecificationExecutor<RiskRecord> {

}
