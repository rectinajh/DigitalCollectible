<template>
	<view>
		<!-- <u-modal v-model="verificationCodeTipFlag" title="提示" :show-cancel-button="false" confirm-text="我知道了">
			<view class="slot-content">
				<view class="verification-code-tip">
					<view>验证码为：6666</view>
					<view>输入后点击登录，即可体验完整功能</view>
				</view>
			</view>
		</u-modal> -->
		<u-verification-code seconds="60" ref="uCode" :keep-running="true" @change="codeChange"></u-verification-code>
		<view class="login-page">
			<view class="u-flex u-row-center">
				<view class="logo">
					<u-image width="240rpx" height="240rpx" src="/static/img/login/logo.png"></u-image>
				</view>
			</view>
			<view class="title">
				<view class="codeTitle" :class="{checked:loginType==='code',unchecked:loginType!=='code'}"
					@click="loginType='code'">
					免密登录
				</view>
				<view class="pswTitle" :class="{checked:loginType==='psw',unchecked:loginType!=='psw'}"
					@click="loginType='psw'">
					账号登录
				</view>
			</view>
			<u-form v-show="loginType==='code'" :border-bottom="false">
				<u-form-item :border-bottom="false">
					<view class="phone formInput" id="phone">
						<u-input placeholder="请输入你的手机号" v-model="mobile" type="number" :clearable="false"
							placeholder-style="color:#666666;font-size:28rpx"></u-input>
					</view>
				</u-form-item>
				<u-form-item :border-bottom="false">
					<view class="verificationCode formInput" id="verificationCode">
						<u-input placeholder="请输入验证码" v-model="verificationCode" type="text" :clearable="false"
							placeholder-style="color:#666666;font-size:28rpx"></u-input>
					</view>
					<view class="codeBtn" @click="getCode">
						{{codeTips}}
					</view>
					<!-- <u-button :hair-line="false" :plain="true" @click="getCode">{{codeTips}}</u-button> -->
				</u-form-item>
				<!-- <u-form-item label-position="top" label="邀请码(选填)" label-width="150">
					<u-input placeholder="邀请码" v-model="inviteCode" type="text" :clearable="false"
						:disabled="inviteCodeReadonly"></u-input>
				</u-form-item> -->
			</u-form>

			<u-form v-show="loginType==='psw'" :border-bottom="false">
				<u-form-item :border-bottom="false">
					<view class="phone formInput" id="phone">
						<u-input placeholder="请输入你的手机号" v-model="mobile" type="number" :clearable="false"
							placeholder-style="color:#666666;font-size:28rpx"></u-input>
					</view>
				</u-form-item>
				<u-form-item :border-bottom="false">
					<view class="verificationCode formInput" id="verificationCode">
						<u-input placeholder="请输入密码" v-model="password" type="password" :clearable="false"
							:password-icon="false" placeholder-style="color:#666666;font-size:28rpx"></u-input>
					</view>
					<view class="forgetPsw" @click="forgetPsw">
						忘记密码
					</view>
					<!-- <u-button :hair-line="false" :plain="true" @click="getCode">{{codeTips}}</u-button> -->
				</u-form-item>
				<!-- <u-form-item label-position="top" label="邀请码(选填)" label-width="150">
					<u-input placeholder="邀请码" v-model="inviteCode" type="text" :clearable="false"
						:disabled="inviteCodeReadonly"></u-input>
				</u-form-item> -->
			</u-form>
			<!-- <view class="login-btn">
				<u-button type="primary" @click="login">立即登录</u-button>
			</view> -->

			<view class="agreement">
				<view class="checkBox" @click="check">
					<view v-show="!agreementChecked" class="uncheck"></view>
					<view v-show="agreementChecked" class="checked">
						<u-image src="@/static/img/login/checkbox.png" width="32" height="32"></u-image>
					</view>
					<view class="dialog" v-show="showTips">
						<view class="img">
							<u-image src="@/static/img/login/dialog.png" width="212rpx" height="58rpx"></u-image>
						</view>
						<view class="text">
							请阅读并同意协议
						</view>
					</view>
				</view>
				<view class="text">
					<span @click="check">我已阅读并同意</span>
					<!-- <span class="agreementClick" @click.stop="openAgreement('user')">《用户协议》</span> -->
					<a class="agreementClick" :href="userAgreementLink" target="_blank">《用户协议》</a>
					和
					<!-- <span class="agreementClick" @click.stop="openAgreement('privacy')">《隐私政策》</span> -->
					<a class="agreementClick" :href="privacyPolicyLink" target="_blank">《隐私政策》</a>
				</view>
				<!-- <u-checkbox label-size="24" shape="circle" icon-size="32" active-color="#FCE6B7" v-model="agreementChecked">
					我已阅读并同意
					<a :href="userAgreementLink" target="_blank">《用户协议》</a>
					和s
					<a :href="privacyPolicyLink" target="_blank">《隐私政策》</a>
				</u-checkbox> -->


			</view>

			<view class="login-btn" :class="{agreementUnchecked:!agreementChecked}" @click="login">
				<!-- <u-button type="primary" :disabled="!agreementChecked" @click="login">立即登录</u-button> -->
				登录
			</view>

			<view class="btnGroup">
				<view class="btnItem" v-for="(item,index) in btnList" :key="index" @click="btnGroupClick(item.value)">
					<view class="border"></view>
					<view class="img">
						<u-image :src="item.src" width="56rpx" height="56rpx"></u-image>
					</view>
				</view>
			</view>

			<!-- <view class="login-tip">未注册的手机号验证后可自动登录</view> -->
		</view>
		<view class="bottom">
			<view class="img">
				<u-image src="@/static/img/login/lingjing.png" width="74rpx" height="36rpx"></u-image>
			</view>
			<view class="slogan">
				<view class="leftLine"></view>
				<view class="text">
					发现更多精彩
				</view>
				<view class="rightLine"></view>
			</view>
		</view>

		<view class="bg">
			<u-image src="@/static/img/login/bg.png" width="750" mode="widthFix"></u-image>
		</view>

		<agreement-popup :showAgreement="showAgreement" :type="agreementType"
			@close="showAgreement=false"></agreement-popup>
	</view>
</template>

<script>
	import agreementPopup from "./components/agreementPopup/agreementPopup.vue"
	import {mapGetters} from 'vuex'
	export default {
		data() {
			return {
				mobile: '',
				agreementChecked: false,
				userAgreementLink: 'https://github.com/rectinajh/DigitalCollectible/blob/main/%E7%81%B5%E5%A2%83%E7%94%A8%E6%88%B7%E6%9C%8D%E5%8A%A1%E5%8D%8F%E8%AE%AE',
				privacyPolicyLink: 'https://github.com/rectinajh/DigitalCollectible/blob/main/%E7%81%B5%E5%A2%83%E9%9A%90%E7%A7%81%E6%9D%83%E6%94%BF%E7%AD%96',
				verificationCode: '',
				password: '',
				inviteCode: '',
				inviteCodeReadonly: false,
				codeTips: '',
				verificationCodeTipFlag: false,
				showAgreement: false,
				agreementType: 'user',
				btnList: [
					// #ifdef APP
					{
						value: "phone",
						src: "/static/img/login/phone.png"
					},
					// #endif
					// {
					// 	value:"test",
					// 	src:"/static/img/login/alipay.png"
					// },
				],
				showTips: false,
				canVerify: true,
				loginType: 'code',
			}
		},
		components: {
			agreementPopup,
		},
		computed:{
			...mapGetters(['userInfo'])
		},
		onLoad(option) {
			var inviteCode = option.inviteCode;
			if (inviteCode) {
				this.inviteCode = inviteCode;
				this.inviteCodeReadonly = true;
			}

		},
		onShow() {
			// #ifdef APP
			uni.preLogin({
				provider: 'univerify',
				success() { //预登录成功
					this.canVerify = true; // 显示一键登录选项
				},
				fail(res) { // 预登录失败
					this.canVerify = false; // 不显示一键登录选项（或置灰）
					// btnList去除value为phone的对象
					this.btnList = this.btnList.filter(item => item.value !== 'phone');
					// 根据错误信息判断失败原因，如有需要可将错误提交给统计服务器
					console.log(res.errCode)
					console.log(res.errMsg)
				}
			})
			// #endif
			if(this.userInfo){
				uni.reLaunch({
					url: '/pages/home/home'
				})
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
				switch (this.loginType) {
					case 'code':
						this.codeLogin();
						break;
					case 'psw':
						this.pswLogin();
						break;
					default:
						break;
				}
			},
			openAgreement(type) {
				this.showAgreement = !this.showAgreement;
				this.agreementType = type;
			},
			btnGroupClick(val) {
				switch (val) {
					case 'phone':
						this.verifyLogin()
						break;
					
					case 'test':
						this.testLogin()
					default:
						break;
				}
			},
			check() {
				this.agreementChecked = !this.agreementChecked;
				this.showTips = false;
			},
			verifyLogin() {
				let that = this

				uni.login({
					provider: 'univerify',
					univerifyStyle: { // 自定义登录框样式
						"backgroundColor": '#181819',
						"authButton": {
							"normalColor": '#FCE6B7',
							"highlightColor": "#FCE6B7",
							"disabledColor": "#d4c19a",
							"textColor": '#644205',
						},
						"closeIcon": {
							"path": "static/img/common/cross.png",
						},
						"phoneNum": {
							"color": "#ffffff" // 手机号文字颜色 默认值：#202020
						},
						"privacyTerms": {
							"defaultCheckBoxState": false, // 条款勾选框初始状态 默认值： true
							"isCenterHint": false, //未勾选服务条款时点击登录按钮的提示是否居中显示 默认值: false (3.7.13+ 版本支持)
							"uncheckedImage": "static/img/login/uncheckbox.png", // 可选 条款勾选框未选中状态图片（仅支持本地图片 建议尺寸 24x24px）(3.2.0+ 版本支持)
							"checkedImage": "static/img/login/checkbox.png", // 可选 条款勾选框选中状态图片（仅支持本地图片 建议尺寸24x24px）(3.2.0+ 版本支持)
							"checkBoxSize": 16, // 可选 条款勾选框大小
							"textColor": "#BBBBBB", // 文字颜色 默认值：#BBBBBB
							"termsColor": "#FCE6B7", //  协议文字颜色 默认值： #5496E3
							"prefix": "我已阅读并同意", // 条款前的文案 默认值：“我已阅读并同意”
							"suffix": "并使用本机号码登录", // 条款后的文案 默认值：“并使用本机号码登录”
							"privacyItems": [ // 自定义协议条款，最大支持2个，需要同时设置url和title. 否则不生效
								{
									"url": "https://github.com/rectinajh/DigitalCollectible/blob/main/%E7%81%B5%E5%A2%83%E7%94%A8%E6%88%B7%E6%9C%8D%E5%8A%A1%E5%8D%8F%E8%AE%AE", // 点击跳转的协议详情页面
									"title": "用户协议" // 协议名称
								},
								{
									"url": "https://github.com/rectinajh/DigitalCollectible/blob/main/%E7%81%B5%E5%A2%83%E9%9A%90%E7%A7%81%E6%9D%83%E6%94%BF%E7%AD%96", // 点击跳转的协议详情页面
									"title": "隐私政策" // 协议名称
								},
							]
						},
						//参考`univerifyStyle 数据结构`
					},
					success(res) { // 登录成功
						// console.log(res.authResult); // {openid:'登录授权唯一标识',access_token:'接口返回的 token'}
						const crypto = require('crypto')
						const secret = 'AKID7UKnlNoK16jvMvtI0UFPH673t4H' // 自己的密钥不要直接使用示例值，且注意不要泄露
						const hmac = crypto.createHmac('sha256', secret);
						// 自有服务器生成签名，并以GET方式发送请求
						const params = res.authResult
						// 字母顺序排序后拼接签名串
						const signStr = Object.keys(params).sort().map(key => {
							return `${key}=${params[key]}`
						}).join('&')
						hmac.update(signStr);
						const sign = hmac.digest('hex')
						uni.request({
							url: 'https://fc-mp-a98015d2-1662-46ff-86f8-ae9ec98d4c2b.next.bspapp.com/lingjing-verify',
							method: 'GET',
							data: {
								access_token: res.authResult.access_token,
								openid: res.authResult.openid,
								sign: sign
							},
							success(response) {
								if(!response.data.phoneNumber){
									uni.showToast({
										title:'授权失败,请重试',
										icon: 'none'
									})
									setTimeout(()=>{
										uni.closeAuthView()
									},1000)
									return
								}
								that.$u.post('/oneClickLogin', {
									mobile: response.data.phoneNumber,
								}).then(res => {
									let tokenInfo = res.data;
									that.handleLogin(tokenInfo)
									uni.closeAuthView()
								});
							},
							fail(res) {
								console.log(res);
							}
						})
					},
					fail(res) { // 登录失败
						console.log(res.errCode)
						console.log(res.errMsg)
					}
				})
			},
			forgetPsw() {
				if (this.mobile === null || this.mobile === "") {
					uni.showToast({
						title: "请输入手机号",
						icon: "none"
					});
					return;
				}
				// 判断手机号格式
				let reg = /^1[3456789]\d{9}$/;
				if (!reg.test(this.mobile)) {
					uni.showToast({
						title: "请输入正确的手机号",
						icon: "none"
					});
					return;
				}
				uni.navigateTo({
					url: `/subPackages/setting/verification/verification?type='pwd'&mobile=${this.mobile}`
				});
			},
			codeLogin() {
				var that = this;
				if (!that.agreementChecked) {
					this.showTips = true;
					uni.showToast({
						title: "请先同意用户协议和隐私政策",
						icon: "none"
					});
					return;
				}
				if (that.mobile === null || that.mobile === "") {
					uni.showToast({
						title: "请输入手机号",
						icon: "none"
					});
					return;
				}
				// 判断手机号格式
				let reg = /^1[3456789]\d{9}$/;
				if (!reg.test(that.mobile)) {
					uni.showToast({
						title: "请输入正确的手机号",
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
					that.handleLogin(tokenInfo)
				});
			},
			pswLogin() {
				var that = this;
				if (!that.agreementChecked) {
					this.showTips = true;
					uni.showToast({
						title: "请先同意用户协议和隐私政策",
						icon: "none"
					});
					return;
				}
				if (that.mobile === null || that.mobile === "") {
					uni.showToast({
						title: "请输入手机号",
						icon: "none"
					});
					return;
				}
				// 判断手机号格式
				let reg = /^1[3456789]\d{9}$/;
				if (!reg.test(that.mobile)) {
					uni.showToast({
						title: "请输入正确的手机号",
						icon: "none"
					});
					return;
				}
				if (that.password === null || that.password === "") {
					uni.showToast({
						title: "请输入密码",
						icon: "none"
					});
					return;
				}

				this.$u.post('/loginByPwd', {
					mobile: that.mobile,
					pwd: that.password,
				}).then(res => {
					var tokenInfo = res.data;
					that.handleLogin(tokenInfo)
				});
			},
			handleLogin(tokenInfo){
				uni.setStorageSync('tokenName', tokenInfo.tokenName);
				uni.setStorageSync('tokenValue', tokenInfo.tokenValue);
				uni.setStorageSync('accountId', tokenInfo.accountId);
				
				// #ifdef APP-PLUS
				//that.updatePushClientId();
				// #endif
				
				if(tokenInfo.hasLoginPwd){
					uni.navigateTo({
						url: "/subPackages/setting/settingPwd/settingPwd"
					});
				}else{
					uni.reLaunch({
						url: "../home/home"
					});
				}
				
				
				this.getMyPersonalInfo()
			},
			getMyPersonalInfo() {
				var that = this;
				this.$u.get('/member/getMyPersonalInfo').then(res => {
					let personalInfo = res.data;
					that.$store.dispatch('updateUserInfo',personalInfo)
				});
			},
			
			testLogin(){
				this.$u.post('/oneClickLogin', {
					mobile: '18161017371',
				}).then(res => {
					let tokenInfo = res.data;
					this.handleLogin(tokenInfo)
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.verification-code-tip {
		padding-left: 32rpx;
		padding-right: 32rpx;
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
		padding-left: 60rpx;
		padding-right: 60rpx;
		position: relative;
		z-index: 1;

		// padding-top: 32rpx;
		.logo {
			margin-top: 148rpx;
		}

		.title {
			margin-top: 88rpx;
			width: 100%;
			font-size: 32rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			padding-bottom: 48rpx;

			.codeTitle {
				width: 128rpx;
				padding-bottom: 16rpx;
			}

			.pswTitle {
				width: 128rpx;
				padding-bottom: 16rpx;
				margin-left: 80rpx;
			}

			.checked {
				border-bottom: 4rpx solid #FCE6B7;
			}

			.unchecked {
				border-bottom: 4rpx solid #0C0C0D;
				color: #999999;
			}
		}

		.formInput {
			width: 630rpx;
			height: 88rpx;
			border-radius: 52rpx;
			background-color: #181819;
			display: flex;
			align-items: center;
			padding-left: 32rpx;
			position: relative;
			color: #fff;
		}

		.codeBtn {
			position: absolute;
			width: 180rpx;
			height: 64rpx;
			line-height: 64rpx;
			text-align: center;
			border-radius: 32rpx;
			color: #FCE6B7;
			font-size: 28rpx;
			right: 70rpx;
		}

		.forgetPsw {
			position: absolute;
			width: 180rpx;
			height: 64rpx;
			line-height: 64rpx;
			text-align: center;
			font-size: 28rpx;
			right: 70rpx;
			color: #FFFFFF;
		}

		.agreement {
			padding: 32rpx 0 80rpx 0;
			font-size: 24rpx;
			display: flex;
			align-items: center;
			color: #999999;

			.checkBox {
				margin-right: 16rpx;
				position: relative;

				.uncheck {
					width: 32rpx;
					height: 32rpx;
					border: 2rpx solid #999999;
					border-radius: 100%;
				}

				.checked {}

				.dialog {
					position: absolute;
					left: -10rpx;
					top: 44rpx;

					.img {
						position: relative;
					}

					.text {
						position: absolute;
						top: 6rpx;
						left: 0;
						width: 212rpx;
						height: 52rpx;
						font-size: 24rpx;
						line-height: 52rpx;
						text-align: center;
					}
				}
			}

			.agreementClick {
				color: #FCE6B7;
				text-decoration: none;
			}


		}

		.login-btn {
			width: 630rpx;
			height: 88rpx;
			border-radius: 52rpx;
			background-color: #FCE6B7;
			color: #644205;
			line-height: 88rpx;
			text-align: center;
			font-size: 32rpx;
		}

		.agreementUnchecked {
			opacity: .2;
		}

		.btnGroup {
			margin-top: 80rpx;
			display: flex;
			align-items: center;
			justify-content: space-evenly;

			.btnItem {
				width: 112rpx;
				height: 112rpx;
				position: relative;

				.border {
					border: 1rpx solid #666666;
					width: 112rpx;
					height: 112rpx;
					border-radius: 100%;
				}

				.img {
					position: absolute;
					left: 28rpx;
					top: 28rpx;
				}
			}
		}

	}

	.bottom {
		position: absolute;
		width: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;
		bottom: 28rpx;
		z-index: 1;

		.img {
			margin-bottom: 16rpx;
		}

		.slogan {
			display: flex;
			align-items: center;

			.leftLine {
				width: 80rpx;
				height: 2rpx;
				// 背景渐变
				background: linear-gradient(to right, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.4));
				margin-right: 32rpx;
			}

			.rightLine {
				width: 80rpx;
				height: 2rpx;
				// 背景渐变
				background: linear-gradient(to left, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.4));
				margin-left: 32rpx;
			}

			.text {
				color: rgba(255, 255, 255, 0.4);
			}
		}
	}


	.bg {
		position: absolute;
		bottom: 0;
		left: 0;
		z-index: 0;
		width: 100%;
	}
</style>