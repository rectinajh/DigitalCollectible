package com.nft.admin.collection.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.collection.param.ExchangeCodeQueryCondParam;
import com.nft.collection.param.ExchangeCodeSummaryQueryCondParam;
import com.nft.collection.param.GenerateExchangeCodeParam;
import com.nft.collection.service.ExchangeCodeService;
import com.nft.collection.vo.ExchangeCodeSummaryVO;
import com.nft.collection.vo.ExchangeCodeVO;
import com.nft.collection.vo.ExchangeRecordVO;
import com.nft.common.operlog.OperLog;
import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;

@Controller
@RequestMapping("/exchangeCode")
public class ExchangeCodeController {

	public static final String 模块_兑换码 = "兑换码";

	@Autowired
	private ExchangeCodeService exchangeCodeService;

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_兑换码, operate = "兑换码作废")
	@GetMapping("/invalid")
	@ResponseBody
	public Result<String> invalid(String collectionId) {
		exchangeCodeService.invalid(collectionId);
		return Result.success();
	}

	@GetMapping("/findExchangeRecord")
	@ResponseBody
	public Result<List<ExchangeRecordVO>> findExchangeRecord(ExchangeCodeQueryCondParam param) {
		return Result.success(exchangeCodeService.findExchangeRecord(param));
	}

	@GetMapping("/findExchangeCodeSummaryByPage")
	@ResponseBody
	public Result<PageResult<ExchangeCodeSummaryVO>> findExchangeCodeSummaryByPage(
			ExchangeCodeSummaryQueryCondParam param) {
		return Result.success(exchangeCodeService.findExchangeCodeSummaryByPage(param));
	}

	@GetMapping("/findExchangeCode")
	@ResponseBody
	public Result<List<ExchangeCodeVO>> findExchangeCode(ExchangeCodeQueryCondParam param) {
		return Result.success(exchangeCodeService.findExchangeCode(param));
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_兑换码, operate = "生成兑换码")
	@PostMapping("/generateExchangeCode")
	@ResponseBody
	public Result<String> generateExchangeCode(GenerateExchangeCodeParam param) {
		exchangeCodeService.generateExchangeCode(param);
		return Result.success();
	}

}
