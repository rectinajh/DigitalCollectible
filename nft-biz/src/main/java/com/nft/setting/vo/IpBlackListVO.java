package com.nft.setting.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class IpBlackListVO implements Comparable<IpBlackListVO> {

	private String ipAddr;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	public static IpBlackListVO convertFor(String ipAddr, Date createTime) {
		IpBlackListVO vo = new IpBlackListVO();
		vo.setIpAddr(ipAddr);
		vo.setCreateTime(createTime);
		return vo;
	}

	@Override
	public int compareTo(IpBlackListVO o) {
		return o.getCreateTime().compareTo(this.getCreateTime());
	}

}
