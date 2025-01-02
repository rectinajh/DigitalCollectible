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
import com.nft.operation.param.RealNameRewardRecordQueryCondParam;
import com.nft.operation.service.RealNameRewardService;
import com.nft.operation.vo.RealNameRewardRecordVO;
import com.nft.setting.param.RealNameRewardSettingParam;
import com.nft.setting.service.SettingService;
import com.nft.setting.vo.RealNameRewardSettingVO;

@Controller
@RequestMapping("/realNameReward")
public class RealNameRewardController {

	public static final String 模块_实名认证奖励 = "实名认证奖励";

	@Autowired
	private RealNameRewardService realNameRewardService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/findRewardRecordByPage")
	@ResponseBody
	public Result<PageResult<RealNameRewardRecordVO>> findRewardRecordByPage(RealNameRewardRecordQueryCondParam param) {
		return Result.success(realNameRewardService.findByPage(param));
	}

	@GetMapping("/getRealNameRewardSetting")
	@ResponseBody
	public Result<RealNameRewardSettingVO> getRealNameRewardSetting() {
		return Result.success(settingService.getRealNameRewardSetting());
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_实名认证奖励, operate = "更新实名认证奖励设置")
	@PostMapping("/updateRealNameRewardSetting")
	@ResponseBody
	public Result<String> updateRealNameRewardSetting(RealNameRewardSettingParam param) {
		settingService.updateRealNameRewardSetting(param);
		return Result.success();
	}

}
