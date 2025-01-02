package com.nft.setting.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class WenChangChainSettingParam {

	@NotBlank
	private String apiGateway;
	
	@NotBlank
	private String apiKey;

	@NotBlank
	private String apiSecret;

	@NotBlank
	private String chainAddrSuper;

}
