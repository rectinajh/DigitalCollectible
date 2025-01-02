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
@Table(name = "operation_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class OperationSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;
	
	private Boolean primaryMarketFun;

	private Boolean giveFun;

	private Boolean consignmentFun;
	
	private Integer giveLimitHoldDay;
	
	private Integer consignmentLimitHoldDay;

	private Double consignmentMinAmount;

	private Double consignmentMaxAmount;
	
	private Integer payOrderDeadline;
	
	private Date latelyUpdateTime;

	public static OperationSetting build() {
		OperationSetting setting = new OperationSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
