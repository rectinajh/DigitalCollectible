package com.nft.setting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.setting.domain.FirstOrderRewardSetting;

public interface FirstOrderRewardSettingRepo
		extends JpaRepository<FirstOrderRewardSetting, String>, JpaSpecificationExecutor<FirstOrderRewardSetting> {

	FirstOrderRewardSetting findTopByOrderByLatelyUpdateTime();

}
