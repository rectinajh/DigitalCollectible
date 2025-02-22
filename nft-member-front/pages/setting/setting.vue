<template>
	<view>
		<u-modal v-model="updateNickNameFlag" title="修改昵称" :show-cancel-button="true" @confirm="updateNickName">
			<view class="slot-content">
				<input class="nick-name-input" focus v-model="nickName" />
			</view>
		</u-modal>
		<view class="setting-info">
			<u-cell-group title="个人信息" :border="false" :title-style="titleStyle">
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
			<u-cell-group title="账号安全" :border="false" :title-style="titleStyle">
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
		</view>
		<view class="fixed-button-group">
			<u-button type="primary" @click="logout">退出登录</u-button>
			</u-row>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				updateNickNameFlag: false,
				nickName: '',
				personalInfo: '',
				titleStyle: {}
			}
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
				this.gotoPage('../bindRealName/bindRealName');
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

<style>
	page {
		height: 100% !important;
	}


	.real-name {
	}

	.unreal-name {
		display: inline-block;
		background: #f00;
		border-radius: 10rpx;
		width: 10rpx;
		height: 10rpx;
		margin-right: 16rpx;
		position: relative;
		top: -2rpx;
	}

	.setting-info .u-cell {}

	.u-cell-title {}

	.nick-name-input {
		background: #ebebeb;
		border-radius: 32rpx;
		margin: 32rpx;
		padding: 20rpx 32rpx;
	}

	.avatar {
		padding-right: 16rpx;
	}

	.fixed-button-group {
		position: fixed;
		bottom: 60rpx;
		left: 0rpx;
		width: 100%;
		padding-left: 32rpx;
		padding-right: 32rpx;
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

	.modal-close-txt {}

	.modal-content {
		padding-top: 10rpx;
		padding-bottom: 10rpx;
	}

	.login-duration-item {
		padding-top: 30rpx;
		padding-bottom: 30rpx;
		display: flex;
		align-items: center;
	}

	.login-duration-item-txt {
		flex: 1;
	}
</style>
