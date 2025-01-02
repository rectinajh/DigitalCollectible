package com.nft.collection.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nft.collection.param.ComposeParam;
import com.nft.collection.service.ComposeActivityService;
import com.nft.collection.vo.ActiveComposeActivityDetailVO;
import com.nft.collection.vo.ActiveComposeActivityVO;
import com.nft.common.operlog.OperLog;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/composeActivity")
public class ComposeActivityController {
	
	public static final String 模块_合成活动 = "合成活动";

	@Autowired
	private ComposeActivityService composeActivityService;

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_合成活动, operate = "合成艺术品")
	@PostMapping("/compose")
	public Result<String> compose(ComposeParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		composeActivityService.compose(param);
		return Result.success();
	}

	@GetMapping("/findActiveComposeActivityDetail")
	public Result<ActiveComposeActivityDetailVO> findActiveComposeActivityDetail(String id) {
		return Result.success(composeActivityService.findActiveComposeActivityDetail(id));
	}

	@GetMapping("/findActiveComposeActivity")
	public Result<List<ActiveComposeActivityVO>> findActiveComposeActivity() {
		return Result.success(composeActivityService.findActiveComposeActivity());
	}

}
