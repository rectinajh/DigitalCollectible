package com.nft.collection.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.collection.domain.ExchangeCode;
import com.nft.collection.domain.IssuedCollection;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ExchangeRecordVO {

	private String id;

	private String code;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date exchangeTime;

	private String memberMobile;

	private String memberBlockChainAddr;

	private Integer collectionSerialNumber;

	public static List<ExchangeRecordVO> convertFor(List<ExchangeCode> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<ExchangeRecordVO> vos = new ArrayList<>();
		for (ExchangeCode po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static ExchangeRecordVO convertFor(ExchangeCode po) {
		if (po == null) {
			return null;
		}
		ExchangeRecordVO vo = new ExchangeRecordVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getMember() != null) {
			vo.setMemberMobile(po.getMember().getMobile());
			vo.setMemberBlockChainAddr(po.getMember().getBlockChainAddr());
		}
		IssuedCollection issuedCollection = po.getIssuedCollection();
		if (issuedCollection != null) {
			vo.setCollectionSerialNumber(issuedCollection.getCollectionSerialNumber());
		}
		return vo;
	}

}
