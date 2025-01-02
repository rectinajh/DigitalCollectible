package com.nft.collection.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.collection.domain.AirDropCondition;

public interface AirDropConditionRepo
		extends JpaRepository<AirDropCondition, String>, JpaSpecificationExecutor<AirDropCondition> {

}
