package com.nft.collection.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.nft.common.utils.IdUtils;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "air_drop_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class AirDropRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private String bizType;

	private String state;

	private Date createTime;

	private Date dealTime;

	@Version
	private Long version;

	@Column(name = "collection_id", length = 32)
	private String collectionId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "collection_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Collection collection;

	@Column(name = "issued_collection_id", length = 32)
	private String issuedCollectionId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "issued_collection_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private IssuedCollection issuedCollection;

	@Column(name = "member_id", length = 32)
	private String memberId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;

	@Column(name = "air_drop_task_id", length = 32)
	private String airDropTaskId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "air_drop_task_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private AirDropTask airDropTask;

	public void fail() {
		this.setDealTime(new Date());
		this.setState(Constant.空投记录状态_库存不足);
	}

	public void success(String issuedCollectionId) {
		this.setIssuedCollectionId(issuedCollectionId);
		this.setDealTime(new Date());
		this.setState(Constant.空投记录状态_已执行);
	}

	public static AirDropRecord buildWithAirDropTask(String memberId, String collectionId, String airDropTaskId) {
		AirDropRecord po = new AirDropRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setBizType(Constant.空投业务类型_空投任务);
		po.setState(Constant.空投记录状态_未执行);
		po.setMemberId(memberId);
		po.setCollectionId(collectionId);
		po.setAirDropTaskId(airDropTaskId);
		return po;
	}

	public static AirDropRecord buildWithFail(String memberId, String collectionId, String bizType) {
		AirDropRecord po = new AirDropRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setBizType(bizType);
		po.setState(Constant.空投记录状态_库存不足);
		po.setCreateTime(new Date());
		po.setDealTime(po.getCreateTime());
		po.setMemberId(memberId);
		po.setCollectionId(collectionId);
		return po;
	}

	public static AirDropRecord buildWithSuccess(String memberId, String collectionId, String issuedCollectionId,
			String bizType) {
		AirDropRecord po = new AirDropRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setBizType(bizType);
		po.setState(Constant.空投记录状态_已执行);
		po.setCreateTime(new Date());
		po.setDealTime(po.getCreateTime());
		po.setMemberId(memberId);
		po.setCollectionId(collectionId);
		po.setIssuedCollectionId(issuedCollectionId);
		return po;
	}

}
