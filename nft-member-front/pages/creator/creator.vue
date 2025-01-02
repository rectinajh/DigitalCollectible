<template>
	<view class="page-content">
		<view class="top-content">
			<view class="avatar-nick-name">
				<view class="member-avatar">
					<u-image height="80rpx" width="80rpx" :src="creator.avatar" shape="circle"></u-image>
				</view>
				<view class="nick-name-mobile">
					<view class="member-nick-name">创作者</view>
					<view class="member-mobile">{{creator.name}}</view>
				</view>
			</view>
		</view>
		<view>
			<view class="custom-tabs">
				<view class="custom-tabs-l">
					<u-tabs :list="tabs" :is-scroll="false" :current="currentTab" @change="switchTab"></u-tabs>
				</view>
			</view>
			<view>
				<view class="no-data" v-show="noDataFlag">
					<u-empty text="暂无数据" mode="favor"></u-empty>
				</view>
				<view class="custom-tab-content">
					<view v-show="currentTab == 0">
						<u-row gutter="8">
							<u-col span="6" v-for="collection in collections">
								<view class="collection">
									<view class="collection-content" @click="latestCollectionDetailPage(collection.id)">
										<image class="collection-cover" style="height: 280rpx; width: 100%;"
											:src="collection.cover">
										</image>
										<view class="collection-name u-line-1">{{collection.name}}</view>
										<view class="quantity-block">
											<view class="quantity-block-l">
												<view class="limit">限量</view>
												<view class="quantity">{{collection.quantity}}份</view>
											</view>
											<view class="resale-price">￥{{collection.price}}</view>
										</view>
									</view>
								</view>
							</u-col>
						</u-row>
					</view>
					<view v-show="currentTab == 1">
						<u-row gutter="8">
							<u-col span="6" v-for="collection in mysteryBoxs">
								<view class="collection">
									<view class="collection-content" @click="latestCollectionDetailPage(collection.id)">
										<image class="collection-cover" style="height: 280rpx; width: 100%;"
											:src="collection.cover">
										</image>
										<view class="collection-name u-line-1">{{collection.name}}</view>
										<view class="quantity-block">
											<view class="quantity-block-l">
												<view class="limit">限量</view>
												<view class="quantity">{{collection.quantity}}份</view>
											</view>
											<view class="resale-price">￥{{collection.price}}</view>
										</view>
									</view>
								</view>
							</u-col>
						</u-row>
					</view>
				</view>
				<view @click="nextPage" v-show="!noDataFlag">
					<u-loadmore margin-top="40" margin-bottom="40" :status="loadingState" />
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				creatorId: '',
				creator: '',
				tabs: [{
					name: '藏品'
				}, {
					name: '盲盒'
				}],
				currentTab: 0,
				collections: [],
				mysteryBoxs: [],
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
			}
		},
		onLoad(option) {
			this.creatorId = option.id;
			this.findCreator();
			this.findByPage();
		},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods: {

			latestCollectionDetailPage(id) {
				uni.navigateTo({
					url: '../latestCollectionDetail/latestCollectionDetail?id=' + id
				});
			},

			findCreator() {
				var that = this;
				this.$u.get('/collection/findCreatorById', {
					id: that.creatorId
				}).then(res => {
					that.creator = res.data;
				});
			},

			switchTab(index) {
				this.currentTab = index;
				this.refreshData();
			},

			refreshData() {
				this.pageNum = 1;
				this.loadingState = 'loading';
				this.findByPage();
			},

			nextPage() {
				if (this.loadingState == 'nomore') {
					return;
				}
				this.pageNum = this.pageNum + 1;
				this.findByPage();
			},

			findByPage() {
				if (this.currentTab == 0) {
					this.findLatestCollectionByPage();
				} else if (this.currentTab == 1) {
					this.findLatestMysteryBoxByPage();
				}
			},

			findLatestCollectionByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.collections = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					creatorId: that.creatorId
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
					var oldRecords = that.collections;
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

			findLatestMysteryBoxByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.mysteryBoxs = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					creatorId: that.creatorId
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
					var oldRecords = that.mysteryBoxs;
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
		}
	}
</script>

<style>
	page {
		height: 100% !important;
	}

	.quantity-block {
		display: flex;
		padding-left: 16rpx;
		justify-content: space-between;
		padding-top: 16rpx;
		align-items: center;
		padding-right: 16rpx;
	}

	.quantity-block-l {
		display: flex;
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

	.msg-unread-num {
		background-color: #d10e0e;
	}

	.setting {
		color: white;
		padding-right: 32rpx;
		position: relative;
		top: -40rpx;
	}

	.msg-read {
		height: 24rpx;
		width: 24rpx;
		border-radius: 24rpx;
		line-height: 24rpx;
		text-align: center;
		font-size: 20rpx;
		transform: scale(0.8);
		position: absolute;
		top: 0rpx;
		left: 46rpx;
	}

	.div-line {
		width: 100%;
		height: 20rpx;
		background: #f0f0f0;
	}

	.resale-price {
		padding-left: 16rpx;
		font-size: larger;
		font-weight: bold;
	}

	.custom-tabs {
		padding-bottom: 32rpx;
		display: flex;
		align-items: center;
	}

	.custom-tabs-l {
		flex: 1;
	}

	.compose-activity {
		background: linear-gradient(to right, #2979ff, #909399);
		height: 56rpx;
		line-height: 56rpx;
		border-radius: 44rpx 0rpx 0rpx 44rpx;
		font-size: small;
		color: white;
		padding-left: 24rpx;
		padding-right: 24rpx;
		margin-left: 12rpx;
	}


	.custom-tab-content {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.collections {}

	.collection {
		padding-bottom: 10rpx;
	}

	.collection-content {
		background: #e7e7e7;
		border-radius: 20rpx;
		padding-bottom: 20rpx;
	}

	.collection-cover {
		border-radius: 20rpx 20rpx 0rpx 0rpx;
	}

	.collection-name {
		font-size: 26rpx;
		padding-left: 16rpx;
		padding-top: 16rpx;
		color: #000000;
	}

	.collection-hold-date {
		font-size: 24rpx;
		padding-left: 16rpx;
	}

	.no-data {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 400rpx;
	}

	.grid-navs {
		display: flex;
		justify-content: space-around;
		align-items: center;
	}

	.grid-nav {
		color: white;
		text-align: center;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
		position: relative;
	}

	.page-content {
		padding-bottom: 140rpx;

	}

	.top-content {
		background: #2f2f2f;
		padding-bottom: 20rpx;
		padding-top: 20rpx;
	}

	.avatar-nick-name {
		display: flex;
		align-items: center;
		padding-bottom: 16rpx;
		padding-left: 32rpx;
	}

	.nick-name-mobile {
		flex: 1;
	}

	.member-avatar {
		padding-right: 20rpx;
	}

	.member-nick-name {
		padding-bottom: 12rpx;
		font-size: small;
		color: #888;
	}

	.member-mobile {
		font-size: larger;
		color: white;
	}

	.block-chain-addr {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-bottom: 16rpx;
	}

	.block-chain-addr-l {
		color: white;
		font-size: small;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		padding-right: 60rpx;
	}

	.block-chain-addr-r {}
</style>
