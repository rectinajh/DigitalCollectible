<template>
	<view class="content">
		<!-- <u-dropdown>
			<u-dropdown-item v-model="selectedOrderState" :title="showSelectedOrderStateLabel()" :options="orderStates"
				@change="refreshData">
			</u-dropdown-item>
			<u-dropdown-item v-model="settlementAccountType" :title="showSettlementAccountTypeLabel()"
				:options="settlementAccountTypes" @change="refreshData">
			</u-dropdown-item>
		</u-dropdown> -->
		<view class="no-data" v-show="noDataFlag">
			<u-empty text="暂无记录" mode="list"></u-empty>
		</view>
		<view class="orders">
			<template v-for="(order,index) in orders">
				<!-- <u-gap height="10" bgColor="#f9f7f7"></u-gap> -->
				<view class="order" @click="orderDetailPage(order)">
					<view class="main" :class="{border:index!==orders.length-1}">
						
						<view class="icon">
							<u-image width="52" height="52"
								:src="'/static/img/wallet/' + order.settlementAccount.type + '.png'">
							</u-image>
						</view>
						
						<view class="text">
							<view class="top">
								<view class="left">
									{{ order.settlementAccount.type=='bankCard'? order.settlementAccount.bankName:order.settlementAccount.account}}
								</view>
								<view class="right">
									{{common.moneyFormat(order.amount)}}
								</view>
							</view>
							<view class="bottom">
								<view class="left">
									{{order.submitTime}}
								</view>
								<view class="right" :class="{theme:order.stateName==='审核中'}">
									{{order.stateName}}
								</view>
							</view>
						</view>
					</view>
					
					
					<!-- <view class="order-section1">
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
					</view> -->
				</view>
				<!-- <u-line /> -->
			</template>
			<!-- <view @click="nextPage" v-show="!noDataFlag">
				<u-loadmore margin-top="40" margin-bottom="40" :status="loadingState" />
			</view> -->
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

<style lang="scss" scoped>
@import 'withdrawRecord.scss';
</style>
