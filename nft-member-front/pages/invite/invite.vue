<template>
	<view>
		<view class="bg-sub">
			<view class="invite-top-tips">
				<view class="invite-top-tip">邀请注册，即有机会</view>
				<view class="invite-top-tip">参与抽奖赢取"豪"礼</view>
			</view>
			<view class="invite-code-label">
				您的专属邀请码为
			</view>
			<view class="invite-code-value">{{inviteCode}}</view>
			<view v-if="inviteCode != null && inviteCode != ''">
				<image style="width: 256rpx; height: 256rpx;" :src="getInviteCodeUrl()">
				</image>
			</view>
			<view class="download-desc">
				<view>扫描二维码</view>
				<view>下载APP注册账号</view>
			</view>
			<view class="invite-actions">
				<view class="invite-action" @click="copyData(inviteLink)">
					<view class="invite-action-icon">
						<u-icon name="attach" size="50"></u-icon>
					</view>
					<view class="invite-action-label">复制链接</view>
				</view>
				<view class="invite-action" @click="saveInviteImg">
					<view class="invite-action-icon">
						<u-icon name="arrow-downward" size="50"></u-icon>
					</view>
					<view class="invite-action-label">保存图片</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				inviteCode: '',
				inviteLink: ''
			}
		},
		onNavigationBarButtonTap(e) {
			this.inviteRecordPage();
		},
		onLoad() {
			this.getInviteInfo();
		},
		methods: {

			getInviteCodeUrl() {
				return this.baseUrl + '/storage/inviteQrcode/' + this.inviteCode;
			},

			inviteRecordPage(notice) {
				uni.navigateTo({
					url: '../inviteRecord/inviteRecord'
				});
			},

			saveInviteImg() {
				uni.previewImage({
					urls: [this.getInviteCodeUrl()]
				});
			},

			copyData(data) {
				var that = this
				uni.setClipboardData({
					data: '' + data,
					success: function() {
						uni.showToast({
							title: '复制成功',
							duration: 2000,
							icon: 'none'
						});
					},
					fail: function(err) {
						uni.showToast({
							title: '复制失败' + err,
							duration: 2000,
							icon: 'none'
						});
					}
				});
			},

			getInviteInfo() {
				var that = this;
				this.$u.get('/member/getInviteInfo', {}).then(res => {
					that.inviteCode = res.data.inviteCode;
					that.inviteLink = res.data.inviteLink;
				});
			},
		}
	}
</script>

<style>
	.invite-top-tips {
		margin-top: 160rpx;
		padding-bottom: 64rpx;
	}

	.invite-top-tip {
		color: #f1d185;
		font-size: large;
		letter-spacing: 4rpx;
		font-weight: bold;
		line-height: 1.5;
	}

	.invite-actions {
		color: #f1d185;
		display: flex;
		text-align: center;
		align-items: center;
		padding-top: 120rpx;
	}

	.invite-action {
		margin-left: 48rpx;
		margin-right: 48rpx;
	}

	.invite-action-icon {}

	.invite-action-label {
		font-size: small;
		padding-top: 8px;
	}

	.download-desc {
		margin-top: 60rpx;
		text-align: center;
		color: rgba(241, 209, 133, .5);
		font-size: smaller;
	}

	.invite-code-label {
		color: rgba(241, 209, 133, .5);
		text-align: center;
	}

	.invite-code-value {
		margin-top: 24rpx;
		background: #f1d185;
		border-radius: 200rpx;
		color: #141519;
		font-style: normal;
		font-size: 40rpx;
		text-align: center;
		padding-left: 56rpx;
		padding-right: 56rpx;
		padding-top: 12rpx;
		padding-bottom: 12rpx;
		margin-bottom: 48rpx;
	}

	.bg-sub {
		background: url('/static/img/invite_bg_sub.png') no-repeat;
		display: flex;
		align-items: center;
		flex-direction: column;
		background-size: 100% 100%;
		border-radius: 64rpx;
		margin: 32rpx;
		padding-bottom: 32rpx;
	}

	page {
		height: 100% !important;
		background: url('/static/img/invite_bg.png') no-repeat;
		background-size: 100% 100%;
	}
</style>
