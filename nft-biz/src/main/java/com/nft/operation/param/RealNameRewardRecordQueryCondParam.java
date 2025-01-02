package com.nft.operation.param;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.nft.common.param.PageParam;
import com.nft.operation.domain.RealNameRewardRecord;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RealNameRewardRecordQueryCondParam extends PageParam {

	private String memberMobile;

	private String state;

	public Specification<RealNameRewardRecord> buildSpecification() {
		RealNameRewardRecordQueryCondParam param = this;
		Specification<RealNameRewardRecord> spec = new Specification<RealNameRewardRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<RealNameRewardRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotEmpty(param.getMemberMobile())) {
					predicates.add(builder.equal(root.join("member").get("mobile"), param.getMemberMobile()));
				}
				if (StrUtil.isNotEmpty(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
