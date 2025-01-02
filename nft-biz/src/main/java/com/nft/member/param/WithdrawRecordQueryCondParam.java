package com.nft.member.param;

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
import com.nft.member.domain.WithdrawRecord;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WithdrawRecordQueryCondParam extends PageParam {

	private String orderNo;

	private String memberId;

	private String state;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date submitTimeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date submitTimeEnd;

	public Specification<WithdrawRecord> buildSpecification() {
		WithdrawRecordQueryCondParam param = this;
		Specification<WithdrawRecord> spec = new Specification<WithdrawRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<WithdrawRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMemberId())) {
					predicates.add(builder.equal(root.get("memberId"), param.getMemberId()));
				}
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (param.getSubmitTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							param.getSubmitTimeStart()));
				}
				if (param.getSubmitTimeEnd() != null) {
					predicates.add(
							builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class), param.getSubmitTimeEnd()));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
