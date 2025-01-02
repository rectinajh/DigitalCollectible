package com.nft.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nft.operation.service.FirstOrderRewardService;
import com.nft.operation.service.InvitedRewardService;
import com.nft.operation.service.RealNameRewardService;

@Component
public class RewardGrant {

	@Autowired
	private RealNameRewardService realNameRewardService;

	@Autowired
	private FirstOrderRewardService firstOrderRewardService;

	@Autowired
	private InvitedRewardService invitedRewardService;

	@Scheduled(fixedRate = 14000)
	public void execute() {
		realNameRewardService.realNameRewardGrant();
		firstOrderRewardService.firstOrderRewardGrant();
		invitedRewardService.invitedRewardGrant();
	}

}
