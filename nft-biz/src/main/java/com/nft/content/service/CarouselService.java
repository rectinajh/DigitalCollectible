package com.nft.content.service;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.common.exception.BizException;
import com.nft.constants.Constant;
import com.nft.content.domain.Carousel;
import com.nft.content.param.AddOrUpdateCarouselParam;
import com.nft.content.repo.CarouselRepo;
import com.nft.content.vo.CarouselVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class CarouselService {

	@Autowired
	private CarouselRepo carouselRepo;

	@Transactional
	public void adjustCarouselOrderNo(List<String> carouselIds) {
		List<Carousel> carousels = carouselRepo.findByDeletedFlagIsFalseOrderByOrderNo();
		for (int i = 0; i < carousels.size(); i++) {
			String carouselId = carouselIds.get(i);
			for (Carousel carousel : carousels) {
				if (carouselId.equals(carousel.getId())) {
					carousel.setOrderNo((double) (i + 1));
					carouselRepo.save(carousel);
					break;
				}
			}
		}
	}

	@Transactional(readOnly = true)
	public CarouselVO findCarouselById(@NotBlank String id) {
		return CarouselVO.convertFor(carouselRepo.getOne(id));
	}

	@Transactional(readOnly = true)
	public List<CarouselVO> findCarousel() {
		return CarouselVO.convertFor(carouselRepo.findByDeletedFlagIsFalseOrderByOrderNo());
	}

	@Transactional
	public void delById(@NotBlank String id) {
		Carousel carousel = carouselRepo.getOne(id);
		carousel.deleted();
		carouselRepo.save(carousel);
	}

	@Transactional
	public void addOrUpdateCarousel(@Valid AddOrUpdateCarouselParam param) {
		if (Constant.轮播点击处理方式_内部链接.equals(param.getClickDealWay())
				|| Constant.轮播点击处理方式_跳转外链.equals(param.getClickDealWay())) {
			if (StrUtil.isBlank(param.getLink())) {
				throw new BizException("链接不能为空");
			}
		}
		if (StrUtil.isBlank(param.getId())) {
			Double orderNo = 1d;
			Carousel maxOrderNo = carouselRepo.findTopByDeletedFlagIsFalseOrderByOrderNoDesc();
			if (maxOrderNo != null) {
				orderNo = maxOrderNo.getOrderNo() + 1;
			}
			Carousel carousel = param.convertToPo(orderNo);
			carouselRepo.save(carousel);
		} else {
			Carousel carousel = carouselRepo.getOne(param.getId());
			BeanUtils.copyProperties(param, carousel);
			carousel.setLastModifyTime(new Date());
			carouselRepo.save(carousel);
		}
	}

}
