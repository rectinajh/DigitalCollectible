package com.nft.operation.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.dictconfig.DictHolder;
import com.nft.operation.domain.FirstOrderRewardRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class FirstOrderRewardRecordVO {

	private String id;

	private String orderNo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dealTime;

	private String state;

	private String stateName;

	private String memberMobile;

	private String memberBlockChainAddr;

	private Double amount;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date paidTime;

	private String collectionName;

	public static List<FirstOrderRewardRecordVO> convertFor(List<FirstOrderRewardRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<FirstOrderRewardRecordVO> vos = new ArrayList<>();
		for (FirstOrderRewardRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static FirstOrderRewardRecordVO convertFor(FirstOrderRewardRecord po) {
		if (po == null) {
			return null;
		}
		FirstOrderRewardRecordVO vo = new FirstOrderRewardRecordVO();
		BeanUtils.copyProperties(po, vo);
		vo.setStateName(DictHolder.getDictItemName("rewardGrantState", vo.getState()));
		if (po.getMember() != null) {
			vo.setMemberMobile(po.getMember().getMobile());
			vo.setMemberBlockChainAddr(po.getMember().getBlockChainAddr());
		}
		if (po.getPayOrder() != null) {
			vo.setPaidTime(po.getPayOrder().getPaidTime());
			vo.setAmount(po.getPayOrder().getAmount());
		}
		if (po.getCollection() != null) {
			vo.setCollectionName(po.getCollection().getName());
		}
		return vo;
	}

}
