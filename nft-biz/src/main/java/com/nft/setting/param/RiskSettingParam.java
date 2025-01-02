package com.nft.setting.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RiskSettingParam {

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer orderUnpaidTimeRange;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer orderUnpaidCount;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer orderUnpaidPunish;

}
