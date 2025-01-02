package com.nft.collection.domain;

import java.io.Serializable;
import java.text.DecimalFormat;
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

import com.nft.common.exception.BizException;
import com.nft.common.utils.IdUtils;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "issued_collection_action_log")
@DynamicInsert(true)
@DynamicUpdate(true)
public class IssuedCollectionActionLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Date actionTime;

	private String actionDesc;

	@Version
	private Long version;

	@Column(name = "member_id", length = 32)
	private String memberId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;

	@Column(name = "issued_collection_id", length = 32)
	private String issuedCollectionId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "issued_collection_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private IssuedCollection issuedCollection;

	public static IssuedCollectionActionLog buildWithIssue(String memberId, String issuedCollectionId, String gainWay,
			Double price) {
		if (Constant.藏品获取方式_兑换码.equals(gainWay)) {
			return buildWithExchangeCode(memberId, issuedCollectionId);
		} else if (Constant.藏品获取方式_合成.equals(gainWay)) {
			return buildWithCompose(memberId, issuedCollectionId);
		} else if (Constant.藏品获取方式_空投.equals(gainWay)) {
			return buildWithAirDrop(memberId, issuedCollectionId);
		} else if (Constant.藏品获取方式_盲盒.equals(gainWay)) {
			return buildWithMysteryBoxGet(memberId, issuedCollectionId);
		} else if (Constant.藏品获取方式_购买.equals(gainWay)) {
			return buildWithBuy(memberId, issuedCollectionId, price);
		}
		throw new BizException("未知的藏品获取方式");
	}

	public static IssuedCollectionActionLog buildWithExchangeCode(String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build("兑换码获得", memberId, issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithAirDrop(String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build("空投", memberId, issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithCompose(String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build("合成", memberId, issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithManualDestroy(String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build("手动销毁", memberId, issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithComposeDestroy(String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build("合成销毁", memberId, issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithOpenMysteryBox(String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build("开启盲盒", memberId, issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithMysteryBoxGet(String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build("盲盒获得", memberId, issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithReceive(String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build("收到赠与", memberId, issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithCancelResale(String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build("取消寄售", memberId, issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithResale(Double price, String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build(
				"寄售" + new DecimalFormat("###################.###########").format(price), memberId,
				issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog buildWithBuy(String memberId, String issuedCollectionId, Double price) {
		IssuedCollectionActionLog po = IssuedCollectionActionLog.build(
				"买入" + new DecimalFormat("###################.###########").format(price), memberId,
				issuedCollectionId);
		return po;
	}

	public static IssuedCollectionActionLog build(String actionDesc, String memberId, String issuedCollectionId) {
		IssuedCollectionActionLog po = new IssuedCollectionActionLog();
		po.setId(IdUtils.getId());
		po.setActionTime(new Date());
		po.setActionDesc(actionDesc);
		po.setMemberId(memberId);
		po.setIssuedCollectionId(issuedCollectionId);
		return po;
	}

}
