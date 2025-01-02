package com.nft.setting.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.nft.common.utils.IdUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wen_chang_chain_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class WenChangChainSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;
	
	private String apiGateway;
	
	private String apiKey;
	
	private String apiSecret;
	
	private String chainAddrSuper;
	
	private Date latelyUpdateTime;
	
	public static WenChangChainSetting build() {
		WenChangChainSetting setting = new WenChangChainSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
