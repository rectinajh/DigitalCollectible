<template>
	<view class="page-content">
		<view class="home-title">
			<u-image width="90rpx" height="90rpx" src="/static/img/c2c.png"></u-image>
			<view class="home-title-r">OPEN NFT</view>
		</view>
		<view class="swiper-content">
			<u-swiper name="cover" :list="carousels" :autoplay="false" :effect3d="true" mode="dot" border-radius="0"
				effect3d-previous-margin="0" @click="carouselClickEvent"></u-swiper>
		</view>
		<view class="system-notice">
			<u-notice-bar mode="vertical" :list="noticeTitles" type="none" padding="18rpx 0rpx" :more-icon="true"
				@click="noticeDetailPage" @getMore="noticePage"></u-notice-bar>
		</view>

		<u-sticky offset-top="-100" :enable="enableStickyFlag" @fixed="stickyFixedFlag = true;"
			@unfixed="stickyFixedFlag = false;">
			<view class="sticky" :class="{'sticky-fixed':stickyFixedFlag}">
				<view class="top-nav">
					<view v-for="tab in tabs" :class="{'top-nav-selected':currentTab == tab.value}"
						@click="switchTab(tab.value)">{{tab.title}}</view>
				</view>
			</view>
		</u-sticky>
		<view class="sell-calendar-content" v-show="currentTab == '3'">
			<view class="sell-plan">近期发售计划</view>
			<view class="sell-date" v-for="dateCollection in forSaleCollections">
				<view class="sell-date-value">{{dateCollection.date}}</view>
				<view class="sell-time" v-for="timeCollection in dateCollection.timeCollections">
					<view class="sell-time-value">{{timeCollection.time}}</view>
					<view class="sell-collection" v-for="collection in timeCollection.collections"
						@click="latestCollectionDetailPage(collection.id)">
						<view class="sell-collection-l">
							<u-image width="100%" height="220rpx" border-radius="50" :src="collection.cover">
							</u-image>
						</view>
						<view class="sell-collection-r">
							<view class="sell-collection-r1 u-line-1">
								{{collection.creatorName}}·{{collection.name}}
							</view>
							<view class="sell-collection-r2">
								<view class="limit">限量</view>
								<view class="quantity">{{collection.quantity}}份</view>
							</view>
							<view class="sell-collection-r3">
								<view>￥</view>
								<view>{{collection.price}}</view>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="more-await">更多内容敬请期待</view>
		</view>
		<view class="collection-tab-content" v-show="currentTab == '1'">
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
				<view class="pre-sale-tip" v-show="collection.preSaleFlag && collection.surplusSecond > 0">
					<view class="pre-sale-block">
						<view>
							<view class="pre-sale">
								<u-icon size="26" name="heart"></u-icon>
								<text>优先购</text>
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
		<view class="collection-tab-content" v-show="currentTab == '2'">
			<view class="latest-collection" v-for="collection in latestMysteryBoxs"
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
				<view class="pre-sale-tip" v-show="collection.preSaleFlag && collection.surplusSecond > 0">
					<view class="pre-sale-block">
						<view>
							<view class="pre-sale">
								<u-icon size="26" name="heart"></u-icon>
								<text>优先购</text>
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
				stickyFixedFlag: false,
				enableStickyFlag: true,
				tabs: [{
					title: '热卖藏品',
					value: '1'
				}, {
					title: '盲盒系列',
					value: '2'
				}, {
					title: '发售日历',
					value: '3'
				}],
				currentTab: '1',
				carousels: [],
				latestCollections: [],
				latestMysteryBoxs: [],
				forSaleCollections: [],
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				pageNum: 1,
				noDataFlag: false,
				notices: [],
				noticeTitles: [],
			}
		},
		onLoad() {
			this.findTopNotice();
			this.findLatestCollectionByPage();
			this.findCarousel();
		},
		onShow() {
			this.enableStickyFlag = true;
		},
		onHide() {
			this.enableStickyFlag = false;
		},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods: {

			carouselClickEvent(index) {
				var carousel = this.carousels[index];
				if (carousel.clickDealWay == '1') {
					return;
				} else if (carousel.clickDealWay == '2') {
					uni.navigateTo({
						url: '../' + carousel.link
					});
				} else if (carousel.clickDealWay == '3') {
					uni.navigateTo({
						url: '../customWebView/customWebView?link=' + carousel.link
					});
				}
			},

			findCarousel() {
				var that = this;
				this.$u.get('/carousel/findCarousel').then(res => {
					that.carousels = res.data;
				});
			},

			noticePage() {
				uni.navigateTo({
					url: '../notice/notice'
				});
			},

			noticeDetailPage(index) {
				uni.navigateTo({
					url: '../noticeDetail/noticeDetail?id=' + this.notices[index].id
				});
			},

			findTopNotice() {
				var that = this;
				var queryParam = {
					pageSize: 3,
					pageNum: 1
				};
				this.$u.get('/notice/findNoticeAbstractByPage', queryParam).then(res => {
					var notices = res.data.content;
					var noticeTitles = [];
					for (var i = 0; i < notices.length; i++) {
						noticeTitles.push(notices[i].title);
					}
					that.notices = notices;
					that.noticeTitles = noticeTitles;
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

			switchTab(tab) {
				this.currentTab = tab;
				this.refreshData();
			},

			refreshData() {
				this.pageNum = 1;
				this.loadingState = 'loading';
				if (this.currentTab == '1') {
					this.findLatestCollectionByPage();
				} else if (this.currentTab == '2') {
					this.findLatestMysteryBoxByPage();
				} else if (this.currentTab == '3') {
					this.findForSaleCollection();
				}
			},

			nextPage() {
				if (this.loadingState == 'nomore') {
					return;
				}
				if (this.currentTab == '1') {
					this.pageNum = this.pageNum + 1;
					this.findLatestCollectionByPage();
				}
				if (this.currentTab == '2') {
					this.pageNum = this.pageNum + 1;
					this.findLatestMysteryBoxByPage();
				}
			},

			latestCollectionDetailPage(id) {
				uni.navigateTo({
					url: '../latestCollectionDetail/latestCollectionDetail?id=' + id
				});
			},

			findLatestMysteryBoxByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.latestMysteryBoxs = [];
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
					var oldRecords = that.latestMysteryBoxs;
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
				this.$u.get('/collection/findLatestCollectionByPage', queryParam).then(res => {
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
			},

			findForSaleCollection() {
				var that = this;
				this.$u.get('/collection/findForSaleCollection', {}).then(res => {
					that.forSaleCollections = res.data;
				});
			},
		}
	}
</script>

<style>
	page {
		height: 100% !important;
	}

	.system-notice {
		padding-top: 12rpx;
	}

	.home-title {
		display: flex;
		align-items: center;
		padding-bottom: 32rpx;
	}

	.home-title-r {
		font-size: 52rpx;
		padding-left: 40rpx;
		color: rgb(53 53 53 / 60%);
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
	
	.pre-sale-tip {
		position: absolute;
		top: 100rpx;
		z-index: 500;
		left: 32rpx;
	}
	
	.pre-sale-block {
	    display: inline-block;
	    border-radius: 10px;
	    background: #000000;
	    padding-left: 6px;
	    padding-right: 6px;
	    padding-top: 3px;
	    padding-bottom: 3px;
	    font-size: small;
	}
	
	.pre-sale-block .u-icon {
		padding-right: 6rpx;
	}
	
	.pre-sale {
		color: #ff9900;
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
		padding-top: 32rpx;
		// #ifdef APP-PLUS
		padding-top: 64rpx;
		// #endif
	}
</style>
