package com.nft.member.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "v_member_invited_count")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MemberInvitedCount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	
	private Integer totalCount;
	
	private Integer realNameCount;
	
	private Integer boughtCount;

}
