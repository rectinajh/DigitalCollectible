package com.nft.collection.domain;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "v_member_paid_summary")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MemberPaidSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Integer paidCount;

	private Double paidAmount;

	private Date firstPaidTime;

	private Date latelyPaidTime;

}
