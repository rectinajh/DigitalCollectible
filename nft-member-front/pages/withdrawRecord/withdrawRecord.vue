<template>
	<view>
		<u-dropdown>
			<u-dropdown-item v-model="selectedOrderState" :title="showSelectedOrderStateLabel()" :options="orderStates"
				@change="refreshData">
			</u-dropdown-item>
			<u-dropdown-item v-model="settlementAccountType" :title="showSettlementAccountTypeLabel()"
				:options="settlementAccountTypes" @change="refreshData">
			</u-dropdown-item>
		</u-dropdown>
		<view class="no-data" v-show="noDataFlag">
			<u-empty text="暂无记录" mode="list"></u-empty>
		</view>
		<view class="orders">
			<template v-for="order in orders">
				<u-gap height="10" bgColor="#f9f7f7"></u-gap>
				<view class="order" @click="orderDetailPage(order)">
					<view class="order-section1">
						<view class="order-section1-l">
							<u-icon name="clock" size="26"></u-icon>
							<text class="submit-time">{{order.submitTime}}</text>
						</view>
						<view class="order-section1-r">
							{{order.stateName}}
							<u-icon name="arrow-right" color="rgb(208 211 217)" size="22"></u-icon>
						</view>
					</view>
					<view class="order-section2">
						<view class="order-section2-l">
							<u-image class="receipt-payment-type-icon" width="34rpx" height="34rpx"
								:src="'/static/img/' + order.settlementAccount.type + '.png'">
							</u-image>
							<view>{{order.settlementAccount.typeName}}</view>
						</view>
						<view class="order-section2-r">金额 {{order.amount}} CNY</view>
					</view>
					<view class="order-section3">
						<view class="order-section3-l">
							订单编号
						</view>
						<view>
							{{order.orderNo}}
						</view>
					</view>
				</view>
				<u-line />
			</template>
			<view @click="nextPage" v-show="!noDataFlag">
				<u-loadmore margin-top="40" margin-bottom="40" :status="loadingState" />
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				selectedOrderState: 'all',
				orderStates: [{
					label: '全部',
					value: 'all'
				}],
				settlementAccountType: 'all',
				settlementAccountTypes: [{
					label: '全部',
					value: 'all'
				}],
				orders: [],
				amount: '',
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
			}
		},
		onLoad() {
			this.findStateDictItem();
			this.findSettlementAccountTypeDictItem();
		},
		onShow() {
			this.refreshData();
		},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods: {

			orderDetailPage(order) {},

			findSettlementAccountTypeDictItem() {
				var that = this;
				this.$u.get('/dictconfig/findDictItemInCache', {
					dictTypeCode: 'settlementAccountType'
				}).then(res => {
					var lists = res.data;
					for (var i = 0; i < lists.length; i++) {
						var item = lists[i];
						that.settlementAccountTypes.push({
							label: item.dictItemName,
							value: item.dictItemCode
						});
					}
				});
			},

			findStateDictItem() {
				var that = this;
				this.$u.get('/dictconfig/findDictItemInCache', {
					dictTypeCode: 'withdrawRecordState'
				}).then(res => {
					var lists = res.data;
					for (var i = 0; i < lists.length; i++) {
						var item = lists[i];
						that.orderStates.push({
							label: item.dictItemName,
							value: item.dictItemCode
						});
					}
				});
			},

			refreshData() {
				this.pageNum = 1;
				this.orders = [];
				this.loadingState = 'loading';
				this.findTradeOrder();
			},

			nextPage() {
				if (this.loadingState == 'nomore') {
					return;
				}
				this.pageNum = this.pageNum + 1;
				this.findTradeOrder();
			},

			findTradeOrder() {
				var that = this;
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					state: that.selectedOrderState != 'all' ? that.selectedOrderState : '',
					settlementAccountType: that.settlementAccountType != 'all' ? that.settlementAccountType : '',
				};
				that.loadingState = 'loading';
				this.$u.get('/withdraw/findByPage', queryParam).then(res => {
					var newOrders = res.data.content;
					var totalPage = res.data.totalPage;
					if (that.pullDownRefreshFlag) {
						that.pullDownRefreshFlag = false;
						uni.stopPullDownRefresh();
					}
					if (newOrders.length == 0) {
						that.loadingState = 'nomore';
					}
					if (totalPage == that.pageNum) {
						that.loadingState = 'nomore';
					}
					var oldOrders = that.orders;
					for (var i = 0; i < newOrders.length; i++) {
						var flag = true;
						for (var j = 0; j < oldOrders.length; j++) {
							if (newOrders[i].id == oldOrders[j].id) {
								flag = false;
								break;
							}
						}
						if (flag) {
							oldOrders.push(newOrders[i]);
						}
					}
					that.noDataFlag = that.orders.length == 0;
				});
			},

			showSelectedOrderStateLabel() {
				if (this.selectedOrderState) {
					if (this.selectedOrderState == 'all') {
						return '订单状态';
					}
					for (var i = 0; i < this.orderStates.length; i++) {
						var orderState = this.orderStates[i];
						if (this.selectedOrderState == orderState.value) {
							return orderState.label;
						}
					}
				}
			},

			showSettlementAccountTypeLabel() {
				if (this.settlementAccountType) {
					if (this.settlementAccountType == 'all') {
						return '结算账户';
					}
					for (var i = 0; i < this.settlementAccountTypes.length; i++) {
						var settlementAccountType = this.settlementAccountTypes[i];
						if (this.settlementAccountType == settlementAccountType.value) {
							return settlementAccountType.label;
						}
					}
				}
			}
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

	.slot-content {
		background-color: white;
	}

	.dropdown-amount-input {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.dropdown-btns {
		display: flex;
	}

	.dropdown-btn {
		flex: 1;
	}

	.trade-type-tab {
		display: flex;
		justify-content: space-between;
		color: white;
		padding-top: 30rpx;
		align-items: center;
	}

	.trade-type-tab-l {
		display: flex;
		flex: 2;
		justify-content: space-around;
		align-items: flex-end;
	}

	.trade-type {
		color: #ccc;
		font-size: 28rpx;
	}

	.trade-type-activated {
		color: white;
		font-size: 44rpx;
	}

	.trade-type-r {
		flex: 1;
		display: flex;
		justify-content: flex-end;
		padding-right: 32rpx;
	}

	.my-order-record {
		padding-right: 40rpx;
	}

	.payment-received-info {
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.info-section1 {
		display: flex;
		align-items: center;
	}

	.payment-received-info-txt {
		flex: 1;
	}

	.type-name {
		padding-bottom: 14rpx;
	}

	.info-detail {
		color: #888;
		font-size: small;
	}

	.info-icon {
		margin-right: 14rpx;
	}

	.buy-all {
		color: #007aff;
	}

	.buy-bottom-tip {
		display: flex;
		justify-content: flex-end;
		padding-bottom: 40rpx;
	}

	.buy-amount-unit {
		color: black;
	}

	.buy-modal {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.modal-title {
		display: flex;
		justify-content: space-between;
	}

	.buy-txt {
		font-weight: bold;
	}

	.close-buy-txt {
		color: #909399;
	}

	.orders {
		padding-bottom: 20rpx;
	}

	.order {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.order-section1 {
		display: flex;
		justify-content: space-between;
		font-size: 26rpx;
		align-items: center;
	}

	.order-section1-l {
		display: flex;
		align-items: center;
	}

	.submit-time {
		padding-left: 4rpx;
	}

	.order-section1-r {
		color: #909399;
	}

	.text-avatar {
		height: 50rpx;
		width: 50rpx;
		border-radius: 50rpx;
		line-height: 50rpx;
		text-align: center;
		color: white;
		margin-right: 12rpx;
		background-color: rgb(41, 121, 255);
	}

	.text-avatar2 {
		background-color: #39496a;
	}

	.order-section2 {
		font-size: 26rpx;
		line-height: 2;
		display: flex;
		justify-content: space-between;
	}

	.order-section2-l {
		display: flex;
		align-items: center;
		color: #909399;
	}

	.order-section2-r {
		font-weight: bold;
		color: #606266;
	}

	.order-section3 {
		display: flex;
		justify-content: space-between;
		align-items: center;
		color: #909399;
		font-size: 24rpx;
	}

	.order-section3-l {}

	.receipt-payment-type-icon {
		margin-right: 14rpx;
	}

	.order-section1-l-txt-sell {
		font-size: 32rpx;
		color: #eb3e3e;
		font-weight: bold;
	}

	.order-section1-l-txt-buy {
		font-size: 32rpx;
		color: green;
		font-weight: bold;
	}

	.order-section1-l-txt2 {
		font-size: 36rpx;
		padding-left: 8rpx;
	}

	.buy-btn {
		height: 50rpx;
		line-height: 50rpx;
	}

	.trade-tip {
		background-color: #2979ff;
		color: white;
		padding-bottom: 32rpx;
		padding-top: 20rpx;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.trade-tip-title {
		display: flex;
		justify-content: center;
	}

	.trade-tip-content {
		font-size: 20rpx;
		line-height: 1.8;
	}
</style>
