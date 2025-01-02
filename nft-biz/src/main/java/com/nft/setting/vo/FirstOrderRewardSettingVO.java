package com.nft.setting.vo;

import org.springframework.beans.BeanUtils;

import com.nft.setting.domain.FirstOrderRewardSetting;

import lombok.Data;

@Data
public class FirstOrderRewardSettingVO {

	private Boolean rewardFun;

	private String rewardArtworkId;

	public static FirstOrderRewardSettingVO convertFor(FirstOrderRewardSetting po) {
		FirstOrderRewardSettingVO vo = new FirstOrderRewardSettingVO();
		if (po != null) {
			BeanUtils.copyProperties(po, vo);
		}
		return vo;
	}

}
