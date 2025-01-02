package com.nft.admin.setting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.common.operlog.OperLog;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;
import com.nft.setting.service.IpBlackListService;
import com.nft.setting.vo.IpBlackListVO;

@Controller
@RequestMapping("/ipBlackList")
public class IpBlackListController {

	public static final String 模块_IP黑名单 = "IP黑名单";

	@Autowired
	private IpBlackListService ipBlackListService;

	@GetMapping("/findIpBlackList")
	@ResponseBody
	public Result<List<IpBlackListVO>> findIpBlackList(String ipAddr) {
		return Result.success(ipBlackListService.findIpBlackList(ipAddr));
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_IP黑名单, operate = "删除ip黑名单")
	@GetMapping("/delIpBlackList")
	@ResponseBody
	public Result<String> delIpBlackList(String ipAddr) {
		ipBlackListService.delIpBlackList(ipAddr);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_IP黑名单, operate = "增加ip黑名单")
	@PostMapping("/addIpBlackList")
	@ResponseBody
	public Result<String> addIpBlackList(String ipAddr) {
		ipBlackListService.addIpBlackList(ipAddr);
		return Result.success();
	}

}
