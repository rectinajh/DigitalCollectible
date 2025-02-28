package com.nft.dictconfig;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nft.dictconfig.service.DictService;
import com.nft.dictconfig.vo.DictItemVO;

@Component
public class DictHolder {

	@Autowired
	private DictService dictService;

	private static DictHolder dictHolder;

	@PostConstruct
	public void init() {
		dictHolder = this;
		dictHolder.dictService = this.dictService;
	}

	public static String getDictItemName(String dictTypeCode, String dictItemCode) {
		DictItemVO dictItemVO = dictHolder.dictService.findDictItemByDictTypeCodeAndDictItemCode(dictTypeCode,
				dictItemCode);
		if (dictItemVO == null) {
			return null;
		}
		return dictItemVO.getDictItemName();
	}

	public static List<DictItemVO> findDictItem(String dictTypeCode) {
		return dictHolder.dictService.findDictItemByDictTypeCode(dictTypeCode);
	}

}
