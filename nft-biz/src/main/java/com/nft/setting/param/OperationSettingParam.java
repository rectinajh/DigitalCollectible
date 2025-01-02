package com.nft.setting.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OperationSettingParam {
	
	@NotNull
	private Boolean primaryMarketFun;

	@NotNull
	private Boolean giveFun;

	@NotNull
	private Boolean consignmentFun;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Integer giveLimitHoldDay;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Integer consignmentLimitHoldDay;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double consignmentMinAmount;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double consignmentMaxAmount;
	
	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer payOrderDeadline;

}
