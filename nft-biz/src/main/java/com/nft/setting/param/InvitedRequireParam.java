package com.nft.setting.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InvitedRequireParam implements Comparable<InvitedRequireParam> {

	@NotBlank
	private String rewardArtworkId;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer invitedCount;
	
	private Integer level;

	@Override
	public int compareTo(InvitedRequireParam o) {
		return this.getInvitedCount() - o.getInvitedCount();
	}

}
