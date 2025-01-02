package com.nft.admin.collection.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.collection.param.AddPreSaleQualifyParam;
import com.nft.collection.param.AddPreSaleTaskParam;
import com.nft.collection.param.PreSaleQualifyQueryCondParam;
import com.nft.collection.param.PreSaleTaskQueryCondParam;
import com.nft.collection.service.PreSaleService;
import com.nft.collection.vo.PreSaleQualifyVO;
import com.nft.collection.vo.PreSaleTaskVO;
import com.nft.common.operlog.OperLog;
import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;

@Controller
@RequestMapping("/preSale")
public class PreSaleController {

	public static final String 模块_优先购 = "优先购";

	@Autowired
	private PreSaleService preSaleService;

	@GetMapping("/findPreSaleQualifyByPage")
	@ResponseBody
	public Result<PageResult<PreSaleQualifyVO>> findPreSaleQualifyByPage(PreSaleQualifyQueryCondParam param) {
		return Result.success(preSaleService.findPreSaleQualifyByPage(param));
	}

	@GetMapping("/findAllPreSaleTask")
	@ResponseBody
	public Result<List<PreSaleTaskVO>> findAllPreSaleTask() {
		return Result.success(preSaleService.findAllPreSaleTask());
	}

	@GetMapping("/findPreSaleTaskByPage")
	@ResponseBody
	public Result<PageResult<PreSaleTaskVO>> findPreSaleTaskByPage(PreSaleTaskQueryCondParam param) {
		return Result.success(preSaleService.findPreSaleTaskByPage(param));
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_优先购, operate = "新增优先购任务")
	@PostMapping("/addPreSaleTask")
	@ResponseBody
	public Result<String> addPreSaleTask(@RequestBody AddPreSaleTaskParam param) {
		preSaleService.addPreSaleTask(param);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_优先购, operate = "发放优先购资格")
	@PostMapping("/addPreSaleQualify")
	@ResponseBody
	public Result<String> addPreSaleQualify(AddPreSaleQualifyParam param) {
		preSaleService.addPreSaleQualify(param);
		return Result.success();
	}

}
