package com.nft.admin.collection.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.collection.param.AddAirDropTaskParam;
import com.nft.collection.param.AirDropRecordQueryCondParam;
import com.nft.collection.param.AirDropTaskQueryCondParam;
import com.nft.collection.service.AirDropService;
import com.nft.collection.vo.AirDropRecordVO;
import com.nft.collection.vo.AirDropTaskVO;
import com.nft.common.operlog.OperLog;
import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;

@Controller
@RequestMapping("/airDrop")
public class AirDropController {

	public static final String 模块_空投 = "空投";

	@Autowired
	private AirDropService airDropService;

	@GetMapping("/findAirDropRecordByPage")
	@ResponseBody
	public Result<PageResult<AirDropRecordVO>> findAirDropRecordByPage(AirDropRecordQueryCondParam param) {
		return Result.success(airDropService.findAirDropRecordByPage(param));
	}

	@GetMapping("/findAllAirDropTask")
	@ResponseBody
	public Result<List<AirDropTaskVO>> findAllAirDropTask() {
		return Result.success(airDropService.findAllAirDropTask());
	}

	@GetMapping("/findAirDropTaskByPage")
	@ResponseBody
	public Result<PageResult<AirDropTaskVO>> findAirDropTaskByPage(AirDropTaskQueryCondParam param) {
		return Result.success(airDropService.findAirDropTaskByPage(param));
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_空投, operate = "新增空投任务")
	@PostMapping("/addAirDropTask")
	@ResponseBody
	public Result<String> addAirDropTask(@RequestBody AddAirDropTaskParam param) {
		airDropService.addAirDropTask(param);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_空投, operate = "发放空投")
	@PostMapping("/airDrop")
	@ResponseBody
	public Result<String> airDrop(String memberId, String collectionId) {
		airDropService.airDrop(memberId, collectionId, Constant.空投业务类型_手动空投);
		return Result.success();
	}

}
