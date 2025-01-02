package com.nft.admin.content.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.common.operlog.OperLog;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;
import com.nft.content.param.AddOrUpdateCarouselParam;
import com.nft.content.service.CarouselService;
import com.nft.content.vo.CarouselVO;

@Controller
@RequestMapping("/carousel")
public class CarouselController {

	public static final String 模块_首页轮播 = "首页轮播";

	@Autowired
	private CarouselService carouselService;

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_首页轮播, operate = "调整显示顺序")
	@PostMapping("/adjustCarouselOrderNo")
	@ResponseBody
	public Result<String> adjustCarouselOrderNo(@RequestBody List<String> carouselIds) {
		carouselService.adjustCarouselOrderNo(carouselIds);
		return Result.success();
	}

	@GetMapping("/findCarouselById")
	@ResponseBody
	public Result<CarouselVO> findCarouselById(String id) {
		return Result.success(carouselService.findCarouselById(id));
	}

	@GetMapping("/findCarousel")
	@ResponseBody
	public Result<List<CarouselVO>> findCarousel() {
		return Result.success(carouselService.findCarousel());
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_首页轮播, operate = "删除")
	@GetMapping("/delById")
	@ResponseBody
	public Result<String> delById(String id) {
		carouselService.delById(id);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_首页轮播, operate = "添加或编辑")
	@PostMapping("/addOrUpdateCarousel")
	@ResponseBody
	public Result<String> addOrUpdateCarousel(AddOrUpdateCarouselParam param) {
		carouselService.addOrUpdateCarousel(param);
		return Result.success();
	}

}
