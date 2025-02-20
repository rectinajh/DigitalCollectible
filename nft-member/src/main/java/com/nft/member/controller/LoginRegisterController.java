package com.nft.member.controller;

import javax.servlet.http.HttpServletRequest;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.nft.member.param.OneClickLoginParam;
import com.nft.member.param.SettingLoginPwdParam;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nft.common.exception.BizException;
import com.nft.common.vo.Result;
import com.nft.common.vo.TokenInfo;
import com.nft.constants.Constant;
import com.nft.log.domain.LoginLog;
import com.nft.log.service.LoginLogService;
import com.nft.member.param.LoginParam;
import com.nft.member.service.MemberService;
import com.nft.member.vo.AccountAuthInfoVO;
import com.nft.setting.service.IpBlackListService;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgentUtil;

@RestController
public class LoginRegisterController {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MemberService memberService;

	@Autowired
	private LoginLogService loginLogService;

	@Autowired
	private IpBlackListService ipBlackListService;

	@GetMapping("/sendLoginVerificationCode")
	public Result<SendSmsResponse> sendLoginVerificationCode(String mobile) {
		return Result.success(memberService.sendLoginVerificationCode(mobile));
	}

	@PostMapping("/login")
	public Result<TokenInfo> login(LoginParam param, HttpServletRequest request) {
		LoginLog loginlLog = getLoginLog(param.getMobile(), request);
		if (ipBlackListService.isIpBlackList(loginlLog.getIpAddr())) {
			loginLogService.recordLoginLog(loginlLog.loginFail("IP地址已被封禁"));
			throw new BizException("IP地址已被封禁");
		}
		String verificationCode = redisTemplate.opsForValue().get(Constant.短信类型_验证码_登录 + param.getMobile());
		if (!param.getVerificationCode().equals(verificationCode)) {
			loginLogService.recordLoginLog(loginlLog.loginFail("验证码不正确"));
			throw new BizException("验证码不正确");
		}
		return getTokenInfoResult(param.getMobile(),param.getInviteCode(), loginlLog);
	}



	@PostMapping("/oneClickLogin")
	public Result<TokenInfo> oneClickLogin(OneClickLoginParam param, HttpServletRequest request) {
		LoginLog loginlLog = getLoginLog(param.getMobile(), request);
		if (ipBlackListService.isIpBlackList(loginlLog.getIpAddr())) {
			loginLogService.recordLoginLog(loginlLog.loginFail("IP地址已被封禁"));
			throw new BizException("IP地址已被封禁");
		}
		return getTokenInfoResult(param.getMobile(),param.getInviteCode(), loginlLog);
	}

	@PostMapping("/logout")
	public Result<String> loginOut() {
		StpUtil.logout();
		return Result.success();
	}
	@PostMapping("/settingLoginPwd")
	public Result<String> settingLoginPwd(SettingLoginPwdParam param) {
		memberService.settingLoginPwd(param.getMobile(),param.getPwd());
		return Result.success();
	}
	@PostMapping("/loginByPwd")
	public Result<TokenInfo> loginByPwd(SettingLoginPwdParam param, HttpServletRequest request) {
		
		LoginLog loginlLog = getLoginLog(param.getMobile(), request);
		if (ipBlackListService.isIpBlackList(loginlLog.getIpAddr())) {
			loginLogService.recordLoginLog(loginlLog.loginFail("IP地址已被封禁"));
			throw new BizException("IP地址已被封禁");
		}

		AccountAuthInfoVO vo = memberService.getAccountAuthInfo(param.getMobile());
		if (vo == null) {
			throw new BizException("请先注册账号");
		}
		if (Constant.功能状态_禁用.equals(vo.getState())) {
			loginLogService.recordLoginLog(loginlLog.loginFail("账号已被禁用"));
			throw new BizException("账号已被禁用");
		}
		if (!SaSecureUtil.sha256(param.getPwd()).equals(vo.getLoginPwd())) {
			loginLogService.recordLoginLog(loginlLog.loginFail("账号或密码不正确"));
			throw new BizException("账号或密码不正确");
		}

		StpUtil.login(vo.getId(),
				new SaLoginModel().setIsLastingCookie(false).setTimeout(60 * 60 * vo.getKeepLoginDuration()));
		StpUtil.getSession().set("mobile", vo.getMobile());
		StpUtil.getSession().set("subSystem", Constant.子系统_会员端);
		TokenInfo tokenInfo = TokenInfo.build();
		tokenInfo.setAccountId(vo.getId());

		loginLogService.recordLoginLog(loginlLog.loginSuccess(StpUtil.getTokenInfo().getTokenValue()));
		memberService.updateLatelyLoginTime(vo.getId());
		return Result.success(tokenInfo);
	}
	private Result<TokenInfo> getTokenInfoResult( String mobile, String inviteCode, LoginLog loginlLog) {
		AccountAuthInfoVO vo = memberService.getAccountAuthInfo(mobile);
		if (vo == null) {
			memberService.registerAccount(mobile, inviteCode);
			vo = memberService.getAccountAuthInfo(mobile);
		}
		if (Constant.功能状态_禁用.equals(vo.getState())) {
			loginLogService.recordLoginLog(loginlLog.loginFail("账号已被禁用"));
			throw new BizException("账号已被禁用");
		}

		StpUtil.login(vo.getId(),
				new SaLoginModel().setIsLastingCookie(false).setTimeout(60 * 60 * vo.getKeepLoginDuration()));
		StpUtil.getSession().set("mobile", vo.getMobile());
		StpUtil.getSession().set("subSystem", Constant.子系统_会员端);
		TokenInfo tokenInfo = TokenInfo.build();
		tokenInfo.setAccountId(vo.getId());

		loginLogService.recordLoginLog(loginlLog.loginSuccess(StpUtil.getTokenInfo().getTokenValue()));
		memberService.updateLatelyLoginTime(vo.getId());
		return Result.success(tokenInfo);
	}
	private LoginLog getLoginLog(String mobile,HttpServletRequest request) {
		return LoginLog.buildLog(mobile, Constant.子系统_会员端, ServletUtil.getClientIP(request),
				UserAgentUtil.parse(request.getHeader("User-Agent")));
	}

}
