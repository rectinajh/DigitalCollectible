package com.nft.setting.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class FirstOrderRewardSettingParam {

	@NotNull
	private Boolean rewardFun;

	@NotBlank
	private String rewardArtworkId;

}
