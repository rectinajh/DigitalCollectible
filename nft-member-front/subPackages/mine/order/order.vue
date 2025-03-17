<template>
	<view class="page-content">
		<view class="tabs">
			<view class="tab" :class="{active:currentTab===index}" v-for="(tabItem,index) in tabs"
			@click="switchTab(index)">
				{{tabItem.name}}
			</view>
		</view>
		
		<view class="list">
			<view class="no-data" v-show="noDataFlag">
				<u-empty text="暂无数据"color="#fff" icon-size="240" 
				src="/static/img/common/default/noData.png"></u-empty>
			</view>
			<order-List :list="orderRecords" @itemClick="payOrderDetailPage" @payClick="showPayModal"></order-List>
		</view>
		
		
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
						<view class="amount">余额</view>
						<view>
							<u-icon name="checkmark-circle-fill" color="#FCE6B7" size="36"></u-icon>
						</view>
					</view>
				</view>
				<view class="custom-pay-modal-btn" @click="confirmPay">
					确认付款
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	import orderList from '@/subPackages/mine/components/order-list/order-list.vue'
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
		components:{
			orderList
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
				// uni.navigateTo({
				// 	url: `/pages/payOrderDetail/payOrderDetail?id=${id}`
				// });
				uni.navigateTo({
					url: `/subPackages/mine/orderDetail/orderDetail?id=${id}`
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

<style lang="scss" scoped>
@import 'order.scss'
</style>
