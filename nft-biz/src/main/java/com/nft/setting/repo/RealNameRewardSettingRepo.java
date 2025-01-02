package com.nft.setting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.setting.domain.RealNameRewardSetting;

public interface RealNameRewardSettingRepo
		extends JpaRepository<RealNameRewardSetting, String>, JpaSpecificationExecutor<RealNameRewardSetting> {

	RealNameRewardSetting findTopByOrderByLatelyUpdateTime();

}
