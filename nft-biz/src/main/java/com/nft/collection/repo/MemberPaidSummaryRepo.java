package com.nft.collection.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.collection.domain.MemberPaidSummary;

public interface MemberPaidSummaryRepo
		extends JpaRepository<MemberPaidSummary, String>, JpaSpecificationExecutor<MemberPaidSummary> {

}
