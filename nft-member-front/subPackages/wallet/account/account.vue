<template>
	<view class="page-content">
		<view class="list"v-if="settlementAccounts.length>0">
			<view class="item" v-for="(settlementAccount,index) in settlementAccounts" :key="index"
			 @click="showActionSheet(settlementAccount)" :class="{border:index!==settlementAccounts.length-1}">
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
					<view class="checkbox" @click.stop="updateActivatedFlag(settlementAccount)">
						<view class="check" v-show="settlementAccount.activated">
							<u-image src="/static/img/login/checkbox.png" width="40" height="40"></u-image>
						</view>
						<view class="uncheck" v-show="!settlementAccount.activated">
							<u-image src="/static/img/login/uncheckbox.png" width="40" height="40"></u-image>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<view class="bottom">
			<view class="confirm-btn" @click="toAddPage">
				添加
			</view>
		</view>
		
		
		<u-action-sheet bgColor="#181819" gabColor="#0A0A0A" border-radius="32" cancelColor="#FFFFFF"
		:list="actions" v-model="showAction" @click="actionEvent"></u-action-sheet>
		<u-modal v-model="delConfirm" content="确认删除？" :show-cancel-button="true" :show-title="false" 
		confirm-color="#FCE6B7" cancel-color="#fff" backgroundColor="#181819" 
		:title-style="{color:'#fff'}" :content-style="{color:'#fff'}" @confirm="del">
		</u-modal>
		<u-modal v-model="bindRealName" title="提示" content="请先进行实名认证" :show-cancel-button="false" 
		confirm-color="#FCE6B7" cancel-color="#fff" backgroundColor="#181819" :title-style="{color:'#fff'}" :content-style="{color:'#fff'}"
		confirm-text="我知道了" @confirm="toBindRealNamePage">
		</u-modal>
		<view class="no-data" v-show="noDataFlag">
			<u-empty text="未添加结算账户" mode="list"></u-empty>
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
				// uni.navigateTo({
				// 	url: '/pages/addSettlementAccount/addSettlementAccount'
				// });
				uni.navigateTo({
					url: '/subPackages/wallet/addAccount/addAccount'
				});
			}
		}
	}
</script>

<style lang="scss">
@import 'account.scss'
</style>
