package com.nft.collection.param;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.nft.collection.domain.MemberHoldArtworkSummary;
import com.nft.common.param.PageParam;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MemberHoldArtworkSummaryQueryCondParam extends PageParam {

	private String artworkId;

	private String condition;

	private String artworkCount;

	public Specification<MemberHoldArtworkSummary> buildSpecification() {
		MemberHoldArtworkSummaryQueryCondParam param = this;
		Specification<MemberHoldArtworkSummary> spec = new Specification<MemberHoldArtworkSummary>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MemberHoldArtworkSummary> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getArtworkId())) {
					predicates.add(builder.equal(root.get("collectionId"), param.getArtworkId()));
				}
				if (StrUtil.isNotBlank(param.getArtworkCount())) {
					Path<Integer> path = root.get("artworkCount");
					Integer value = Integer.parseInt(param.getArtworkCount());
					if ("大于等于".equals(param.getCondition())) {
						predicates.add(builder.greaterThanOrEqualTo(path, value));
					} else if ("大于".equals(param.getCondition())) {
						predicates.add(builder.greaterThan(path, value));
					} else if ("等于".equals(param.getCondition())) {
						predicates.add(builder.equal(path, value));
					} else if ("小于等于".equals(param.getCondition())) {
						predicates.add(builder.lessThanOrEqualTo(path, value));
					} else if ("小于".equals(param.getCondition())) {
						predicates.add(builder.lessThan(path, value));
					}
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
