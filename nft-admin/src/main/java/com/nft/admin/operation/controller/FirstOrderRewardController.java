package com.nft.admin.operation.controller;

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
import com.nft.operation.param.FirstOrderRewardRecordQueryCondParam;
import com.nft.operation.service.FirstOrderRewardService;
import com.nft.operation.vo.FirstOrderRewardRecordVO;
import com.nft.setting.param.FirstOrderRewardSettingParam;
import com.nft.setting.service.SettingService;
import com.nft.setting.vo.FirstOrderRewardSettingVO;

@Controller
@RequestMapping("/firstOrderReward")
public class FirstOrderRewardController {

	public static final String 模块_首单奖励 = "首单奖励";

	@Autowired
	private FirstOrderRewardService firstOrderRewardService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/findRewardRecordByPage")
	@ResponseBody
	public Result<PageResult<FirstOrderRewardRecordVO>> findRewardRecordByPage(
			FirstOrderRewardRecordQueryCondParam param) {
		return Result.success(firstOrderRewardService.findByPage(param));
	}

	@GetMapping("/getFirstOrderRewardSetting")
	@ResponseBody
	public Result<FirstOrderRewardSettingVO> getFirstOrderRewardSetting() {
		return Result.success(settingService.getFirstOrderRewardSetting());
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_首单奖励, operate = "更新首单奖励设置")
	@PostMapping("/updateFirstOrderRewardSetting")
	@ResponseBody
	public Result<String> updateFirstOrderRewardSetting(FirstOrderRewardSettingParam param) {
		settingService.updateFirstOrderRewardSetting(param);
		return Result.success();
	}

}
