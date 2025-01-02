package com.nft.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nft.collection.service.TransactionService;

@Component
public class PayOrderTimeoutCancel {

	@Autowired
	private TransactionService transactionService;

	@Scheduled(fixedRate = 12000)
	public void execute() {
		transactionService.payOrderTimeoutCancel();
	}

}
