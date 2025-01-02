package com.nft.collection.vo;

import lombok.Data;

@Data
public class AirDropResultVO {

	private Boolean successFlag;

	private String issuedCollectionId;

	public static AirDropResultVO success(String issuedCollectionId) {
		AirDropResultVO result = new AirDropResultVO();
		result.setSuccessFlag(true);
		result.setIssuedCollectionId(issuedCollectionId);
		return result;
	}

	public static AirDropResultVO fail() {
		AirDropResultVO result = new AirDropResultVO();
		result.setSuccessFlag(false);
		return result;
	}

}
