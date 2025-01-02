package com.nft.content.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.content.param.NoticeQueryCondParam;
import com.nft.content.service.NoticeService;
import com.nft.content.vo.NoticeAbstractVO;
import com.nft.content.vo.NoticeVO;

@RestController
@RequestMapping("/notice")
public class NoticeController {

	@Autowired
	private NoticeService noticeService;

	@GetMapping("/getNoticeDetail")
	public Result<NoticeVO> getNoticeDetail(String id) {
		return Result.success(noticeService.findById(id));
	}

	@GetMapping("/findNoticeAbstractByPage")
	public Result<PageResult<NoticeAbstractVO>> findNoticeAbstractByPage(NoticeQueryCondParam param) {
		return Result.success(noticeService.findPublishedNoticeByPage(param));
	}

}
