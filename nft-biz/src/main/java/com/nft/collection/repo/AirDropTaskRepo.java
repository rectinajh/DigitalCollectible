package com.nft.collection.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.collection.domain.AirDropTask;

public interface AirDropTaskRepo extends JpaRepository<AirDropTask, String>, JpaSpecificationExecutor<AirDropTask> {

	List<AirDropTask> findByStateAndExecuteTimeLessThan(String state, Date executeTime);

}
