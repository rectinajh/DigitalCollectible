package com.nft.collection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nft.collection.param.ExchangeParam;
import com.nft.collection.service.ExchangeCodeService;
import com.nft.collection.vo.ExchangeResultVO;
import com.nft.common.operlog.OperLog;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/exchangeCode")
public class ExchangeCodeController {
	
	public static final String 模块_兑换码 = "兑换码";

	@Autowired
	private ExchangeCodeService exchangeCodeService;

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_兑换码, operate = "兑换艺术品")
	@PostMapping("/exchange")
	public Result<ExchangeResultVO> exchange(ExchangeParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(exchangeCodeService.exchange(param));
	}

}
