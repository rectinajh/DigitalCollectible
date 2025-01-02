package com.nft.setting.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nft.constants.Constant;
import com.nft.setting.vo.IpBlackListVO;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

@Service
public class IpBlackListService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Transactional(readOnly = true)
	public Boolean isIpBlackList(String ipAddr) {
		String value = redisTemplate.opsForValue().get(Constant.IP黑名单 + ipAddr);
		return StrUtil.isNotBlank(value);
	}

	@Transactional(readOnly = true)
	public List<IpBlackListVO> findIpBlackList(String ipAddr) {
		String keyPrefix = Constant.IP黑名单;
		List<IpBlackListVO> vos = new ArrayList<>();
		Set<String> keys = redisTemplate.keys(keyPrefix + (StrUtil.isNotBlank(ipAddr) ? ipAddr : "") + "*");
		for (String key : keys) {
			String ipAddrTmp = key.split(keyPrefix)[1];
			String createTime = redisTemplate.opsForValue().get(key);
			vos.add(IpBlackListVO.convertFor(ipAddrTmp, DateUtil.parse(createTime, DatePattern.NORM_DATETIME_PATTERN)));
		}

		Collections.sort(vos);
		return vos;
	}

	@Transactional
	public void delIpBlackList(String ipAddr) {
		redisTemplate.delete(Constant.IP黑名单 + ipAddr);
	}

	@Transactional
	public void addIpBlackList(String ipAddr) {
		redisTemplate.opsForValue().set(Constant.IP黑名单 + ipAddr,
				DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
	}

}
