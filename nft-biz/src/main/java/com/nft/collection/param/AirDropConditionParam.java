package com.nft.collection.param;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.nft.collection.domain.AirDropCondition;
import com.nft.common.utils.IdUtils;

import lombok.Data;

@Data
public class AirDropConditionParam {

	@NotBlank
	private String fieldName;

	@NotBlank
	private String cond;

	@NotBlank
	private String fieldValue;

	private String logicalOperation;

	public AirDropCondition convertToPo(String airDropTaskId, Double orderNo) {
		AirDropCondition po = new AirDropCondition();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setAirDropTaskId(airDropTaskId);
		po.setOrderNo(orderNo);
		return po;
	}

}
