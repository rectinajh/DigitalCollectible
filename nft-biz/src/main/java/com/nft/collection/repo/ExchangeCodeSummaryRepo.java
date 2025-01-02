package com.nft.collection.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.collection.domain.ExchangeCodeSummary;

public interface ExchangeCodeSummaryRepo
		extends JpaRepository<ExchangeCodeSummary, String>, JpaSpecificationExecutor<ExchangeCodeSummary> {

}
