package com.nft.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nft.collection.service.AirDropService;

@Component
public class AirDropGrant {

	@Autowired
	private AirDropService airDropService;

	@Scheduled(fixedRate = 12000)
	public void execute() {
		airDropService.airDropGrant();
	}

}
