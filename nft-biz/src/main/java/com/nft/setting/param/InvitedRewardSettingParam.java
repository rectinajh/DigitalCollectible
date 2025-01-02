package com.nft.setting.param;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InvitedRewardSettingParam {

	@NotNull
	private Boolean rewardFun;

	@Valid
	@NotEmpty
	private List<InvitedRequireParam> invitedRequires;



}
