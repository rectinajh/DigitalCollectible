package com.nft.collection.param;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.collection.domain.AirDropTask;
import com.nft.common.utils.IdUtils;
import com.nft.constants.Constant;

import lombok.Data;

@Data
public class AddAirDropTaskParam {

	@NotBlank
	private String taskName;

	@NotBlank
	private String collectionId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date executeTime;

	@Valid
	private List<AirDropConditionParam> airDropConditions;

	public AirDropTask convertToPo() {
		AirDropTask po = new AirDropTask();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setState(Constant.空投任务状态_未执行);
		if (po.getExecuteTime() == null) {
			po.setExecuteTime(new Date());
		}
		return po;
	}

}
