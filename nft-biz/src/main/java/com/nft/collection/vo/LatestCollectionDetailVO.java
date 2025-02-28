package com.nft.collection.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nft.collection.domain.Collection;
import com.nft.collection.domain.CollectionStory;
import com.nft.collection.domain.MysteryBoxCommodity;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.Data;

@Data
public class LatestCollectionDetailVO {

	private String id;

	private String commodityType;

	private String name;

	private String cover;

	private Double price;

	private Integer quantity;

	private Integer stock;

	@JsonFormat(pattern = "MM.dd HH:mm", timezone = "GMT+8")
	private Date saleTime;
	
	private Boolean preSaleFlag;

	private Long surplusSecond;
	
	private String creatorId;

	private String creatorName;

	private String creatorAvatar;

	private List<String> storyPicLinks = new ArrayList<>();

	private List<MysteryBoxCommodityVO> subCommoditys = new ArrayList<>();

	public static LatestCollectionDetailVO convertFor(Collection po) {
		if (po == null) {
			return null;
		}
		LatestCollectionDetailVO vo = new LatestCollectionDetailVO();
		BeanUtils.copyProperties(po, vo);
		long surplusSecond = DateUtil.between(new Date(), po.getSaleTime(), DateUnit.SECOND, false);
		vo.setSurplusSecond(surplusSecond > 0 ? surplusSecond : 0);
		if (po.getCreator() != null) {
			vo.setCreatorName(po.getCreator().getName());
			vo.setCreatorAvatar(po.getCreator().getAvatar());
		}
		for (CollectionStory collectionStory : po.getCollectionStorys()) {
			vo.getStoryPicLinks().add(collectionStory.getPicLink());
		}
		Set<MysteryBoxCommodity> mysteryBoxCommoditys = po.getSubCommoditys();
		for (MysteryBoxCommodity mysteryBoxCommodity : mysteryBoxCommoditys) {
			vo.getSubCommoditys().add(MysteryBoxCommodityVO.convertFor(mysteryBoxCommodity));
		}
		return vo;
	}

}
