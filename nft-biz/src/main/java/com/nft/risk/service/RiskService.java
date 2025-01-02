package com.nft.risk.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.collection.param.PayOrderQueryCondParam;
import com.nft.collection.repo.PayOrderRepo;
import com.nft.common.vo.PageResult;
import com.nft.constants.Constant;
import com.nft.risk.domain.RiskRecord;
import com.nft.risk.param.RiskRecordQueryCondParam;
import com.nft.risk.repo.RiskRecordRepo;
import com.nft.risk.vo.RiskRecordVO;
import com.nft.setting.domain.RiskSetting;
import com.nft.setting.repo.RiskSettingRepo;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

@Validated
@Service
public class RiskService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private RiskSettingRepo riskSettingRepo;

	@Autowired
	private PayOrderRepo payOrderRepo;

	@Autowired
	private RiskRecordRepo riskRecordRepo;

	@Transactional(readOnly = true)
	public PageResult<RiskRecordVO> findRiskRecordByPage(@Valid RiskRecordQueryCondParam param) {
		Page<RiskRecord> result = riskRecordRepo.findAll(param.buildSpecification(),
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<RiskRecordVO> pageResult = new PageResult<>(RiskRecordVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void orderUnpaid(String memberId) {
		RiskSetting riskSetting = riskSettingRepo.findTopByOrderByLatelyUpdateTime();
		Date now = new Date();
		PayOrderQueryCondParam param = new PayOrderQueryCondParam();
		param.setMemberId(memberId);
		param.setState(Constant.支付订单状态_已取消);
		param.setCreateTimeStart(
				DateUtil.offset(now, DateField.MINUTE, -riskSetting.getOrderUnpaidTimeRange()).toJdkDate());
		param.setCreateTimeEnd(now);
		long count = payOrderRepo.count(param.buildSpecification());
		if (count < riskSetting.getOrderUnpaidCount()) {
			return;
		}

		RiskRecord riskRecord = RiskRecord.build(memberId, Constant.风控原因_下单未付款, count, Constant.风控处罚_限制下单,
				DateUtil.offset(now, DateField.MINUTE, riskSetting.getOrderUnpaidPunish()).toJdkDate());
		riskRecordRepo.save(riskRecord);
		redisTemplate.opsForValue().set(riskRecord.getRiskPunish() + memberId, memberId, riskRecord.getRiskSecond(),
				TimeUnit.SECONDS);
	}

}
