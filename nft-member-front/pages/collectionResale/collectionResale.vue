<template>
	<view>
		<view class="sub-title">将以下藏品进行寄售</view>
		<view class="collection-info">
			<view class="collection-info-l">
				<u-image class="collection-cover" width="160rpx" height="160rpx" border-radius="10"
					:src="collectionDetail.collectionCover">
				</u-image>
			</view>
			<view class="collection-info-r">
				<view class="collection-name u-line-1">
					{{collectionDetail.collectionName}}
				</view>
				<view class="collection-serial-number">#
					{{collectionDetail.collectionSerialNumber}}/{{collectionDetail.quantity}}
				</view>
				<view class="creator">{{collectionDetail.creatorName}}</view>
			</view>
		</view>
		<view class="friend-account-tip">
			请设置售价:
		</view>
		<view class="account-textarea">
			<input type="number" placeholder="请设置售价" v-model.number="resalePrice" />
		</view>
		<view class="action-btn">
			<u-button type="primary" @click="collectionResale">确认寄售</u-button>
		</view>

		<view class="give-explain">
			<view class="give-explain-title">寄售说明</view>
			<view class="give-explain-items">
				<view class="give-explain-item">
					1、请您确认您具备寄售数字藏品的民事行为能力；
				</view>
				<view class="give-explain-item">
					2、请您确认您已通过平台的实名认证并遵守相关法律法规及平台协议；
				</view>
				<view class="give-explain-item">
					3、藏品未经购买前，可取消寄售。一经购买，无法取消寄售。
				</view>
				<view class="give-explain-item">
					4、藏品出售成功后，售卖收入将默认进行钱包余额。
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				collectionId: '',
				collectionDetail: '',
				resalePrice: '',
			}
		},
		onLoad(option) {
			this.collectionId = option.id;
			this.getCollectionDetail();
		},
		methods: {
			
			collectionResale() {
				var that = this;
				if (that.resalePrice === null || that.resalePrice === "") {
					uni.showToast({
						title: "请输入售价",
						icon: "none"
					});
					return;
				}
				this.$u.post('/transaction/collectionResale', {
					resalePrice: that.resalePrice,
					holdCollectionId: that.collectionId,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '发布成功!',
						duration: 2000,
						mask: true,
						complete: function() {
							setTimeout(() => {
								uni.reLaunch({
									url: "../my/my"
								});
							}, 2000);
						}
					});
				});
			},

			getCollectionDetail() {
				var that = this;
				this.$u.get('/myArtwork/findMyHoldCollectionDetail', {
					id: that.collectionId
				}).then(res => {
					that.collectionDetail = res.data;
				});
			},
		}
	}
</script>

<style>
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
		padding-top: 32rpx;
		padding-right: 32rpx;
	}

	.give-explain-title {
		line-height: 2;
	}

	.give-explain-items {
		font-size: smaller;
		color: #888;
	}

	.give-explain-item {}

	.action-btn {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
	}

	.friend-account-tip {
		padding-top: 64rpx;
		padding-left: 23rpx;
		padding-bottom: 20rpx;
	}


	.account-textarea {
		background: #e7e7e7;
		padding-left: 32rpx;
		padding-right: 32rpx;
		margin-left: 32rpx;
		margin-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
		border-radius: 20rpx;
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
