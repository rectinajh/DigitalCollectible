package com.nft.operation.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.dictconfig.DictHolder;
import com.nft.member.domain.MemberInvitedCount;
import com.nft.operation.domain.InvitedRewardRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class InvitedRewardRecordVO {

	private String id;

	private String orderNo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dealTime;

	private String state;

	private String stateName;

	private Integer level;

	private Integer invitedCount;

	private String memberMobile;

	private String memberBlockChainAddr;
	
	private Integer currnetMemberInvitedCount;

	private String collectionName;

	public static List<InvitedRewardRecordVO> convertFor(List<InvitedRewardRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<InvitedRewardRecordVO> vos = new ArrayList<>();
		for (InvitedRewardRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static InvitedRewardRecordVO convertFor(InvitedRewardRecord po) {
		if (po == null) {
			return null;
		}
		InvitedRewardRecordVO vo = new InvitedRewardRecordVO();
		BeanUtils.copyProperties(po, vo);
		vo.setStateName(DictHolder.getDictItemName("rewardGrantState", vo.getState()));
		if (po.getMember() != null) {
			vo.setMemberMobile(po.getMember().getMobile());
			vo.setMemberBlockChainAddr(po.getMember().getBlockChainAddr());
			MemberInvitedCount memberInvitedCount = po.getMember().getMemberInvitedCount();
			if (memberInvitedCount != null) {
				vo.setCurrnetMemberInvitedCount(memberInvitedCount.getRealNameCount());
			}
		}
		if (po.getCollection() != null) {
			vo.setCollectionName(po.getCollection().getName());
		}
		return vo;
	}

}
