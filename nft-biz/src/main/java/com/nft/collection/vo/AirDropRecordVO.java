package com.nft.collection.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.collection.domain.AirDropRecord;
import com.nft.collection.domain.IssuedCollection;
import com.nft.dictconfig.DictHolder;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class AirDropRecordVO {

	private String id;

	private String orderNo;

	private String bizType;

	private String bizTypeName;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dealTime;

	private String taskName;

	private String collectionName;

	private String collectionCover;

	private Integer collectionQuantity;

	private Integer collectionSerialNumber;

	private String memberMobile;

	private String memberBlockChainAddr;

	public static List<AirDropRecordVO> convertFor(List<AirDropRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<AirDropRecordVO> vos = new ArrayList<>();
		for (AirDropRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static AirDropRecordVO convertFor(AirDropRecord po) {
		if (po == null) {
			return null;
		}
		AirDropRecordVO vo = new AirDropRecordVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getAirDropTask() != null) {
			vo.setTaskName(po.getAirDropTask().getTaskName());
		}
		if (po.getCollection() != null) {
			vo.setCollectionName(po.getCollection().getName());
			vo.setCollectionCover(po.getCollection().getCover());
			vo.setCollectionQuantity(po.getCollection().getQuantity());
		}
		IssuedCollection issuedCollection = po.getIssuedCollection();
		if (issuedCollection != null) {
			vo.setCollectionSerialNumber(issuedCollection.getCollectionSerialNumber());
		}
		if (po.getMember() != null) {
			vo.setMemberMobile(po.getMember().getMobile());
			vo.setMemberBlockChainAddr(po.getMember().getBlockChainAddr());
		}
		vo.setStateName(DictHolder.getDictItemName("airDropRecordState", vo.getState()));
		vo.setBizTypeName(DictHolder.getDictItemName("airDropBizType", vo.getBizType()));
		return vo;
	}

}
