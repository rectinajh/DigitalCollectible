package com.nft.member.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.nft.member.domain.Member;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MemberSnapshotVO {

	private String id;

	private String mobile;

	private String blockChainAddr;

	public static List<MemberSnapshotVO> convertFor(List<Member> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MemberSnapshotVO> vos = new ArrayList<>();
		for (Member po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MemberSnapshotVO convertFor(Member po) {
		if (po == null) {
			return null;
		}
		MemberSnapshotVO vo = new MemberSnapshotVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
