package com.nft.collection.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.collection.domain.MemberHoldArtworkSummary;

public interface MemberHoldArtworkSummaryRepo
		extends JpaRepository<MemberHoldArtworkSummary, String>, JpaSpecificationExecutor<MemberHoldArtworkSummary> {

}
