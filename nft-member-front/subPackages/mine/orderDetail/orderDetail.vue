<template>
	<view class="page-content">
		<view class="top">
			<view class="title" v-for="(item,index) in iconList" :key="index" v-if="item.value == orderDetail.state">
				<view class="icon">
					<u-image :src="item.icon" width="44" height="44"></u-image>
				</view>
				<view class="status" :class="item.class">
					{{item.name}}
				</view>
			</view>
			<view class="handleWait" v-if="orderDetail.state == '1'">
				<view class="text">
					<view class="amount">
						需支付：￥{{moneyFormat(orderDetail.amount)}}
					</view>
					<view class="countdown">
						剩余：
						<u-count-down :show-days="false" :show-hours="false" color="#fff"
							separator="zh" separator-size="26" bg-color="#181819"
							separator-color="#fff" font-size="26" :timestamp="orderDetail.surplusSecond">
						</u-count-down>
					</view>
				</view>
				<view class="btn" @click="showPayModalFlag = true;">
					去支付
				</view>
			</view>
			<view v-else style="height: 40rpx;width: 100%;"></view>
		</view>
		
		<view class="info">
			<view class="creator">
				<view class="creator-name">
					{{orderDetail.creatorName}}
				</view>
				<!-- <view class="arrow">
					<u-image src="/static/img/common/cell/arrow.png" width="12rpx" mode="widthFix"></u-image>
				</view> -->
			</view>
			<view class="collection">
				<view class="left">
					<u-image width="160" height="160" border-radius="16" mode="aspectFill" :src="orderDetail.collectionCover">
					</u-image>
				</view>
				<view class="right">
					<view class="name">
						{{orderDetail.collectionName}}
					</view>
					<view class="count">
						数量：1
					</view>
					<view class="price">
						￥{{moneyFormat(orderDetail.amount)}}
					</view>
				</view>
			</view>
		</view>
		
		<view class="order">
			<view class="item" v-for="(item,index) in orderFormList" v-if="item.value" :key="index"
			:class="{space:index!=orderFormList.length-1}">
				<view class="label">
					{{item.label}}:
				</view>
				<view class="value">
					{{item.value}}
				</view>
			</view>
		</view>
		
		<view class="fixed-bottom" v-if="orderDetail.state == '1'">
			<view class="btnGroup">
				<view class="cancel btn" @click="showCancelModalFlag = true;">
					取消订单
				</view>
				<view class="pay btn" @click="showPayModalFlag = true;">
					去支付
				</view>
			</view>
		</view>
		
		
		<u-modal v-model="showCancelModalFlag" title="提示" :show-cancel-button="true" cancel-text="暂时不要"
		confirm-color="#FCE6B7" cancel-color="#fff" backgroundColor="#181819" :title-style="{color:'#fff'}" :content-style="{color:'#fff'}"
		confirm-text="取消交易" content="是否要取消交易" @confirm="cancelPay">
		</u-modal>
		<u-popup v-model="showPayModalFlag" mode="bottom" border-radius="14" :closeable="true">
			<view class="pay-modal">
				<view class="pay-modal-amount">
					<text>￥</text><text class="pay-modal-amount-value">{{moneyFormat(orderDetail.amount)}}</text>
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
	export default {
		data() {
			return {
				showPayModalFlag: false,
				showCancelModalFlag: false,
				orderId: '',
				orderDetail: '',
				iconList:[
					{
						icon:'/static/img/mine/order/wait.png',
						value:'1',
						name:'等待付款',
						class:'wait'
					},
					{
						icon:'/static/img/mine/order/complete.png',
						value:'2',
						name:'交易完成',
						class:'complete'
					},
					{
						icon:'/static/img/mine/order/cancel.png',
						value:'3',
						name:'已取消',
						class:'cancel'
					},
				],
				orderFormList:[
					{
						label:'订单编号',
						value:'',
						params:'orderNo'
					},
					{
						label:'创建时间',
						value:'',
						params:'createTime'
					},
					{
						label:'付款时间',
						value:'',
						params:'paidTime'
					},
					{
						label:'支付方式',
						value:'余额',
						params:''
					},
				]
			}
		},
		onLoad(option) {
			this.orderId = option.id;
			this.getOrderDetail();
		},
		methods: {
	
			confirmPay() {
				var that = this;
				this.$u.post('/transaction/confirmPay', {
					orderId: that.orderId,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '支付成功!',
						duration: 2000,
						mask: true,
						complete: function() {
							that.showPayModalFlag = false;
							that.getOrderDetail();
						}
					});
				});
			},
	
			cancelPay() {
				var that = this;
				this.$u.post('/transaction/cancelPay', {
					orderId: that.orderId,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '取消成功!',
						duration: 2000,
						mask: true,
						complete: function() {
							that.getOrderDetail();
						}
					});
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
	
			getOrderDetail() {
				var that = this;
				this.$u.get('/transaction/findMyPayOrderDetail', {
					id: that.orderId
				}).then(res => {
					that.orderDetail = res.data;
					for (let i = 0; i < that.orderFormList.length; i++) {
						let item = that.orderFormList[i];
						if (item.params) {
							item.value = that.orderDetail[item.params];
						}
					}
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
@import 'orderDetail.scss'
</style>
