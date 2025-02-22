<template>
	<view class="item" @click="latestCollectionDetailPage(item.id)">
		<view class="imgInfo">
			<view class="head">
				<view class="type">
					<view class="common origin" v-show="item.price%2!=0">
						<view class="img">
							<u-image src="@/static/img/common/original.png" width="112" height="40"></u-image>
						</view>
						<view class="text">
							原创设计
						</view>
					</view>
					<view class="common choiceness" v-show="item.price%2==0">
						<view class="img">
							<u-image src="@/static/img/common/choiceness.png" width="112" height="40"></u-image>
						</view>
						<view class="text">
							灵境精选
						</view>
					</view>
				</view>
				<view class="status" v-show="item.surplusSecond > 0 && item.surplusSecond <= 86400">
					<view class="img">
						<u-image src="@/static/img/common/longMask.png" width="196" height="40"></u-image>
					</view>
					<view class="text presale">
						<u-count-down :show-days="false" color="#9CFF8E" separator-size="20"
							separator-color="#9CFF8E" bg-color="rgba(0,0,0,0)" font-size="20"
							:timestamp="item.surplusSecond"></u-count-down>
						<p class="intruduction">即将开售</p>
					</view>
				</view>
				<view class="status" v-show="item.stock <= 10">
					<view class="img">
						<u-image src="@/static/img/common/shortMask.png" width="106" height="40"></u-image>
					</view>
					<view class="text shortage">
						即将售罄
					</view>
				</view>
			</view>
			<view class="img">
				<u-image mode="aspectFill" width="332rpx" height="332rpx" :src="item.cover"></u-image>
			</view>
		</view>
		<view class="textInfo">
			<view class="title">
				<view class="name">
					{{item.name}}
				</view>
				<view class="btnPreview">
					<u-image width="32rpx" height="32rpx" src="@/static/img/common/3d.png"></u-image>
				</view>
			</view>
			<view class="creator">
				<view class="avatar">
					<u-image width="32rpx" height="32rpx" :src="item.creatorAvatar"></u-image>
				</view>
				<view class="name">
					{{item.creatorName}}
				</view>
			</view>
			<view class="count">
				<view class="stock">
					<view class="left">
						限量
					</view>
					<view class="right">
						{{item.quantity}}份
					</view>
				</view>
				<view class="follow">
					0人订阅
				</view>
			</view>
			<view class="price">
				<view class="symble">￥</view>
				<!-- price转换为小数点后两位 -->
				<view class="num">{{(item.price*100/100).toFixed(2)}}</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name:"waterfall-item",
		props:{
			item:{
				type:Object,
				default:{}
			}
		},
		data() {
			return {
				
			};
		},
		mounted() {
			
		},
		methods:{
			latestCollectionDetailPage(id) {
				uni.navigateTo({
					url: '/subPackages/collection/collectionDetail/collectionDetail?id=' + id
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.item{
		margin-bottom: 20rpx;
		min-height: 332rpx;
		background: linear-gradient(180deg, #282828 0%, #1F1F1F 100%);
		border-radius: 24rpx;
		border: 1rpx solid linear-gradient(180deg, #2D2D2D00 0%, #3D3D3D 100%);
		overflow: hidden;
		.imgInfo{
			position: relative;
			.head{
				position: absolute;
				z-index: 100;
				left: 16rpx;
				top: 16rpx;
				display: flex;
				.type{
					.text{
						position: absolute;
						top: 6rpx;
						left: 8rpx;
						right: 16rpx;
						bottom: 4rpx;
					}
					.common{
						font-size: 22rpx;
					}
					.origin{
						color: #230D38;
					}
					.choiceness{
						color: #644205;
					}
				}
				.status{
					position: relative;
					.text{
						display: flex;
						align-items: center;
						font-size: 20rpx;
						position: absolute;
						top: 6rpx;
						left: 16rpx;
					}
					.intruduction{
						margin-left: 8rpx;
					}
					.presale{
						color: #9CFF8E;
					}
					.shortage{
						color: #FF7366;
					}
				}
			}
		}
		.textInfo{
			padding: 20rpx;
			.title{
				position: relative;
				.name{
					padding-right: 50rpx;
					font-size: 28rpx;
					line-height: 40rpx;
					// 超过两行显示省略号
					display: -webkit-box;
					-webkit-line-clamp: 2;
					-webkit-box-orient: vertical;
					overflow: hidden;
					text-overflow: ellipsis;
				}
				.btnPreview{
					position: absolute;
					right: 0rpx;
					top: 0rpx;
				}
				.btnPreview{
					position: absolute;
					right: 0rpx;
					top: 0rpx;
				}
			}
			.creator{
				display: flex;
				margin-top: 10rpx;
				.name{
					font-size: 22rpx;
					color: #ccc;
					margin-left: 8rpx;
					// 超过一行显示省略号
					display: -webkit-box;
					-webkit-line-clamp: 1;
					-webkit-box-orient: vertical;
					overflow: hidden;
					text-overflow: ellipsis;
					line-height: 30rpx;
				}
				.avatar{
					border-radius: 100%;
					overflow: hidden;
				}
			}
			.count{
				display: flex;
				align-content: center;
				justify-content: space-between;
				margin-top: 10rpx;
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
				.follow{
					font-size: 22rpx;
					color: #ccc;
					margin-top: 10rpx;
				}
			}
			.price{
				display: flex;
				align-items: flex-end;
				color: #FCE6B7;
				font-family: 'OPPOSans-M';
				.symble{
					font-size: 24rpx;
				}
				.num{
					margin-top: 24rpx;
					font-size: 32rpx;
				}
			}
		}
	}
</style>