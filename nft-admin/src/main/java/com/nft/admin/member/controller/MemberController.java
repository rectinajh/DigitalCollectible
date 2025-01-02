package com.nft.admin.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.chain.service.ChainService;
import com.nft.common.operlog.OperLog;
import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;
import com.nft.member.param.AddMemberParam;
import com.nft.member.param.MemberQueryCondParam;
import com.nft.member.param.MemberSnapshotQueryParam;
import com.nft.member.param.SettlementAccountQueryCondParam;
import com.nft.member.param.UpdateMemberParam;
import com.nft.member.service.MemberService;
import com.nft.member.service.SettlementAccountService;
import com.nft.member.vo.MemberFundInfoVO;
import com.nft.member.vo.MemberSnapshotVO;
import com.nft.member.vo.MemberStatisticDataVO;
import com.nft.member.vo.MemberVO;
import com.nft.member.vo.SettlementAccountVO;

@Controller
@RequestMapping("/member")
public class MemberController {

	public static final String 模块_会员账号 = "会员账号";

	@Autowired
	private MemberService memberService;

	@Autowired
	private ChainService chainService;
	
	@Autowired
	private SettlementAccountService settlementAccountService;

	@PostMapping("/findMemberSnapshot")
	@ResponseBody
	public Result<List<MemberSnapshotVO>> findMemberSnapshot(@RequestBody List<MemberSnapshotQueryParam> params) {
		return Result.success(memberService.findMemberSnapshot(params));
	}
	
	@GetMapping("/findSettlementAccount")
	@ResponseBody
	public Result<List<SettlementAccountVO>> findSettlementAccount(SettlementAccountQueryCondParam param) {
		return Result.success(settlementAccountService.findAll(param));
	}

	@GetMapping("/createBlockChainAddr")
	@ResponseBody
	public Result<String> createBlockChainAddr(String id) {
		return Result.success(chainService.createBlockChainAddr(id));
	}

	@GetMapping("/getMemberFundInfo")
	@ResponseBody
	public Result<MemberFundInfoVO> getMemberFundInfo() {
		return Result.success(memberService.getMemberFundInfo());
	}

	@GetMapping("/getMemberStatisticData")
	@ResponseBody
	public Result<MemberStatisticDataVO> getMemberStatisticData() {
		return Result.success(memberService.getMemberStatisticData());
	}

	@GetMapping("/findMemberById")
	@ResponseBody
	public Result<MemberVO> findMemberById(String id) {
		return Result.success(memberService.findMemberById(id));
	}

	@GetMapping("/findMemberByPage")
	@ResponseBody
	public Result<PageResult<MemberVO>> findMemberByPage(MemberQueryCondParam param) {
		return Result.success(memberService.findMemberByPage(param));
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_会员账号, operate = "修改支付密码")
	@PostMapping("/modifyPayPwd")
	@ResponseBody
	public Result<String> modifyPayPwd(String id, String newPwd) {
		memberService.modifyPayPwd(id, newPwd);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_会员账号, operate = "修改会员信息")
	@PostMapping("/updateMember")
	@ResponseBody
	public Result<String> updateMember(UpdateMemberParam param) {
		memberService.updateMember(param);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_会员账号, operate = "删除")
	@GetMapping("/delMember")
	@ResponseBody
	public Result<String> delMember(String id) {
		memberService.delMember(id);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_会员账号, operate = "新增会员")
	@PostMapping("/addMember")
	@ResponseBody
	public Result<String> addMember(AddMemberParam param) {
		memberService.addMember(param);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_会员账号, operate = "增加余额")
	@PostMapping("/addBalance")
	@ResponseBody
	public Result<String> addBalance(String id, Double amount) {
		memberService.addBalance(id, amount, null);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_会员账号, operate = "减少余额")
	@PostMapping("/reduceBalance")
	@ResponseBody
	public Result<String> reduceBalance(String id, Double amount) {
		memberService.reduceBalance(id, amount);
		return Result.success();
	}

}
