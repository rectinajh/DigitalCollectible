<template>
	<view>
		<view class="no-data" v-show="noDataFlag">
			<u-empty text="暂无更多活动" mode="news"></u-empty>
		</view>
		<view class="activitys">
			<view class="activity" v-for="activity in activitys" @click="composeCollectionPage(activity.id)">
				<view class="activity-title u-line-1">{{activity.title}}</view>
				<view class="activity-2">
					<view class="activity-progress">合成进度<text
							class="stock">{{activity.stock}}</text>/{{activity.quantity}}</view>
					<view class="activity-end-time">截止时间:{{activity.activityTimeEnd}}</view>
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
				uni.navigateTo({
					url: '../composeCollection/composeCollection?id=' + id
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

<style>
	.no-data {
		padding-top: 64rpx;
	}

	.activitys {}

	.activity {
		background: white;
		margin: 32rpx;
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-bottom: 32rpx;
	}

	.activity-title {
		line-height: 3;
		font-size: large;
		color: black;
	}

	.activity-2 {
		display: flex;
		justify-content: space-between;
		font-size: small;
		color: #888;
	}

	.activity-progress {}

	.stock {
		color: #e99331;
	}

	.activity-end-time {}

	page {
		height: 100% !important;
		background-color: #f3f3f3;
	}
</style>
