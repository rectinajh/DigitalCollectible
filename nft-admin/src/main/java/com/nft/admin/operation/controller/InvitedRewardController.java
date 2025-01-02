package com.nft.admin.operation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.common.operlog.OperLog;
import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;
import com.nft.operation.param.InvitedRewardRecordQueryCondParam;
import com.nft.operation.service.InvitedRewardService;
import com.nft.operation.vo.InvitedRewardRecordVO;
import com.nft.setting.param.InvitedRewardSettingParam;
import com.nft.setting.service.SettingService;
import com.nft.setting.vo.InvitedRewardSettingVO;

@Controller
@RequestMapping("/invitedReward")
public class InvitedRewardController {

	public static final String 模块_邀请奖励 = "邀请奖励";

	@Autowired
	private InvitedRewardService invitedRewardService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/findRewardRecordByPage")
	@ResponseBody
	public Result<PageResult<InvitedRewardRecordVO>> findRewardRecordByPage(InvitedRewardRecordQueryCondParam param) {
		return Result.success(invitedRewardService.findByPage(param));
	}

	@GetMapping("/getInvitedRewardSetting")
	@ResponseBody
	public Result<InvitedRewardSettingVO> getInvitedRewardSetting() {
		return Result.success(settingService.getInvitedRewardSetting());
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_邀请奖励, operate = "更新邀请奖励设置")
	@PostMapping("/updateInvitedRewardSetting")
	@ResponseBody
	public Result<String> updateInvitedRewardSetting(@RequestBody InvitedRewardSettingParam param) {
		settingService.updateInvitedRewardSetting(param);
		return Result.success();
	}

}
