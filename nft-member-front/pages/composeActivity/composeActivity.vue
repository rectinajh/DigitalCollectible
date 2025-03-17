<template>
	<view>
		<view class="no-data" v-show="noDataFlag">
			<u-empty text="暂无更多活动" mode="news"></u-empty>
		</view>
		<view class="activitys">
			<view class="activity" v-for="activity in activitys" @click="composeCollectionPage(activity.id)">
				<view class="left">
					<u-image src="/static/img/common/default/activityDefault.png" mode="aspectFill" width="160" height="160"></u-image>
				</view>
				<view class="right">
					<view class="activity-title u-line-1">{{activity.title}}</view>
					<view class="activity-2">
						<view class="progress">
							<view class="activity-progress">合成进度
							<text class="stock">{{activity.stock}}</text>/{{activity.quantity}}</view>
						</view>
						<view class="activity-end-time">截止时间:{{activity.activityTimeEnd}}</view>
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
				activitys: [],
				noDataFlag: false,
			}
		},
		onLoad() {
			this.findActiveComposeActivity();
		},
		methods: {

			composeCollectionPage(id) {
				// uni.navigateTo({
				// 	url: '../composeCollection/composeCollection?id=' + id
				// });
				uni.navigateTo({
					url: `/subPackages/collection/compose/compose?id=${id}`
				});
			},

			findActiveComposeActivity() {
				var that = this;
				this.$u.get('/composeActivity/findActiveComposeActivity').then(res => {
					that.activitys = res.data;
					that.noDataFlag = that.activitys.length == 0;
				});
			},

		}
	}
</script>

<style lang="scss" scoped>
	.no-data {
		padding-top: 64rpx;
	}

	.activitys {
		margin-top: 8rpx;
		padding: 0 24rpx;
	}

	.activity {
		background: #212121;
		border-radius: 24rpx;
		padding: 24rpx;
		margin-top: 32rpx;
		display: flex;
		align-items: center;
		
		.left{}
		.right{
			margin-left: 24rpx;
			height: 160rpx;
			display: flex;
			flex-direction: column;
			justify-content: space-evenly;
			padding: 8rpx 0;
		}
	}

	.activity-title {
		font-size: 30rpx;
		color: #fff;
	}

	.activity-2 {
		// display: flex;
		// justify-content: space-between;
		color: #999;
		.progress{
			font-size: 28rpx;
			.activity-progress {}
			.stock {
				color: #fff;
				margin-left: 8rpx;
			}
		}
	}

	

	.activity-end-time {
		font-size: 24rpx;
		color: #666666;
	}

	page {
		// height: 100% !important;
		// background-color: #f3f3f3;
	}
</style>
