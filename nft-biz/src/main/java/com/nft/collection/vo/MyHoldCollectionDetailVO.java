package com.nft.collection.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nft.collection.domain.CollectionStory;
import com.nft.collection.domain.Creator;
import com.nft.collection.domain.IssuedCollection;
import com.nft.collection.domain.MemberHoldCollection;
import com.nft.collection.domain.MysteryBoxCommodity;

import lombok.Data;

@Data
public class MyHoldCollectionDetailVO {

	private String id;

	private String issuedCollectionId;

	private String commodityType;

	private String collectionName;

	private String collectionCover;

	private Integer quantity;

	private Integer collectionSerialNumber;

	private String uniqueId;

	private String collectionHash;

	private String transactionHash;

	private String creatorAvatar;
	
	private String creatorId;

	private String creatorName;

	private String holderNickName;

	private String holderAvatar;

	private String holderBlockChainAddr;

	private List<String> storyPicLinks = new ArrayList<>();

	private List<MysteryBoxCommodityVO> subCommoditys = new ArrayList<>();

	public static MyHoldCollectionDetailVO convertFor(MemberHoldCollection po) {
		if (po == null) {
			return null;
		}
		MyHoldCollectionDetailVO vo = new MyHoldCollectionDetailVO();
		vo.setId(po.getId());
		vo.setIssuedCollectionId(po.getIssuedCollectionId());
		vo.setTransactionHash(po.getTransactionHash());
		if (po.getCollection() != null) {
			vo.setCommodityType(po.getCollection().getCommodityType());
			vo.setCollectionName(po.getCollection().getName());
			vo.setCollectionCover(po.getCollection().getCover());
			vo.setQuantity(po.getCollection().getQuantity());
			vo.setCollectionHash(po.getCollection().getCollectionHash());
			IssuedCollection issuedCollection = po.getIssuedCollection();
			if (issuedCollection != null) {
				vo.setCollectionSerialNumber(issuedCollection.getCollectionSerialNumber());
				vo.setUniqueId(issuedCollection.getUniqueId());
			}
			Creator creator = po.getCollection().getCreator();
			if (creator != null) {
				vo.setCreatorId(creator.getId());
				vo.setCreatorName(creator.getName());
				vo.setCreatorAvatar(creator.getAvatar());
			}
			Set<CollectionStory> collectionStorys = po.getCollection().getCollectionStorys();
			for (CollectionStory collectionStory : collectionStorys) {
				vo.getStoryPicLinks().add(collectionStory.getPicLink());
			}
			Set<MysteryBoxCommodity> mysteryBoxCommoditys = po.getCollection().getSubCommoditys();
			for (MysteryBoxCommodity mysteryBoxCommodity : mysteryBoxCommoditys) {
				vo.getSubCommoditys().add(MysteryBoxCommodityVO.convertFor(mysteryBoxCommodity));
			}
		}
		if (po.getMember() != null) {
			vo.setHolderNickName(po.getMember().getNickName());
			vo.setHolderAvatar(po.getMember().getAvatar());
			vo.setHolderBlockChainAddr(po.getMember().getBlockChainAddr());
		}

		return vo;
	}

}
