package com.nft.admin.collection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nft.collection.param.ResaleCollectionQueryCondParam;
import com.nft.collection.service.CollectionService;
import com.nft.collection.service.TransactionService;
import com.nft.collection.vo.MemberResaleCollectionVO;
import com.nft.common.operlog.OperLog;
import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;

@Controller
@RequestMapping("/memberResaleCollection")
public class MemberResaleCollectionController {

	public static final String 模块_二级市场艺术品 = "二级市场艺术品";

	@Autowired
	private CollectionService collectionService;

	@Autowired
	private TransactionService transactionService;

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_二级市场艺术品, operate = "取消寄售")
	@GetMapping("/cancelResale")
	@ResponseBody
	public Result<String> cancelResale(String id) {
		transactionService.cancelResaleInner(id);
		return Result.success();
	}

	@GetMapping("/findMemberResaleCollectionByPage")
	@ResponseBody
	public Result<PageResult<MemberResaleCollectionVO>> findMemberResaleCollectionByPage(
			ResaleCollectionQueryCondParam param) {
		return Result.success(collectionService.findMemberResaleCollectionByPage(param));
	}

}
