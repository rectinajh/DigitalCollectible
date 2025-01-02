package com.nft.collection.param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.nft.collection.domain.MemberPaidSummary;
import com.nft.common.param.PageParam;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MemberPaidSummaryQueryCondParam extends PageParam {

	private String paidAmountCondition;

	private String paidAmountValue;

	private String paidCountCondition;

	private String paidCountValue;

	private String firstPaidTimeCondition;

	private String firstPaidTimeValue;

	private String latelyPaidTimeCondition;

	private String latelyPaidTimeValue;

	public Specification<MemberPaidSummary> buildSpecification() {
		MemberPaidSummaryQueryCondParam param = this;
		Specification<MemberPaidSummary> spec = new Specification<MemberPaidSummary>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MemberPaidSummary> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getPaidAmountValue())) {
					double paidAmount = Double.parseDouble(param.getPaidAmountValue());
					if ("大于等于".equals(param.getPaidAmountCondition())) {
						predicates.add(builder.greaterThanOrEqualTo(root.get("paidAmount"), paidAmount));
					} else if ("大于".equals(param.getPaidAmountCondition())) {
						predicates.add(builder.greaterThan(root.get("paidAmount"), paidAmount));
					} else if ("等于".equals(param.getPaidAmountCondition())) {
						predicates.add(builder.equal(root.get("paidAmount"), paidAmount));
					} else if ("小于等于".equals(param.getPaidAmountCondition())) {
						predicates.add(builder.lessThanOrEqualTo(root.get("paidAmount"), paidAmount));
					} else if ("小于".equals(param.getPaidAmountCondition())) {
						predicates.add(builder.lessThan(root.get("paidAmount"), paidAmount));
					}
				}

				if (StrUtil.isNotBlank(param.getPaidCountValue())) {
					double paidCount = Double.parseDouble(param.getPaidCountValue());
					if ("大于等于".equals(param.getPaidCountCondition())) {
						predicates.add(builder.greaterThanOrEqualTo(root.get("paidCount"), paidCount));
					} else if ("大于".equals(param.getPaidCountCondition())) {
						predicates.add(builder.greaterThan(root.get("paidCount"), paidCount));
					} else if ("等于".equals(param.getPaidCountCondition())) {
						predicates.add(builder.equal(root.get("paidCount"), paidCount));
					} else if ("小于等于".equals(param.getPaidCountCondition())) {
						predicates.add(builder.lessThanOrEqualTo(root.get("paidCount"), paidCount));
					} else if ("小于".equals(param.getPaidCountCondition())) {
						predicates.add(builder.lessThan(root.get("paidCount"), paidCount));
					}
				}

				if (StrUtil.isNotBlank(param.getFirstPaidTimeValue())) {
					Date date = DateUtil.parse(param.getFirstPaidTimeValue(), DatePattern.NORM_DATE_PATTERN)
							.toJdkDate();
					if ("大于等于".equals(param.getFirstPaidTimeCondition())) {
						predicates.add(builder.greaterThanOrEqualTo(root.get("firstPaidTime").as(Date.class),
								DateUtil.beginOfDay(date)));
					} else if ("大于".equals(param.getFirstPaidTimeCondition())) {
						predicates.add(builder.greaterThan(root.get("firstPaidTime").as(Date.class),
								DateUtil.beginOfDay(date)));
					} else if ("等于".equals(param.getFirstPaidTimeCondition())) {
						predicates.add(
								builder.equal(root.get("firstPaidTime").as(Date.class), DateUtil.beginOfDay(date)));
					} else if ("小于等于".equals(param.getFirstPaidTimeCondition())) {
						predicates.add(builder.lessThanOrEqualTo(root.get("firstPaidTime").as(Date.class),
								DateUtil.beginOfDay(date)));
					} else if ("小于".equals(param.getFirstPaidTimeCondition())) {
						predicates.add(
								builder.lessThan(root.get("firstPaidTime").as(Date.class), DateUtil.beginOfDay(date)));
					}
				}

				if (StrUtil.isNotBlank(param.getLatelyPaidTimeValue())) {
					Date date = DateUtil.parse(param.getLatelyPaidTimeValue(), DatePattern.NORM_DATE_PATTERN)
							.toJdkDate();
					if ("大于等于".equals(param.getLatelyPaidTimeCondition())) {
						predicates.add(builder.greaterThanOrEqualTo(root.get("latelyPaidTime").as(Date.class),
								DateUtil.beginOfDay(date)));
					} else if ("大于".equals(param.getLatelyPaidTimeCondition())) {
						predicates.add(builder.greaterThan(root.get("latelyPaidTime").as(Date.class),
								DateUtil.beginOfDay(date)));
					} else if ("等于".equals(param.getLatelyPaidTimeCondition())) {
						predicates.add(
								builder.equal(root.get("latelyPaidTime").as(Date.class), DateUtil.beginOfDay(date)));
					} else if ("小于等于".equals(param.getLatelyPaidTimeCondition())) {
						predicates.add(builder.lessThanOrEqualTo(root.get("latelyPaidTime").as(Date.class),
								DateUtil.beginOfDay(date)));
					} else if ("小于".equals(param.getLatelyPaidTimeCondition())) {
						predicates.add(
								builder.lessThan(root.get("latelyPaidTime").as(Date.class), DateUtil.beginOfDay(date)));
					}
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
