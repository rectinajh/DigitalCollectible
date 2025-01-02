<template>
	<view>
		<u-tabs-swiper ref="tabs" name="dictItemName" :list="noticeTypes" :current="tabIndex" :is-scroll="false"
			@change="switchTab"></u-tabs-swiper>
		<view class="no-data" v-show="noDataFlag">
			<u-empty text="暂无公告" mode="news"></u-empty>
		</view>
		<view class="notices">
			<view class="notice" v-for="record in records" @click="detailPage(record)">
				<view class="notice-title">{{record.title}}</view>
				<view class="notice-time">{{record.publishTime}}</view>
				<u-line color="#cecece"></u-line>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				noticeTypes: [],
				tabIndex: 0,
				records: [],
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
				showAllMarkReadModal: false,
			}
		},
		onLoad() {
			this.findNoticeType();
		},
		onShow() {},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods: {

			switchTab(index) {
				this.tabIndex = index;
				this.refreshData();
			},

			findNoticeType() {
				var that = this;
				this.$u.get('/dictconfig/findDictItemInCache', {
					dictTypeCode: 'noticeType'
				}).then(res => {
					that.noticeTypes = res.data;
					that.findByPage();
				});
			},

			detailPage(notice) {
				uni.navigateTo({
					url: '../noticeDetail/noticeDetail?id=' + notice.id
				});
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
					that.records = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					type: that.noticeTypes[that.tabIndex].dictItemCode
				};
				that.loadingState = 'loading';
				this.$u.get('/notice/findNoticeAbstractByPage', queryParam).then(res => {
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
			}
		}
	}
</script>

<style>
	.no-data {
		padding-top: 64rpx;
	}

	.notices {
		padding-top: 16rpx;
	}

	.notice {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.notice-title {
		padding-top: 20rpx;
		padding-bottom: 12rpx;
		padding-left: 4rpx;
	}

	.notice-time {
		color: #888;
		font-size: small;
		padding-left: 4rpx;
		padding-bottom: 20rpx;
	}
</style>
