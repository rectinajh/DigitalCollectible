<template>
	<view class="page-content">
		<view class="collection-tab-content">
			<view class="latest-collection" v-for="collection in latestCollections"
				@click="latestCollectionDetailPage(collection.id)">
				<view class="state-tip">
					<view class="state-block">
						<view class="sell-out" v-show="collection.stock == 0">
							<u-icon size="26" name="clock"></u-icon>
							<text>已售罄</text>
						</view>
						<view v-show="collection.stock > 0">
							<view class="for-sale" v-show="collection.surplusSecond == 0">
								<u-icon size="26" name="clock"></u-icon>
								<text>抢购中</text>
							</view>
							<view class="future-sale" v-show="collection.surplusSecond > 86400">
								<u-icon size="26" name="clock"></u-icon>
								<text>敬请期待</text>
								<text>{{collection.saleTime}}</text>
								<text>开售</text>
							</view>
							<view class="for-sale"
								v-show="collection.surplusSecond > 0 && collection.surplusSecond <= 86400">
								<u-icon size="26" name="clock"></u-icon>
								<text>即将开售</text>
								<u-count-down :show-days="false" color="#59d195" separator-size="26"
									separator-color="#59d195" bg-color="#000000" font-size="26"
									:timestamp="collection.surplusSecond"></u-count-down>
							</view>
						</view>
					</view>
				</view>
				<image class="collection-cover" style="height: 480rpx; width: 100%;" :src="collection.cover"></image>
				<view class="collection-name">{{collection.name}}</view>
				<view class="quantity-block">
					<view class="limit">限量</view>
					<view class="quantity">{{collection.quantity}}份</view>
				</view>
				<view class="creator-block">
					<view class="creator-info">
						<u-image class="creator-avatar" width="48rpx" height="48rpx" shape="circle"
							:src="collection.creatorAvatar">
						</u-image>
						<view class="creator-name">{{collection.creatorName}}</view>
					</view>
					<view class="price-info">
						<view>￥</view>
						<view>{{moneyFormat(collection.price)}}</view>
					</view>
				</view>
			</view>
			<view class="more-await">更多内容敬请期待</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				latestCollections: [],
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				pageNum: 1,
				noDataFlag: false,
			}
		},
		onLoad() {
			this.findLatestCollectionByPage();
		},
		onShow() {},
		onHide() {},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods: {
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


			refreshData() {
				this.pageNum = 1;
				this.loadingState = 'loading';
				this.findLatestCollectionByPage();
			},

			nextPage() {
				if (this.loadingState == 'nomore') {
					return;
				}
				this.pageNum = this.pageNum + 1;
				this.findLatestCollectionByPage();
			},

			latestCollectionDetailPage(id) {
				uni.navigateTo({
					url: '../latestCollectionDetail/latestCollectionDetail?id=' + id
				});
			},

			findLatestCollectionByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.latestCollections = [];
				}
				var queryParam = {
					pageSize: 5,
					pageNum: that.pageNum
				};
				that.loadingState = 'loading';
				this.$u.get('/collection/findLatestMysteryBoxByPage', queryParam).then(res => {
					var newRecords = res.data.content;
					var totalPage = res.data.totalPage;
					if (that.pullDownRefreshFlag) {
						that.pullDownRefreshFlag = false;
						uni.stopPullDownRefresh();
					}
					if (newRecords.length == 0) {
						that.loadingState = 'nomore';
					}
					if (totalPage == that.pageNum) {
						that.loadingState = 'nomore';
					}
					var oldRecords = that.latestCollections;
					for (var i = 0; i < newRecords.length; i++) {
						var flag = true;
						for (var j = 0; j < oldRecords.length; j++) {
							if (newRecords[i].id == oldRecords[j].id) {
								flag = false;
								break;
							}
						}
						if (flag) {
							oldRecords.push(newRecords[i]);
						}
					}
					that.noDataFlag = oldRecords.length == 0;
				});
			}

		}
	}
</script>

<style>
	page {
		height: 100% !important;
	}

	.more-await {
		color: #797c81;
		text-align: center;
		padding-top: 60rpx;
	}

	.sell-plan {
		color: #baa484;
		font-weight: bold;
		font-size: larger;
		font-style: oblique;
		padding-top: 20rpx;
	}

	.sell-date {
		padding-top: 12rpx;
		font-size: larger;
	}

	.sell-date-value {
		color: #353535;
		font-weight: bold;
		line-height: 2;
	}

	.sell-time {}

	.sell-time-value {
		color: #888;
		font-weight: bold;
		padding-bottom: 8rpx;
		font-size: smaller;
	}


	.sell-collection {
		display: flex;
		align-items: center;
		border-radius: 64rpx;
		margin-bottom: 20rpx;
		background: #e7e7e7;
	}

	.sell-collection-l {
		flex: 1;
	}

	.sell-collection-r {
		flex: 2;
		padding-left: 28rpx;
	}

	.sell-collection-r1 {
		color: #000000;
		line-height: 3;
		width: 400rpx;
	}

	.sell-collection-r2 {
		display: flex;
		font-size: small;
	}

	.sell-collection-r3 {
		color: #353535;
		display: flex;
		line-height: 3;
		font-weight: bold;
	}

	.sell-collection-r3 view {
		padding-right: 4rpx;

	}

	.collection-tab-content {}

	.latest-collection {
		position: relative;
		top: 4rpx;
		background: #e7e7e7;
		border-radius: 20rpx;
		padding-bottom: 20rpx;
		margin-bottom: 24rpx;
	}

	.state-tip {
		position: absolute;
		top: 40rpx;
		z-index: 500;
		left: 32rpx;
	}

	.state-block {
		display: inline-block;
		border-radius: 20rpx;
		background: #000000;
		padding-left: 12rpx;
		padding-right: 12rpx;
		padding-top: 6rpx;
		padding-bottom: 6rpx;
		font-size: small;
	}

	.state-block .u-icon {
		padding-right: 6rpx;
	}

	.sell-out {
		color: #aaa4a4;
	}

	.for-sale {
		color: #59d195;
	}

	.for-sale .u-countdown {
		padding-left: 8rpx;
	}

	.future-sale {
		color: #59d195;
	}

	.future-sale text {
		padding-left: 6rpx;
	}

	.collection-name {
		padding-top: 20rpx;
		padding-bottom: 20rpx;
		font-size: large;
		padding-left: 20rpx;
	}

	.collection-cover {
		border-radius: 20rpx 20rpx 0rpx 0rpx;
	}

	.quantity-block {
		display: flex;
		padding-left: 20rpx;
	}

	.limit {
		font-size: smaller;
		padding-left: 10rpx;
		padding-right: 10rpx;
		background: #f9c371f2;
		color: #767272;
	}

	.quantity {
		font-size: smaller;
		padding-left: 10rpx;
		padding-right: 10rpx;
		background: #7c7c7c;
		color: #f9c371f2;
	}

	.creator-block {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-top: 10rpx;
		padding-left: 20rpx;
	}

	.creator-info {
		display: flex;
		color: #888;
		font-size: smaller;
		justify-content: center;
		align-items: center;
	}

	.creator-name {
		padding-left: 6rpx;
	}

	.price-info {
		display: flex;
		font-size: large;
		font-weight: bold;
	}

	.price-info view {
		padding-right: 8rpx;
	}

	.sticky {
		background-color: #ffffff;
	}

	.sticky-fixed {
		padding-bottom: 12rpx;
	}

	.top-nav {
		display: flex;
		color: #969696;
		font-size: 36rpx;
		line-height: 3;
	}

	.top-nav view {
		padding-right: 32rpx;
	}

	.top-nav-selected {
		color: #000000;
		font-weight: bold;
	}

	.page-content {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-bottom: 140rpx;
	}
</style>
