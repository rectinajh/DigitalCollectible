package com.nft.collection.param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;

import com.nft.collection.domain.PayOrder;
import com.nft.common.param.PageParam;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PayOrderQueryCondParam extends PageParam {

	private String memberMobile;

	private String memberId;

	private String collectionName;

	private String bizMode;

	private String state;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTimeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTimeEnd;

	public Specification<PayOrder> buildSpecification() {
		PayOrderQueryCondParam param = this;
		Specification<PayOrder> spec = new Specification<PayOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<PayOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotEmpty(param.getMemberId())) {
					predicates.add(builder.equal(root.get("memberId"), param.getMemberId()));
				}
				if (StrUtil.isNotEmpty(param.getMemberMobile())) {
					predicates.add(builder.equal(root.join("member").get("mobile"), param.getMemberMobile()));
				}
				if (StrUtil.isNotEmpty(param.getCollectionName())) {
					predicates.add(builder.equal(root.join("collection").get("name"), param.getCollectionName()));
				}
				if (StrUtil.isNotEmpty(param.getBizMode())) {
					predicates.add(builder.equal(root.get("bizMode"), param.getBizMode()));
				}
				if (StrUtil.isNotEmpty(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (param.getCreateTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							param.getCreateTimeStart()));
				}
				if (param.getCreateTimeEnd() != null) {
					predicates.add(
							builder.lessThanOrEqualTo(root.get("createTime").as(Date.class), param.getCreateTimeEnd()));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

}
