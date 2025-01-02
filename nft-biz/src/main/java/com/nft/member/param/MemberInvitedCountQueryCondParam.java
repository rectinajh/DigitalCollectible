package com.nft.member.param;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.nft.common.param.PageParam;
import com.nft.member.domain.MemberInvitedCount;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MemberInvitedCountQueryCondParam extends PageParam {

	private String invitedCountCondition;

	private String invitedCountValue;

	public Specification<MemberInvitedCount> buildSpecification() {
		MemberInvitedCountQueryCondParam param = this;
		Specification<MemberInvitedCount> spec = new Specification<MemberInvitedCount>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MemberInvitedCount> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getInvitedCountValue())) {
					Path<Integer> path = root.get("realNameCount");
					Integer value = Integer.parseInt(param.getInvitedCountValue());
					if ("大于等于".equals(param.getInvitedCountCondition())) {
						predicates.add(builder.greaterThanOrEqualTo(path, value));
					} else if ("大于".equals(param.getInvitedCountCondition())) {
						predicates.add(builder.greaterThan(path, value));
					} else if ("等于".equals(param.getInvitedCountCondition())) {
						predicates.add(builder.equal(path, value));
					} else if ("小于等于".equals(param.getInvitedCountCondition())) {
						predicates.add(builder.lessThanOrEqualTo(path, value));
					} else if ("小于".equals(param.getInvitedCountCondition())) {
						predicates.add(builder.lessThan(path, value));
					}
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
