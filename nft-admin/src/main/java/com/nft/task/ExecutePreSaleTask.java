package com.nft.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nft.collection.service.PreSaleService;

@Component
public class ExecutePreSaleTask {

	@Autowired
	private PreSaleService preSaleService;

	@Scheduled(fixedRate = 12000)
	public void execute() {
		preSaleService.executePreSaleTask();
	}

}
