package com.nft.setting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.setting.domain.OperationSetting;

public interface OperationSettingRepo
		extends JpaRepository<OperationSetting, String>, JpaSpecificationExecutor<OperationSetting> {

	OperationSetting findTopByOrderByLatelyUpdateTime();
	
}
