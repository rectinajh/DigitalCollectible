package com.nft.collection.vo;

import org.springframework.beans.BeanUtils;

import com.nft.collection.domain.AirDropCondition;
import com.nft.constants.Constant;
import com.nft.dictconfig.DictHolder;

import lombok.Data;

@Data
public class AirDropConditionVO {

	private String fieldName;

	private String fieldLabel;

	private String cond;

	private String fieldValue;

	private String logicalOperation;

	public static AirDropConditionVO convertFor(AirDropCondition po) {
		if (po == null) {
			return null;
		}
		AirDropConditionVO vo = new AirDropConditionVO();
		BeanUtils.copyProperties(po, vo);
		if (!po.getFieldName().startsWith(Constant.快照参数_持有艺术品)) {
			vo.setFieldLabel(DictHolder.getDictItemName("snapshotParam", vo.getFieldName()));
		}
		return vo;
	}

}
