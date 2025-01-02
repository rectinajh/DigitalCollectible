<template>
	<view>
		<view>
			<u-form class="transfer-form" :border-bottom="false">
				<u-form-item label-position="top" label="提现金额" label-width="150" :border-bottom="false">
					<u-input placeholder="请输入提现金额" v-model="amount" type="number" :clearable="false"></u-input>
					<view slot="right" class="all-amount" @click="allAmount">全部</view>
				</u-form-item>
				<view class="amount-bottom-tip">
					<view>
						可用余额：{{balance}}
					</view>
				</view>
			</u-form>

			<view class="settlement-account-form">
				<view class="settlement-account-title">
					<view class="settlement-account-title-l">结算账户</view>
					<view class="settlement-account-title-r" @click="toSettlementAccountPage">更多账户<u-icon name="arrow-right" size="22"></u-icon>
					</view>
				</view>
				<template v-for="(settlementAccount,index) in settlementAccounts">
					<view class="payment-received-info" @click="selectedSettlementAccount(settlementAccount)">
						<view class="info-section1">
							<view>
								<u-image class="info-icon" width="48rpx" height="48rpx"
									:src="'/static/img/' + settlementAccount.type + '.png'">
								</u-image>
							</view>
							<view class="payment-received-info-txt">
								<view class="type-name">
									<text
										v-show="settlementAccount.type == 'wechat' || settlementAccount.type == 'alipay'">
										{{settlementAccount.typeName}}
									</text>
									<text v-show="settlementAccount.type == 'bankCard'">
										{{settlementAccount.bankName}}
									</text>
								</view>
								<view class="info-detail">
									<text v-show="settlementAccount.type == 'bankCard'">
										{{cardNumberFormat(settlementAccount.cardNumber)}}
									</text>
									<text
										v-show="settlementAccount.type == 'wechat' || settlementAccount.type == 'alipay'">
										{{settlementAccount.account}}
									</text>
								</view>
							</view>
							<view>
								<u-checkbox v-model="checkboxTrue" shape="circle" :disabled="true"
									v-show="mySettlementAccount == settlementAccount">
								</u-checkbox>
								<u-checkbox v-model="checkboxFalse" shape="circle" :disabled="true"
									v-show="mySettlementAccount != settlementAccount">
								</u-checkbox>
							</view>
						</view>
					</view>
					<u-line color="#e4e7ed"></u-line>
				</template>
			</view>
		</view>
		<view class="fixed-bottom">
			<view class="actual-amount">
				<view class="actual-amount-l">到账金额</view>
				<view class="actual-amount-r">{{actualAmount}} CNY</view>
			</view>
			<u-button type="primary" @click="withdraw">申请提现</u-button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				amount: '',
				balance: '',
				handlingFee: 0,
				settlementAccounts: [],
				mySettlementAccount: '',
				checkboxTrue: true,
				checkboxFalse: false,
			}
		},
		computed: {
			actualAmount: function() {
				var actualAmount = 0;
				if (this.balance === null || this.balance === '') {
					return actualAmount;
				}
				if (this.amount === null || this.amount === '') {
					return actualAmount;
				}
				actualAmount = parseFloat(Number(this.amount - this.handlingFee).toFixed(2));
				if (actualAmount <= 0) {
					return 0;
				}
				return actualAmount;
			}
		},
		onNavigationBarButtonTap(e) {
			uni.navigateTo({
				url: '../withdrawRecord/withdrawRecord'
			});
		},
		onLoad() {
			this.getBalance();
			this.findActivatedSettlementAccount();
		},
		methods: {
			
			toSettlementAccountPage() {
				uni.navigateTo({
					url: "../settlementAccount/settlementAccount"
				});
			},

			cardNumberFormat(cardNumber) {
				return cardNumber.replace(/(.{4})/g, "$1 ");
			},

			selectedSettlementAccount(settlementAccount) {
				this.mySettlementAccount = settlementAccount;
			},

			findActivatedSettlementAccount() {
				var that = this;
				uni.showLoading({
					title: ''
				});
				this.$u.get('/settlementAccount/findAll', {
					activated: true
				}).then(res => {
					that.settlementAccounts = res.data;
				});
			},

			getBalance() {
				var that = this;
				this.$u.get('/member/getBalance').then(res => {
					that.balance = res.data;
				});
			},

			allAmount() {
				this.amount = this.balance;
			},

			withdraw() {
				var that = this;
				if (that.amount === null || that.amount === "") {
					uni.showToast({
						title: "请输入提现金额",
						icon: "none"
					});
					return;
				}
				if (that.mySettlementAccount === null || that.mySettlementAccount === "") {
					uni.showToast({
						title: "请选择结算账户",
						icon: "none"
					});
					return;
				}
				this.$u.post('/withdraw/withdraw', {
					settlementAccountId: that.mySettlementAccount.id,
					amount: that.amount
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '申请成功!',
						duration: 2000,
						complete: function() {
							setTimeout(() => {
								uni.navigateBack();
							}, 2000);
						}
					});
				});

			},
		}
	}
</script>

<style>
	::v-deep .u-form-item--right__content {
		border: 2rpx solid #e4e7ed;
		padding-left: 20rpx;
		padding-right: 20rpx;
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

	.payment-received-info {
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.info-section1 {
		display: flex;
		align-items: center;
	}

	.payment-received-info-txt {
		flex: 1;
	}

	.type-name {
		padding-bottom: 14rpx;
	}

	.info-detail {
		color: #888;
		font-size: small;
	}

	.info-icon {
		margin-right: 14rpx;
	}

	.reconfirm-popup {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.popup-title {
		display: flex;
		justify-content: space-between;
	}

	.popup-title-l {
		font-weight: bold;
	}

	.popup-title-r {
		color: #909399;
	}

	.fixed-bottom {
		position: fixed;
		bottom: 60rpx;
		left: 0rpx;
		width: 100%;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.actual-amount {
		display: flex;
		justify-content: space-between;
		padding-bottom: 20rpx;
	}

	.actual-amount-l {
		color: #909399;
	}

	.actual-amount-r {}

	.handling-fee {
		display: flex;
		justify-content: space-between;
		padding-bottom: 20rpx;
	}

	.handling-fee-l {
		color: #909399;
	}

	.handling-fee-r {}

	.amount-bottom-tip {
		display: flex;
		justify-content: space-between;
		padding-bottom: 20rpx;
	}

	.all-amount {
		color: #000000;
	}

	.settlement-account-title {
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.settlement-account-title-l {
		height: 70rpx;
		line-height: 70rpx;
	}

	.settlement-account-title-r {
		display: flex;
		align-items: center;
		color: #353535;
		font-size: small;
	}

	.settlement-account-form {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.transfer-form {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}
</style>
