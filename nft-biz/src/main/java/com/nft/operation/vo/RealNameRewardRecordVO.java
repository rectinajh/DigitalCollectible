package com.nft.operation.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.dictconfig.DictHolder;
import com.nft.operation.domain.RealNameRewardRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class RealNameRewardRecordVO {

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
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date bindRealNameTime;

	private String collectionName;

	public static List<RealNameRewardRecordVO> convertFor(List<RealNameRewardRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<RealNameRewardRecordVO> vos = new ArrayList<>();
		for (RealNameRewardRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static RealNameRewardRecordVO convertFor(RealNameRewardRecord po) {
		if (po == null) {
			return null;
		}
		RealNameRewardRecordVO vo = new RealNameRewardRecordVO();
		BeanUtils.copyProperties(po, vo);
		vo.setStateName(DictHolder.getDictItemName("rewardGrantState", vo.getState()));
		if (po.getMember() != null) {
			vo.setMemberMobile(po.getMember().getMobile());
			vo.setMemberBlockChainAddr(po.getMember().getBlockChainAddr());
			vo.setBindRealNameTime(po.getMember().getBindRealNameTime());
		}
		if (po.getCollection() != null) {
			vo.setCollectionName(po.getCollection().getName());
		}
		return vo;
	}

}
