package com.nft.content.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.content.domain.Carousel;
import com.nft.dictconfig.DictHolder;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class CarouselVO {

	private String id;

	private String clickDealWay;

	private String clickDealWayName;

	private String cover;

	private String link;

	private Double orderNo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date lastModifyTime;

	public static List<CarouselVO> convertFor(List<Carousel> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<CarouselVO> vos = new ArrayList<>();
		for (Carousel po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static CarouselVO convertFor(Carousel po) {
		if (po == null) {
			return null;
		}
		CarouselVO vo = new CarouselVO();
		BeanUtils.copyProperties(po, vo);
		vo.setClickDealWayName(DictHolder.getDictItemName("carouselClickDealWay", vo.getClickDealWay()));
		return vo;
	}

}
