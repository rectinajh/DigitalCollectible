<template>
	<view class="page-content">
		<view class="list" v-show="!editFlag">
			<view class="item" v-for="(settlementType,index) in settlementTypes" :key="index"
			 @click="selectSettlementType(settlementType)" :class="{border:index!==settlementTypes.length-1}">
				<view class="icon">
					<u-image width="52" height="52"
						:src="'/static/img/wallet/' + settlementType.type + '.png'">
					</u-image>
				</view>
				<view class="right">
					<view class="text">
						<view class="type">
							{{settlementType.name}}
						</view>
					</view>
					<view class="checkbox">
						<u-image src="/static/img/wallet/arrow.png" width="14rpx" mode="widthFix"></u-image>
					</view>
				</view>
			</view>
		</view>
		
		<view class="edit" v-show="editFlag">
			<view class="title">添加{{settlementType.name}}</view>
			<view class="form">
				<view class="item">
					<view class="label">
						姓名
					</view>
					<view class="input">
						<u-input v-model="realName" type="text" :disabled="true" :clearable="false"></u-input>
					</view>
				</view>
				<view class="item">
					<view class="label">
						账号
					</view>
					<view class="input">
						<u-input placeholder="请输入账号" v-model="settlementAccount.account" type="text" :clearable="false"></u-input>
					</view>
				</view>
				
				
				<!-- <u-form>
					<u-form-item label-position="top" label="姓名" label-width="150">
						<u-input v-model="realName" type="text" :disabled="true"></u-input>
					</u-form-item>
					<template v-if="settlementType.type == 'wechat' || settlementType.type == 'alipay'">
						<u-form-item label-position="top" label="账号" label-width="150">
							<u-input placeholder="请输入账号" v-model="settlementAccount.account" type="text">
							</u-input>
						</u-form-item>
					</template>
				</u-form> -->
			</view>
			<view class="fixed-button">
				<view class="btn" :class="{opacity:!settlementAccount.account}" @click="add">
					保存
				</view>
				<!-- <u-button type="primary" @click="add" :disabled="!validData()">保存</u-button>
				</u-row> -->
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				settlementTypes: [{
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

<style lang="scss" scoped>
@import "addAccount.scss"
</style>
