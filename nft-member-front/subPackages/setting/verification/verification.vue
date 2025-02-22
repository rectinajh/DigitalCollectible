<template>
	<view class="main">
		<view class="title">
			{{title}}
		</view>
		<view class="info">
			<view class="user">
				<!-- 将userInfo.mobile手机号中间4位替换为星号 -->
				<span>当前账号手机号：{{mobile.replace(mobile.slice(3,7),'****')}}</span>
			</view>
			<view class="text" v-show="isCounting">
				<span>短信验证码已发送</span>
			</view>
		</view>
		<view class="codeInput">
			<u-message-input :breathe="false" inactive-color="#333" active-color="#FCE6B7" :focus="true"
			font-size="30" fontColor='#fff' :maxlength="6" @finish="confirm"></u-message-input>
		</view>
		<view class="codeBtn" @click="sendCode">
			<span style="margin-right: 8rpx;">{{codeTips}}</span>
		</view>
		<u-verification-code seconds="60" ref="uCode" start-text="发送验证码" 
		:keep-running="true" @change="codeChange" @start="countStart"></u-verification-code>
	</view>
</template>

<script>
	import {mapGetters} from 'vuex';
import userInfo from '../../../store/modules/userInfo';
	export default {
		data() {
			return {
				title:'修改密码',
				btnText:'发送验证码',
				type:'',
				verificationCode:'',
				codeTips:'',
				isCounting:false,
				mobile:''
			};
		},
		onLoad(options) {
			if(options.type){
				this.initText(options.type)
			}
			else{
				uni.navigateBack()
			}
			if(options.mobile){
				this.mobile = options.mobile
			}
		},
		mounted() {
			this.initCountdown()
			if(userInfo.mobile){
				this.mobile = userInfo.mobile
			}
		},
		computed:{
			...mapGetters(['userInfo'])
		},
		methods:{
			initText(type){
				this.type = type;
				switch(type){
					case 'pwd':
						this.title = '修改密码';
						break;
					default:
						
						break;
				}
			},
			codeChange(text) {
				this.codeTips = text;
			},
			countStart(){
				this.isCounting=true;
			},
			initCountdown(){
				// 如果本地缓存countdown中存在当前类型的时间戳，则对比当前时间戳和本地时间戳，如果小于60s，则继续倒计时
				let countdown = uni.getStorageSync('countdown') || {}
				let difference = 60 - Math.floor((new Date().getTime()-countdown[this.type])/1000)
				if(countdown[this.type] && difference>0){
					this.defaultTime = difference
					this.btnText = '重新发送';
					this.$refs.uCountDown.start();
				}
			},
			sendCode(){
				if (!this.$refs.uCode.canGetCode) {
					return;
				}
				
				this.$u.get('/sendModifyLoginPwdVerificationCode', {
					mobile: this.userInfo.mobile,
				}).then(res => {
					this.$refs.uCode.start();
				});
				
			},
			confirm(code){
				this.$u.post('/eqModifyLoginPwdVerificationCode', {
					mobile: this.userInfo.mobile,
					verificationCode: code,
				}).then(res => {
					uni.redirectTo({
						url:'/subPackages/setting/settingPwd/settingPwd'
					})
				});
				
			}
		}
	}
</script>

<style lang="scss" scoped>
	@import 'verification.scss';
</style>
