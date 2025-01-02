package com.nft.setting.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nft.common.utils.IdUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invited_reward_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class InvitedRewardSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Boolean rewardFun;
	
	private String invitedRequire;

	private Date latelyUpdateTime;

	public static InvitedRewardSetting build() {
		InvitedRewardSetting setting = new InvitedRewardSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
