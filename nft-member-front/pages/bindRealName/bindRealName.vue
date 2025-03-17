<template>
	<view class="content">
		<view class="title">
			请填写实名认证信息
		</view>
		<view class="form">
			<view class="item">
				<view class="label">
					真实姓名
				</view>
				<view class="input">
					<u-input placeholder="请输入真实姓名" placeholder-style="{color:#999999}" v-model="realName" type="text" :clearable="false">
					</u-input>
				</view>
			</view>
			<view class="item">
				<view class="label">
					身份证号
				</view>
				<view class="input">
					<u-input placeholder="请输入身份证号" placeholder-style="{color:#999999}" v-model="identityCard" type="text" :clearable="false">
					</u-input>
				</view>
			</view>
			<view class="item">
				<view class="label">
					联系方式
				</view>
				<view class="input">
					<u-input v-model="mobile" placeholder-style="{color:#999999}" type="text" :clearable="false" :disabled="true">
					</u-input>
				</view>
			</view>
			
		</view>
		<view class="action-btn" @click="bindRealName">
			确认
		</view>
		<view class="tips">
			<p style="margin-bottom: 8rpx;">
				1.根据法律法规要求，实名信息须与注册手机号持有人相符，且年满21周岁并小于60周岁。
			</p>
			<p>
				2.信息安全保障中，以下信息仅用于身份确认，未经您同意不会用于其它用途。
			</p>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				customActionBtnStyle: {
					width: '70%'
				},
				realName: '',
				identityCard: '',
				mobile: ''
			}
		},
		computed: {},
		onLoad(option) {
			this.getMyPersonalInfo();
		},
		methods: {

			getMyPersonalInfo() {
				var that = this;
				this.$u.get('/member/getMyPersonalInfo').then(res => {
					that.mobile = res.data.mobile;
				});
			},

			bindRealName() {
				var that = this;
				if (that.realName === null || that.realName === "") {
					uni.showToast({
						title: "请输入真实姓名",
						icon: "none"
					});
					return;
				}
				if (that.identityCard === null || that.identityCard === "") {
					uni.showToast({
						title: "请输入身份证号",
						icon: "none"
					});
					return;
				}
				this.$u.post('/member/bindRealName', {
					realName: that.realName,
					identityCard: that.identityCard
				}).then(res => {
					if(res.data.code!='200'){
						uni.showToast({
							icon: 'error',
							title: res.data.msg,
							duration: 2000,
							mask: false,
						});
					}
					else{
						let {data} = res.data.data;
						if(data.result!='1'){
							uni.showToast({
								icon: 'error',
								title: data.resultMsg,
								duration: 2000,
								mask: false,
							});
						}
						else{
							uni.showToast({
								icon: 'success',
								title: '认证成功',
								duration: 2000,
								mask: true,
								complete: function() {
									setTimeout(() => {
										uni.navigateBack();
									}, 2000);
								}
							});
						}
					}
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.content{
		padding: 0 32rpx;
		.title{
			font-size: 48rpx;
			padding-top: 64rpx;
			padding-left: 28rpx;
		}
		.form{
			padding: 48rpx 0rpx 0rpx 0rpx;
			.item{
				background-color: #212121;
				border-radius: 16rpx;
				margin-bottom: 24rpx;
				display: flex;
				align-items: center;
				padding: 28rpx;
				height: 96rpx;
				box-sizing: border-box;
				.label{
					font-size: 30rpx;
				}
				.input{
					font-size: 28rpx;
					margin-left: 36rpx;
				}
			}
		}
		
		.action-btn {
			width: 100%;
			height: 88rpx;
			line-height: 88rpx;
			background-color: #FCE6B7;
			color: #644205;
			border-radius: 60rpx;
			font-size: 32rpx;
			text-align: center;
		}
		.tips{
			font-size: 24rpx;
			color: #666;
			line-height: 28rpx;
			letter-spacing: 0rpx;
			margin-top: 24rpx;
		}
	}
</style>
