<template>
	<view class="page-content">
		<status-bar></status-bar>
		<view class="home-title">
			<u-image width="76rpx" height="64rpx" src="/static/img/home/logo.png"></u-image>
			<u-search placeholder="发现更多精彩" bgColor="#242425" :showAction="false" placeholderColor="#666666" color="#fff"
			margin="0 32rpx" v-model="keyword" searchIcon="/static/img/common/search.png"></u-search>
			<u-image width="40rpx" height="40rpx" src="/static/img/home/msg.png"></u-image>
		</view>
		<view class="titleSpace"></view>
		<view class="main">
			<view class="swiper-content">
				<u-swiper name="cover" :list="carousels" :autoplay="false" :effect3d="true" mode="dot"
					border-radius="24" effect3d-previous-margin="0" @click="carouselClickEvent" height="272"
					bgColor="#0C0C0D"></u-swiper>
			</view>
			<view class="system-notice">
				<view class="img">
					<u-image src="@/static/img/home/notice.png" width="42rpx" height="26rpx"></u-image>
				</view>
				<view class="notice">
					<u-notice-bar mode="vertical" :list="noticeTitles" type="none" padding="16rpx 16rpx 16rpx 90rpx"
						color="#fff" :font-size="24" :volume-icon="false" :more-icon="true" @click="noticeDetailPage"
						@getMore="noticePage"></u-notice-bar>
				</view>
			</view>

			<u-sticky :offset-top="statusbarHeight" :enable="enableStickyFlag" @fixed="stickyFixedFlag = true;"
				@unfixed="stickyFixedFlag = false;">
				<view class="sticky" :class="{'sticky-fixed':stickyFixedFlag}">
					<view class="top-nav">
						<view v-for="tab in tabs" :class="{'top-nav-selected':currentTab == tab.value}"
							@click="switchTab(tab.value)">{{tab.title}}</view>
					</view>
				</view>
			</u-sticky>
			<view class="sell-calendar-content" v-show="currentTab == '3'">
				<calendar-list :list="forSaleCollections"></calendar-list>
				<view class="more-await">更多内容敬请期待</view>
			</view>
			<view class="collection-tab-content" v-show="currentTab == '1'">
				<waterfall-list :list="latestCollections"></waterfall-list>
				<view class="more-await">更多内容敬请期待</view>
			</view>
			<view class="collection-tab-content" v-show="currentTab == '2'">
				<waterfall-list :list="latestMysteryBoxs"></waterfall-list>
			</view>
		</view>
	</view>

</template>

<script>
	import waterfallList from "@/components/waterfall-list/waterfall-list.vue"
	import calendarList from "@/components/calendar-list/calendar-list.vue"
	import {
		mapGetters
	} from 'vuex'
	export default {
		data() {
			return {
				stickyFixedFlag: false,
				enableStickyFlag: true,
				tabs: [{
						title: '热卖藏品',
						value: '1'
					},
					{
						title: '盲盒系列',
						value: '2'
					}, 
					{
						title: '发售日历',
						value: '3'
					}
				],
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
				keyword: '', //搜索关键词
				statusbarHeight:''
			}
		},
		components: {
			waterfallList,
			calendarList
		},
		computed: {
			...mapGetters(['userInfo'])
		},
		onLoad(options) {
			this.findTopNotice();
			this.findLatestCollectionByPage();
			this.findForSaleCollection();
			this.findCarousel();
			// #ifdef APP
			this.statusbarHeight=41 + 128
			// #endif
			
			// #ifdef H5
			this.statusbarHeight= 41
			// #endif
			// let userInfo = {
			// 	mobile: '18161017379'
			// }
			// this.$store.dispatch('updateUserInfo', userInfo)
			// if(options.toPwd){
			// 	this.$nextTick(()=>{
			// 		uni.navigateTo({
			// 			url:'/subPackages/setting/settingPwd/settingPwd',
			// 		})
			// 	})
			// }
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
				if (tab === this.currentTab) return
				this.currentTab = tab;
				this.refreshData();
				if (tab == 3) {
					uni.pageScrollTo({
						selector: '.top-nav',
						duration: 0
					});
				}
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

<style lang="scss" scoped>
	page {
		height: 100% !important;
	}

	.system-notice {
		position: relative;
		width: 686rpx;
		height: 68rpx;
		background-color: #272727;
		border-radius: 12rpx;
		margin-top: 32rpx;

		.img {
			position: absolute;
			left: 24rpx;
			top: 18rpx;
		}

		.notice {}
	}

	.home-title {
		display: flex;
		align-items: center;
		padding-bottom: 32rpx;
		position: fixed;
		padding: 32rpx 32rpx 32rpx 32rpx;
		z-index: 1000;
		background-color: #0C0C0D;
		width: 100%;
	}

	.titleSpace {
		height: 128rpx;
	}

	.home-title-r {
		font-size: 52rpx;
		padding-left: 40rpx;
		color: rgb(53, 53, 53 / 60%);
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
		background-color: #0C0C0D;
	}

	.sticky-fixed {
		padding-bottom: 12rpx;
		background-color: #0C0C0D;
		width: 101%;
		// margin-top: 128rpx;
	}

	.top-nav {
		display: flex;
		color: #999999;
		background-color: #0C0C0D;
		font-size: 32rpx;
		line-height: 3;
		width: 100%;
		padding-left: 32rpx;
		margin-left: -32rpx;
	}

	.top-nav view {
		padding-right: 32rpx;
	}

	.top-nav-selected {
		color: #FFFFFF;
		// font-weight: bold;
	}

	.page-content {
		background-color: #0C0C0D;
		// padding-left: 32rpx;
		// padding-right: 32rpx;
		// padding-bottom: 140rpx;
		// padding-top: 32rpx;
		// #ifdef APP-PLUS
		// padding-top: 64rpx;

		// #endif
		.main {
			padding: 32rpx 32rpx 140rpx 32rpx;
			// .swiper-content{
			// 	width: 686rpx;
			// 	height: 272rpx;
			// 	border-radius: 1rpx;
			// }
		}
	}
</style>