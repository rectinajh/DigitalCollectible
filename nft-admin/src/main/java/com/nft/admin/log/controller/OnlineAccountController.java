package com.nft.admin.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.common.operlog.OperLog;
import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;
import com.nft.log.param.LoginLogQueryCondParam;
import com.nft.log.service.LoginLogService;
import com.nft.log.vo.OnlineAccountVO;

@Controller
@RequestMapping("/onlineAccount")
public class OnlineAccountController {

	public static final String 模块_在线账号 = "在线账号";

	@Autowired
	private LoginLogService loginLogService;

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_在线账号, operate = "强制退出")
	@PostMapping("/forceLogout")
	@ResponseBody
	public Result<String> forceLogout(String token) {
		loginLogService.forceLogout(token);
		return Result.success();
	}

	@GetMapping("/findOnlineAccountByPage")
	@ResponseBody
	public Result<PageResult<OnlineAccountVO>> findOnlineAccountByPage(LoginLogQueryCondParam param) {
		return Result.success(loginLogService.findOnlineAccountByPage(param));
	}

}
