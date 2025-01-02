package com.nft.admin.risk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.risk.param.RiskRecordQueryCondParam;
import com.nft.risk.service.RiskService;
import com.nft.risk.vo.RiskRecordVO;

@Controller
@RequestMapping("/risk")
public class RiskController {

	@Autowired
	private RiskService riskService;

	@GetMapping("/findRiskRecordByPage")
	@ResponseBody
	public Result<PageResult<RiskRecordVO>> findRiskRecordByPage(RiskRecordQueryCondParam param) {
		return Result.success(riskService.findRiskRecordByPage(param));
	}

}
