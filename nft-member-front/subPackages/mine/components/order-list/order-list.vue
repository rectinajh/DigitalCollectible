<template>
	<view>
		<view class="item" v-for="(item,index) in list" :key="index" @click="itemClick(item.id)">
			<view class="top">
				<view class="code">
					订单编号：{{item.orderNo}}
				</view>
				<view class="type" v-if="item.giveDirection" :class="{given:item.giveDirection == 'from'}">
					{{item.giveDirection == 'from' ? '已转出' : '已转入'}}
				</view> 
				<view class="type" v-else :class="[{given:item.state == '2'},{wait:item.state == '1'}]">
					{{item.stateName}}
				</view>
			</view>
			<view class="main">
				<view class="left">
					<u-image class="collection-cover" width="160rpx" height="160rpx" mode="aspectFill" 
					border-radius="16" :src="item.collectionCover">
					</u-image>
					<view class="tag" v-if="item.commodityType==2">
						盲盒
					</view>
				</view>
				<view class="right">
					<view class="name">
						{{item.collectionName}}
					</view>
					<view class="user" v-if="item.giveDirection">
						{{item.giveDirection == 'from' ? `接受者:${item.giveToMobile}` : `转赠者:${item.giveFromMobile}`}}
					</view>
					<view class="price" v-if="item.amount">
						￥{{common.moneyFormat(item.amount)}}
					</view>
					<view class="date">
						{{item.giveTime||item.createTime}}
					</view>
				</view>
			</view>
			
			<view class="btn" v-if="item.state == '1'" @click.stop="payClick(item)">
				去支付
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name:"order-list",
		props:{
			list:{
				type:Array,
				default:[]
			}
		},
		data() {
			return {
				
			};
		},
		methods:{
			payClick(item){
				this.$emit('payClick',item)
			},
			itemClick(id){
				this.$emit('itemClick',id)
			}
		}
	}
</script>

<style lang="scss" scoped>
	.item{
		margin-top: 24rpx;
		border-radius: 24rpx;
		background-color: #212121;
		padding: 24rpx;
		position: relative;
		.top{
			display: flex;
			align-items: center;
			justify-content: space-between;
			font-size: 28rpx;
			padding-bottom: 24rpx;
			.code{
				color: #999999;
			}
			.type{
				color: #CCCCCC;
			}
			.given{
				color: #22D86E;
			}
			.wait{
				color: #FCE6B7;
			}
		}
		.main{
			padding-top: 24rpx;
			display: flex;
			align-items: center;
			border-top: 1rpx solid #FFFFFF14;
			.left{
				position: relative;
				.tag{
					background: #00000099;
					position: absolute;
					left: 0;
					top: 0;
					font-size: 22rpx;
					width: 64rpx;
					height: 38rpx;
					line-height: 38rpx;
					text-align: center;
					border-radius: 0 0 16rpx 0;
				}
			}
			.right{
				margin-left: 24rpx;
				.name{
					font-size: 30rpx;
				}
				.user{
					font-size: 28rpx;
					color: #CCCCCC;
					margin-top: 16rpx;
					margin-bottom: 16rpx;
				}
				.date{
					font-size: 22rpx;
					color: #999999;
				}
				.price{
					margin-top: 44rpx;
					font-size: 28rpx;
				}
			}
		}
		
		.btn{
			width: 144rpx;
			height: 56rpx;
			line-height: 58rpx;
			font-size: 24rpx;
			color: #644205;
			background-color: #FCE6B7;
			border-radius: 38rpx;
			position: absolute;
			text-align: center;
			right: 24rpx;
			bottom: 32rpx;
		}
	}
</style>
