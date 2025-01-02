package com.nft.setting.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.common.exception.BizException;
import com.nft.setting.domain.FirstOrderRewardSetting;
import com.nft.setting.domain.InvitedRewardSetting;
import com.nft.setting.domain.OperationSetting;
import com.nft.setting.domain.RealNameRewardSetting;
import com.nft.setting.domain.RiskSetting;
import com.nft.setting.domain.SystemSetting;
import com.nft.setting.param.FirstOrderRewardSettingParam;
import com.nft.setting.param.InvitedRequireParam;
import com.nft.setting.param.InvitedRewardSettingParam;
import com.nft.setting.param.OperationSettingParam;
import com.nft.setting.param.RealNameRewardSettingParam;
import com.nft.setting.param.RiskSettingParam;
import com.nft.setting.param.SystemSettingParam;
import com.nft.setting.repo.FirstOrderRewardSettingRepo;
import com.nft.setting.repo.InvitedRewardSettingRepo;
import com.nft.setting.repo.OperationSettingRepo;
import com.nft.setting.repo.RealNameRewardSettingRepo;
import com.nft.setting.repo.RiskSettingRepo;
import com.nft.setting.repo.SystemSettingRepo;
import com.nft.setting.vo.AppSchemaVO;
import com.nft.setting.vo.FirstOrderRewardSettingVO;
import com.nft.setting.vo.InvitedRewardSettingVO;
import com.nft.setting.vo.LatestAppInfoVO;
import com.nft.setting.vo.OperationSettingVO;
import com.nft.setting.vo.RealNameRewardSettingVO;
import com.nft.setting.vo.RiskSettingVO;
import com.nft.setting.vo.SystemSettingVO;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Validated
@Service
@Slf4j
public class SettingService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private SystemSettingRepo systemSettingRepo;

	@Autowired
	private OperationSettingRepo operationSettingRepo;

	@Autowired
	private RealNameRewardSettingRepo realNameRewardSettingRepo;

	@Autowired
	private FirstOrderRewardSettingRepo firstOrderRewardSettingRepo;

	@Autowired
	private InvitedRewardSettingRepo invitedRewardSettingRepo;

	@Autowired
	private RiskSettingRepo riskSettingRepo;

	@Transactional(readOnly = true)
	public RiskSettingVO getRiskSetting() {
		RiskSetting setting = riskSettingRepo.findTopByOrderByLatelyUpdateTime();
		return RiskSettingVO.convertFor(setting);
	}

	@Transactional
	public void updateRiskSetting(@Valid RiskSettingParam param) {
		RiskSetting setting = riskSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = RiskSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		riskSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public InvitedRewardSettingVO getInvitedRewardSetting() {
		InvitedRewardSetting setting = invitedRewardSettingRepo.findTopByOrderByLatelyUpdateTime();
		return InvitedRewardSettingVO.convertFor(setting);
	}

	@Transactional
	public void updateInvitedRewardSetting(@Valid InvitedRewardSettingParam param) {
		Map<Integer, Integer> invitedCountMap = new HashMap<>();
		List<InvitedRequireParam> invitedRequires = param.getInvitedRequires();
		Collections.sort(invitedRequires);
		int level = 1;
		for (InvitedRequireParam invitedRequire : invitedRequires) {
			if (invitedCountMap.get(invitedRequire.getInvitedCount()) != null) {
				throw new BizException("有效邀请人数不能重复");
			}
			invitedCountMap.put(invitedRequire.getInvitedCount(), invitedRequire.getInvitedCount());
			invitedRequire.setLevel(level);
			level++;
		}

		InvitedRewardSetting setting = invitedRewardSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = InvitedRewardSetting.build();
		}
		setting.setRewardFun(param.getRewardFun());
		setting.setInvitedRequire(JSONUtil.toJsonStr(invitedRequires));
		setting.setLatelyUpdateTime(new Date());
		invitedRewardSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public FirstOrderRewardSettingVO getFirstOrderRewardSetting() {
		FirstOrderRewardSetting setting = firstOrderRewardSettingRepo.findTopByOrderByLatelyUpdateTime();
		return FirstOrderRewardSettingVO.convertFor(setting);
	}

	@Transactional
	public void updateFirstOrderRewardSetting(@Valid FirstOrderRewardSettingParam param) {
		FirstOrderRewardSetting setting = firstOrderRewardSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = FirstOrderRewardSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		firstOrderRewardSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public RealNameRewardSettingVO getRealNameRewardSetting() {
		RealNameRewardSetting setting = realNameRewardSettingRepo.findTopByOrderByLatelyUpdateTime();
		return RealNameRewardSettingVO.convertFor(setting);
	}

	@Transactional
	public void updateRealNameRewardSetting(@Valid RealNameRewardSettingParam param) {
		RealNameRewardSetting setting = realNameRewardSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = RealNameRewardSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		realNameRewardSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public OperationSettingVO getOperationSetting() {
		OperationSetting setting = operationSettingRepo.findTopByOrderByLatelyUpdateTime();
		return OperationSettingVO.convertFor(setting);
	}

	@Transactional
	public void updateOperationSetting(@Valid OperationSettingParam param) {
		OperationSetting setting = operationSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = OperationSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		operationSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public String getH5Gateway() {
		SystemSetting setting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
		return setting.getH5Gateway();
	}

	@Transactional(readOnly = true)
	public AppSchemaVO getAppSchema() {
		SystemSetting setting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
		return AppSchemaVO.convertFor(setting);
	}

	@Transactional(readOnly = true)
	public LatestAppInfoVO getLatestAppInfo() {
		SystemSetting setting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
		return LatestAppInfoVO.convertFor(setting);
	}

	@Transactional(readOnly = true)
	public SystemSettingVO getSystemSetting() {
		SystemSetting setting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
		return SystemSettingVO.convertFor(setting);
	}

	@Transactional
	public void updateSystemSetting(@Valid SystemSettingParam param) {
		SystemSetting setting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = SystemSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		systemSettingRepo.save(setting);
	}

	public void refreshCache() {
		List<String> cacheItems = Arrays.asList("dict*", "findMenuTreeByAccountId_*");
		List<String> deleteSuccessKeys = new ArrayList<>();
		List<String> deleteFailKeys = new ArrayList<>();
		for (String cacheItem : cacheItems) {
			Set<String> keys = redisTemplate.keys(cacheItem);
			for (String key : keys) {
				Boolean flag = redisTemplate.delete(key);
				if (flag) {
					deleteSuccessKeys.add(key);
				} else {
					deleteFailKeys.add(key);
				}
			}
		}
		if (!deleteFailKeys.isEmpty()) {
			log.warn("以下的缓存删除失败:", deleteFailKeys);
		}
	}

}
