<template>
	<view>
		<u-action-sheet :list="actions" v-model="showAction" @click="actionEvent"></u-action-sheet>
		<u-modal v-model="delConfirm" content="确认删除？" :show-cancel-button="true" :show-title="false" @confirm="del">
		</u-modal>
		<u-modal v-model="bindRealName" title="提示" content="请先进行实名认证" :show-cancel-button="false" confirm-text="我知道了"
			@confirm="toBindRealNamePage">
		</u-modal>
		<view class="no-data" v-show="noDataFlag">
			<u-empty text="未添加结算账户" mode="list"></u-empty>
		</view>
		<template v-for="(settlementAccount,index) in settlementAccounts">
			<view class="receipt-payment-info" @click="showActionSheet(settlementAccount)">
				<view class="info-section1">
					<view class="info-section1-l">
						<u-image class="info-icon" width="36rpx" height="36rpx"
							:src="'/static/img/' + settlementAccount.type + '.png'">
						</u-image>
						<text v-show="settlementAccount.type == 'bankCard'">
							{{settlementAccount.bankName}}
						</text>
						<text v-show="settlementAccount.type == 'wechat' || settlementAccount.type == 'alipay'">
							{{settlementAccount.typeName}}
						</text>
					</view>
					<view class="info-section1-r" @click.stop="updateActivatedFlag(settlementAccount)">
						<u-checkbox v-model="checkboxTrue" shape="circle" :disabled="true"
							v-show="settlementAccount.activated">
							已激活
						</u-checkbox>
						<u-checkbox v-model="checkboxFalse" shape="circle" :disabled="true"
							v-show="!settlementAccount.activated">
							未激活
						</u-checkbox>
					</view>
				</view>
				<view class="info-section2">
					{{settlementAccount.realName}}
				</view>
				<view class="info-section3">
					<text v-show="settlementAccount.type == 'bankCard'">
						{{cardNumberFormat(settlementAccount.cardNumber)}}
					</text>
					<text v-show="settlementAccount.type == 'wechat' || settlementAccount.type == 'alipay'">
						{{settlementAccount.account}}
					</text>
				</view>
			</view>
			<u-gap height="20" bgColor="#f9f7f7"></u-gap>
		</template>

		<view class="fixed-button-group">
			<u-button type="primary" @click="toAddPage">添加</u-button>
			</u-row>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				settlementAccounts: [],
				selectedReceiptPaymentInfo: '',
				actions: [{
					text: '删除'
				}],
				showAction: false,
				delConfirm: false,
				noDataFlag: false,
				checkboxTrue: true,
				checkboxFalse: false,
				bindRealName: false,
				realName: '',
			}
		},
		onLoad() {

		},
		onShow() {
			this.getRealName();
			this.findAll();
		},
		methods: {
			getRealName() {
				var that = this;
				this.$u.get('/member/getMyPersonalInfo').then(res => {
					that.realName = res.data.realName;
				});
			},

			del() {
				var that = this;
				this.$u.post('/settlementAccount/del', {
					id: that.selectedReceiptPaymentInfo.id
				}).then(res => {
					that.findAll();
				});
			},

			actionEvent(index) {
				this.delConfirm = true;
			},

			showActionSheet(receiptPaymentInfo) {
				this.selectedReceiptPaymentInfo = receiptPaymentInfo;
				this.showAction = true;
			},

			viewQrcode(qrcode) {
				uni.previewImage({
					urls: [this.baseUrl + '/storage/fetch/' + qrcode]
				});
			},

			updateActivatedFlag(receiptPaymentInfo) {
				var that = this;
				this.$u.post('/settlementAccount/updateActivatedFlag', {
					id: receiptPaymentInfo.id,
					activated: !receiptPaymentInfo.activated
				}).then(res => {
					that.findAll();
				});
			},
			cardNumberFormat(cardNumber) {
				return cardNumber.replace(/(.{4})/g, "$1 ");
			},

			findAll() {
				var that = this;
				uni.showLoading({
					title: ''
				});
				this.$u.get('/settlementAccount/findAll').then(res => {
					var settlementAccounts = res.data;
					for (var i = 0; i < settlementAccounts.length; i++) {
						settlementAccounts[i].show = false;
					}
					that.settlementAccounts = settlementAccounts;
					that.noDataFlag = that.settlementAccounts.length == 0;
					uni.hideLoading();
				});
			},

			toBindRealNamePage() {
				uni.navigateTo({
					url: '../bindRealName/bindRealName'
				});
			},

			toAddPage() {
				if (this.realName == null || this.realName == '') {
					this.bindRealName = true;
					return;
				}
				uni.navigateTo({
					url: '../addSettlementAccount/addSettlementAccount'
				});
			}
		}
	}
</script>
<style>
	::v-deep .u-checkbox__label {
		margin-right: 0;
	}

	::v-deep .u-checkbox__icon-wrap--disabled {
		color: transparent !important;
		background-color: transparent !important;
		border-color: #c8c9cc !important;
	}

	::v-deep .u-checkbox__icon-wrap--disabled--checked {
		color: #fff !important;
		background-color: #2979ff !important;
		border-color: #2979ff !important;
	}

	.no-data {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 400rpx;
	}

	.activated {
		color: #3C93EF;
	}

	.fixed-button-group {
		position: fixed;
		bottom: 30rpx;
		left: 0rpx;
		width: 100%;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.receipt-payment-info {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.info-section1 {
		display: flex;
		justify-content: space-between;
	}

	.info-section1-l {
		display: flex;
		align-items: center;
	}

	.info-icon {
		margin-right: 14rpx;
	}

	.info-section2 {
		line-height: 2.5;
		color: #909399;
	}

	.info-section3 {
		font-weight: bold;
		display: flex;
		justify-content: space-between;
	}
</style>
