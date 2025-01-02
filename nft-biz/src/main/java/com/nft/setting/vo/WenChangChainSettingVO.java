package com.nft.setting.vo;

import org.springframework.beans.BeanUtils;

import com.nft.setting.domain.WenChangChainSetting;

import lombok.Data;

@Data
public class WenChangChainSettingVO {

	private String apiGateway;
	
	private String apiKey;

	private String apiSecret;

	private String chainAddrSuper;

	public static WenChangChainSettingVO convertFor(WenChangChainSetting po) {
		WenChangChainSettingVO vo = new WenChangChainSettingVO();
		if (po != null) {
			BeanUtils.copyProperties(po, vo);
		}
		return vo;
	}

}
