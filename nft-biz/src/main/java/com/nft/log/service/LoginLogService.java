package com.nft.log.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.common.vo.PageResult;
import com.nft.log.domain.LoginLog;
import com.nft.log.param.LoginLogQueryCondParam;
import com.nft.log.repo.LoginLogRepo;
import com.nft.log.vo.LoginLogVO;
import com.nft.log.vo.OnlineAccountVO;

import cn.dev33.satoken.stp.StpUtil;

@Validated
@Service
public class LoginLogService {

	@Autowired
	private LoginLogRepo loginLogRepo;

	public void forceLogout(String token) {
		StpUtil.logoutByTokenValue(token);
	}

	public List<String> findLoginToken() {
		List<String> loginTokens = new ArrayList<String>();
		String prefix = "token:";
		List<String> splicingKeyTokenValues = StpUtil.searchTokenValue("", -1, -1, false);
		for (String s : splicingKeyTokenValues) {
			loginTokens.add(s.substring(s.lastIndexOf(prefix) + prefix.length()));
		}
		return loginTokens;
	}

	@Transactional(readOnly = true)
	public PageResult<OnlineAccountVO> findOnlineAccountByPage(@Valid LoginLogQueryCondParam param) {
		param.setLoginTokens(findLoginToken());
		Page<LoginLog> result = loginLogRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("loginTime"))));
		PageResult<OnlineAccountVO> pageResult = new PageResult<>(OnlineAccountVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<LoginLogVO> findLoginLogByPage(@Valid LoginLogQueryCondParam param) {
		Page<LoginLog> result = loginLogRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("loginTime"))));
		PageResult<LoginLogVO> pageResult = new PageResult<>(LoginLogVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void recordLoginLog(LoginLog loginLog) {
		loginLogRepo.save(loginLog);
	}

}
