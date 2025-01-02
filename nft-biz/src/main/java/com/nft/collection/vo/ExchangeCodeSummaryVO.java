package com.nft.collection.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.nft.collection.domain.Collection;
import com.nft.collection.domain.ExchangeCodeSummary;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ExchangeCodeSummaryVO {

	private String id;

	private Integer totalCount;

	private Integer unusedCount;

	private Integer usedCount;

	private Integer invalidCount;

	private Integer expiredCount;

	private String collectionName;

	private String collectionCover;

	public static List<ExchangeCodeSummaryVO> convertFor(List<ExchangeCodeSummary> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<ExchangeCodeSummaryVO> vos = new ArrayList<>();
		for (ExchangeCodeSummary po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static ExchangeCodeSummaryVO convertFor(ExchangeCodeSummary po) {
		if (po == null) {
			return null;
		}
		ExchangeCodeSummaryVO vo = new ExchangeCodeSummaryVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getCollection() != null) {
			Collection collection = po.getCollection();
			vo.setCollectionName(collection.getName());
			vo.setCollectionCover(collection.getCover());
		}
		return vo;
	}

}
