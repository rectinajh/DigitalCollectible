package com.nft.collection.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.collection.domain.AirDropRecord;

public interface AirDropRecordRepo
		extends JpaRepository<AirDropRecord, String>, JpaSpecificationExecutor<AirDropRecord> {

	List<AirDropRecord> findByState(String state);

}
