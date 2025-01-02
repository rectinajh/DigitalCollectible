<template>
	<view>
		<u-popup v-model="showConfirmModalFlag" mode="bottom" border-radius="14">
			<view class="common-modal">
				<view class="modal-title">
					<view>
						<u-icon name="arrow-leftward" @click="showConfirmModalFlag = false;"></u-icon>
					</view>
					<view class="modal-title-txt">{{collectionDetail.collectionName}}</view>
					<view class="close-modal-txt" @click="showConfirmModalFlag = false;">关闭</view>
				</view>
				<view>
					<view class="receiver-info">
						<view class="receiver-info-title">
							受赠人信息
						</view>
						<view class="receiver-info-item">
							<view class="receiver-info-item-t">受赠人区块链地址:</view>
							<view class="receiver-info-item-b">{{receiverInfo.blockChainAddr}}</view>
						</view>
						<view class="receiver-info-item">
							<view class="receiver-info-item-t">受赠人手机号:</view>
							<view class="receiver-info-item-b">{{receiverInfo.mobile}}</view>
						</view>
					</view>
					<view class="checked-flag">
						<u-checkbox-group>
							<u-checkbox v-model="checkedFlag" shape="circle" label-size="28">本人承诺本次转赠仅用于好友之间交流分享
							</u-checkbox>
						</u-checkbox-group>
					</view>
					<u-button type="primary" :disabled="!checkedFlag" @click="collectionGive">确认转赠</u-button>
				</view>
			</view>
		</u-popup>
		<view class="sub-title">将以下藏品转赠给好友</view>
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
			请输入好友的区块链地址/手机号:
		</view>
		<view class="account-textarea">
			<textarea class="account-textarea-inner" placeholder="请输入好友的区块链地址/手机号" v-model="giveToAccount"></textarea>
		</view>
		<view class="action-btn">
			<u-button type="primary" @click="getCollectionReceiverInfo">确认转赠</u-button>
		</view>

		<view class="give-explain">
			<view class="give-explain-title">转赠说明</view>
			<view class="give-explain-items">
				<view class="give-explain-item">
					1、请您确认您具备赠送数字藏品的民事行为能力；
				</view>
				<view class="give-explain-item">
					2、请您确认您与受赠人均已通过平台的实名认证并遵守相关法律法规及平台协议；
				</view>
				<view class="give-explain-item">
					3、请您确认本次赠送行为未设定任何形式的对价；
				</view>
				<view class="give-explain-item">
					4、转赠操作无法撤销；
				</view>
				<view class="give-explain-item">
					5、与数字藏品相关的权利将会同步且毫无保留地转移至受赠人；
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
				giveToAccount: '',
				receiverInfo: '',
				showConfirmModalFlag: false,
				checkedFlag: false,
			}
		},
		onLoad(option) {
			this.collectionId = option.id;
			this.getCollectionDetail();
		},
		methods: {

			collectionGive() {
				var that = this;
				this.$u.post('/transaction/collectionGive', {
					giveToAccount: that.giveToAccount,
					holdCollectionId: that.collectionId,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '转赠成功!',
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

			getCollectionReceiverInfo() {
				var that = this;
				if (that.giveToAccount === null || that.giveToAccount === "") {
					uni.showToast({
						title: "请输入好友的区块链地址/手机号",
						icon: "none"
					});
					return;
				}
				this.$u.get('/transaction/getCollectionReceiverInfo', {
					giveToAccount: that.giveToAccount
				}).then(res => {
					that.receiverInfo = res.data;
					that.showConfirmModalFlag = true;
					that.checkedFlag = false;
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

	.account-textarea-inner {
		padding-top: 32rpx;
	}

	.account-textarea {
		background: #e7e7e7;
		padding-left: 32rpx;
		padding-right: 32rpx;
		margin-left: 32rpx;
		margin-right: 32rpx;
		border-radius: 20rpx;
		height: 260rpx;
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
