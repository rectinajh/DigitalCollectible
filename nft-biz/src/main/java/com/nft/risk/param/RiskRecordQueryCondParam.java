package com.nft.risk.param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import com.nft.common.param.PageParam;
import com.nft.risk.domain.RiskRecord;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RiskRecordQueryCondParam extends PageParam {

	private String mobile;

	private String riskCause;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date timeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date timeEnd;

	public Specification<RiskRecord> buildSpecification() {
		RiskRecordQueryCondParam param = this;
		Specification<RiskRecord> spec = new Specification<RiskRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<RiskRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getMobile())) {
					predicates.add(builder.equal(root.join("member").get("mobile"), param.getMobile()));
				}
				if (StrUtil.isNotBlank(param.getRiskCause())) {
					predicates.add(builder.equal(root.get("riskCause"), param.getRiskCause()));
				}
				if (param.getTimeStart() != null) {
					predicates.add(
							builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), param.getTimeStart()));
				}
				if (param.getTimeEnd() != null) {
					predicates
							.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class), param.getTimeEnd()));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
