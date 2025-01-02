package com.nft.chain.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nft.common.utils.ThreadPoolUtils;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;
import com.nft.member.repo.MemberRepo;
import com.nft.setting.repo.ChainSettingRepo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;

@Service
public class ChainService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private ChainSettingRepo chainSettingRepo;
	
	@Autowired
	private MemberRepo memberRepo;

	@Transactional
	public ChainAbstractService getChainServiceImpl() {
		String currentInUseChain = chainSettingRepo.findTopByOrderByLatelyUpdateTime().getCurrentInUseChain();
		ChainAbstractService chainAbstractService = SpringUtil.getBean(currentInUseChain + "Service",
				ChainAbstractService.class);
		return chainAbstractService;
	}

	@Transactional
	public String syncTransactionHash(String id) {
		return getChainServiceImpl().syncTransactionHash(id);
	}

	@Transactional
	public String syncUniqueId(String id) {
		return getChainServiceImpl().syncUniqueId(id);
	}

	@Transactional
	public void transferArtwork(String id) {
		getChainServiceImpl().chainTransfer(id);
	}

	@Transactional
	public void marketBuyArtwork(String id) {
		getChainServiceImpl().chainTransfer(id);
	}

	@Transactional
	public String chainTransfer(String id) {
		return getChainServiceImpl().chainTransfer(id);
	}

	@Transactional
	public void destroyArtwork(String id) {
		getChainServiceImpl().destroyArtwork(id);
	}

	@Transactional
	public String mintArtwork(String id) {
		return getChainServiceImpl().mintArtwork(id);
	}

	@Transactional
	public String createBlockChainAddr(String id) {
		String result = getChainServiceImpl().createBlockChainAddr(id);
		if ("已上链".equals(result)) {
			Member member = memberRepo.getOne(id);
			String inviterId = member.getInviterId();
			ThreadPoolUtils.getRewardPool().schedule(() -> {
				redissonClient.getTopic(Constant.实名认证奖励).publish(id);
				if (StrUtil.isNotBlank(inviterId)) {
					redissonClient.getTopic(Constant.邀请奖励).publish(inviterId);
				}
			}, 1, TimeUnit.SECONDS);
		}
		return result;
	}

	@Transactional
	public String syncArtworkHash(String id) {
		return getChainServiceImpl().syncArtworkHash(id);
	}

	@Transactional
	public String chainAddArtwork(String id) {
		return getChainServiceImpl().chainAddArtwork(id);
	}

}
