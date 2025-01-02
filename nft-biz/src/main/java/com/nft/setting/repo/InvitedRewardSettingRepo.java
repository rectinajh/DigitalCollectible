package com.nft.setting.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.nft.setting.domain.InvitedRewardSetting;

public interface InvitedRewardSettingRepo
		extends JpaRepository<InvitedRewardSetting, String>, JpaSpecificationExecutor<InvitedRewardSetting> {
	
	InvitedRewardSetting findTopByOrderByLatelyUpdateTime();

}
