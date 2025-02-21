package com.nft.member.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.nft.common.utils.SendSmsClient;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.collection.domain.MemberHoldArtworkSummary;
import com.nft.collection.domain.MemberPaidSummary;
import com.nft.collection.param.MemberHoldArtworkSummaryQueryCondParam;
import com.nft.collection.param.MemberPaidSummaryQueryCondParam;
import com.nft.collection.repo.MemberHoldArtworkSummaryRepo;
import com.nft.collection.repo.MemberPaidSummaryRepo;
import com.nft.common.exception.BizException;
import com.nft.common.utils.ThreadPoolUtils;
import com.nft.common.vo.PageResult;
import com.nft.constants.Constant;
import com.nft.log.domain.MemberBalanceChangeLog;
import com.nft.log.repo.MemberBalanceChangeLogRepo;
import com.nft.member.domain.Member;
import com.nft.member.domain.MemberInvitedCount;
import com.nft.member.param.AddMemberParam;
import com.nft.member.param.BindRealNameParam;
import com.nft.member.param.EffectiveMemberQueryCondParam;
import com.nft.member.param.MemberInvitedCountQueryCondParam;
import com.nft.member.param.MemberQueryCondParam;
import com.nft.member.param.MemberSnapshotQueryParam;
import com.nft.member.param.ModifyPayPwdParam;
import com.nft.member.param.UpdateMemberParam;
import com.nft.member.repo.MemberInvitedCountRepo;
import com.nft.member.repo.MemberRepo;
import com.nft.member.vo.AccountAuthInfoVO;
import com.nft.member.vo.InviteInfoVO;
import com.nft.member.vo.MemberFundInfoVO;
import com.nft.member.vo.MemberSnapshotVO;
import com.nft.member.vo.MemberStatisticDataVO;
import com.nft.member.vo.MemberVO;
import com.nft.member.vo.MyInviteRecordVO;
import com.nft.member.vo.MyPersonalInfoVO;
import com.nft.setting.repo.SystemSettingRepo;
import com.nft.sms.domain.SmsSendRecord;
import com.nft.sms.repo.SmsSendRecordRepo;
import com.zengtengpeng.annotation.Lock;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class MemberService {
	@Value("${tencent-cloud.sdkAppId}")
	private String sdkAppId;
	@Value("${tencent-cloud.signName}")
	private String signName;
	@Value("${tencent-cloud.templateId}")
	private String templateId;
	//腾讯短信验证码客户端
	@Autowired
	private SendSmsClient sendSmsClient;

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MemberRepo memberRepo;

	@Autowired
	private MemberBalanceChangeLogRepo memberBalanceChangeLogRepo;

	@Autowired
	private SmsSendRecordRepo smsSendRecordRepo;

	@Autowired
	private SystemSettingRepo systemSettingRepo;

	@Autowired
	private MemberPaidSummaryRepo memberPaidSummaryRepo;

	@Autowired
	private MemberInvitedCountRepo memberInvitedCountRepo;

	@Autowired
	private MemberHoldArtworkSummaryRepo memberHoldArtworkSummaryRepo;

	@Transactional(readOnly = true)
	public List<MemberSnapshotVO> findMemberSnapshot(List<MemberSnapshotQueryParam> params) {
		int size = params.size();
		if (size == 0) {
			List<Member> members = memberRepo.findAll(new EffectiveMemberQueryCondParam().buildSpecification(),
					Sort.by(Sort.Order.desc("registeredTime")));
			return MemberSnapshotVO.convertFor(members);
		}

		MemberSnapshotQueryParam param = params.get(0);
		String newLogicalOperation = param.getLogicalOperation();
		List<String> memberIds = findMemberIdSnapshot(param.getFieldName(), param.getCond(), param.getFieldValue());
		for (int i = 1; i < size; i++) {
			MemberSnapshotQueryParam p = params.get(i);
			List<String> tmpMemberIds = findMemberIdSnapshot(p.getFieldName(), p.getCond(), p.getFieldValue());
			if ("且".equals(newLogicalOperation)) {
				memberIds = new ArrayList<>(CollUtil.intersectionDistinct(memberIds, tmpMemberIds));
			} else if ("或".equals(newLogicalOperation)) {
				memberIds = new ArrayList<>(CollUtil.unionDistinct(memberIds, tmpMemberIds));
			}
			newLogicalOperation = p.getLogicalOperation();
		}
		List<String> effectiveMemberIds = findMemberIdSnapshot(Constant.快照参数_全部有效会员, null, null);
		effectiveMemberIds = new ArrayList<>(CollUtil.intersectionDistinct(effectiveMemberIds, memberIds));

		List<Member> members = memberRepo.findByIdIn(effectiveMemberIds);
		return MemberSnapshotVO.convertFor(members);
	}

	public List<String> findMemberIdSnapshot(String fieldName, String cond, String fieldValue) {
		List<String> memberIds = new ArrayList<String>();
		if (Constant.快照参数_全部有效会员.equals(fieldName)) {
			EffectiveMemberQueryCondParam param = new EffectiveMemberQueryCondParam();
			List<Member> members = memberRepo.findAll(param.buildSpecification(),
					Sort.by(Sort.Order.desc("registeredTime")));
			memberIds = members.stream().map(p -> p.getId()).collect(Collectors.toList());
		} else if (Constant.快照参数_注册时间.equals(fieldName)) {
			EffectiveMemberQueryCondParam param = new EffectiveMemberQueryCondParam();
			param.setRegisteredTimeCondition(cond);
			param.setRegisteredTimeValue(fieldValue);
			List<Member> members = memberRepo.findAll(param.buildSpecification(),
					Sort.by(Sort.Order.desc("registeredTime")));
			memberIds = members.stream().map(p -> p.getId()).collect(Collectors.toList());
		} else if (Constant.快照参数_支付金额.equals(fieldName)) {
			MemberPaidSummaryQueryCondParam param = new MemberPaidSummaryQueryCondParam();
			param.setPaidAmountCondition(cond);
			param.setPaidAmountValue(fieldValue);
			List<MemberPaidSummary> memberPaidSummarys = memberPaidSummaryRepo.findAll(param.buildSpecification());
			memberIds = memberPaidSummarys.stream().map(p -> p.getId()).collect(Collectors.toList());
		} else if (Constant.快照参数_支付次数.equals(fieldName)) {
			MemberPaidSummaryQueryCondParam param = new MemberPaidSummaryQueryCondParam();
			param.setPaidCountCondition(cond);
			param.setPaidCountValue(fieldValue);
			List<MemberPaidSummary> memberPaidSummarys = memberPaidSummaryRepo.findAll(param.buildSpecification());
			memberIds = memberPaidSummarys.stream().map(p -> p.getId()).collect(Collectors.toList());
		} else if (Constant.快照参数_首次支付时间.equals(fieldName)) {
			MemberPaidSummaryQueryCondParam param = new MemberPaidSummaryQueryCondParam();
			param.setFirstPaidTimeCondition(cond);
			param.setFirstPaidTimeValue(fieldValue);
			List<MemberPaidSummary> memberPaidSummarys = memberPaidSummaryRepo.findAll(param.buildSpecification());
			memberIds = memberPaidSummarys.stream().map(p -> p.getId()).collect(Collectors.toList());
		} else if (Constant.快照参数_最近支付时间.equals(fieldName)) {
			MemberPaidSummaryQueryCondParam param = new MemberPaidSummaryQueryCondParam();
			param.setLatelyPaidTimeCondition(cond);
			param.setLatelyPaidTimeValue(fieldValue);
			List<MemberPaidSummary> memberPaidSummarys = memberPaidSummaryRepo.findAll(param.buildSpecification());
			memberIds = memberPaidSummarys.stream().map(p -> p.getId()).collect(Collectors.toList());
		} else if (Constant.快照参数_有效邀请人数.equals(fieldName)) {
			MemberInvitedCountQueryCondParam param = new MemberInvitedCountQueryCondParam();
			param.setInvitedCountCondition(cond);
			param.setInvitedCountValue(fieldValue);
			List<MemberInvitedCount> memberInvitedCounts = memberInvitedCountRepo.findAll(param.buildSpecification());
			memberIds = memberInvitedCounts.stream().map(p -> p.getId()).collect(Collectors.toList());
		} else if (fieldName.startsWith(Constant.快照参数_持有艺术品)) {
			MemberHoldArtworkSummaryQueryCondParam param = new MemberHoldArtworkSummaryQueryCondParam();
			param.setArtworkId(fieldName.split("_")[1]);
			param.setCondition(cond);
			param.setArtworkCount(fieldValue);
			List<MemberHoldArtworkSummary> memberHoldArtworkSummarys = memberHoldArtworkSummaryRepo
					.findAll(param.buildSpecification());
			memberIds = memberHoldArtworkSummarys.stream().map(p -> p.getMemberId()).collect(Collectors.toList());
		}
		return memberIds;
	}

	@Transactional(readOnly = true)
	public InviteInfoVO getInviteInfo(@NotBlank String memberId) {
		String h5Gateway = systemSettingRepo.findTopByOrderByLatelyUpdateTime().getH5Gateway();
		Member member = memberRepo.getOne(memberId);
		return InviteInfoVO.build(member.getInviteCode(), h5Gateway);
	}

	/**
	 * 短信验证码修改支付密码
	 * @param memberId
	 */
	@Lock(keys = "'sendModifyPayPwdVerificationCode' + #memberId")
	@Transactional
	public void sendModifyPayPwdVerificationCode(@NotBlank String memberId) {
		Member member = memberRepo.getOne(memberId);
		sendVerificationCode(member.getMobile(), Constant.短信类型_验证码_修改支付密码);
	}

	/**
	 * 短信验证码登录
	 * @param mobile
	 * @return
	 */
	@Lock(keys = "'sendLoginVerificationCode' + #mobile")
	@Transactional
	public SendSmsResponse sendLoginVerificationCode(@NotBlank String mobile) {
		return sendVerificationCode(mobile, Constant.短信类型_验证码_登录);
	}

	/**
	 * 设置登录密码
	 * @param mobile
	 * @param pwd
	 */
	@Lock(keys = "'settingLoginPwd' + #mobile")
	@Transactional
	public void settingLoginPwd(@NotBlank String mobile, @NotBlank String pwd) {
		settingPwd(mobile, pwd);
	}
	public void settingPwd(@NotBlank String mobile, @NotBlank String pwd) {
		Member newAccount = memberRepo.findByMobileAndDeletedFlagIsFalse(mobile);
		if(newAccount == null){
			throw new BizException("请先注册登录");
		}else {
			String loginPwd = SaSecureUtil.sha256(pwd);
			newAccount.setLoginPwd(loginPwd);
			memberRepo.save(newAccount);
		}

	}
	/**
	 * 注册账号
	 * @param mobile
	 * @param inviteCode
	 */
	@Lock(keys = "'registerAccount' + #mobile")
	@Transactional
	public void registerAccount(@NotBlank String mobile, String inviteCode) {
		Member newAccount = memberRepo.findByMobileAndDeletedFlagIsFalse(mobile);
		if (newAccount == null) {
			newAccount = Member.build("藏家_" + RandomUtil.randomString(9), mobile);
			if (StrUtil.isNotBlank(inviteCode)) {
				Member inviter = memberRepo.findByInviteCodeAndDeletedFlagIsFalse(inviteCode);
				if (inviter != null) {
					newAccount.updateInviteInfo(inviter);
				}
			}
			memberRepo.save(newAccount);

			addBalance(newAccount.getId(), 5000d, null);
		}
	}

	@Transactional
	public SendSmsResponse sendVerificationCode(@NotBlank String mobile, @NotBlank String type) {
		SendSmsResponse res= null;
		try {
			SmsClient client = sendSmsClient.getClient();
			/**
			 * 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
			 *              * 您可以直接查询SDK源码确定接口有哪些属性可以设置
			 *              * 属性可能是基本类型，也可能引用了另一个数据结构
			 *              * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明
			 */
			SendSmsRequest req = new SendSmsRequest();
			/* 填充请求参数,这里request对象的成员变量即对应接口的入参
			 * 您可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
			 * 基本类型的设置:
			 * 帮助链接：
			 * 短信控制台: https://console.cloud.tencent.com/smsv2
			 * 腾讯云短信小助手: https://cloud.tencent.com/document/product/382/3773#.E6.8A.80.E6.9C.AF.E4.BA.A4.E6.B5.81 */

			/* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
			// 应用 ID 可前往 [短信控制台](https://console.cloud.tencent.com/smsv2/app-manage) 查看
			//String sdkAppId = "1400963771";
			req.setSmsSdkAppId(sdkAppId);

			/* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名 */
			// 签名信息可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-sign) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-sign) 的签名管理查看
			req.setSignName(signName);

			/* 模板 ID: 必须填写已审核通过的模板 ID */
			// 模板 ID 可前往 [国内短信](https://console.cloud.tencent.com/smsv2/csms-template) 或 [国际/港澳台短信](https://console.cloud.tencent.com/smsv2/isms-template) 的正文模板管理查看
			req.setTemplateId(templateId);

			/* 模板参数: 模板参数的个数需要与 TemplateId 对应模板的变量个数保持一致，若无模板参数，则设置为空 */
			//随机六位数
			String smsCodeGet = redisTemplate.opsForValue().get(Constant.限制 + type + mobile);
			if (StrUtil.isNotBlank(smsCodeGet)) {
				throw new BizException("请不要频繁获取验证码");
			}
			redisTemplate.opsForValue().set(Constant.限制 + type + mobile, "1", 60, TimeUnit.SECONDS);

			double mathRandom = (Math.random() * 9 + 1) * (Math.pow(10, 5));
			int numberCode = (int)mathRandom;
			String code = String.valueOf(numberCode);
			String verificationCode = Constant.短信类型_验证码_登录 + mobile;
			redisTemplate.opsForValue().set(verificationCode, code,  5, TimeUnit.MINUTES);
			String[] templateParamSet = {code};
			req.setTemplateParamSet(templateParamSet);

			/* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
			 * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
			String phoneNumSub = phoneNumSub(mobile);
			String[] phoneNumberSet = {phoneNumSub};
			req.setPhoneNumberSet(phoneNumberSet);

			/* 用户的 session 内容（无需要可忽略）: 可以携带用户侧 ID 等上下文信息，server 会原样返回 */
			String sessionContext = "";
			req.setSessionContext(sessionContext);

			/* 短信码号扩展号（无需要可忽略）: 默认未开通，如需开通请联系 [腾讯云短信小助手] */
			String extendCode = "";
			req.setExtendCode(extendCode);

			/* 国内短信无需填写该项；国际/港澳台短信已申请独立 SenderId 需要填写该字段，默认使用公共 SenderId，无需填写该字段。注：月度使用量达到指定量级可申请独立 SenderId 使用，详情请联系 [腾讯云短信小助手](https://cloud.tencent.com/document/product/382/3773#.E6.8A.80.E6.9C.AF.E4.BA.A4.E6.B5.81)。*/
			String senderid = "";
			req.setSenderId(senderid);

			/* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
			 * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
			res = client.SendSms(req);

			// 输出json格式的字符串回包
			System.out.println(SendSmsResponse.toJsonString(res));

			// 也可以取出单个值，您可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
			// System.out.println(res.getRequestId());

			SmsSendRecord smsSendRecord = SmsSendRecord.build(mobile, type, verificationCode);
			smsSendRecordRepo.save(smsSendRecord);

			ThreadPoolUtils.getSendSmsPool().schedule(() -> {
				redissonClient.getTopic(Constant.发送短信).publish(smsSendRecord.getId());
			}, 1, TimeUnit.SECONDS);
		} catch (TencentCloudSDKException | IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	@Transactional(readOnly = true)
	public MemberStatisticDataVO getMemberStatisticData() {
		List<Member> result = memberRepo.findAll(new MemberQueryCondParam().buildSpecification(),
				Sort.by(Sort.Order.desc("registeredTime")));
		MemberStatisticDataVO vo = new MemberStatisticDataVO();
		String today = DateUtil.today();
		for (Member data : result) {
			String createDate = DateUtil.format(data.getRegisteredTime(), DatePattern.NORM_DATE_PATTERN);
			if (createDate.equals(today)) {
				vo.setTodayRegisterCount(vo.getTodayRegisterCount() + 1);
			}
			if (data.getBindRealNameTime() != null) {
				vo.setRealNameCount(vo.getRealNameCount() + 1);
			}
		}
		vo.setAccountCount(result.size());
		return vo;
	}

	@Transactional(readOnly = true)
	public MemberFundInfoVO getMemberFundInfo() {
		List<Member> result = memberRepo.findAll(new MemberQueryCondParam().buildSpecification(),
				Sort.by(Sort.Order.desc("registeredTime")));
		return MemberFundInfoVO.convertFor(result);
	}

	@Transactional
	public void updateKeepLoginDuration(@NotBlank String memberId,
			@NotNull @DecimalMin(value = "0", inclusive = false) Integer keepLoginDuration) {
		Member member = memberRepo.getOne(memberId);
		member.setKeepLoginDuration(keepLoginDuration);
		memberRepo.save(member);
	}

	@Transactional(readOnly = true)
	public MyPersonalInfoVO getMyPersonalInfo(@NotBlank String memberId) {
		return MyPersonalInfoVO.convertFor(memberRepo.getOne(memberId));
	}

	@Transactional(readOnly = true)
	public Double getBalance(String accountId) {
		Member member = memberRepo.getOne(accountId);
		return member.getBalance();
	}

	@Transactional
	public void bindRealName(@Valid BindRealNameParam param) {
		Member member = memberRepo.getOne(param.getMemberId());
		if (member.getBindRealNameTime() != null) {
			throw new BizException("该账号已完成实名");
		}
		member.bindRealName(param.getRealName(), param.getIdentityCard());
		memberRepo.save(member);

		ThreadPoolUtils.getBlockChainAddrPool().schedule(() -> {
			redissonClient.getTopic(Constant.创建区块链地址).publish(member.getId());
		}, 1, TimeUnit.SECONDS);
	}

	@Transactional
	public void updateNickName(@NotBlank String memberId, @NotBlank String nickName) {
		Member member = memberRepo.getOne(memberId);
		member.setNickName(nickName);
		memberRepo.save(member);
	}

	@Transactional
	public void updateAvatar(@NotBlank String memberId, @NotBlank String avatar) {
		Member member = memberRepo.getOne(memberId);
		member.setAvatar(avatar);
		memberRepo.save(member);
	}

	@Transactional
	public void updateLatelyLoginTime(String id) {
		Member member = memberRepo.getOne(id);
		member.setLatelyLoginTime(new Date());
		memberRepo.save(member);
	}

	@Transactional
	public void updateMember(@Valid UpdateMemberParam param) {
		Member existAccount = memberRepo.findByMobileAndDeletedFlagIsFalse(param.getMobile());
		if (existAccount != null && !existAccount.getId().equals(param.getId())) {
			throw new BizException("手机号已存在");
		}
		Member member = memberRepo.getOne(param.getId());
		BeanUtils.copyProperties(param, member);
		memberRepo.save(member);
	}

	@Transactional(readOnly = true)
	public MemberVO findMemberById(@NotBlank String accountId) {
		Member member = memberRepo.getOne(accountId);
		return MemberVO.convertFor(member);
	}

	@Transactional(readOnly = true)
	public List<MyInviteRecordVO> findMyInviteRecord(@NotBlank String inviterId) {
		MemberQueryCondParam param = new MemberQueryCondParam();
		param.setInviterId(inviterId);
		List<Member> result = memberRepo.findAll(param.buildSpecification(),
				Sort.by(Sort.Order.desc("registeredTime")));
		return MyInviteRecordVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<MemberVO> findMemberByPage(@Valid MemberQueryCondParam param) {
		Page<Member> result = memberRepo.findAll(param.buildSpecification(), PageRequest.of(param.getPageNum() - 1,
				param.getPageSize(), Sort.by(Sort.Order.desc("registeredTime"))));
		PageResult<MemberVO> pageResult = new PageResult<>(MemberVO.convertFor(result.getContent()), param.getPageNum(),
				param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void modifyPayPwd(@Valid ModifyPayPwdParam param) {
		Member member = memberRepo.getOne(param.getMemberId());
		String verificationCode = redisTemplate.opsForValue().get(Constant.短信类型_验证码_修改支付密码 + member.getMobile());
		if (!param.getVerificationCode().equals(verificationCode)) {
			throw new BizException("验证码不正确");
		}
		modifyPayPwd(param.getMemberId(), param.getNewPwd());
	}

	@Transactional
	public void modifyPayPwd(@NotBlank String accountId, @NotBlank String newPwd) {
		Member member = memberRepo.getOne(accountId);
		member.setPayPwd(SaSecureUtil.sha256(newPwd));
		memberRepo.save(member);
	}

	@Transactional(readOnly = true)
	public AccountAuthInfoVO getAccountAuthInfo(@NotBlank String mobile) {
		Member member = memberRepo.findByMobileAndDeletedFlagIsFalse(mobile);
		return AccountAuthInfoVO.convertFor(member);
	}

	@Transactional
	public void addMember(@Valid AddMemberParam param) {
		addMemberInner(param.getNickName(), param.getMobile());
	}

	@Lock(keys = "'addMember' + #mobile")
	public void addMemberInner(String nickName, String mobile) {
		Member existAccount = memberRepo.findByMobileAndDeletedFlagIsFalse(mobile);
		if (existAccount != null) {
			throw new BizException("手机号已存在");
		}
		Member newMember = Member.build(nickName, mobile);
		memberRepo.save(newMember);
	}

	@Transactional
	public void delMember(@NotBlank String accountId) {
		Member member = memberRepo.getOne(accountId);
		member.deleted();
		memberRepo.save(member);
	}

	@Transactional
	public void addBalance(@NotBlank String id, @NotNull @DecimalMin(value = "0", inclusive = true) Double amount,
			String note) {
		Member member = memberRepo.getOne(id);
		double balanceAfter = NumberUtil.round(member.getBalance() + amount, 2).doubleValue();
		member.setBalance(balanceAfter);
		memberRepo.save(member);

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithSystem(member, amount, note));
	}

	@Transactional
	public void reduceBalance(@NotBlank String id, @NotNull @DecimalMin(value = "0", inclusive = true) Double amount) {
		Member member = memberRepo.getOne(id);
		double balanceAfter = NumberUtil.round(member.getBalance() - amount, 2).doubleValue();
		if (balanceAfter < 0) {
			throw new BizException("余额不能少于0");
		}
		member.setBalance(balanceAfter);
		memberRepo.save(member);

		memberBalanceChangeLogRepo.save(MemberBalanceChangeLog.buildWithSystem(member, -amount, null));
	}
	public String phoneNumSub(String phoneNum){
		String sub = phoneNum.substring(0, 2);
		if(!"+86".equals(sub)){
			phoneNum = "+86"+phoneNum;
		}
		return phoneNum;
	}

}
