<template>
	<view class="page-content">
		<view class="form">
			<view class="title">
				提现金额（元）
			</view>
			<view class="input-box">
				<view class="input-left">
					<view class="sign">
						￥
					</view>
					<view class="input">
						<u-input type="number" :placeholder="`可提现余额${balance}`" v-model="amount" :clearable="false"
						placeholder-style="color:#999;fontSize:30rpx" :custom-style="{fontSize:'40rpx'}"></u-input>
					</view>
				</view>
				<view class="btn" @click="allAmount">
					全部提现
				</view>
			</view>
			
			<view class="tips">
				<view class="tips-top">
					<view class="left">
						实际到账
					</view>
					<view class="right">
						{{actualAmount}}
					</view>
				</view>
				<view class="tips-bottom">
					<view class="left">
						手续费
					</view>
					<view class="right">
						0
					</view>
				</view>
			</view>
		</view>
		
		<view class="methods">
			<view class="title">
				<view class="title-text">
					提现方式
				</view>
				<view class="btn" @click="toSettlementAccountPage">
					<view class="text">
						更多账户
					</view>
					<view class="arrow">
						<u-image src="/static/img/wallet/arrow.png" width="12rpx" mode="widthFix"></u-image>
					</view>
				</view>
			</view>
			
			<view class="list">
				<view class="item" v-for="(settlementAccount,index) in settlementAccounts" :key="index"
				@click="selectedSettlementAccount(settlementAccount)">
					<view class="icon">
						<u-image width="52" height="52"
							:src="'/static/img/wallet/' + settlementAccount.type + '.png'">
						</u-image>
					</view>
					<view class="right">
						<view class="text">
							<view class="type">
								{{settlementAccount.typeName||settlementAccount.bankName}}
							</view>
							<view class="account">
								{{settlementAccount.account||cardNumberFormat(settlementAccount.cardNumber)}}
							</view>
						</view>
						<view class="checkbox">
							<view class="check" v-show="mySettlementAccount == settlementAccount">
								<u-image src="/static/img/login/checkbox.png" width="40" height="40"></u-image>
							</view>
							<view class="uncheck" v-show="mySettlementAccount != settlementAccount">
								<u-image src="/static/img/login/uncheckbox.png" width="40" height="40"></u-image>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<view class="bottom">
			<view class="confirm-btn" :class="{opacity:!amount}" @click="withdraw">
				确认提现
			</view>
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
				titleStyle:{
					
				}
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
				url: '/pages/withdrawRecord/withdrawRecord'
			});
		},
		onLoad() {
			this.getBalance();
			this.findActivatedSettlementAccount();
		},
		methods: {
			toSettlementAccountPage() {
				// uni.navigateTo({
				// 	url: "/pages/settlementAccount/settlementAccount"
				// });
				uni.navigateTo({
					url: "/subPackages/wallet/account/account"
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

<style lang="scss" scoped>
	@import 'withdraw.scss'
</style>