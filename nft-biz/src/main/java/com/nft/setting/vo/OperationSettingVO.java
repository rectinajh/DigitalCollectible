package com.nft.setting.vo;

import org.springframework.beans.BeanUtils;

import com.nft.setting.domain.OperationSetting;

import lombok.Data;

@Data
public class OperationSettingVO {

	private Boolean primaryMarketFun;

	private Boolean giveFun;

	private Boolean consignmentFun;

	private Integer giveLimitHoldDay;

	private Integer consignmentLimitHoldDay;

	private Double consignmentMinAmount;

	private Double consignmentMaxAmount;

	private Integer payOrderDeadline;

	public static OperationSettingVO convertFor(OperationSetting po) {
		OperationSettingVO vo = new OperationSettingVO();
		if (po != null) {
			BeanUtils.copyProperties(po, vo);
		}
		return vo;
	}

}
