<template>
	<view>
		<u-action-sheet :list="changeTypes" @click="filterChangeType" v-model="showFilterFlag"></u-action-sheet>
		<view class="balance-and-recharge">
			<view class="balance">
				<view class="balance-title">余额</view>
				<view class="balance-value"><text class="balance-unit">￥</text><text>{{moneyFormat(balance)}}</text>
				</view>
			</view>
			<view class="action-btns">
				<view class="recharge-btn" @click="rechagePage">
					<text class="recharge-btn-txt">充值</text>
				</view>
				<view class="withdraw-btn" @click="withdrawPage">
					<text class="withdraw-btn-txt">提现</text>
				</view>
			</view>
			
		</view>
		<u-gap height="10" bgColor="#f9f7f7"></u-gap>
		<view class="change-log">
			<view class="change-log-title">
				<view class="change-log-title-l">账户明细</view>
				<view class="change-log-title-r">
					<u-icon name="hourglass" size="40" :color="changeType != 'all' ? '#007aff' : ''"
						@click="showFilterFlag = true;">
					</u-icon>
				</view>
			</view>

			<view class="no-data" v-show="noDataFlag">
				<u-empty text="暂无记录" mode="list"></u-empty>
			</view>
			<view class="orders">
				<template v-for="record in records">
					<view class="order">
						<view class="order-section1">
							<view class="order-section1-t">{{record.changeTypeName}}</view>
							<view class="order-section1-b">{{record.changeTime}}</view>
						</view>
						<view class="order-section2">
							<text class="order-section2-l">{{record.balanceChange < 0 ? '-' : '+'}}</text>
							<text>￥{{moneyFormat(Math.abs(record.balanceChange))}}</text>
						</view>
					</view>
					<u-line />
				</template>
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
				changeTypes: [{
					text: '全部',
					color: '#007aff',
					value: 'all'
				}],
				changeType: 'all',
				balance: '',
				records: [],
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
				showFilterFlag: false,

			}
		},
		onLoad() {
			this.findTypeDictItem();
			this.getBalance();
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
			
			withdrawPage() {
				uni.navigateTo({
					url: '../withdraw/withdraw'
				});
			},

			rechagePage() {
				uni.showToast({
					title: "敬请期待",
					icon: "none"
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

			filterChangeType(index) {
				var that = this;
				that.changeTypes.map((val, idx) => {
					if (index == idx) {
						val.color = '#007aff';
						that.changeType = val.value;
					} else {
						val.color = '#303133';
					}
				});
				that.refreshData();
			},

			findTypeDictItem() {
				var that = this;
				this.$u.get('/dictconfig/findDictItemInCache', {
					dictTypeCode: 'memberBalanceChangeType'
				}).then(res => {
					var lists = res.data;
					for (var i = 0; i < lists.length; i++) {
						var item = lists[i];
						that.changeTypes.push({
							text: item.dictItemName,
							color: '#303133',
							value: item.dictItemCode
						});
					}
				});
			},

			refreshData() {
				this.pageNum = 1;
				this.records = [];
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
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					changeType: that.changeType != 'all' ? that.changeType : '',
				};
				that.loadingState = 'loading';
				this.$u.get('/memberBalanceChangeLog/findByPage', queryParam).then(res => {
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

			getBalance() {
				var that = this;
				this.$u.get('/member/getBalance').then(res => {
					that.balance = res.data;
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


	.orders {
		padding-bottom: 20rpx;
	}

	.order {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.order-section1 {}

	.order-section1-t {
		line-height: 2;
	}

	.order-section1-b {
		line-height: 2;
		color: #888;
	}

	.order-section2 {}

	.order-section2-l {
		padding-right: 8rpx;
	}

	.change-log-title {
		display: flex;
		justify-content: space-between;
		align-items: center;
		line-height: 3;
		font-size: 40rpx;
		font-weight: bold;
	}

	.change-log {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.balance-and-recharge {
		display: flex;
		justify-content: space-between;
		padding-left: 48rpx;
		padding-bottom: 32rpx;
		padding-top: 32rpx;
	}

	.balance-title {}

	.balance-value {
		font-size: 44rpx;
		line-height: 2;
	}

	.balance-unit {
		padding-right: 4rpx;
	}
	
	.action-btns {
		    display: flex;
		    align-items: center;
	}
	
	.withdraw-btn {
		text-align: center;
		width: 200rpx;
		height: 80rpx;
		line-height: 80rpx;
		background: #545353;
		color: white;
		background: linear-gradient(to right, rgb(66, 83, 216), rgb(213, 51, 186));
	}
	
	.withdraw-btn-txt {
		padding-right: 4rpx;
	}

	.recharge-btn {
		text-align: center;
		width: 200rpx;
		height: 80rpx;
		line-height: 80rpx;
		background: #545353;
		color: white;
		background: linear-gradient(to right, #2f2f2f, #747171);
	}

	.recharge-btn-txt {
		padding-right: 4rpx;
	}
</style>
