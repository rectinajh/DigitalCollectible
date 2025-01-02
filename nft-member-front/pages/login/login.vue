<template>
	<view>
		<u-modal v-model="verificationCodeTipFlag" title="提示" :show-cancel-button="false" confirm-text="我知道了">
			<view class="slot-content">
				<view class="verification-code-tip">
					<view>验证码为：6666</view>
					<view>输入后点击登录，即可体验完整功能</view>
				</view>
			</view>
		</u-modal>
		<u-verification-code seconds="60" ref="uCode" @change="codeChange"></u-verification-code>
		<view class="login-page">
			<view class="u-flex u-row-center">
				<u-image width="256rpx" height="256rpx" src="/static/img/c2c.png"></u-image>
			</view>
			<u-form>
				<u-form-item label-position="top" label="手机号" label-width="150">
					<u-input placeholder="请输入11位手机号" v-model="mobile" type="number" :clearable="false"></u-input>
				</u-form-item>
				<u-form-item label-position="top" label="验证码" label-width="150">
					<u-input placeholder="请输入验证码" v-model="verificationCode" type="text" :clearable="false"></u-input>
					<u-button slot="right" size="mini" @click="getCode">{{codeTips}}</u-button>
				</u-form-item>
				<u-form-item label-position="top" label="邀请码(选填)" label-width="150">
					<u-input placeholder="邀请码" v-model="inviteCode" type="text" :clearable="false"
						:disabled="inviteCodeReadonly"></u-input>
				</u-form-item>
			</u-form>
			<view class="login-btn">
				<u-button type="primary" @click="login">立即登录</u-button>
			</view>

			<view class="login-tip">未注册的手机号验证后可自动登录</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				mobile: '',
				verificationCode: '',
				inviteCode: '',
				inviteCodeReadonly: false,
				codeTips: '',
				verificationCodeTipFlag: false,
			}
		},
		onLoad(option) {
			var inviteCode = option.inviteCode;
			if (inviteCode) {
				this.inviteCode = inviteCode;
				this.inviteCodeReadonly = true;
			}
		},
		methods: {

			codeChange(text) {
				this.codeTips = text;
			},

			getCode() {
				var that = this;
				if (!this.$refs.uCode.canGetCode) {
					return;
				}
				if (that.mobile === null || that.mobile === "") {
					uni.showToast({
						title: "请输入手机号",
						icon: "none"
					});
					return;
				}
				if (that.mobile.length != 11) {
					uni.showToast({
						title: "请输入11位的手机号",
						icon: "none"
					});
					return;
				}
				uni.showLoading({
					title: '正在获取验证码',
					mask: true
				});
				this.$u.get('/sendLoginVerificationCode', {
					mobile: that.mobile
				}).then(res => {
					uni.hideLoading();
					this.$refs.uCode.start();
					that.verificationCodeTipFlag = true;
				});
			},

			updatePushClientId() {
				var clientInfo = plus.push.getClientInfo();
				this.$u.post('/member/updatePushClientId', {
					pushClientId: clientInfo.clientid
				}).then(res => {});
			},

			login() {
				var that = this;
				if (that.mobile === null || that.mobile === "") {
					uni.showToast({
						title: "请输入手机号",
						icon: "none"
					});
					return;
				}
				if (that.verificationCode === null || that.verificationCode === "") {
					uni.showToast({
						title: "请输入验证码",
						icon: "none"
					});
					return;
				}
				this.$u.post('/login', {
					mobile: that.mobile,
					verificationCode: that.verificationCode,
					inviteCode: that.inviteCode
				}).then(res => {
					var tokenInfo = res.data;
					uni.setStorageSync('tokenName', tokenInfo.tokenName);
					uni.setStorageSync('tokenValue', tokenInfo.tokenValue);
					uni.setStorageSync('accountId', tokenInfo.accountId);

					// #ifdef APP-PLUS
					//that.updatePushClientId();
					// #endif
					uni.reLaunch({
						url: "../home/home"
					});
				});
			},

		}
	}
</script>

<style>
	.verification-code-tip {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.login-btn {
		padding-top: 32rpx;
	}

	.welcome {
		color: #3c93ef;
		font-weight: bold;
		font-size: 28px;
		line-height: 2;
	}

	.login-tip {
		color: #888;
		text-align: center;
		padding-top: 64rpx;
		font-size: small;
	}

	.login-page {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 32rpx;
	}
</style>
