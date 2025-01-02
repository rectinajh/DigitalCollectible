package com.nft;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nft.chain.service.ChainService;
import com.nft.collection.service.AirDropService;
import com.nft.collection.service.PreSaleService;
import com.nft.collection.service.TransactionService;
import com.nft.constants.Constant;
import com.nft.operation.service.FirstOrderRewardService;
import com.nft.operation.service.InvitedRewardService;
import com.nft.operation.service.RealNameRewardService;
import com.nft.risk.service.RiskService;
import com.nft.sms.service.SmsService;
import com.zengtengpeng.annotation.MQListener;

@Component
public class RedisMqListener {

	@Autowired
	private SmsService smsService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private ChainService chainService;

	@Autowired
	private RealNameRewardService realNameRewardService;

	@Autowired
	private FirstOrderRewardService firstOrderRewardService;

	@Autowired
	private InvitedRewardService invitedRewardService;

	@Autowired
	private AirDropService airDropService;

	@Autowired
	private RiskService riskService;

	@Autowired
	private PreSaleService preSaleService;

	@MQListener(name = Constant.执行优先购任务)
	public void executePreSaleTask(String topicName, String value) {
		preSaleService.executePreSaleTask(value);
	}

	@MQListener(name = Constant.风控原因_下单未付款)
	public void orderUnpaid(String topicName, String value) {
		riskService.orderUnpaid(value);
	}

	@MQListener(name = Constant.空投发放)
	public void airDropGrant(String topicName, String value) {
		airDropService.airDropGrant(value);
	}

	@MQListener(name = Constant.执行空投任务)
	public void executeAirDropTask(String topicName, String value) {
		airDropService.executeAirDropTask(value);
	}

	@MQListener(name = Constant.邀请奖励)
	public void createInvitedRewardRecord(String topicName, String value) {
		invitedRewardService.createInvitedRewardRecord(value);
	}

	@MQListener(name = Constant.邀请奖励发放)
	public void invitedRewardGrant(String topicName, String value) {
		invitedRewardService.invitedRewardGrant(value);
	}

	@MQListener(name = Constant.首单奖励)
	public void createFirstOrderRewardRecord(String topicName, String value) {
		firstOrderRewardService.createFirstOrderRewardRecord(value);
	}

	@MQListener(name = Constant.首单奖励发放)
	public void firstOrderRewardGrant(String topicName, String value) {
		firstOrderRewardService.firstOrderRewardGrant(value);
	}

	@MQListener(name = Constant.实名认证奖励)
	public void createRealNameRewardRecord(String topicName, String value) {
		realNameRewardService.createRealNameRewardRecord(value);
	}

	@MQListener(name = Constant.实名认证奖励发放)
	public void realNameRewardGrant(String topicName, String value) {
		realNameRewardService.realNameRewardGrant(value);
	}

	@MQListener(name = Constant.上链_同步交易HASH)
	public void syncTransactionHash(String topicName, String value) {
		chainService.syncTransactionHash(value);
	}

	@MQListener(name = Constant.上链_同步唯一标识)
	public void syncUniqueId(String topicName, String value) {
		chainService.syncUniqueId(value);
	}

	@MQListener(name = Constant.上链_销毁艺术品)
	public void destroyArtwork(String topicName, String value) {
		chainService.destroyArtwork(value);
	}

	@MQListener(name = Constant.上链_转让艺术品)
	public void transferArtwork(String topicName, String value) {
		chainService.transferArtwork(value);
	}

	@MQListener(name = Constant.上链_二级市场购买艺术品)
	public void marketBuyArtwork(String topicName, String value) {
		chainService.marketBuyArtwork(value);
	}

	@MQListener(name = Constant.上链_铸造艺术品)
	public void mintArtwork(String topicName, String value) {
		chainService.mintArtwork(value);
	}

	@MQListener(name = Constant.上链_同步艺术品HASH)
	public void syncArtworkHash(String topicName, String value) {
		chainService.syncArtworkHash(value);
	}

	@MQListener(name = Constant.上链_创建艺术品)
	public void chainAddArtwork(String topicName, String value) {
		chainService.chainAddArtwork(value);
	}

	@MQListener(name = Constant.创建区块链地址)
	public void createBlockChainAddr(String topicName, String value) {
		chainService.createBlockChainAddr(value);
	}

	@MQListener(name = Constant.支付订单超时取消)
	public void buyerPaidConfirm(String topicName, String value) {
		transactionService.cancelPay(value);
	}

	@MQListener(name = Constant.发送短信)
	public void sendSms(String topicName, String value) {
		smsService.sendSms(value);
	}

}
