package com.nft.common;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.nft.constants.Constant;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;

@Component
public class StpInterfaceImpl implements StpInterface {

	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		List<String> list = new ArrayList<String>();
		return list;
	}

	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		List<String> list = new ArrayList<String>();
		String subSystem = StpUtil.getSession().getString("subSystem");
		if (Constant.子系统_后台管理.equals(subSystem)) {
			list.add("admin");
		} else if (Constant.子系统_会员端.equals(subSystem)) {
			list.add("member");
		}
		return list;
	}

}
