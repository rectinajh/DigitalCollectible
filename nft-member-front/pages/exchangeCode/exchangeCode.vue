<template>
	<view>
		<u-modal v-model="exchangeResultFlag" :show-title="false" :show-cancel-button="false" confirm-text="我知道了"
			@confirm="toMyPage">
			<view class="slot-content">
				<view class="mystery-box-result-content">
					<view class="mystery-box-result-title">恭喜获得</view>
					<view class="mystery-box-result-name">{{exchangeResult.name}}</view>
					<image style="width: 200rpx; height: 200rpx;" :src="exchangeResult.cover">
					</image>
				</view>
			</view>
		</u-modal>
		<view class="friend-account-tip">
			请输入空投码:
		</view>
		<view class="account-textarea">
			<textarea class="account-textarea-inner" placeholder="请输入空投码" v-model="code"></textarea>
		</view>
		<view class="action-btn">
			<u-button type="primary" @click="exchange">立即兑换</u-button>
		</view>

		<view class="give-explain">
			<view class="give-explain-title">规则说明</view>
			<view class="give-explain-items">
				<view class="give-explain-item">
					1、一个空投码仅可使用一次，兑换码使用后即无效；
				</view>
				<view class="give-explain-item">
					2、空投码兑换成功后，藏品可在我的藏品里查看；
				</view>
				<view class="give-explain-item">
					3、严禁空投码场外交易，请不要向陌生人购买空投码，谨防诈骗盒恶意炒作；
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				code: '',
				exchangeResultFlag: false,
				exchangeResult: '',
			}
		},
		onLoad(option) {},
		methods: {

			toMyPage() {
				uni.reLaunch({
					url: "../my/my"
				});
			},

			exchange() {
				var that = this;
				if (that.code === null || that.code === "") {
					uni.showToast({
						title: "请输入空投码",
						icon: "none"
					});
					return;
				}
				var that = this;
				this.$u.post('/exchangeCode/exchange', {
					code: that.code
				}).then(res => {
					that.exchangeResult = res.data;
					that.exchangeResultFlag = true;
				});
			}
		}
	}
</script>

<style>
	
	.mystery-box-result-content {
		text-align: center;
		padding-top: 20px;
		padding-bottom: 20px;
	}
	
	.mystery-box-result-name {
		line-height: 2;
		color: #888;
	}
	
	.mystery-box-result-title {
		line-height: 2;
		font-size: larger;
	}
	
	.checked-flag {
		padding-bottom: 32rpx;
	}

	.receiver-info {
		padding-top: 32rpx;

	}

	.receiver-info-title {
		text-align: center;
	}

	.receiver-info-item {
		font-size: small;
		padding-bottom: 12rpx;
		padding-top: 12rpx;
	}

	.receiver-info-item-t {}

	.receiver-info-item-b {
		color: #888;
	}

	.common-modal {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.modal-title {
		display: flex;
		justify-content: space-between;
	}

	.modal-title-txt {
		font-weight: bold;
	}

	.close-modal-txt {
		color: #909399;
	}

	.give-explain {
		padding-left: 32rpx;
		padding-top: 32rpx;
		padding-right: 32rpx;
	}

	.give-explain-title {
		line-height: 2;
	}

	.give-explain-items {
		font-size: smaller;
		color: #888;
	}

	.give-explain-item {}

	.action-btn {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
	}

	.friend-account-tip {
		padding-top: 64rpx;
		padding-left: 23rpx;
		padding-bottom: 20rpx;
	}

	.account-textarea-inner {
		padding-top: 32rpx;
	}

	.account-textarea {
		background: #e7e7e7;
		padding-left: 32rpx;
		padding-right: 32rpx;
		margin-left: 32rpx;
		margin-right: 32rpx;
		border-radius: 20rpx;
		height: 260rpx;
	}

	.sub-title {
		padding: 32rpx;
	}

	.collection-info {
		padding-left: 32rpx;
		padding-right: 32rpx;
		background: #e7e7e7;
		margin-left: 32rpx;
		margin-right: 32rpx;
		display: flex;
		padding-top: 16rpx;
		padding-bottom: 16rpx;
		align-items: center;
		border-radius: 20rpx;
		font-size: small;
	}

	.collection-info-l {}

	.collection-info-r {
		padding-left: 32rpx;
		flex: 1;
		width: 0;
	}

	.collection-name {
		font-size: larger;
	}

	.collection-serial-number {
		line-height: 2;
		color: #888;
	}

	.creator {}
</style>
