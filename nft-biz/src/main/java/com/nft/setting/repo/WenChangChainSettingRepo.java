package com.nft.setting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.setting.domain.WenChangChainSetting;

public interface WenChangChainSettingRepo
		extends JpaRepository<WenChangChainSetting, String>, JpaSpecificationExecutor<WenChangChainSetting> {

	WenChangChainSetting findTopByOrderByLatelyUpdateTime();

}
