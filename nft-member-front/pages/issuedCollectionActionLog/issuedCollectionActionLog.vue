<template>
	<view>
		<view class="orders">
			<template v-for="record in actionLogs">
				<view class="order">
					<view class="order-section1">
						<view class="order-section1-t">{{record.memberNickName}}</view>
						<view class="order-section1-b">{{record.actionTime}}</view>
					</view>
					<view class="order-section2">
						<text>{{record.actionDesc}}</text>
					</view>
				</view>
				<u-line />
			</template>
		</view>
		<view class="no-data">我是有底线的~</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				issuedCollectionId: '',
				actionLogs: []
			}
		},
		onLoad(option) {
			this.issuedCollectionId = option.id;
			this.findIssuedCollectionActionLog();
		},
		methods: {
			findIssuedCollectionActionLog() {
				var that = this;
				this.$u.get('/collection/findIssuedCollectionActionLog', {
					issuedCollectionId: that.issuedCollectionId
				}).then(res => {
					that.actionLogs = res.data;
				});
			},
		}
	}
</script>

<style>
	.no-data {
		text-align: center;
		line-height: 3;
	}

	.orders {
		padding-bottom: 20rpx;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.order {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-top: 12rpx;
		padding-bottom: 12rpx;
	}

	.order-section1 {}

	.order-section1-t {
		line-height: 2;
	}

	.order-section1-b {
		line-height: 2;
		color: #888;
	}

	.order-section2 {}

	.order-section2-l {
		padding-right: 8rpx;
	}
</style>
