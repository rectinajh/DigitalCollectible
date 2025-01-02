<template>
	<view class="page-content">
		<u-modal v-model="showCancelResaleModalFlag" title="提示" :show-cancel-button="true" cancel-text="暂时不要"
			confirm-text="取消发布" content="是否要取消发布" @confirm="cancelResale">
		</u-modal>
		<view class="collection-cover">
			<image style="width: 100%;" mode="widthFix" :src="collectionDetail.collectionCover">
			</image>
		</view>
		<view class="collection-name">
			<view class="collection-name-inner">{{collectionDetail.collectionName}}</view>
			<view class="collection-number"># {{collectionDetail.collectionSerialNumber}}/{{collectionDetail.quantity}}
			</view>
		</view>
		<view class="holder-info">
			<view>
				<u-image class="holder-avatar" width="72rpx" height="72rpx" shape="circle" :src="getHolderAvatar()">
				</u-image>
			</view>
			<view class="holder-info-r">
				<view class="holder-info-rt"><text class="holder-nick-name">{{collectionDetail.holderNickName}}</text>
					<u-tag text="持有者" type="primary" size="mini" mode="dark"></u-tag>
				</view>
				<view class="holder-info-rb u-line-1">{{collectionDetail.holderBlockChainAddr}}</view>
			</view>
		</view>
		<u-line color="#cecece"></u-line>
		<view class="creator-info">
			<view>
				<u-image class="creator-avatar" width="72rpx" height="72rpx" shape="circle"
					:src="collectionDetail.creatorAvatar">
				</u-image>
			</view>
			<view class="creator-info-r">
				<view class="creator-info-rt">创作者</view>
				<view class="creator-info-rb u-line-1">{{collectionDetail.creatorName}}</view>
			</view>
			<view class="creator-info-r2" @click="creatorPage">更多作品<u-icon name="arrow-right" size="22"></u-icon>
			</view>
		</view>
		<view class="div-line"></view>
		<view class="info-block">
			<view class="tech-info">
				<view class="tech-title">技术信息</view>
				<view class="tech-info-content">
					<view class="tech-info-item">
						<text class="tech-info-item-label">唯一标识</text><text
							class="tech-info-item-value">{{collectionDetail.uniqueId ? collectionDetail.uniqueId : '上链确认中'}}</text>
					</view>
					<view class="tech-info-item">
						<text class="tech-info-item-label">作品HASH</text><text
							class="tech-info-item-value">{{collectionDetail.collectionHash}}</text>
					</view>
					<view class="tech-info-item">
						<text class="tech-info-item-label">交易HASH</text><text
							class="tech-info-item-value">{{collectionDetail.transactionHash ? collectionDetail.transactionHash : '上链确认中'}}</text>
					</view>
					<!-- <view class="tech-info-item">
						<text class="tech-info-item-label">区块链</text><text class="tech-info-item-value">区块链®</text>
					</view> -->
				</view>
			</view>
			<view v-show="collectionDetail.commodityType == '2'">
				<view class="div-line"></view>
				<view class="mystery-box">
					<view class="mystery-box-title">
						<view class="mystery-box-title-l">作品描述</view>
						<view class="mystery-box-title-r">您可能抽到以下作品中的<text>1</text>个</view>
					</view>
					<view class="mystery-box-commoditys">
						<view class="mystery-box-commodity" v-for="subCommodity in collectionDetail.subCommoditys">
							<view class="mystery-box-commodity-l">
								<u-image width="120rpx" height="120rpx" border-radius="10" :src="subCommodity.cover">
								</u-image>
							</view>
							<view class="mystery-box-commodity-r">
								<view class="mystery-box-commodity-r1 u-line-1">
									{{subCommodity.name}}
								</view>
								<view class="mystery-box-commodity-r2">概率：{{subCommodity.probability}}%</view>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="div-line"></view>
			<view class="story">
				<view class="story-title">作品故事</view>
				<view class="story-content">
					<image style="width: 100%;" mode="widthFix" v-for="storyPicLink in collectionDetail.storyPicLinks"
						:src="storyPicLink">
					</image>
				</view>
			</view>
			<view class="action-log">
				<view class="action-log-l">流转记录</view>
				<view class="action-log-r" @click="issuedCollectionActionLogPage">查看流转记录<u-icon name="arrow-right">
					</u-icon>
				</view>
			</view>
		</view>
		<view class="fixed-bottom">
			<view class="fixed-bottom-content">
				<view class="price-info">
					<view>￥</view>
					<view>{{moneyFormat(collectionDetail.resalePrice)}}</view>
				</view>
				<view>
					<view class="cancel-btn" @click="showCancelResaleModalFlag = true;" v-show="collectionDetail && canCancel()">取消发布</view>
					<view class="lock-btn" v-show="collectionDetail && !canCancel()">商品锁定中，待支付...</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				collectionId: '',
				collectionDetail: '',
				showCancelResaleModalFlag: false,
			}
		},
		onLoad(option) {
			this.collectionId = option.id;
			this.getCollectionDetail();
		},
		methods: {
			
			creatorPage() {
				uni.navigateTo({
					url: '../creator/creator?id=' + this.collectionDetail.creatorId
				});
			},
			
			canCancel() {
				var lockPayMemberId = this.collectionDetail.lockPayMemberId;
				if (lockPayMemberId === null || lockPayMemberId === '') {
					return true;
				}
				return false;
			},

			issuedCollectionActionLogPage() {
				uni.navigateTo({
					url: '../issuedCollectionActionLog/issuedCollectionActionLog?id=' + this.collectionDetail
						.issuedCollectionId
				});
			},

			getHolderAvatar() {
				return this.collectionDetail.holderAvatar ? this.baseUrl + '/storage/fetch/' + this.collectionDetail
					.holderAvatar :
					'/static/img/avatar.png';
			},

			cancelResale() {
				var that = this;
				this.$u.post('/transaction/cancelResale', {
					resaleCollectionId: that.collectionId,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '取消成功!',
						duration: 2000,
						mask: true,
						complete: function() {
							setTimeout(() => {
								uni.reLaunch({
									url: "../my/my"
								});
							}, 2000);
						}
					});
				});
			},

			moneyFormat(money, len) {
				len = len || 2
				if (!money && money !== 0)
					return ''
				if (isNaN(+money))
					return ''
				if (money === 0 || money === '0')
					return '0.' + '0'.repeat(len)
				var arr = (money + '').split('.')
				var intStr = arr[0] ? arr[0] : 0
				var floatStr = arr[1] ? arr[1] : 0
				if (floatStr === 0) {
					floatStr = '0'.repeat(len)
				} else {
					floatStr = (+('0.' + floatStr)).toFixed(len).split('.')[1]
				}
				money = (intStr + '.' + floatStr).replace(/(\d{1,3})(?=(?:\d{3})+\.)/g, `$1,`);
				return money
			},

			getCollectionDetail() {
				var that = this;
				this.$u.get('/myArtwork/findMyResaleCollectionDetail', {
					id: that.collectionId
				}).then(res => {
					that.collectionDetail = res.data;
					uni.setNavigationBarTitle({
						title: that.collectionDetail.collectionName
					});
				});
			},
		}
	}
</script>

<style>
	
	.mystery-box {}
	
	.mystery-box-title {
		padding-left: 16rpx;
		padding-right: 16rpx;
		display: flex;
		justify-content: space-between;
		padding-bottom: 16rpx;
		padding-top: 16rpx;
		align-items: center;
	}
	
	.mystery-box-title-l {
		color: #353535;
		font-size: 36rpx;
	}
	
	.mystery-box-title-r {
		font-size: small;
		color: #888;
	}
	
	.mystery-box-title-r text {
		color: #000000;
	}
	
	.mystery-box-commoditys {}
	
	.mystery-box-commodity {
		display: flex;
		align-items: center;
		padding-left: 20rpx;
		padding-right: 20rpx;
		background: #ebebeb;
		padding-top: 10rpx;
		padding-bottom: 10rpx;
		margin-bottom: 10rpx;
		margin-left: 20rpx;
		margin-right: 20rpx;
		font-size: small;
		border-radius: 20rpx;
	}
	
	.mystery-box-commodity-l {
		flex: 1;
	}
	
	.mystery-box-commodity-r {
		flex: 2.5;
	}
	
	.mystery-box-commodity-r1 {
		line-height: 2;
	}
	
	.mystery-box-commodity-r2 {
		font-size: smaller;
		color: #888;
	}
	.action-log {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 16rpx;
	}

	.action-log-l {
		color: #353535;
		font-size: 36rpx;
	}

	.action-log-r {}

	.div-line {
		width: 100%;
		height: 20rpx;
		background: #f0f0f0;
	}

	.common-modal {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.modal-title {
		display: flex;
		justify-content: space-between;
	}

	.modal-title-txt {
		font-weight: bold;
	}

	.close-modal-txt {
		color: #909399;
	}

	.modal-tip {
		padding-bottom: 32rpx;
	}

	.modal-tip-title {}

	.modal-tip-item {}

	.give-to-account {
		line-height: 6.5;
	}

	.give-to-account text {
		padding-left: 12rpx;
	}

	.limit {
		color: white;
	}

	.tech-info {}

	.tech-title {
		font-size: 36rpx;
		padding-top: 16rpx;
		padding-bottom: 16rpx;
		padding-left: 16rpx;
	}

	.tech-info-content {}

	.tech-info-item {
		display: flex;
		justify-content: space-between;
		line-height: 2;
		font-size: 28rpx;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.tech-info-item-label {
		flex-basis: 70%;
	}

	.tech-info-item-value {
		opacity: 0.6;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		text-align: right;
		color: #000;
	}

	.tech-support-txt {
		padding-left: 10rpx;
	}

	.tech-support {
		display: flex;
		align-items: center;
		color: #888;
		justify-content: center;
		line-height: 3.5;
	}

	.buy-instructions {
		background: rgb(36 35 35 / 60%);
		border-radius: 20rpx;
		padding-left: 12rpx;
		padding-right: 12rpx;
		padding-top: 12rpx;
		padding-bottom: 36rpx;
	}

	.buy-instructions-title {
		color: white;
		text-align: center;
		font-size: large;
		font-weight: bold;
		line-height: 3;
	}

	.buy-instructions-content {
		color: #888;
	}

	.lock-icon {
		padding-right: 6rpx;
	}

	.buy-lock-tip {
		display: flex;
		align-items: center;
		color: #888;
		justify-content: center;
		line-height: 3;
	}

	.buy-lock-tip-content {
		margin-left: 20rpx;
		margin-right: 20rpx;
	}

	.holder-info {
		display: flex;
		color: #888;
		align-items: center;
		padding-bottom: 16rpx;
		padding-left: 16rpx;
		padding-right: 16rpx;
	}

	.holder-info-r {
		padding-left: 20rpx;
		flex: 1;
		width: 0;
	}

	.holder-info-rt {
		font-size: 26rpx;
		display: flex;
		align-items: center;
	}

	.holder-nick-name {
		color: black;
		padding-right: 16rpx;
	}

	.holder-info-rb {
		opacity: 0.6;
		color: #000;
	}

	.creator-info {
		display: flex;
		color: #888;
		align-items: center;
		padding-bottom: 16rpx;
		padding-left: 16rpx;
		padding-right: 16rpx;
		padding-top: 16rpx;
	}

	.creator-info-r {
		padding-left: 20rpx;
		flex: 1;
		width: 0;
		padding-right: 16rpx;
	}

	.creator-info-rt {
		font-size: 26rpx;
	}

	.creator-info-rb {
		color: #353535;
	}

	.creator-info-r2 {
		text-align: right;
		color: #353535;
		font-size: small;
	}

	.story-title {
		color: #353535;
		font-size: 36rpx;
		padding-bottom: 16rpx;
		padding-left: 16rpx;
		padding-top: 16rpx;
	}

	.story-content {
		display: flex;
		flex-direction: column;
	}

	.story-content-pic {}

	.fixed-bottom-navs {
		display: flex;
		justify-content: space-around;
		align-items: center;
	}

	.fixed-bottom-nav {
		color: #222222;
		text-align: center;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.cancel-btn {
		background: red;
		font-size: large;
		font-weight: bold;
		color: white;
		padding: 20rpx 60rpx;
	}
	
	.lock-btn {
		background: #777777;
		font-size: large;
		font-weight: bold;
		color: white;
		padding: 20rpx 60rpx;
	}

	.price-info {
		display: flex;
		color: #353535;
		font-size: large;
		font-weight: bold;
		line-height: 4;
	}

	.price-info view {
		padding-right: 8rpx;
	}

	.fixed-bottom-content {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.fixed-bottom {
		position: fixed;
		bottom: 0rpx;
		left: 0rpx;
		width: 100%;
		background: white;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.collection-info {
		display: flex;
		justify-content: space-around;
		position: relative;
		top: -80rpx;
	}

	.info-block {}

	.collection-info-sub {}

	.collection-name {
		padding-bottom: 16rpx;
		display: flex;
		justify-content: space-between;
		padding-right: 16rpx;
		align-items: center;
	}

	.collection-name-inner {
		color: #4c4c4c;
		font-size: large;
		font-weight: bold;
		padding-left: 16rpx;
		flex: 1;
	}

	.collection-number {}


	.page-content {
		padding-bottom: 140rpx;
	}
</style>
