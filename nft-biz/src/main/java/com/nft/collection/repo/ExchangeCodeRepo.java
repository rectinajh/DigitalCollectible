package com.nft.collection.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.collection.domain.ExchangeCode;

public interface ExchangeCodeRepo extends JpaRepository<ExchangeCode, String>, JpaSpecificationExecutor<ExchangeCode> {

	ExchangeCode findTopByCodeAndStateAndDeletedFlagFalse(String code, String state);

	List<ExchangeCode> findByStateAndEffectiveTimeLessThan(String state, Date effectiveTime);
	
	List<ExchangeCode> findByCollectionIdAndStateAndDeletedFlagFalse(String collectionId, String state);
	
}
