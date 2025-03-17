<template>
	<view class="item" @click="latestCollectionDetailPage(item.id)">
		<view class="imgInfo">
			<view class="img">
				<u-image mode="aspectFill" width="332rpx" height="332rpx" :src="item.collectionCover"></u-image>
			</view>
		</view>
		<view class="textInfo">
			<view class="title">
				<view class="name">
					{{item.collectionName}}
				</view>
			</view>
			<view class="count">
				<!-- <view class="creator">
					<view class="avatar" v-if="item.creatorAvatar">
						<u-image width="32rpx" height="32rpx" :src="item.creatorAvatar"></u-image>
					</view>
					<view class="name">
						{{item.creatorName}}
					</view>
				</view> -->
				<view class="follow">
					#{{item.collectionSerialNumber}}/{{item.quantity}}
				</view>
				<view class="price">
					<view class="symble">￥</view>
					<!-- price转换为小数点后两位 -->
					<view class="num">{{item.resalePrice}}</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name:"market-item",
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
				this.$emit('itemClick',id)
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
		}
		.textInfo{
			padding: 20rpx;
			.title{
				position: relative;
				.name{
					// padding-right: 50rpx;
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
			.count{
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin-top: 10rpx;
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
						max-width: 170rpx;
					}
					.avatar{
						border-radius: 100%;
						overflow: hidden;
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