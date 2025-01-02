<template>
	<view>
		<view class="block1" v-show="!editFlag">
			<view class="block1-title">添加</view>
			<view class="receipt-payment-type" v-for="settlementType in settlementTypes"
				@click="selectSettlementType(settlementType)">
				<view class="type-icon">
					<u-image width="36rpx" height="36rpx" :src="'/static/img/' + settlementType.type + '.png'">
					</u-image>
				</view>
				<view class="type-name">
					<u-cell-item :title="settlementType.name">
					</u-cell-item>
				</view>
			</view>
		</view>
		<view class="block1" v-show="editFlag">
			<view class="block1-title">添加{{settlementType.name}}</view>
			<view class="edit-block">
				<u-form>
					<u-form-item label-position="top" label="姓名" label-width="150">
						<u-input v-model="realName" type="text" :disabled="true"></u-input>
					</u-form-item>
					<template v-if="settlementType.type == 'bankCard'">
						<u-form-item label-position="top" label="银行卡号" label-width="150">
							<u-input placeholder="请输入银行卡号" v-model="settlementAccount.cardNumber" type="number">
							</u-input>
						</u-form-item>
						<u-form-item label-position="top" label="开户银行" label-width="150">
							<u-input placeholder="请输入开户银行" v-model="settlementAccount.bankName" type="text"></u-input>
						</u-form-item>
					</template>
					<template v-if="settlementType.type == 'wechat' || settlementType.type == 'alipay'">
						<u-form-item label-position="top" label="账号" label-width="150">
							<u-input placeholder="请输入账号" v-model="settlementAccount.account" type="text">
							</u-input>
						</u-form-item>
					</template>
				</u-form>
			</view>
			<view class="fixed-button-group">
				<u-button type="primary" @click="add" :disabled="!validData()">保存</u-button>
				</u-row>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				settlementTypes: [{
					type: 'bankCard',
					name: '银行卡'
				}, {
					type: 'alipay',
					name: '支付宝'
				}, {
					type: 'wechat',
					name: '微信'
				}],
				settlementType: '',
				settlementAccount: {},
				realName: '',
				editFlag: false,
			}
		},
		onBackPress(event) {
			if (event.from == 'backbutton') {
				if (this.editFlag) {
					this.editFlag = false;
					return true;
				}
			}
		},
		onLoad() {
			this.getRealName();
		},
		methods: {

			getRealName() {
				var that = this;
				this.$u.get('/member/getMyPersonalInfo').then(res => {
					that.realName = res.data.realName;
				});
			},
			add() {
				var that = this;
				if (!that.validData()) {
					return;
				}
				uni.showLoading({
					title: ''
				});
				that.settlementAccount.type = that.settlementType.type;
				this.$u.post('/settlementAccount/add', that.settlementAccount).then(res => {
					uni.hideLoading();
					uni.navigateBack();
				});
			},

			validData() {
				if (this.settlementType.type === 'bankCard') {
					if (this.settlementAccount.cardNumber === null || this.settlementAccount.cardNumber === "") {
						return false;
					}
					if (this.settlementAccount.bankName === null || this.settlementAccount.bankName === "") {
						return false;
					}
					return true;
				}
				if (this.settlementType.type === 'wechat' || this.settlementType.type === 'alipay') {
					if (this.settlementAccount.account === null || this.settlementAccount.account === "") {
						return false;
					}
					return true;
				}
			},
			selectSettlementType(settlementType) {
				this.settlementType = settlementType;
				this.editFlag = true;
				this.settlementAccount = {
					cardNumber: '',
					bankName: '',
					account: ''
				};
			},
		}
	}
</script>

<style>
	.fixed-button-group {
		position: fixed;
		bottom: 30rpx;
		left: 0rpx;
		width: 100%;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.edit-block {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.block1 {
		padding-top: 20rpx;
		padding-bottom: 20rpx;
	}

	.block1-title {
		padding-left: 32rpx;
		padding-right: 32rpx;
		font-weight: bold;
		font-size: 24px;
		line-height: 2;
	}

	.receipt-payment-type {
		display: flex;
		align-items: center;
		padding-left: 32rpx;
	}

	.type-icon {}

	.type-name {
		flex: 1;

	}
</style>
