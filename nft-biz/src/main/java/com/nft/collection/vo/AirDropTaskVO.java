package com.nft.collection.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.collection.domain.AirDropCondition;
import com.nft.collection.domain.AirDropTask;
import com.nft.dictconfig.DictHolder;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class AirDropTaskVO {
	
	private String id;

	private String taskName;

	private String state;
	
	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date executeTime;

	private String collectionName;

	private String collectionCover;

	private List<AirDropConditionVO> airDropConditions = new ArrayList<>();

	public static List<AirDropTaskVO> convertFor(List<AirDropTask> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<AirDropTaskVO> vos = new ArrayList<>();
		for (AirDropTask po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static AirDropTaskVO convertFor(AirDropTask po) {
		if (po == null) {
			return null;
		}
		AirDropTaskVO vo = new AirDropTaskVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getCollection() != null) {
			vo.setCollectionName(po.getCollection().getName());
			vo.setCollectionCover(po.getCollection().getCover());
		}
		Set<AirDropCondition> airDropConditions = po.getAirDropConditions();
		for (AirDropCondition airDropCondition : airDropConditions) {
			vo.getAirDropConditions().add(AirDropConditionVO.convertFor(airDropCondition));
		}
		vo.setStateName(DictHolder.getDictItemName("airDropTaskState", vo.getState()));
		return vo;
	}

}
