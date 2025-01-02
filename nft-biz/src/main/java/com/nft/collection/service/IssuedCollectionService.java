package com.nft.collection.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nft.collection.domain.Collection;
import com.nft.collection.domain.IssuedCollection;
import com.nft.collection.domain.IssuedCollectionActionLog;
import com.nft.collection.domain.MemberHoldCollection;
import com.nft.collection.domain.PayOrder;
import com.nft.collection.repo.CollectionRepo;
import com.nft.collection.repo.IssuedCollectionActionLogRepo;
import com.nft.collection.repo.IssuedCollectionRepo;
import com.nft.collection.repo.MemberHoldCollectionRepo;
import com.nft.collection.vo.IssueResultVO;
import com.nft.common.utils.ThreadPoolUtils;
import com.nft.constants.Constant;
import com.nft.member.domain.Member;

@Validated
@Service
public class IssuedCollectionService {

	@Autowired
	private RedissonClient redissonClient;

	@Autowired
	private CollectionRepo collectionRepo;

	@Autowired
	private IssuedCollectionRepo issuedCollectionRepo;

	@Autowired
	private IssuedCollectionActionLogRepo issuedCollectionActionLogRepo;

	@Autowired
	private MemberHoldCollectionRepo memberHoldCollectionRepo;

	@Transactional
	public void manualDestroy(String id) {
		MemberHoldCollection holdCollection = memberHoldCollectionRepo.getOne(id);
		holdCollection.manualDestroy();
		memberHoldCollectionRepo.save(holdCollection);

		IssuedCollection mysteryBoxIssuedCollection = holdCollection.getIssuedCollection();
		mysteryBoxIssuedCollection.deleted();
		issuedCollectionRepo.save(mysteryBoxIssuedCollection);

		issuedCollectionActionLogRepo.save(IssuedCollectionActionLog
				.buildWithManualDestroy(holdCollection.getMemberId(), holdCollection.getIssuedCollectionId()));

		redissonClient.getTopic(Constant.上链_销毁艺术品).publish(holdCollection.getId());
	}

	@Transactional
	public void openMysteryBoxDestroy(MemberHoldCollection holdCollection) {
		holdCollection.openMysteryBoxDestroy();
		memberHoldCollectionRepo.save(holdCollection);

		IssuedCollection mysteryBoxIssuedCollection = holdCollection.getIssuedCollection();
		mysteryBoxIssuedCollection.deleted();
		issuedCollectionRepo.save(mysteryBoxIssuedCollection);

		issuedCollectionActionLogRepo.save(IssuedCollectionActionLog
				.buildWithOpenMysteryBox(holdCollection.getMemberId(), holdCollection.getIssuedCollectionId()));

		redissonClient.getTopic(Constant.上链_销毁艺术品).publish(holdCollection.getId());

	}

	@Transactional
	public void composeDestroy(MemberHoldCollection holdCollection) {
		holdCollection.composeDestroy();
		memberHoldCollectionRepo.save(holdCollection);

		IssuedCollection issuedCollection = holdCollection.getIssuedCollection();
		issuedCollection.deleted();
		issuedCollectionRepo.save(issuedCollection);

		issuedCollectionActionLogRepo.save(IssuedCollectionActionLog
				.buildWithComposeDestroy(holdCollection.getMemberId(), holdCollection.getIssuedCollectionId()));

		redissonClient.getTopic(Constant.上链_销毁艺术品).publish(holdCollection.getId());
	}

	@Transactional
	public void resale(MemberHoldCollection sellerHoldCollection, Member buyer, PayOrder payOrder) {
		sellerHoldCollection.sold();
		memberHoldCollectionRepo.save(sellerHoldCollection);

		MemberHoldCollection buyerHoldCollection = sellerHoldCollection.buildWithBuyerHold(buyer.getId(),
				payOrder.getAmount());
		buyerHoldCollection.setPreId(sellerHoldCollection.getId());
		memberHoldCollectionRepo.save(buyerHoldCollection);

		issuedCollectionActionLogRepo.save(IssuedCollectionActionLog.buildWithBuy(buyer.getId(),
				sellerHoldCollection.getIssuedCollectionId(), payOrder.getAmount()));

		ThreadPoolUtils.getSyncChainPool().schedule(() -> {
			redissonClient.getTopic(Constant.上链_二级市场购买艺术品).publish(buyerHoldCollection.getId());
		}, 1, TimeUnit.SECONDS);
	}

	@Transactional
	public void give(MemberHoldCollection holdCollection, Member giveTo) {
		holdCollection.give();
		memberHoldCollectionRepo.save(holdCollection);

		MemberHoldCollection receiveCollection = holdCollection.buildWithGive(giveTo.getId());
		receiveCollection.setPreId(holdCollection.getId());
		memberHoldCollectionRepo.save(receiveCollection);

		issuedCollectionActionLogRepo.save(
				IssuedCollectionActionLog.buildWithReceive(giveTo.getId(), holdCollection.getIssuedCollectionId()));

		ThreadPoolUtils.getSyncChainPool().schedule(() -> {
			redissonClient.getTopic(Constant.上链_转让艺术品).publish(receiveCollection.getId());
		}, 1, TimeUnit.SECONDS);
	}

	@Transactional
	public IssueResultVO issue(Collection collection, String memberId, String gainWay) {
		collection.setStock(collection.getStock() - 1);
		collectionRepo.save(collection);

		IssuedCollection issuedCollection = collection.issue();
		issuedCollectionRepo.save(issuedCollection);

		issuedCollectionActionLogRepo.save(IssuedCollectionActionLog.buildWithIssue(memberId, issuedCollection.getId(),
				gainWay, collection.getPrice()));

		MemberHoldCollection memberHoldCollection = issuedCollection.firstIssueToMember(memberId, collection.getPrice(),
				gainWay);
		memberHoldCollectionRepo.save(memberHoldCollection);

		ThreadPoolUtils.getSyncChainPool().schedule(() -> {
			redissonClient.getTopic(Constant.上链_铸造艺术品).publish(memberHoldCollection.getId());
		}, 1, TimeUnit.SECONDS);

		return IssueResultVO.builder().issuedCollectionId(issuedCollection.getId())
				.memberHoldCollectionId(memberHoldCollection.getId()).build();
	}

}
