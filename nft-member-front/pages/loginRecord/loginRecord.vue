<template>
	<view>
		<view class="no-data" v-show="noDataFlag">
			<u-empty text="暂无记录" mode="list"></u-empty>
		</view>
		<view class="records">
			<template v-for="record in records">
				<view class="record">
					<view class="record-item">
						<view class="record-item-l">
							时间
						</view>
						<view class="record-item-r">
							{{record.loginTime}}
						</view>
					</view>
					<view class="record-item">
						<view class="record-item-l">
							状态
						</view>
						<view class="record-item-r" v-show="record.state == '1'">
							{{record.stateName}}
						</view>
						<view class="record-item-r record-item-r-red" v-show="record.state == '0'">
							{{record.stateName}} {{record.msg}} 
						</view>
					</view>
					<view class="record-item">
						<view class="record-item-l">
							IP
						</view>
						<view class="record-item-r">
							{{record.ipAddr}}
						</view>
					</view>
				</view>
				<u-line></u-line>
			</template>
			<view @click="nextPage" v-show="!noDataFlag">
				<u-loadmore margin-top="40" margin-bottom="40" :status="loadingState"></u-loadmore>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				records: [],
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
			}
		},
		onLoad() {
			this.findLoginLogByPage();
		},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods: {
			refreshData() {
				this.pageNum = 1;
				this.records = [];
				this.loadingState = 'loading';
				this.findLoginLogByPage();
			},

			nextPage() {
				if (this.loadingState == 'nomore') {
					return;
				}
				this.pageNum = this.pageNum + 1;
				this.findLoginLogByPage();
			},

			findLoginLogByPage() {
				var that = this;
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum
				};
				that.loadingState = 'loading';
				this.$u.get('/member/findLoginLogByPage', queryParam).then(res => {
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
					var oldRecords = that.records;
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
					that.noDataFlag = that.records.length == 0;
				});
			},
		}
	}
</script>

<style>
	.no-data {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 400rpx;
	}

	.records {
		padding-bottom: 20rpx;
	}

	.record {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.record-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		font-size: 28rpx;
		line-height: 2;
	}

	.record-item-l {
		color: #909399;
	}

	.record-item-r {}

	.record-item-r-red {
		color: #e64340;
	}
</style>
