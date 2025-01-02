package com.nft.risk.domain;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.nft.common.utils.IdUtils;
import com.nft.member.domain.Member;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "risk_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class RiskRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Date createTime;

	private String riskCause;

	private Long hitCount;

	private String riskPunish;

	private Date riskFinishTime;

	@Column(name = "member_id", length = 32)
	private String memberId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;

	public long getRiskSecond() {
		return DateUtil.between(this.getCreateTime(), this.getRiskFinishTime(), DateUnit.SECOND);
	}

	public static RiskRecord build(String memberId, String riskCause, Long hitCount, String riskPunish,
			Date riskFinishTime) {
		RiskRecord po = new RiskRecord();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setMemberId(memberId);
		po.setRiskCause(riskCause);
		po.setHitCount(hitCount);
		po.setRiskPunish(riskPunish);
		po.setRiskFinishTime(riskFinishTime);
		return po;
	}

}
