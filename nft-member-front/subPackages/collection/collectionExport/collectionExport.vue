<template>
	<view class="page-content">
		<view class="info">
			<view class="tips">
				{{tips}}
			</view>
			<view class="collection">
				<view class="img">
					<u-image width="160rpx" height="160rpx" border-radius="16" :src="collectionDetail.collectionCover">
					</u-image>
				</view>
				<view class="text">
					<view class="name">
						{{collectionDetail.collectionName}}
					</view>
					<view class="code">
						#{{collectionDetail.collectionSerialNumber}}/{{collectionDetail.quantity}}
					</view>
					<view class="creatorName">
						{{collectionDetail.creatorName}}
					</view>
				</view>
			</view>
		</view>
		<view class="form">
			<view class="title">
				{{formTitle}}
			</view>
			<view class="input">
				<view v-if="type=='give'" class="textarea">
					<textarea v-model="formInput" placeholder="请输入好友的区块链地址/手机号" :auto-height="true" />
				</view>
				<view v-if="type=='resale'" class="resaleInput">
					<view class="sign">
						￥
					</view>
					<u-input type="number" :value="0" placeholder="0.00" v-model="formInput"/>
				</view>
			</view>

			<view class="btn" :class="{mask:formInput.length<=0}" @click="confirm">
				{{btnText}}
			</view>
		</view>
		<view class="bottom">
			<view class="title">
				{{explainTitle}}
			</view>
			<view class="content">
				<view class="explain-item" v-for="(item,index) in explainList">
					{{item}}
				</view>
			</view>
		</view>
		
		<u-popup v-model="showConfirmModalFlag" mode="center" border-radius="14" length="94%">
			<view class="confirmPopup">
				<view class="title">
					{{collectionDetail.collectionName}}
				</view>
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
				<view class="checked-flag" @click="checkedFlag=!checkedFlag">
					<view class="checkBox">
						<view v-show="!checkedFlag" class="uncheck"></view>
						<view v-show="checkedFlag" class="checked">
							<u-image src="@/static/img/login/checkbox.png" width="32" height="32"></u-image>
						</view>
					</view>
					<view class="text">
						本人承诺本次转赠仅用于好友之间交流分享
					</view>
				</view>	
				<view class="btn" :class="{mask:!checkedFlag}" @click="collectionGive">
					确认转赠
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				tips: '将以下藏品转增给好友',
				formTitle: '请输入好友的区块链地址/手机号',
				btnText: '确认转增',
				explainTitle: '转赠说明：',
				explainList: [],
				collectionId: '',
				collectionDetail: {},
				type: 'give',
				formInput: '',
				giveToAccount: '',
				resalePrice: '',
				receiverInfo: '',
				showConfirmModalFlag: false,
				checkedFlag: false,
			};
		},
		onLoad(option) {
			this.collectionId = option.id;
			this.type = option.type;
			this.init(this.type)
			this.getCollectionDetail();
		},
		methods: {
			init(type) {
				switch (type) {
					case 'give':
						uni.setNavigationBarTitle({
							title: '转增好友'
						})
						this.tips = "将以下藏品转增给好友";
						this.formTitle = "请输入好友的区块链地址/手机号"
						this.btnText = "确认转增"
						this.explainTitle = "转赠说明："
						this.explainList = [
							'1.请您确认您具备赠送数字藏品的民事行为能力；',
							'2.请您确认您与受赠人均已通过平台的实名认证并遵守相关法律法规及平台协议；',
							'3.请您确认本次赠送行为未设定任何形式的对价；',
							'4.转赠操作无法撤销；',
							'5.与数字藏品相关的权利将会同步且毫无保留地转移至受赠人；'
						]
						break;
					case 'resale':
						uni.setNavigationBarTitle({
							title: '寄售藏品'
						})
						this.tips = "将以下藏品进行寄售";
						this.formTitle = "请设置售价"
						this.btnText = "确认寄售"
						this.explainTitle = "寄售说明："
						this.explainList = [
							'1.请您确认您具备寄售数字藏品的民事行为能力;',
							'2.请您确认您已通过平台的实名认证并遵守相关法律法规及平台协议；',
							'3.藏品未经购买前可取消寄售。一经购买，无法取消寄售；',
							'4.藏品出售成功后售卖收入将默认进行钱包余额；',
						]
						break;
					default:
						break;
				}
			},
			getCollectionDetail() {
				var that = this;
				this.$u.get('/myArtwork/findMyHoldCollectionDetail', {
					id: that.collectionId
				}).then(res => {
					that.collectionDetail = res.data;
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
			collectionGive() {
				if(!this.checkedFlag){
					return
				}
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
									url: "/pages/my/my"
								});
							}, 2000);
						}
					});
				});
			},
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
									url: "/pages/my/my"
								});
							}, 2000);
						}
					});
				});
			},
			
			confirm(){
				switch (this.type){
					case 'give':
						this.giveToAccount=this.formInput
						this.getCollectionReceiverInfo()
						break;
					case 'resale':
						this.resalePrice=this.formInput
						this.collectionResale()
						break;
					default:
						break;
				}
			},
		}
	}
</script>

<style lang="scss" scoped>
@import 'collectionExport.scss'
</style>