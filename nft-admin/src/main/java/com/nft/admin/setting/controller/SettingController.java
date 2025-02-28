package com.nft.admin.setting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.common.operlog.OperLog;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;
import com.nft.setting.param.OperationSettingParam;
import com.nft.setting.param.RiskSettingParam;
import com.nft.setting.param.SystemSettingParam;
import com.nft.setting.param.WenChangChainSettingParam;
import com.nft.setting.service.ChainSettingService;
import com.nft.setting.service.SettingService;
import com.nft.setting.vo.OperationSettingVO;
import com.nft.setting.vo.RiskSettingVO;
import com.nft.setting.vo.SystemSettingVO;
import com.nft.setting.vo.WenChangChainSettingVO;

@Controller
@RequestMapping("/setting")
public class SettingController {

	public static final String 模块_系统功能调控 = "系统功能调控";

	@Autowired
	private SettingService service;

	@Autowired
	private ChainSettingService chainSettingService;

	@GetMapping("/getRiskSetting")
	@ResponseBody
	public Result<RiskSettingVO> getRiskSetting() {
		return Result.success(service.getRiskSetting());
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_系统功能调控, operate = "更新风控设置")
	@PostMapping("/updateRiskSetting")
	@ResponseBody
	public Result<String> updateRiskSetting(RiskSettingParam param) {
		service.updateRiskSetting(param);
		return Result.success();
	}

	@GetMapping("/getOperationSetting")
	@ResponseBody
	public Result<OperationSettingVO> getOperationSetting() {
		return Result.success(service.getOperationSetting());
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_系统功能调控, operate = "更新运营设置")
	@PostMapping("/updateOperationSetting")
	@ResponseBody
	public Result<String> updateOperationSetting(OperationSettingParam param) {
		service.updateOperationSetting(param);
		return Result.success();
	}

	@GetMapping("/getWenChangChainSetting")
	@ResponseBody
	public Result<WenChangChainSettingVO> getWenChangChainSetting() {
		return Result.success(chainSettingService.getWenChangChainSetting());
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_系统功能调控, operate = "更新文昌链参数")
	@PostMapping("/updateWenChangChainSetting")
	@ResponseBody
	public Result<String> updateWenChangChainSetting(WenChangChainSettingParam param) {
		chainSettingService.updateWenChangChainSetting(param);
		return Result.success();
	}

	@GetMapping("/getCurrentInUseChain")
	@ResponseBody
	public Result<String> getCurrentInUseChain() {
		return Result.success(chainSettingService.getCurrentInUseChain());
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_系统功能调控, operate = "更新区块链设置")
	@PostMapping("/updateCurrentInUseChain")
	@ResponseBody
	public Result<String> updateCurrentInUseChain(String currentInUseChain) {
		chainSettingService.updateCurrentInUseChain(currentInUseChain);
		return Result.success();
	}

	@GetMapping("/getSystemSetting")
	@ResponseBody
	public Result<SystemSettingVO> getSystemSetting() {
		return Result.success(service.getSystemSetting());
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_系统功能调控, operate = "更新系统设置")
	@PostMapping("/updateSystemSetting")
	@ResponseBody
	public Result<String> updateSystemSetting(SystemSettingParam param) {
		service.updateSystemSetting(param);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_系统功能调控, operate = "刷新缓存")
	@PostMapping("/refreshCache")
	@ResponseBody
	public Result<String> refreshCache() {
		service.refreshCache();
		return Result.success();
	}

}
