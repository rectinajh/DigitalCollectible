<template>
	<view>
		<view class="setting">
			<view class="user">
				<view class="item border" @click="gotoPage('/subPackages/setting/userinfo/userinfo')">
					<lj-cell title="账户信息" icon="/static/img/common/cell/account.png"></lj-cell>
				</view>
				<view class="item border" @click="bindRealName">
					<lj-cell title="实名认证" icon="/static/img/common/cell/safe.png" 
					:isClick="personalInfo.bindRealNameTime ? false : true"
					:description="personalInfo.bindRealNameTime ? '已认证' : '未认证'"></lj-cell>
				</view>
				<view class="item border" @click="settingPwd">
					<lj-cell title="修改密码" icon="/static/img/common/cell/password.png"></lj-cell>
				</view>
				<view class="item" @click="gotoPage('/pages/loginRecord/loginRecord')">
					<lj-cell title="登录记录" icon="/static/img/common/cell/lock.png" 
					:description="personalInfo.latelyLoginTime"></lj-cell>
				</view>
			</view>
			<view class="app">
				<!-- <view class="item">
					<lj-cell title="关于我们" icon="/static/img/common/cell/about.png"></lj-cell>
				</view> -->
				<!-- <view class="item">
					<lj-cell title="分享灵境" icon="/static/img/common/cell/safe.png" 
					:description="personalInfo.bindRealNameTime ? '已认证' : '未认证'"></lj-cell>
				</view> -->
			</view>
			
			<view class="fixed-button-group" @click="logout">
				退出登录
			</view>
		</view>
		
		<!-- <view class="setting-info">
			<u-cell-group :border="false" :title-style="titleStyle">
				<u-cell-item title="头像" :arrow="false" :border-bottom="false" :title-style="titleStyle"
					@click="chooseAvatar">
					<view slot="right-icon">
						<view class="avatar">
							<u-image height="54rpx" width="54rpx" :src="getAvatar()" shape="circle"></u-image>
						</view>
					</view>
				</u-cell-item>
				<u-cell-item title="昵称" :arrow="true" :border-bottom="false" :title-style="titleStyle"
					:value="personalInfo.nickName" @click="showUpdateNickNameModal">
				</u-cell-item>
				<u-cell-item title="手机号" :arrow="false" :border-bottom="false" :title-style="titleStyle"
					:value="personalInfo.mobile">
				</u-cell-item>
				<u-cell-item title="区块链地址" :arrow="false" :border-bottom="false" :title-style="titleStyle">
					<view slot="label">{{getBlockChainAddr()}}</view>
				</u-cell-item>
			</u-cell-group>
			<u-cell-group :border="false" :title-style="titleStyle">
				<u-cell-item title="密码设置" :arrow="true" :border-bottom="false" :title-style="titleStyle"
					@click="settingPwd">
				</u-cell-item>
				<u-cell-item title="实名认证" :arrow="true" :border-bottom="false" :title-style="titleStyle"
					@click="bindRealName">
					<view class="real-name" v-show="personalInfo">
						<view class="unreal-name"
							v-show="personalInfo.bindRealNameTime == null || personalInfo.bindRealNameTime == ''">
						</view>{{personalInfo.bindRealNameTime ? '已认证' : '未认证'}}
					</view>
				</u-cell-item>
				<u-cell-item title="查看登录记录" :arrow="true" :border-bottom="false"
					@click="gotoPage('../loginRecord/loginRecord')">
					<view slot="label">最近登录时间： {{personalInfo.latelyLoginTime}}</view>
				</u-cell-item>
			</u-cell-group>
		</view> -->
	</view>
</template>

<script>
	import ljCell from './components/lj-cell/lj-cell.vue';
	export default {
		data() {
			return {
				updateNickNameFlag: false,
				nickName: '',
				personalInfo: '',
				titleStyle: {}
			}
		},
		components:{
			ljCell
		},
		onLoad() {

		},
		onShow() {
			this.getMyPersonalInfo();
		},
		methods: {
			
			getBlockChainAddr() {
				if (this.personalInfo) {
					if (this.personalInfo.bindRealNameTime === null || this.personalInfo.bindRealNameTime === '') {
						return '实名认证后生成区块链地址...';
					} else {
						return this.personalInfo.blockChainAddr ? this.personalInfo.blockChainAddr : '等待创建区块链地址...';
					}
				}
			},

			updateNickName() {
				var that = this;
				if (that.nickName === null || that.nickName === "") {
					uni.showToast({
						title: "请输入昵称",
						icon: "none"
					});
					return;
				}
				this.$u.post('/member/updateNickName', {
					nickName: that.nickName,
				}).then(res => {
					that.updateNickNameFlag = false;
					that.getMyPersonalInfo();
				});
			},

			showUpdateNickNameModal() {
				this.updateNickNameFlag = true;
				this.nickName = this.personalInfo.nickName;
			},

			getAvatar() {
				return this.personalInfo.avatar ? this.baseUrl + '/storage/fetch/' + this.personalInfo.avatar :
					'/static/img/avatar.png';
			},

			chooseAvatar() {
				var that = this;
				uni.chooseImage({
					count: 1,
					success: function(res) {
						var header = {};
						var tokenName = uni.getStorageSync('tokenName');
						var tokenValue = uni.getStorageSync('tokenValue');
						header[tokenName] = tokenValue;
						uni.uploadFile({
							url: that.baseUrl + '/storage/upload',
							filePath: res.tempFilePaths[0],
							header: header,
							name: 'file_data',
							formData: {},
							success: (uploadFileRes) => {
								that.updateAvatar(JSON.parse(uploadFileRes.data).data[0]);
							}
						});
					}
				});
			},

			updateAvatar(avatar) {
				var that = this;
				this.$u.post('/member/updateAvatar', {
					avatar: avatar
				}).then(res => {
					that.getMyPersonalInfo();
				});
			},

			bindRealName() {
				if (this.personalInfo.bindRealNameTime) {
					return;
				}
				this.gotoPage('/pages/bindRealName/bindRealName');
			},

			logout() {
				var that = this;
				this.$u.post('/logout').then(res => {
					try {
						uni.removeStorageSync('tokenName');
					} catch (e) {}
					uni.reLaunch({
						url: "../login/login"
					});
				});
				this.$store.dispatch('logout')
			},

			getMyPersonalInfo() {
				var that = this;
				uni.showLoading({
					title: ''
				});
				this.$u.get('/member/getMyPersonalInfo').then(res => {
					that.personalInfo = res.data;
					uni.hideLoading();
				});
			},

			gotoPage(path) {
				uni.navigateTo({
					url: path
				});
			},
			
			settingPwd(){
				let path="/subPackages/setting/settingPwd/settingPwd"
				this.gotoPage(`/subPackages/setting/verification/verification?type='pwd'`)
			}

		}
	}
</script>

<style lang="scss" scoped>
@import 'setting.scss'
</style>
