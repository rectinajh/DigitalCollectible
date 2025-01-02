package com.nft.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nft.collection.service.ExchangeCodeService;

@Component
public class ExchangeCodeTimeoutExpire {

	@Autowired
	private ExchangeCodeService exchangeCodeService;

	@Scheduled(fixedRate = 120 * 1000)
	public void execute() {
		exchangeCodeService.timeoutExpire();
	}

}
