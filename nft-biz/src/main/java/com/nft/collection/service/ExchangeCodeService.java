package com.nft.collection.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.collection.domain.Collection;
import com.nft.collection.domain.ExchangeCode;
import com.nft.collection.domain.ExchangeCodeSummary;
import com.nft.collection.param.ExchangeCodeQueryCondParam;
import com.nft.collection.param.ExchangeCodeSummaryQueryCondParam;
import com.nft.collection.param.ExchangeParam;
import com.nft.collection.param.GenerateExchangeCodeParam;
import com.nft.collection.repo.ExchangeCodeRepo;
import com.nft.collection.repo.ExchangeCodeSummaryRepo;
import com.nft.collection.vo.ExchangeCodeSummaryVO;
import com.nft.collection.vo.ExchangeCodeVO;
import com.nft.collection.vo.ExchangeRecordVO;
import com.nft.collection.vo.ExchangeResultVO;
import com.nft.collection.vo.IssueResultVO;
import com.nft.common.exception.BizException;
import com.nft.common.vo.PageResult;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;
import com.nft.member.repo.MemberRepo;

@Validated
@Service
public class ExchangeCodeService {

	@Autowired
	private IssuedCollectionService issuedCollectionService;

	@Autowired
	private ExchangeCodeRepo exchangeCodeRepo;

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private ExchangeCodeSummaryRepo exchangeCodeSummaryRepo;

	@Transactional
	public void invalid(@NotBlank String collectionId) {
		List<ExchangeCode> exchangeCodes = exchangeCodeRepo.findByCollectionIdAndStateAndDeletedFlagFalse(collectionId,
				Constant.兑换码状态_未使用);
		for (ExchangeCode exchangeCode : exchangeCodes) {
			exchangeCode.setState(Constant.兑换码状态_已作废);
			exchangeCodeRepo.save(exchangeCode);
		}
	}

	@Transactional(readOnly = true)
	public List<ExchangeRecordVO> findExchangeRecord(ExchangeCodeQueryCondParam param) {
		param.setState(Constant.兑换码状态_已使用);
		List<ExchangeCode> result = exchangeCodeRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("createTime")));
		return ExchangeRecordVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<ExchangeCodeSummaryVO> findExchangeCodeSummaryByPage(
			@Valid ExchangeCodeSummaryQueryCondParam param) {
		Page<ExchangeCodeSummary> result = exchangeCodeSummaryRepo.findAll(param.buildSpecification(), PageRequest
				.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("lastGenerateTime"))));
		PageResult<ExchangeCodeSummaryVO> pageResult = new PageResult<>(
				ExchangeCodeSummaryVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void timeoutExpire() {
		List<ExchangeCode> exchangeCodes = exchangeCodeRepo.findByStateAndEffectiveTimeLessThan(Constant.兑换码状态_未使用,
				new Date());
		for (ExchangeCode exchangeCode : exchangeCodes) {
			exchangeCode.setState(Constant.兑换码状态_已过期);
			exchangeCodeRepo.save(exchangeCode);
		}
	}

	@Transactional(readOnly = true)
	public List<ExchangeCodeVO> findExchangeCode(ExchangeCodeQueryCondParam param) {
		param.setState(Constant.兑换码状态_未使用);
		List<ExchangeCode> result = exchangeCodeRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("createTime")));
		return ExchangeCodeVO.convertFor(result);
	}

	@Transactional
	public void generateExchangeCode(@Valid GenerateExchangeCodeParam param) {
		Date now = new Date();
		List<ExchangeCode> exchangeCodes = new ArrayList<ExchangeCode>();
		for (int i = 0; i < param.getQuantity(); i++) {
			exchangeCodes.add(ExchangeCode.build(param.getCollectionId(), now, param.getEffectiveDays()));
		}
		exchangeCodeRepo.saveAll(exchangeCodes);
	}

	@Transactional
	public ExchangeResultVO exchange(@Valid ExchangeParam param) {
		Member member = memberRepo.getOne(param.getMemberId());
		member.validBasicRisk();

		ExchangeCode exchangeCode = exchangeCodeRepo.findTopByCodeAndStateAndDeletedFlagFalse(param.getCode(),
				Constant.兑换码状态_未使用);
		if (exchangeCode == null) {
			throw new BizException("兑换码无效");
		}
		Collection collection = exchangeCode.getCollection();
		if (collection.getStock() <= 0) {
			throw new BizException("库存不足了");
		}

		IssueResultVO firstIssueResultVO = issuedCollectionService.issue(collection, param.getMemberId(),
				Constant.藏品获取方式_兑换码);

		exchangeCode.used(firstIssueResultVO.getIssuedCollectionId(), param.getMemberId());
		exchangeCodeRepo.save(exchangeCode);

		return ExchangeResultVO.build(collection.getName(), collection.getCover());
	}

}
