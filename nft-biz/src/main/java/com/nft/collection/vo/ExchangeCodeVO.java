package com.nft.collection.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.collection.domain.ExchangeCode;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ExchangeCodeVO {

	private String id;

	private String code;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date effectiveTime;

	public static List<ExchangeCodeVO> convertFor(List<ExchangeCode> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<ExchangeCodeVO> vos = new ArrayList<>();
		for (ExchangeCode po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static ExchangeCodeVO convertFor(ExchangeCode po) {
		if (po == null) {
			return null;
		}
		ExchangeCodeVO vo = new ExchangeCodeVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
