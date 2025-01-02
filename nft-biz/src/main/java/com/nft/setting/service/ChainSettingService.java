package com.nft.setting.service;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.setting.domain.ChainSetting;
import com.nft.setting.domain.WenChangChainSetting;
import com.nft.setting.param.WenChangChainSettingParam;
import com.nft.setting.repo.ChainSettingRepo;
import com.nft.setting.repo.WenChangChainSettingRepo;
import com.nft.setting.vo.WenChangChainSettingVO;

@Validated
@Service
public class ChainSettingService {

	@Autowired
	private ChainSettingRepo chainSettingRepo;

	@Autowired
	private WenChangChainSettingRepo wenChangChainSettingRepo;

	@Transactional(readOnly = true)
	public String getCurrentInUseChain() {
		ChainSetting setting = chainSettingRepo.findTopByOrderByLatelyUpdateTime();
		return setting != null ? setting.getCurrentInUseChain() : "";
	}

	@Transactional
	public void updateCurrentInUseChain(@NotBlank String currentInUseChain) {
		ChainSetting setting = chainSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = ChainSetting.build();
		}
		setting.setCurrentInUseChain(currentInUseChain);
		setting.setLatelyUpdateTime(new Date());
		chainSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public WenChangChainSettingVO getWenChangChainSetting() {
		WenChangChainSetting setting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();
		return WenChangChainSettingVO.convertFor(setting);
	}

	@Transactional
	public void updateWenChangChainSetting(@Valid WenChangChainSettingParam param) {
		WenChangChainSetting setting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = WenChangChainSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		wenChangChainSettingRepo.save(setting);
	}

}
