<template>
	<view>
		<u-modal v-model="exchangeResultFlag" :show-title="false" :show-cancel-button="false" confirm-text="我知道了"
			backgroundColor="#181819" confirm-color="#FCE6B7" @confirm="toMyPage">
			<view class="slot-content">
				<view class="mystery-box-result-content">
					<view class="mystery-box-result-title">恭喜获得</view>
					<view class="mystery-box-result-name">{{exchangeResult.name}}</view> 
					<u-image width="200" height="200" :src="exchangeResult.cover" mode="aspectFill">
					</u-image>
				</view>
			</view>
		</u-modal>
		<view class="friend-account-tip">
			请输入空投码
		</view>
		<view class="account-textarea" @click="textFocus=true">
			<textarea class="account-textarea-inner" ref="myTextarea" :auto-height="true" :focus="textFocus"
			@blur="textFocus=false" v-model="code"></textarea>
		</view>
		<view class="action-btn" @click="exchange">
			立即兑换
		</view>

		<view class="give-explain">
			<view class="give-explain-title">规则说明:</view>
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
				textFocus:true,
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
		display: flex;
		flex-direction: column;
		align-items: center;
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
		padding-top: 48rpx;
		padding-right: 32rpx;
	}

	.give-explain-title {
		line-height: 2;
		color: #ccc;
		font-size: 26rpx;
	}

	.give-explain-items {
		font-size: smaller;
		color: #666666;
		font-size: 24rpx;
		line-height: 36rpx;
	}

	.give-explain-item {
		margin-top: 8rpx;
	}

	.action-btn {
		width: 686rpx;
		margin-left: 32rpx;
		height: 88rpx;
		background-color: #FCE6B7;
		color: #644205;
		font-size: 32rpx;
		line-height: 88rpx;
		text-align: center;
		border-radius: 60rpx;
		margin-top: 46rpx;
	}

	.friend-account-tip {
		padding-top: 78rpx;
		padding-bottom: 40rpx;
		width: 100%;
		text-align: center;
		font-size: 32rpx;
	}

	.account-textarea-inner {
		width: 100%;
		font-size: 28rpx;
	}

	.account-textarea {
		background: #212121;
		margin-left: 32rpx;
		margin-right: 32rpx;
		padding: 96rpx 120rpx;
		border-radius: 24rpx;
		min-height: 280rpx;
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
