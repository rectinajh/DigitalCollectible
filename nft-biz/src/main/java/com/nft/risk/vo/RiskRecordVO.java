package com.nft.risk.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.dictconfig.DictHolder;
import com.nft.risk.domain.RiskRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class RiskRecordVO {

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String riskCause;

	private String riskCauseName;
	
	private Long hitCount;

	private String riskPunish;

	private String riskPunishName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date riskFinishTime;
	
	private String memberMobile;
	
	public static List<RiskRecordVO> convertFor(List<RiskRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<RiskRecordVO> vos = new ArrayList<>();
		for (RiskRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static RiskRecordVO convertFor(RiskRecord po) {
		if (po == null) {
			return null;
		}
		RiskRecordVO vo = new RiskRecordVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getMember() != null) {
			vo.setMemberMobile(po.getMember().getMobile());
		}
		vo.setRiskCauseName(DictHolder.getDictItemName("riskCause", vo.getRiskCause()));
		vo.setRiskPunishName(DictHolder.getDictItemName("riskPunish", vo.getRiskPunish()));
		return vo;
	}

}
