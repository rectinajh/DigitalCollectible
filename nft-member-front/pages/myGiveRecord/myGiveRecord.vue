<template>
	<view class="page-content">
		<view class="custom-tabs">
			<u-tabs :list="tabs" :is-scroll="false" :current="currentTab" @change="switchTab"></u-tabs>
		</view>
		<view>
			<view class="no-data" v-show="noDataFlag">
				<u-empty text="暂无数据" mode="favor"></u-empty>
			</view>
			<view>
				<view class="give-record" v-for="giveRecord in giveRecords">
					<view class="give-record-content">
						<view class="give-record-content-top">
							<view class="give-record-content-top-l">
								订单编号：{{giveRecord.orderNo}}
							</view>
							<view class="give-record-content-top-r">{{giveRecord.giveDirection == 'from' ? '转出' : '转入'}}
							</view>
						</view>
						<view class="give-record-content-middle">
							<view class="give-record-content-middle-l">
								<u-image class="collection-cover" width="140rpx" height="140rpx" border-radius="10"
									:src="giveRecord.collectionCover">
								</u-image>
							</view>
							<view class="give-record-content-middle-r">
								<view class="collection-name">
									{{giveRecord.collectionName}}
								</view>
								<view v-show="giveRecord.giveDirection == 'from'">接收者：{{giveRecord.giveToMobile}}</view>
								<view v-show="giveRecord.giveDirection == 'to'">转赠者：{{giveRecord.giveFromMobile}}</view>
								<view>时间：{{giveRecord.giveTime}}</view>
							</view>
						</view>
						<!-- <view class="give-record-content-bottom">
							<view>
								交易HASH:cbf8b97f8fe73daa905a5b4721eae0e2519aae7533aba5915be66dad59d4fb1d
							</view>
						</view> -->
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
				tabs: [{
					name: '全部'
				}, {
					name: '转出'
				}, {
					name: '转入',
				}],
				currentTab: 0,
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
				giveRecords: [],
			}
		},
		onLoad() {
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
				var that = this;
				if (that.pageNum == 1) {
					that.giveRecords = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					giveDirection: that.currentTab == 0 ? '' : that.currentTab == 1 ? 'from' : 'to'
				};
				that.loadingState = 'loading';
				this.$u.get('/transaction/findMyGiveRecordByPage', queryParam).then(res => {
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
					var oldRecords = that.giveRecords;
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
	.custom-tabs {
		padding-bottom: 20rpx;
	}

	.give-record {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-bottom: 20rpx;
	}


	.give-record-content {
		background: #e7e7e7;
		padding-left: 20rpx;
		padding-right: 20rpx;
		padding-top: 10rpx;
		padding-bottom: 10rpx;
		border-radius: 20rpx;
		font-size: small;
	}

	.give-record-content-top {
		display: flex;
		justify-content: space-between;
		line-height: 2;
	}

	.give-record-content-top-l {
		color: #888;
	}

	.give-record-content-top-r {
		color: #007aff;
		font-weight: bold;
	}

	.give-record-content-middle {
		display: flex;
		align-items: center;

	}

	.give-record-content-middle-l {
		flex: 1;
	}

	.give-record-content-middle-r {
		flex: 2.5;
		padding-left: 20rpx;
	}

	.give-record-content-bottom {
		display: flex;
		justify-content: space-between;
		line-height: 2;
	}

	.collection-name {
		font-weight: bold;
	}
</style>
