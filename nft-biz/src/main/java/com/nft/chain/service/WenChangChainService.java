package com.nft.chain.service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.chain.utils.WenChangChainUtils;
import com.nft.collection.domain.Collection;
import com.nft.collection.domain.IssuedCollection;
import com.nft.collection.domain.MemberHoldCollection;
import com.nft.collection.repo.CollectionRepo;
import com.nft.collection.repo.IssuedCollectionRepo;
import com.nft.collection.repo.MemberHoldCollectionRepo;
import com.nft.common.utils.ThreadPoolUtils;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;
import com.nft.member.repo.MemberRepo;
import com.nft.setting.domain.WenChangChainSetting;
import com.nft.setting.repo.WenChangChainSettingRepo;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
public class WenChangChainService implements ChainAbstractService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private WenChangChainSettingRepo wenChangChainSettingRepo;

	@Autowired
	private CollectionRepo collectionRepo;

	@Autowired
	private IssuedCollectionRepo issuedCollectionRepo;

	@Autowired
	private MemberHoldCollectionRepo memberHoldCollectionRepo;

	@Autowired
	private MemberRepo memberRepo;

	@Override
	@Lock(keys = "'syncTransactionHash' + #id")
	@Transactional
	public String syncTransactionHash(String id) {
		MemberHoldCollection memberHoldCollection = memberHoldCollectionRepo.getOne(id);
		if (StrUtil.isNotBlank(memberHoldCollection.getTransactionHash())) {
			return "已上链";
		}
		WenChangChainSetting wenChangChainSetting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();

		Long currentTime = System.currentTimeMillis();
		String path = "/v1beta1/tx/" + id;
		String signature = WenChangChainUtils.signRequest(path, null, null, currentTime,
				wenChangChainSetting.getApiSecret());
		String url = wenChangChainSetting.getApiGateway() + path;
		Map<String, String> headerMap = WenChangChainUtils.buildHeader(signature, currentTime,
				wenChangChainSetting.getApiKey());
		HttpRequest post = HttpRequest.get(url);
		post.addHeaders(headerMap);

		String responseStr = "";
		boolean exceptionFlag = false;
		try {
			responseStr = post.execute().body();
		} catch (Exception e) {
			exceptionFlag = true;
			responseStr = e.getMessage();
			log.error(MessageFormat.format("上链_同步交易HASH接口请求失败,id为{0}", id), e);
		}
		if (exceptionFlag) {
			return responseStr;
		}
		if (!JSONUtil.isTypeJSONObject(responseStr)) {
			return "接口返回非json数据:" + responseStr;
		}
		JSONObject jsonObject = JSONUtil.parseObj(responseStr);
		JSONObject errorJsonObject = jsonObject.getJSONObject("error");
		if (errorJsonObject != null) {
			log.error(MessageFormat.format("上链_同步交易HASH接口异常,id为{0},异常信息：{1}", id, responseStr));
			return "接口异常:" + responseStr;
		}
		JSONObject dataJsonObject = jsonObject.getJSONObject("data");
		String status = dataJsonObject.getStr("status");
		if ("0".equals(status)) {
			return "处理中";
		} else if ("3".equals(status)) {
			return "未处理";
		} else if ("2".equals(status)) {
			String errorMsg = dataJsonObject.getStr("message");
			log.error(MessageFormat.format("上链_同步交易HASH失败,id为{0},异常信息：{1}", id, errorMsg));
			return errorMsg;
		}
		String txHash = dataJsonObject.getStr("tx_hash");

		memberHoldCollection.syncChain(txHash);
		memberHoldCollectionRepo.save(memberHoldCollection);
		return "已上链";
	}

	@Override
	@Lock(keys = "'syncUniqueId' + #id")
	@Transactional
	public String syncUniqueId(String id) {
		MemberHoldCollection memberHoldCollection = memberHoldCollectionRepo.getOne(id);
		IssuedCollection issuedCollection = memberHoldCollection.getIssuedCollection();
		if (StrUtil.isNotBlank(issuedCollection.getUniqueId())) {
			return "已上链";
		}
		WenChangChainSetting wenChangChainSetting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();

		Long currentTime = System.currentTimeMillis();
		String path = "/v1beta1/tx/" + id;
		String signature = WenChangChainUtils.signRequest(path, null, null, currentTime,
				wenChangChainSetting.getApiSecret());
		String url = wenChangChainSetting.getApiGateway() + path;
		Map<String, String> headerMap = WenChangChainUtils.buildHeader(signature, currentTime,
				wenChangChainSetting.getApiKey());
		HttpRequest post = HttpRequest.get(url);
		post.addHeaders(headerMap);

		String responseStr = "";
		boolean exceptionFlag = false;
		try {
			responseStr = post.execute().body();
		} catch (Exception e) {
			exceptionFlag = true;
			responseStr = e.getMessage();
			log.error(MessageFormat.format("上链_同步唯一标识接口请求失败,id为{0}", id), e);
		}
		if (exceptionFlag) {
			return responseStr;
		}
		if (!JSONUtil.isTypeJSONObject(responseStr)) {
			return "接口返回非json数据:" + responseStr;
		}
		JSONObject jsonObject = JSONUtil.parseObj(responseStr);
		JSONObject errorJsonObject = jsonObject.getJSONObject("error");
		if (errorJsonObject != null) {
			log.error(MessageFormat.format("上链_同步唯一标识接口异常,id为{0},异常信息：{1}", id, responseStr));
			return "接口异常:" + responseStr;
		}
		JSONObject dataJsonObject = jsonObject.getJSONObject("data");
		String status = dataJsonObject.getStr("status");
		if ("0".equals(status)) {
			return "处理中";
		} else if ("3".equals(status)) {
			return "未处理";
		} else if ("2".equals(status)) {
			String errorMsg = dataJsonObject.getStr("message");
			log.error(MessageFormat.format("上链_同步唯一标识失败,id为{0},异常信息：{1}", id, errorMsg));
			return errorMsg;
		}
		String nftId = dataJsonObject.getStr("nft_id");
		String txHash = dataJsonObject.getStr("tx_hash");

		issuedCollection.syncChain(nftId);
		issuedCollectionRepo.save(issuedCollection);

		memberHoldCollection.syncChain(txHash);
		memberHoldCollectionRepo.save(memberHoldCollection);
		return "已上链";
	}

	@Override
	@Transactional
	public void transferArtwork(String id) {
		chainTransfer(id);
	}

	@Override
	@Transactional
	public void marketBuyArtwork(String id) {
		chainTransfer(id);
	}

	@Override
	@Lock(keys = "'chainTransfer' + #id")
	@Transactional
	public String chainTransfer(String id) {
		MemberHoldCollection toHoldCollection = memberHoldCollectionRepo.getOne(id);
		if (StrUtil.isNotBlank(toHoldCollection.getTransactionHash())) {
			return "已上链";
		}
		String limitGet = redisTemplate.opsForValue().get(Constant.限制 + Constant.上链_转让艺术品 + id);
		if (StrUtil.isNotBlank(limitGet)) {
			return syncTransactionHash(id);
		}
		WenChangChainSetting wenChangChainSetting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();
		MemberHoldCollection fromHoldCollection = memberHoldCollectionRepo.getOne(toHoldCollection.getPreId());

		Long currentTime = System.currentTimeMillis();
		Map<String, Object> body = new HashMap<>();
		body.put("recipient", toHoldCollection.getMember().getBlockChainAddr());
		body.put("operation_id", toHoldCollection.getId());
		String path = "/v1beta1/nft/nft-transfers/" + "nft" + toHoldCollection.getCollectionId() + "/"
				+ fromHoldCollection.getMember().getBlockChainAddr() + "/"
				+ toHoldCollection.getIssuedCollection().getUniqueId();
		String signature = WenChangChainUtils.signRequest(path, null, body, currentTime,
				wenChangChainSetting.getApiSecret());
		String url = wenChangChainSetting.getApiGateway() + path;
		Map<String, String> headerMap = WenChangChainUtils.buildHeader(signature, currentTime,
				wenChangChainSetting.getApiKey());
		HttpRequest post = HttpRequest.post(url);
		post.addHeaders(headerMap);
		post.body(JSONUtil.toJsonStr(body));

		String responseStr = "";
		boolean exceptionFlag = false;
		try {
			responseStr = post.execute().body();
		} catch (Exception e) {
			exceptionFlag = true;
			responseStr = e.getMessage();
			log.error(MessageFormat.format("上链_转让艺术品接口请求失败,id为{0}", id), e);
		}
		if (exceptionFlag) {
			return responseStr;
		}
		if (!JSONUtil.isTypeJSONObject(responseStr)) {
			return "接口返回非json数据:" + responseStr;
		}
		JSONObject jsonObject = JSONUtil.parseObj(responseStr);
		JSONObject errorJsonObject = jsonObject.getJSONObject("error");
		if (errorJsonObject != null) {
			String errorCode = errorJsonObject.getStr("code");
			if ("DUPLICATE_REQUEST".equals(errorCode)) {
				return syncTransactionHash(id);
			} else {
				log.error(MessageFormat.format("上链_转让艺术品接口异常,id为{0},异常信息：{1}", id, responseStr));
				return "接口异常:" + responseStr;
			}
		}
		redisTemplate.opsForValue().set(Constant.限制 + Constant.上链_转让艺术品 + id, "1", 60, TimeUnit.SECONDS);
		ThreadPoolUtils.getSyncChainPool().schedule(() -> {
			redissonClient.getTopic(Constant.上链_同步交易HASH).publish(toHoldCollection.getId());
		}, 35, TimeUnit.SECONDS);
		return "上链确认中";
	}

	@Override
	@Transactional
	public void destroyArtwork(String id) {
		WenChangChainSetting wenChangChainSetting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();
		MemberHoldCollection memberHoldCollection = memberHoldCollectionRepo.getOne(id);

		Long currentTime = System.currentTimeMillis();
		Map<String, Object> body = new HashMap<>();
		body.put("operation_id", IdUtil.getSnowflake().nextIdStr());
		String path = "/v1beta1/nft/nfts/" + "nft" + memberHoldCollection.getCollectionId() + "/"
				+ memberHoldCollection.getMember().getBlockChainAddr() + "/"
				+ memberHoldCollection.getIssuedCollection().getUniqueId();
		String signature = WenChangChainUtils.signRequest(path, null, body, currentTime,
				wenChangChainSetting.getApiSecret());
		String url = wenChangChainSetting.getApiGateway() + path;
		Map<String, String> headerMap = WenChangChainUtils.buildHeader(signature, currentTime,
				wenChangChainSetting.getApiKey());
		HttpRequest post = HttpRequest.delete(url);
		post.addHeaders(headerMap);
		post.body(JSONUtil.toJsonStr(body));
		String responseStr = post.execute().body();
		System.out.println(responseStr);
	}

	@Override
	@Lock(keys = "'mintArtwork' + #id")
	@Transactional
	public String mintArtwork(String id) {
		MemberHoldCollection memberHoldCollection = memberHoldCollectionRepo.getOne(id);
		IssuedCollection issuedCollection = memberHoldCollection.getIssuedCollection();
		if (StrUtil.isNotBlank(issuedCollection.getUniqueId())) {
			return "已上链";
		}
		String limitGet = redisTemplate.opsForValue().get(Constant.限制 + Constant.上链_铸造艺术品 + id);
		if (StrUtil.isNotBlank(limitGet)) {
			return syncUniqueId(id);
		}
		WenChangChainSetting wenChangChainSetting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();

		Long currentTime = System.currentTimeMillis();
		Map<String, Object> body = new HashMap<>();
		body.put("name", memberHoldCollection.getCollection().getName() + "#"
				+ memberHoldCollection.getIssuedCollection().getCollectionSerialNumber());
		body.put("recipient", memberHoldCollection.getMember().getBlockChainAddr());
		body.put("operation_id", memberHoldCollection.getId());
		String path = "/v1beta1/nft/nfts/" + "nft" + memberHoldCollection.getCollectionId();
		String signature = WenChangChainUtils.signRequest(path, null, body, currentTime,
				wenChangChainSetting.getApiSecret());
		String url = wenChangChainSetting.getApiGateway() + path;

		Map<String, String> headerMap = WenChangChainUtils.buildHeader(signature, currentTime,
				wenChangChainSetting.getApiKey());
		HttpRequest post = HttpRequest.post(url);
		post.addHeaders(headerMap);
		post.body(JSONUtil.toJsonStr(body));

		String responseStr = "";
		boolean exceptionFlag = false;
		try {
			responseStr = post.execute().body();
		} catch (Exception e) {
			exceptionFlag = true;
			responseStr = e.getMessage();
			log.error(MessageFormat.format("上链_铸造艺术品接口请求失败,id为{0}", id), e);
		}
		if (exceptionFlag) {
			return responseStr;
		}
		if (!JSONUtil.isTypeJSONObject(responseStr)) {
			return "接口返回非json数据:" + responseStr;
		}
		JSONObject jsonObject = JSONUtil.parseObj(responseStr);
		JSONObject errorJsonObject = jsonObject.getJSONObject("error");
		if (errorJsonObject != null) {
			String errorCode = errorJsonObject.getStr("code");
			if ("DUPLICATE_REQUEST".equals(errorCode)) {
				return syncUniqueId(id);
			} else {
				log.error(MessageFormat.format("上链_铸造艺术品接口异常,id为{0},异常信息：{1}", id, responseStr));
				return "接口异常:" + responseStr;
			}
		}
		redisTemplate.opsForValue().set(Constant.限制 + Constant.上链_铸造艺术品 + id, "1", 60, TimeUnit.SECONDS);
		ThreadPoolUtils.getSyncChainPool().schedule(() -> {
			redissonClient.getTopic(Constant.上链_同步唯一标识).publish(id);
		}, 35, TimeUnit.SECONDS);
		return "上链确认中";
	}

	@Override
	@Lock(keys = "'createBlockChainAddr' + #id")
	@Transactional
	public String createBlockChainAddr(String id) {
		Member member = memberRepo.getOne(id);
		if (StrUtil.isNotBlank(member.getBlockChainAddr())) {
			return "已上链";
		}
		String limitGet = redisTemplate.opsForValue().get(Constant.限制 + Constant.创建区块链地址 + id);
		if (StrUtil.isNotBlank(limitGet)) {
			return "处理中";
		}
		redisTemplate.opsForValue().set(Constant.限制 + Constant.创建区块链地址 + id, "1", 60, TimeUnit.SECONDS);
		WenChangChainSetting wenChangChainSetting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();

		Long currentTime = System.currentTimeMillis();
		Map<String, Object> body = new HashMap<>();
		body.put("name", id);
		body.put("operation_id", id);
		String path = "/v1beta1/account";
		String signature = WenChangChainUtils.signRequest(path, null, body, currentTime,
				wenChangChainSetting.getApiSecret());
		String url = wenChangChainSetting.getApiGateway() + path;
		Map<String, String> headerMap = WenChangChainUtils.buildHeader(signature, currentTime,
				wenChangChainSetting.getApiKey());
		HttpRequest post = HttpRequest.post(url);
		post.addHeaders(headerMap);
		post.body(JSONUtil.toJsonStr(body));

		String responseStr = "";
		boolean exceptionFlag = false;
		try {
			responseStr = post.execute().body();
		} catch (Exception e) {
			exceptionFlag = true;
			responseStr = e.getMessage();
			log.error(MessageFormat.format("上链_创建区块链地址接口请求失败,id为{0}", id), e);
		}
		if (exceptionFlag) {
			return responseStr;
		}
		if (!JSONUtil.isTypeJSONObject(responseStr)) {
			return "接口返回非json数据:" + responseStr;
		}
		JSONObject jsonObject = JSONUtil.parseObj(responseStr);
		JSONObject errorJsonObject = jsonObject.getJSONObject("error");
		if (errorJsonObject != null) {
			log.error(MessageFormat.format("上链_创建区块链地址接口异常,id为{0},异常信息：{1}", id, responseStr));
			return "接口异常:" + responseStr;
		}
		JSONObject dataJsonObject = jsonObject.getJSONObject("data");
		String blockChainAddr = dataJsonObject.getStr("account");

		member.syncChain(blockChainAddr);
		memberRepo.save(member);
		return "已上链";
	}

	@Override
	@Lock(keys = "'syncArtworkHash' + #id")
	@Transactional
	public String syncArtworkHash(String id) {
		Collection artwork = collectionRepo.getOne(id);
		if (StrUtil.isNotBlank(artwork.getCollectionHash())) {
			return "已上链";
		}
		WenChangChainSetting wenChangChainSetting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();

		Long currentTime = System.currentTimeMillis();
		String path = "/v1beta1/tx/" + id;
		String signature = WenChangChainUtils.signRequest(path, null, null, currentTime,
				wenChangChainSetting.getApiSecret());
		String url = wenChangChainSetting.getApiGateway() + path;
		Map<String, String> headerMap = WenChangChainUtils.buildHeader(signature, currentTime,
				wenChangChainSetting.getApiKey());
		HttpRequest post = HttpRequest.get(url);
		post.addHeaders(headerMap);

		String responseStr = "";
		boolean exceptionFlag = false;
		try {
			responseStr = post.execute().body();
		} catch (Exception e) {
			exceptionFlag = true;
			responseStr = e.getMessage();
			log.error(MessageFormat.format("上链_同步艺术品HASH接口请求失败,id为{0}", id), e);
		}
		if (exceptionFlag) {
			return responseStr;
		}
		if (!JSONUtil.isTypeJSONObject(responseStr)) {
			return "接口返回非json数据:" + responseStr;
		}
		JSONObject jsonObject = JSONUtil.parseObj(responseStr);
		JSONObject errorJsonObject = jsonObject.getJSONObject("error");
		if (errorJsonObject != null) {
			log.error(MessageFormat.format("上链_同步艺术品HASH接口异常,id为{0},异常信息：{1}", id, responseStr));
			return "接口异常:" + responseStr;
		}
		JSONObject dataJsonObject = jsonObject.getJSONObject("data");
		String status = dataJsonObject.getStr("status");
		if ("0".equals(status)) {
			return "处理中";
		} else if ("3".equals(status)) {
			return "未处理";
		} else if ("2".equals(status)) {
			String errorMsg = dataJsonObject.getStr("message");
			log.error(MessageFormat.format("上链_同步艺术品HASH失败,id为{0},异常信息：{1}", id, errorMsg));
			return errorMsg;
		}
		String txHash = dataJsonObject.getStr("tx_hash");

		artwork.syncChain(txHash);
		collectionRepo.save(artwork);
		return "已上链";
	}

	@Override
	@Lock(keys = "'chainAddArtwork' + #id")
	@Transactional
	public String chainAddArtwork(String id) {
		Collection artwork = collectionRepo.getOne(id);
		if (StrUtil.isNotBlank(artwork.getCollectionHash())) {
			return "已上链";
		}
		String limitGet = redisTemplate.opsForValue().get(Constant.限制 + Constant.上链_创建艺术品 + id);
		if (StrUtil.isNotBlank(limitGet)) {
			return syncArtworkHash(id);
		}
		WenChangChainSetting wenChangChainSetting = wenChangChainSettingRepo.findTopByOrderByLatelyUpdateTime();

		Long currentTime = System.currentTimeMillis();
		Map<String, Object> body = new HashMap<>();
		body.put("name", artwork.getName());
		body.put("class_id", "nft" + artwork.getId());
		body.put("owner", wenChangChainSetting.getChainAddrSuper());
		body.put("operation_id", artwork.getId());
		String path = "/v1beta1/nft/classes";
		String signature = WenChangChainUtils.signRequest(path, null, body, currentTime,
				wenChangChainSetting.getApiSecret());
		String url = wenChangChainSetting.getApiGateway() + path;
		Map<String, String> headerMap = WenChangChainUtils.buildHeader(signature, currentTime,
				wenChangChainSetting.getApiKey());
		HttpRequest post = HttpRequest.post(url);
		post.addHeaders(headerMap);
		post.body(JSONUtil.toJsonStr(body));

		String responseStr = "";
		boolean exceptionFlag = false;
		try {
			responseStr = post.execute().body();
		} catch (Exception e) {
			exceptionFlag = true;
			responseStr = e.getMessage();
			log.error(MessageFormat.format("上链_创建艺术品接口请求失败,id为{0}", id), e);
		}
		if (exceptionFlag) {
			return responseStr;
		}
		if (!JSONUtil.isTypeJSONObject(responseStr)) {
			return "接口返回非json数据:" + responseStr;
		}
		JSONObject jsonObject = JSONUtil.parseObj(responseStr);
		JSONObject errorJsonObject = jsonObject.getJSONObject("error");
		if (errorJsonObject != null) {
			String errorCode = errorJsonObject.getStr("code");
			if ("DUPLICATE_REQUEST".equals(errorCode)) {
				return syncArtworkHash(id);
			} else {
				log.error(MessageFormat.format("上链_创建艺术品接口异常,id为{0},异常信息：{1}", id, responseStr));
				return "接口异常:" + responseStr;
			}
		}
		redisTemplate.opsForValue().set(Constant.限制 + Constant.上链_创建艺术品 + id, "1", 60, TimeUnit.SECONDS);
		ThreadPoolUtils.getSyncChainPool().schedule(() -> {
			redissonClient.getTopic(Constant.上链_同步艺术品HASH).publish(id);
		}, 35, TimeUnit.SECONDS);
		return "上链确认中";
	}

}
