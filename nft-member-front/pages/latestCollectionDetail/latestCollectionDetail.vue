<template>
	<view class="page-content">
		<u-popup v-model="showPayModalFlag" mode="bottom" border-radius="14" :closeable="true">
			<view class="pay-modal">
				<view class="pay-modal-amount">
					<text>￥</text><text class="pay-modal-amount-value">{{moneyFormat(collectionDetail.price)}}</text>
				</view>
				<view class="pay-modal-pay-way-tip">选择支付方式</view>
				<view class="pay-modal-pay-ways">
					<view class="pay-modal-pay-way">
						<view class="pay-modal-pay-way-label">余额</view>
						<view>
							<u-icon name="checkmark-circle-fill" color="#2979ff" size="36"></u-icon>
						</view>
					</view>
				</view>
				<view>
					<u-button class="custom-pay-modal-btn" type="primary" @click="confirmPay">确认付款</u-button>
				</view>
			</view>
		</u-popup>
		<view class="collection-cover">
			<image style="width: 100%;" mode="widthFix" :src="collectionDetail.cover">
			</image>
		</view>
		<view class="chain-tip">
			<image style="width: 36rpx; height: 30rpx;" src="/static/img/chain_icon.png">
			</image>
			官方授权 • 限量发行
		</view>
		<view class="collection-name">
			<view class="collection-name-inner">{{collectionDetail.name}}</view>
			<view class="limit-block">
				<view class="limit">限量</view>
				<view class="quantity">{{collectionDetail.quantity}}份</view>
			</view>
		</view>
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
		<view v-show="collectionDetail.subCommoditys && collectionDetail.subCommoditys.length > 0">
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
		<view class="creator-story">
			<view class="story-title">作品故事</view>
			<view class="story-content">
				<image style="width: 100%;" mode="widthFix" v-for="storyPicLink in collectionDetail.storyPicLinks"
					:src="storyPicLink">
				</image>
			</view>
		</view>
		<view class="buy-instructions" v-show="collectionDetail.commodityType == '2'">
			<view class="buy-instructions-title">关于盲盒</view>
			<view class="buy-instructions-content">
				<view>盲盒是平台基于精选IP所推出的数字藏品新玩法，每个盲盒内都有一个或多个数字藏品。</view>
				<view>所有藏品都是通过区块链技术发行，盲盒的抽取也是完全随机，在盲盒没有打开之前，没有人可以知道盲盒里的数字藏品是什么。</view>
				<view>每一期盲盒都会设置不同级别的数字藏品，让用户充分感受开盲盒的乐趣。</view>
			</view>
		</view>
		<view class="buy-instructions">
			<view class="buy-instructions-title">关于数字藏品</view>
			<view class="buy-instructions-content">
				数字藏品为虚拟数字商品，而非实物，仅限实名认证为年满14周岁的中国大陆用户购买。数字藏品的版权由发行方或原创者拥有，除另外取得版权拥有者书面同意外，用户不得将数字藏品用于任何商业用途。本商品一经售出，不支持退换。本商品源文件不支持本地下载。请勿对数字藏品进行炒作、场外交易、欺诈、或以任何其他非法方式进行使用。
			</view>
		</view>
		<view class="fixed-bottom">
			<view class="fixed-bottom-content">
				<view class="price-info">
					<view>￥</view>
					<view>{{moneyFormat(collectionDetail.price)}}</view>
				</view>
				<view>
					<view class="sell-out" v-show="collectionDetail.stock == 0">已售罄</view>
					<view v-show="collectionDetail.stock > 0">
						<view v-show="!hasPreSale">
							<view class="buy-now" v-show="collectionDetail.surplusSecond == 0"
								@click="latestCollectionCreateOrder">立即购买
							</view>
							<view class="future-sale" v-show="collectionDetail.surplusSecond > 86400">
								<view class="future-sale1">敬请期待</view>
								<view class="future-sale2">
									{{collectionDetail.saleTime}}
								</view>
							</view>
							<view class="for-sale"
								v-show="collectionDetail.surplusSecond > 0 && collectionDetail.surplusSecond <= 86400">
								<view class="for-sale1">即将开售</view>
								<view class="for-sale2">
									<u-count-down :show-days="false" color="white" separator-size="26"
										separator-color="white" bg-color="#4c4c4c" font-size="26"
										:timestamp="collectionDetail.surplusSecond">
									</u-count-down>
								</view>
							</view>
						</view>
						<view v-show="hasPreSale">
							<view class="buy-now" @click="latestCollectionCreateOrder">优先购买
							</view>
						</view>
					</view>
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
				showPayModalFlag: false,
				orderId: '',
				hasPreSale: false,
			}
		},
		onLoad(option) {
			this.collectionId = option.id;
			this.getCollectionDetail();
		},
		methods: {

			isLogin() {
				var tokenName = uni.getStorageSync('tokenName');
				return tokenName != null && tokenName != '';
			},

			checkHasPreSale() {
				var that = this;
				this.$u.get('/transaction/checkHasPreSale', {
					collectionId: that.collectionId
				}).then(res => {
					that.hasPreSale = res.data;
					console.log(that.hasPreSale);
				});
			},

			creatorPage() {
				uni.navigateTo({
					url: '../creator/creator?id=' + this.collectionDetail.creatorId
				});
			},

			latestCollectionCreateOrder() {
				var that = this;
				this.$u.post('/transaction/latestCollectionCreateOrder', {
					collectionId: that.collectionId,
				}).then(res => {
					that.orderId = res.data;
					that.showPayModalFlag = true;
				});
			},

			confirmPay() {
				var that = this;
				this.$u.post('/transaction/confirmPay', {
					orderId: that.orderId,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '购买成功!',
						duration: 2000,
						mask: true,
						complete: function() {
							setTimeout(() => {
								uni.reLaunch({
									url: "../my/my?tab=" + (that.collectionDetail
										.commodityType == '1' ? '0' : '1')
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
				this.$u.get('/collection/findLatestCollectionDetailById', {
					id: that.collectionId
				}).then(res => {
					that.collectionDetail = res.data;
					uni.setNavigationBarTitle({
						title: that.collectionDetail.name
					});
					if (that.collectionDetail.preSaleFlag && that.isLogin()) {
						that.checkHasPreSale();
					}
				});
			},
		}
	}
</script>

<style>
	.div-line {
		width: 100%;
		height: 20rpx;
		background: #f0f0f0;
	}

	.collection-cover {}

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
		padding-left: 12rpx;
		padding-right: 12rpx;
		padding-top: 12rpx;
		padding-bottom: 36rpx;
	}

	.buy-instructions-title {
		color: #353535;
		text-align: center;
		line-height: 3;
	}

	.buy-instructions-content {
		color: #909399;
		font-size: small;
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

	.creator-story {}

	.creator-info {
		display: flex;
		color: #888;
		align-items: center;
		padding-bottom: 16rpx;
		padding-left: 16rpx;
		padding-right: 16rpx;
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

	.buy-now {
		background: #353535;
		font-size: large;
		font-weight: bold;
		color: white;
		padding: 20rpx 60rpx;
	}

	.sell-out {
		background: #c9c9c9;
		font-size: large;
		font-weight: bold;
		color: white;
		padding: 20rpx 60rpx;
	}

	.for-sale {
		background: #4c4c4c;
		color: white;
		padding: 4rpx 60rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}

	.for-sale1 {
		font-size: large;
	}

	.for-sale2 {
		line-height: 0;
	}

	.future-sale {
		background: #4c4c4c;
		color: white;
		padding: 2rpx 60rpx;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}

	.future-sale1 {
		font-size: large;
	}

	.future-sale2 {}

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
		background: #ffffff;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.collection-info {
		display: flex;
		justify-content: space-around;
	}

	.collection-info-sub {}

	.chain-tip {
		padding-left: 16rpx;
		font-size: smaller;
		color: #888;
		line-height: 2;
		display: flex;
		align-items: center;
	}

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
		padding-right: 16rpx;
		flex: 1;
	}

	.limit-block {
		display: flex;
		align-items: center;
	}

	.limit {
		font-size: small;
		padding-left: 10rpx;
		padding-right: 10rpx;
		background: #f9c371f2;
		color: #767272;
	}

	.quantity {
		font-size: small;
		padding-left: 10rpx;
		padding-right: 10rpx;
		background: #7c7c7c;
		color: #f9c371f2;
	}

	.page-content {
		padding-bottom: 140rpx;
	}
</style>
