package com.nft.member.param;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;

import com.nft.collection.domain.AirDropCondition;
import com.nft.collection.domain.PreSaleCondition;

import lombok.Data;

@Data
public class MemberSnapshotQueryParam {

	@NotBlank
	private String fieldName;

	@NotBlank
	private String cond;

	@NotBlank
	private String fieldValue;

	private String logicalOperation;
	
	public static List<MemberSnapshotQueryParam> buildWithPreSale(Set<PreSaleCondition> conditions) {
		List<MemberSnapshotQueryParam> params = new ArrayList<MemberSnapshotQueryParam>();
		for (PreSaleCondition condition : conditions) {
			MemberSnapshotQueryParam param = new MemberSnapshotQueryParam();
			param.setFieldName(condition.getFieldName());
			param.setCond(condition.getCond());
			param.setFieldValue(condition.getFieldValue());
			param.setLogicalOperation(condition.getLogicalOperation());
			params.add(param);
		}
		return params;
	}

	public static List<MemberSnapshotQueryParam> buildWithAirDrop(Set<AirDropCondition> conditions) {
		List<MemberSnapshotQueryParam> params = new ArrayList<MemberSnapshotQueryParam>();
		for (AirDropCondition condition : conditions) {
			MemberSnapshotQueryParam param = new MemberSnapshotQueryParam();
			param.setFieldName(condition.getFieldName());
			param.setCond(condition.getCond());
			param.setFieldValue(condition.getFieldValue());
			param.setLogicalOperation(condition.getLogicalOperation());
			params.add(param);
		}
		return params;
	}

}
