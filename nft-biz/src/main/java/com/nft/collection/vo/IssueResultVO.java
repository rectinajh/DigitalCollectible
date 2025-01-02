package com.nft.collection.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IssueResultVO {
	
	private String issuedCollectionId;
	
	private String memberHoldCollectionId;

}
