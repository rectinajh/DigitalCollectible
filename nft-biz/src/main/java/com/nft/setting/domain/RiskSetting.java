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
@Table(name = "risk_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class RiskSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;
	
	private Integer orderUnpaidTimeRange;

	private Integer orderUnpaidCount;

	private Integer orderUnpaidPunish;

	private Date latelyUpdateTime;

	public static RiskSetting build() {
		RiskSetting setting = new RiskSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
