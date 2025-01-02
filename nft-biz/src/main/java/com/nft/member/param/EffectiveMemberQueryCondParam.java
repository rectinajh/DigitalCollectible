package com.nft.member.param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.nft.common.param.PageParam;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class EffectiveMemberQueryCondParam extends PageParam {

	private String registeredTimeCondition;

	private String registeredTimeValue;

	public Specification<Member> buildSpecification() {
		EffectiveMemberQueryCondParam param = this;
		Specification<Member> spec = new Specification<Member>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				predicates.add(builder.equal(root.get("state"), Constant.功能状态_启用));
				predicates.add(builder.isNotNull(root.get("blockChainAddr")));
				if (StrUtil.isNotBlank(param.getRegisteredTimeValue())) {
					Date date = DateUtil.parse(param.getRegisteredTimeValue(), DatePattern.NORM_DATE_PATTERN).toJdkDate();
					if ("大于等于".equals(param.getRegisteredTimeCondition())) {
						predicates.add(builder.greaterThanOrEqualTo(root.get("registeredTime").as(Date.class),
								DateUtil.beginOfDay(date)));
					} else if ("大于".equals(param.getRegisteredTimeCondition())) {
						predicates.add(builder.greaterThan(root.get("registeredTime").as(Date.class),
								DateUtil.beginOfDay(date)));
					} else if ("等于".equals(param.getRegisteredTimeCondition())) {
						predicates
								.add(builder.equal(root.get("registeredTime").as(Date.class), DateUtil.beginOfDay(date)));
					} else if ("小于等于".equals(param.getRegisteredTimeCondition())) {
						predicates.add(builder.lessThanOrEqualTo(root.get("registeredTime").as(Date.class),
								DateUtil.beginOfDay(date)));
					} else if ("小于".equals(param.getRegisteredTimeCondition())) {
						predicates.add(
								builder.lessThan(root.get("registeredTime").as(Date.class), DateUtil.beginOfDay(date)));
					}
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
