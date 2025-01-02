package com.nft.collection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nft.collection.param.CancelPayParam;
import com.nft.collection.param.CollectionCancelResaleParam;
import com.nft.collection.param.CollectionGiveParam;
import com.nft.collection.param.CollectionGiveRecordQueryCondParam;
import com.nft.collection.param.CollectionResaleParam;
import com.nft.collection.param.ConfirmPayParam;
import com.nft.collection.param.LatestCollectionCreateOrderParam;
import com.nft.collection.param.OpenMysteryBoxParam;
import com.nft.collection.param.PayOrderQueryCondParam;
import com.nft.collection.param.ResaleCollectionCreateOrderParam;
import com.nft.collection.service.CollectionGiveService;
import com.nft.collection.service.PreSaleService;
import com.nft.collection.service.TransactionService;
import com.nft.collection.vo.CollectionReceiverInfoVO;
import com.nft.collection.vo.MyGiveRecordVO;
import com.nft.collection.vo.MyPayOrderDetailVO;
import com.nft.collection.vo.MyPayOrderVO;
import com.nft.collection.vo.OpenMysteryBoxResultVO;
import com.nft.common.operlog.OperLog;
import com.nft.common.vo.PageResult;
import com.nft.common.vo.Result;
import com.nft.constants.Constant;

import cn.dev33.satoken.stp.StpUtil;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	public static final String 模块_持有艺术品 = "持有艺术品";

	public static final String 模块_交易 = "交易";

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private CollectionGiveService collectionGiveService;

	@Autowired
	private PreSaleService preSaleService;

	@GetMapping("/checkHasPreSale")
	public Result<Boolean> checkHasPreSale(String collectionId) {
		return Result.success(preSaleService.checkHasPreSale(collectionId, StpUtil.getLoginIdAsString()));
	}

	@GetMapping("/findMyGiveRecordByPage")
	public Result<PageResult<MyGiveRecordVO>> findMyGiveRecordByPage(CollectionGiveRecordQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(collectionGiveService.findMyGiveRecordByPage(param));
	}

	@GetMapping("/getCollectionReceiverInfo")
	public Result<CollectionReceiverInfoVO> getCollectionReceiverInfo(String giveToAccount) {
		return Result
				.success(collectionGiveService.getCollectionReceiverInfo(giveToAccount, StpUtil.getLoginIdAsString()));
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_持有艺术品, operate = "艺术品转赠")
	@PostMapping("/collectionGive")
	public Result<String> collectionGive(CollectionGiveParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		collectionGiveService.collectionGive(param);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_持有艺术品, operate = "开盲盒")
	@PostMapping("/openMysteryBox")
	public Result<OpenMysteryBoxResultVO> openMysteryBox(OpenMysteryBoxParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(transactionService.openMysteryBox(param));
	}

	@GetMapping("/findMyPayOrderDetail")
	public Result<MyPayOrderDetailVO> findMyPayOrderDetail(String id) {
		return Result.success(transactionService.findMyPayOrderDetail(id));
	}

	@GetMapping("/findMyPayOrderByPage")
	public Result<PageResult<MyPayOrderVO>> findMyPayOrderByPage(PayOrderQueryCondParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		return Result.success(transactionService.findMyPayOrderByPage(param));
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_交易, operate = "取消付款")
	@PostMapping("/cancelPay")
	public Result<String> cancelPay(CancelPayParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		transactionService.cancelPay(param);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_交易, operate = "确认付款")
	@PostMapping("/confirmPay")
	public Result<String> confirmPay(ConfirmPayParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		transactionService.confirmPay(param);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_交易, operate = "创建二级市场订单")
	@PostMapping("/resaleCollectionCreateOrder")
	public Result<String> resaleCollectionCreateOrder(ResaleCollectionCreateOrderParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		String orderId = transactionService.resaleCollectionCreateOrder(param);
		return Result.success().setData(orderId);
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_交易, operate = "创建一级市场订单")
	@PostMapping("/latestCollectionCreateOrder")
	public Result<String> latestCollectionCreateOrder(LatestCollectionCreateOrderParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		String orderId = transactionService.latestCollectionCreateOrder(param);
		return Result.success().setData(orderId);
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_持有艺术品, operate = "取消寄售")
	@PostMapping("/cancelResale")
	public Result<String> cancelResale(CollectionCancelResaleParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		transactionService.cancelResale(param);
		return Result.success();
	}

	@OperLog(subSystem = Constant.子系统_会员端, module = 模块_持有艺术品, operate = "寄售")
	@PostMapping("/collectionResale")
	public Result<String> collectionResale(CollectionResaleParam param) {
		param.setMemberId(StpUtil.getLoginIdAsString());
		transactionService.collectionResale(param);
		return Result.success();
	}

}
