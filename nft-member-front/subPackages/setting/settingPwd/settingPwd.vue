<template>
	<view>
		<view class="title">
			请修改登录密码
		</view>
		<view class="form">
			<view class="item">
				<view class="label">
					新密码
				</view>
				<view class="phone formInput" id="phone">
					<u-input placeholder="最少8位数字、字母和特殊符号" v-model="passwordFir" type="password" :clearable="false"
						maxlength="12" :password-icon="true"
						placeholder-style="color:#666666;font-size:28rpx"></u-input>
				</view>
			</view>
			<view class="item">
				<view class="label">
					确认密码
				</view>
				<view class="verificationCode formInput" id="verificationCode">
					<u-input placeholder="请再次填写上面的密码" v-model="passwordSec" type="password" :clearable="false"
						maxlength="12" :password-icon="true"
						placeholder-style="color:#666666;font-size:28rpx"></u-input>
				</view>
			</view>

			<view class="login-btn" :class="{unable:!btnEnable}" @click="confirm">
				确认
			</view>
		</view>

	</view>
</template>

<script>
	import {
		mapGetters
	} from 'vuex'
	export default {
		data() {
			return {
				passwordFir: '',
				passwordSec: '',
				btnEnable: false
			};
		},
		computed: {
			...mapGetters(['userInfo'])
		},
		watch: {
			passwordSec(val) {
				if (val.length != '' && this.passwordFir == this.passwordSec) {
					this.btnEnable = true
				} else {
					this.btnEnable = false
				}
			},
			passwordFir(val) {
				if (val.length != '' && this.passwordFir == this.passwordSec) {
					this.btnEnable = true
				} else {
					this.btnEnable = false
				}
			}
		},
		methods: {
			confirm() {
				if (!this.btnEnable) {
					uni.showToast({
						title: '两次密码不一致',
						icon: 'none'
					})
					return
				}
				if(this.verifyPwd()){
					this.$u.post('/settingLoginPwd', {
						mobile: this.userInfo.mobile,
						pwd: this.passwordFir,
					}).then(res => {
						uni.showToast({
							title: '修改成功',
							icon: 'success',
							mask: true
						})
						setTimeout(()=>{
							uni.navigateBack()
						},1000)
					});
				};
			},
			verifyPwd() {
				// 用正则判断密码是否大于等于8位并且包含数字字母和特殊符号
				let reg = /^(?=.*\d)(?=.*[a-zA-Z])(?=.*[`~!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/;
				if (!reg.test(this.passwordFir)) {
					uni.showToast({
						title: '密码最少8位并且包含数字、字母和特殊符号',
						icon: 'none'
					})
					return false
				} else {}

				return true
			}
		}
	}
</script>

<style lang="scss" scoped>
	@import 'settingPwd.scss';
</style>