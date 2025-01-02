<template>
	<view>
		<view class="no-data" v-show="noDataFlag">
			<u-empty text="暂无邀请记录" mode="list"></u-empty>
		</view>
		<view class="invite-records">
			<view class="invite-record" v-for="(record,index) in inviteRecords">
				<view class="invite-num">{{index + 1}}</view>
				<view class="invite-mobile">
					<view class="invite-mobile-label">用户手机号</view>
					<view class="invite-mobile-value">{{record.mobile}}</view>
				</view>
				<view class="bought-state-info">
					<view class="invite-state">{{record.inviteSuccessFlag ? '邀请成功' : '待邀请成功'}}</view>
					<view class="bought-state">{{record.boughtFlag ? '购买过藏品' : '尚未购买藏品'}}</view>
				</view>
			</view>
		</view>
		<view class="no-more" v-show="!noDataFlag">没有更多记录了-</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				inviteRecords: [],
				noDataFlag: false,
			}
		},
		onLoad() {
			this.findMyInviteRecord();
		},
		methods: {

			findMyInviteRecord() {
				var that = this;
				this.$u.get('/member/findMyInviteRecord', {}).then(res => {
					that.inviteRecords = res.data;
					that.noDataFlag = that.inviteRecords.length == 0;
				});
			},

		}
	}
</script>

<style>
	page {
		height: 100% !important;
		background-color: #f3f3f3;
	}

	.no-more {
		text-align: center;
		padding-top: 40rpx;
		font-size: small;
		color: #777a7e;
	}

	.no-data {
		padding-top: 64rpx;
	}

	.bought-state {
		font-size: small;
		color: #888;
	}

	.invite-state {
		padding-bottom: 12rpx;
		color: orange;
		font-weight: bold;
		letter-spacing: 4rpx;
	}

	.bought-state-info {
		flex: 1;
		text-align: right;
	}

	.invite-mobile-value {}

	.invite-mobile-label {
		color: #888;
		padding-bottom: 12rpx;
		font-size: small;
	}

	.invite-mobile {}

	.invite-num {
		background-color: #eeeeee;
		color: #b6b6b6;
		border-radius: 50%;
		height: 48rpx;
		width: 48rpx;
		text-align: center;
		font-size: small;
		line-height: 48rpx;
		margin-right: 20rpx;
	}

	.invite-record {
		display: flex;
		align-items: center;
		padding: 20rpx 32rpx;
		margin-top: 20rpx;
		background: white;
	}

	.invite-records {}
</style>
