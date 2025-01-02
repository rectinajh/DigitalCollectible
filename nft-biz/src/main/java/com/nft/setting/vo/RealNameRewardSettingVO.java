package com.nft.setting.vo;

import org.springframework.beans.BeanUtils;

import com.nft.setting.domain.RealNameRewardSetting;

import lombok.Data;

@Data
public class RealNameRewardSettingVO {

	private Boolean rewardFun;

	private String rewardArtworkId;

	public static RealNameRewardSettingVO convertFor(RealNameRewardSetting po) {
		RealNameRewardSettingVO vo = new RealNameRewardSettingVO();
		if (po != null) {
			BeanUtils.copyProperties(po, vo);
		}
		return vo;
	}

}
