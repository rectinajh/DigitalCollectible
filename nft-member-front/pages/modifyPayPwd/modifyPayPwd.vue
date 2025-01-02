<template>
	<view>
		<u-verification-code seconds="60" ref="uCode" @change="codeChange"></u-verification-code>
		<u-popup v-model="showReconfirmModal" mode="bottom" border-radius="14" :mask-close-able="false">
			<view class="reconfirm-popup">
				<view class="popup-title">
					<view class="popup-title-l">安全验证</view>
					<view class="popup-title-r" @click="showReconfirmModal = false;">取消</view>
				</view>
				<view>
					<u-form>
						<u-form-item label-position="top" label="" label-width="150">
							<u-input placeholder="请输入验证码" v-model="verificationCode" type="text" :clearable="false">
							</u-input>
							<u-button slot="right" type="success" size="mini" @click="getCode">{{codeTips}}</u-button>
						</u-form-item>
					</u-form>
					<u-button type="primary" @click="modifyPwdInner">
						确认</u-button>
				</view>
			</view>
		</u-popup>
		<view class="page-content">
			<u-form>
				<u-form-item label-position="top" label="输入新支付密码" label-width="150">
					<u-input placeholder="8-15位,且首位必须为字母" v-model="newPwd" type="password" :clearable="false"></u-input>
				</u-form-item>
				<u-form-item label-position="top" label="确认新密码" label-width="150">
					<u-input placeholder="请确认您的支付密码" v-model="confirmPwd" type="password" :clearable="false"></u-input>
				</u-form-item>
			</u-form>
		</view>
		<view class="fixed-bottom">
			<u-button type="primary" @click="modifyPwd">确认</u-button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				newPwd: '',
				confirmPwd: '',
				showReconfirmModal: false,
				verificationCode: '',
				codeTips: '',
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
				uni.showLoading({
					title: '正在获取验证码',
					mask: true
				});
				this.$u.get('/member/sendModifyPayPwdVerificationCode', {}).then(res => {
					uni.hideLoading();
					uni.showToast({
						title: "验证码已发送",
						icon: "none"
					});
					this.$refs.uCode.start();
				});
			},

			modifyPwdInner() {
				var that = this;
				if (that.verificationCode == null || that.verificationCode == '') {
					uni.showToast({
						title: "请输入验证码",
						icon: "none"
					});
					return;
				}
				this.$u.post('/member/modifyPayPwd', {
					newPwd: that.newPwd,
					verificationCode: that.verificationCode,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '密码修改成功!',
						duration: 2000,
						complete: function() {
							setTimeout(() => {
								uni.navigateBack();
							}, 2000);
						}
					});
				});
			},

			modifyPwd() {
				var that = this;
				if (that.newPwd == null || that.newPwd == '') {
					uni.showToast({
						title: "请输入新支付密码",
						icon: "none"
					});
					return;
				}
				if (that.confirmPwd == null || that.confirmPwd == '') {
					uni.showToast({
						title: "请再输入新支付密码",
						icon: "none"
					});
					return;
				}
				if (that.newPwd != that.confirmPwd) {
					uni.showToast({
						title: "密码不一致",
						icon: "none"
					});
					return;
				}
				var passwordPatt = /^[A-Za-z][A-Za-z0-9]{5,14}$/;
				if (!passwordPatt.test(that.newPwd)) {
					uni.showToast({
						title: "请输入以字母开头,长度为6-15个字母和数字的密码",
						icon: "none"
					});
					return;
				}
				that.showReconfirmModal = true;
				that.verificationCode = '';
			},
		}
	}
</script>

<style>
	.page-content {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.reconfirm-popup {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.popup-title {
		display: flex;
		justify-content: space-between;
	}

	.popup-title-l {
		font-weight: bold;
	}

	.popup-title-r {
		color: #909399;
	}

	.fixed-bottom {
		position: fixed;
		bottom: 60rpx;
		left: 0rpx;
		width: 100%;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}
</style>
