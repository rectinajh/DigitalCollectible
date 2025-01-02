<template>
	<view class="page-content">
		<u-modal v-model="showCancelModalFlag" title="提示" :show-cancel-button="true" cancel-text="暂时不要"
			confirm-text="取消交易" content="是否要取消交易" @confirm="cancelPay">
		</u-modal>
		<u-popup v-model="showPayModalFlag" mode="bottom" border-radius="14" :closeable="true">
			<view class="pay-modal">
				<view class="pay-modal-amount">
					<text>￥</text><text class="pay-modal-amount-value">{{moneyFormat(selectedOrder.amount)}}</text>
				</view>
				<view class="pay-modal-pay-way-tip">选择支付方式</view>
				<view class="pay-modal-pay-ways">
					<view class="pay-modal-pay-way">
						<view class="pay-modal-pay-way-label">余额</view>
						<view>
							<u-icon name="checkmark-circle-fill" color="#2979ff" size="36"></u-icon>
						</view>
					</view>
				</view>
				<view>
					<u-button class="custom-pay-modal-btn" type="primary" @click="confirmPay">确认付款</u-button>
				</view>
			</view>
		</u-popup>
		<view class="custom-tabs">
			<u-tabs :list="tabs" :is-scroll="false" :current="currentTab" @change="switchTab"></u-tabs>
		</view>
		<view>
			<view class="no-data" v-show="noDataFlag">
				<u-empty text="暂无数据" mode="favor"></u-empty>
			</view>
			<view>
				<view class="order-record" v-for="orderRecord in orderRecords"
					@click="payOrderDetailPage(orderRecord.id)">
					<view class="order-record-content">
						<view class="order-record-content-top">
							<view class="order-record-content-top-l">
								订单编号：{{orderRecord.orderNo}}
							</view>
							<view class="order-record-content-top-r">
								<view class="payment-state" v-show="orderRecord.state == '1'">
									<u-icon name="clock-fill" size="26"></u-icon>
									<text class="payment-state-txt">{{orderRecord.stateName}}</text>
								</view>
								<text v-show="orderRecord.state == '2'">{{orderRecord.stateName}}</text>
								<text class="cancel-state"
									v-show="orderRecord.state == '3'">{{orderRecord.stateName}}</text>
							</view>
						</view>
						<view class="order-record-content-middle">
							<view class="order-record-content-middle-l">
								<u-image class="collection-cover" width="120rpx" height="120rpx" border-radius="10"
									:src="orderRecord.collectionCover">
								</u-image>
							</view>
							<view class="order-record-content-middle-r">
								<view class="collection-name">
									<view class="collection-name-l">
										{{orderRecord.collectionName}}
									</view>
									<view class="collection-name-r" v-show="orderRecord.commodityType == '2'">
										<u-tag text="盲盒" type="primary" mode="dark" size="mini"></u-tag>
									</view>
								</view>
								<view>{{orderRecord.creatorName}}</view>
							</view>
						</view>
						<view class="order-record-content-bottom">
							<view class="order-record-time">
								{{orderRecord.createTime}}
							</view>
							<view class="order-record-amount">实付款：￥{{moneyFormat(orderRecord.amount)}}</view>
						</view>
						<view v-show="orderRecord.state == '1'">
							<u-line color="#9c9c9c"></u-line>
							<view class="order-record-actions">
								<view class="cancel-btn">
									<u-button type="error" size="mini" @click.stop="showCancelModal(orderRecord)">取消订单
									</u-button>
								</view>
								<view>
									<u-button type="primary" size="mini" @click.stop="showPayModal(orderRecord)">立即付款
									</u-button>
								</view>
							</view>
						</view>

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
					name: '全部',
					value: '',
				}, {
					name: '待付款',
					value: '1'
				}, {
					name: '已付款',
					value: '2'
				}, {
					name: '已取消',
					value: '3'
				}],
				currentTab: 0,
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
				orderRecords: [],
				showPayModalFlag: false,
				showCancelModalFlag: false,
				selectedOrder: '',
			}
		},
		onLoad() {
			
		},
		onShow() {
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

			confirmPay() {
				var that = this;
				this.$u.post('/transaction/confirmPay', {
					orderId: that.selectedOrder.id,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '支付成功!',
						duration: 2000,
						mask: true,
						complete: function() {
							that.showPayModalFlag = false;
							that.refreshData();
						}
					});
				});
			},

			cancelPay() {
				var that = this;
				this.$u.post('/transaction/cancelPay', {
					orderId: that.selectedOrder.id,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '取消成功!',
						duration: 2000,
						mask: true,
						complete: function() {
							that.refreshData();
						}
					});
				});
			},

			showCancelModal(order) {
				this.selectedOrder = order;
				this.showCancelModalFlag = true;
			},

			showPayModal(order) {
				this.selectedOrder = order;
				this.showPayModalFlag = true;
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

			payOrderDetailPage(id) {
				uni.navigateTo({
					url: '../payOrderDetail/payOrderDetail?id=' + id
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
				var that = this;
				if (that.pageNum == 1) {
					that.orderRecords = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					state: that.tabs[that.currentTab].value
				};
				that.loadingState = 'loading';
				this.$u.get('/transaction/findMyPayOrderByPage', queryParam).then(res => {
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
					var oldRecords = that.orderRecords;
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

	.order-record {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-bottom: 20rpx;
	}


	.order-record-content {
		background: #e7e7e7;
		padding-top: 10rpx;
		padding-bottom: 10rpx;
		border-radius: 20rpx;
		font-size: small;
	}

	.order-record-content-top {
		display: flex;
		justify-content: space-between;
		line-height: 2;
		padding-left: 20rpx;
		padding-right: 20rpx;
	}

	.order-record-content-top-l {
		color: #888;
	}

	.order-record-content-top-r {
		color: #18b566;
		font-weight: bold;
	}

	.cancel-state {
		color: #82848a;
	}

	.payment-state {
		color: #ff9900;
	}

	.payment-state-txt {
		padding-left: 4rpx;
	}

	.order-record-content-middle {
		display: flex;
		align-items: center;
		padding-left: 20rpx;
		padding-right: 20rpx;

	}

	.order-record-content-middle-l {
		flex: 1;
	}

	.order-record-content-middle-r {
		flex: 2.5;
	}

	.order-record-content-bottom {
		display: flex;
		justify-content: space-between;
		line-height: 2;
		padding-left: 20rpx;
		padding-right: 20rpx;
	}


	.order-record-time {
		color: #888;
	}

	.order-record-amount {
		color: black;
	}

	.order-record-actions {
		display: flex;
		justify-content: flex-end;
		padding-left: 20rpx;
		padding-right: 20rpx;
		padding-top: 10rpx;
	}

	.cancel-btn {
		padding-right: 28rpx;
	}

	.collection-name {
		display: flex;
		justify-content: space-between;
	}

	.collection-name-l {
		font-weight: bold;
	}

	.collection-name-r {}
</style>
