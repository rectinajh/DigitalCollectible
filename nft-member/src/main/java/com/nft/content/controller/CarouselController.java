package com.nft.content.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nft.common.vo.Result;
import com.nft.content.service.CarouselService;
import com.nft.content.vo.CarouselVO;

@RestController
@RequestMapping("/carousel")
public class CarouselController {

	@Autowired
	private CarouselService carouselService;

	@GetMapping("/findCarousel")
	public Result<List<CarouselVO>> findCarousel() {
		return Result.success(carouselService.findCarousel());
	}

}
