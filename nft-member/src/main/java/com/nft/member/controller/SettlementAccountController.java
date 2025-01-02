package com.nft.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nft.common.operlog.OperLog;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;
import com.nft.member.param.AddSettlementAccountParam;
import com.nft.member.param.SettlementAccountQueryCondParam;
import com.nft.member.param.UpdateSettlementAccountStateParam;
import com.nft.member.service.SettlementAccountService;
import com.nft.member.vo.SettlementAccountVO;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/settlementAccount")
public class SettlementAccountController {
	
	public static final String 模块_结算账户 = "结算账户";

	@Autowired
	private SettlementAccountService settlementAccountService;

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_结算账户, operate = "更新是否启用状态")
	@PostMapping("/updateActivatedFlag")
	public Result<String> updateActivatedFlag(UpdateSettlementAccountStateParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		settlementAccountService.updateActivatedFlag(param);
		return Result.success();
	}

	@GetMapping("/findAll")
	public Result<List<SettlementAccountVO>> findAll(SettlementAccountQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(settlementAccountService.findAll(param));
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_结算账户, operate = "删除")
	@PostMapping("/del")
	public Result<String> del(String id) {
		settlementAccountService.del(id, StpUtil.getLoginIdAsString());
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_结算账户, operate = "新增")
	@PostMapping("/add")
	public Result<String> add(AddSettlementAccountParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		settlementAccountService.add(param);
		return Result.success();
	}

}
