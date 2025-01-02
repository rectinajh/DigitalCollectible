package com.nft.content.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.nft.common.utils.IdUtils;
import com.nft.content.domain.Carousel;

import lombok.Data;

@Data
public class AddOrUpdateCarouselParam {
	
	private String id;

	@NotBlank
	private String clickDealWay;

	@NotBlank
	private String cover;

	private String link;

	public Carousel convertToPo(Double orderNo) {
		Carousel po = new Carousel();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setLastModifyTime(po.getCreateTime());
		po.setDeletedFlag(false);
		po.setOrderNo(orderNo);
		return po;
	}

}
