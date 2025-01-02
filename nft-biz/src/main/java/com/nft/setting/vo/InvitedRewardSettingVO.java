package com.nft.setting.vo;

import java.util.ArrayList;
import java.util.List;

import com.nft.setting.domain.InvitedRewardSetting;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;

@Data
public class InvitedRewardSettingVO {

	private Boolean rewardFun;

	private List<InvitedRequireVO> invitedRequires = new ArrayList<InvitedRequireVO>();

	public static InvitedRewardSettingVO convertFor(InvitedRewardSetting po) {
		InvitedRewardSettingVO vo = new InvitedRewardSettingVO();
		if (po != null) {
			vo.setRewardFun(po.getRewardFun());
			if (StrUtil.isNotBlank(po.getInvitedRequire())) {
				List<InvitedRequireVO> invitedRequires = JSONUtil.toList(po.getInvitedRequire(), InvitedRequireVO.class);
				vo.setInvitedRequires(invitedRequires);
			}
		}
		return vo;
	}

}
