<template>
	<view class="item" @click="latestCollectionDetailPage(item.id)">
		<view class="img">
			<u-image :src="item.cover" width="240" height="240"></u-image>
		</view>
		<view class="info">
			<view class="name">
				{{item.name}}
			</view>
			<view class="stock">
				<view class="left">
					限量
				</view>
				<view class="right">
					{{item.quantity}}份
				</view>
			</view>
			<view class="small" v-if="showTimeInfo">
				<view class="time">
					3月21日 12:00
				</view>
				<view class="price">
					￥{{(item.price*100/100).toFixed(2)}}
				</view>
			</view>
			<view class="large" v-else>
				<view class="price">
					￥{{(item.price*100/100).toFixed(2)}}
				</view>
			</view>
		</view>
		<view class="btnNotice">
			开售提醒
		</view>
	</view>
</template>

<script>
	export default {
		name:"presale-item",
		props:{
			item:{
				type:Object, 
				default:()=>{
					return {}
				}
			},
			showTimeInfo:{
				type:Boolean,
				default:false
			}
		},
		mounted() {
			
		},
		data() {
			return {
				
			};
		},
		methods:{
			latestCollectionDetailPage(id) {
				this.$emit('clickItem',id)
			},
		}
	}
</script>

<style lang="scss" scoped>
	.item{
		display: flex;
		background-color: #212121;
		border: 1rpx solid #2D2D2D;
		border-radius: 24rpx;
		overflow: hidden;
		margin-bottom: 20rpx;
		position: relative;
		height: 240rpx;
		.img{
			border-radius: 24rpx;
			overflow: hidden;
		}
		.info{
			margin-left: 24rpx;
			margin-top: 24rpx;
			.name{
				font-size: 30rpx;
				color: #fff;
				margin-bottom: 16rpx;
			}
			.stock{
				display: flex;
				align-content: center;
				margin-top: 10rpx;
				border-radius: 8rpx; 
				overflow: hidden;
				width: 144rpx;
				height: 32rpx;
				.left{
					font-size: 22rpx;
					color: #644205;
					background-color: #FCE6B7;
					width: 52rpx;
					text-align: center;
				}
				.right{
					font-size: 22rpx;
					color: #FCE6B7;
					background-color: #46433B;
					width: 92rpx;
					text-align: center;
				}
			}
			.large{
				.price{
					margin-top: 60rpx;
					font-size: 32rpx;
					font-family: 'OPPOSans-M';
				}
			}
			.small{
				.time{
					font-size: 22rpx;
					color: #999999;
					margin-top: 16rpx;
				}
				.price{
					margin-top: 26rpx;
					font-size: 22rpx;
					font-family: 'OPPOSans-M';
				}
			}
			
		}
		.btnNotice{
			position: absolute;
			width: 144rpx;
			height: 56rpx;
			right: 24rpx;
			bottom: 24rpx;
			background-color: #FCE6B7;
			border-radius: 38rpx;
			font-size: 24rpx;
			color: #644205;
			text-align: center;
			line-height: 56rpx;
		}
	}
</style>