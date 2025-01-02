package com.nft.setting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.setting.domain.RiskSetting;

public interface RiskSettingRepo extends JpaRepository<RiskSetting, String>, JpaSpecificationExecutor<RiskSetting> {

	RiskSetting findTopByOrderByLatelyUpdateTime();

}
