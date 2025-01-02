package com.nft.admin.content.controller;

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
import com.nft.content.param.AddOrUpdateNoticeParam;
import com.nft.content.param.NoticeQueryCondParam;
import com.nft.content.service.NoticeService;
import com.nft.content.vo.NoticeVO;

@Controller
@RequestMapping("/notice")
public class NoticeController {

	public static final String 模块_站内公告 = "站内公告";

	@Autowired
	private NoticeService noticeService;

	@GetMapping("/findById")
	@ResponseBody
	public Result<NoticeVO> findById(String id) {
		return Result.success(noticeService.findById(id));
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_站内公告, operate = "删除")
	@GetMapping("/delById")
	@ResponseBody
	public Result<String> delById(String id) {
		noticeService.delById(id);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_后台管理, module = 模块_站内公告, operate = "添加或编辑")
	@PostMapping("/addOrUpdateNotice")
	@ResponseBody
	public Result<String> addOrUpdateNotice(AddOrUpdateNoticeParam param) {
		noticeService.addOrUpdateNotice(param);
		return Result.success();
	}

	@GetMapping("/findByPage")
	@ResponseBody
	public Result<PageResult<NoticeVO>> findByPage(NoticeQueryCondParam param) {
		return Result.success(noticeService.findByPage(param));
	}

}
