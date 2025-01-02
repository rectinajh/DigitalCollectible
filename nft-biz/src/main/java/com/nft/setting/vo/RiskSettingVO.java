package com.nft.setting.vo;

import org.springframework.beans.BeanUtils;

import com.nft.setting.domain.RiskSetting;

import lombok.Data;

@Data
public class RiskSettingVO {

	private Integer orderUnpaidTimeRange;

	private Integer orderUnpaidCount;

	private Integer orderUnpaidPunish;

	public static RiskSettingVO convertFor(RiskSetting po) {
		RiskSettingVO vo = new RiskSettingVO();
		if (po != null) {
			BeanUtils.copyProperties(po, vo);
		}
		return vo;
	}

}
