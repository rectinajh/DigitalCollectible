<template>
	<view>
		<view class="page-content">
			<view class="notice-title">{{notice.title}}</view>
			<view class="notice-publish-time"><text class="type-name">{{notice.typeName}}</text>{{notice.publishTime}}
			</view>
			<view class="notice-content">
				<u-parse :html="notice.content"></u-parse>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				id: '',
				notice: '',
			}
		},
		onLoad(option) {
			this.id = option.id;
			this.getDetail();
		},
		methods: {
			getDetail() {
				var that = this;
				this.$u.get('/notice/getNoticeDetail', {
					id: that.id
				}).then(res => {
					that.notice = res.data;
				});
			},
		}
	}
</script>

<style>
	.notice-content {}

	.notice-publish-time {
		font-size: 24rpx;
		color: #888;
		line-height: 3;
		padding-bottom: 24rpx;
	}

	.type-name {
		font-size: 26rpx;
		padding-right: 12rpx;
		color: blue;
		font-weight: bold;
	}

	.notice-title {
		font-size: 39rpx;
	}

	.page-content {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}
</style>
