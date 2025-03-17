<template>
	<view class="setting">
		<view class="user">
			<view class="item border" @click="chooseAvatar">
				<lj-cell title="头像">
					<template slot="description">
						<u-image :src="getAvatar()" width="72rpx" height="72rpx" shape="circle"></u-image>
					</template>
				</lj-cell>
			</view>
			<view class="item" @click="showUpdateNickNameModal">
				<lj-cell title="昵称"
				:description="personalInfo.nickName"></lj-cell>
			</view>
		</view>
		<u-modal v-model="updateNickNameFlag" title="修改昵称" confirm-color="#FCE6B7" cancel-color="#fff"
		:title-style="titleStyle" backgroundColor="#181819"
		:show-cancel-button="true" @confirm="updateNickName">
			<view class="slot-content">
				<input class="nick-name-input" focus v-model="nickName" />
			</view>
		</u-modal>
	</view>
</template>

<script>
	import ljCell from '@/pages/setting/components/lj-cell/lj-cell.vue';
	export default {
		data() {
			return {
				updateNickNameFlag: false,
				nickName: '',
				personalInfo: '',
				titleStyle: {
					color:'#fff',
				},
			};
		},
		components:{
			ljCell
		},
		onShow() {
			this.getMyPersonalInfo();
		},
		methods: {
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
		
		}
	}
</script>

<style lang="scss" scoped>
@import 'userinfo.scss'
</style>
