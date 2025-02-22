<template>
	<view>
		<view class="navbar" :style="{backgroundColor:bgColor}">
			<view class="btnGroup">
				<view class="left" v-slot="leftBtn" @click="back()">
					<u-image src="/static/img/common/leftArrow.png" width="16" mode="widthFix"></u-image>
				</view>
				<view class="right" v-slot="rightBtn">
					<u-image src="/static/img/common/share.png" width="42" mode="widthFix"></u-image>
				</view>
			</view>
		</view>
		<view v-if="spaceFlag" class="space"></view>
	</view>
</template>

<script>
	export default {
		name:"custom-navbar",
		props:{
			spaceFlag:{
				type:Boolean,
				default:true
			},
			bgColor:{
				type:String,
				default:'rgba(0,0,0,0)',
			}
		},
		data() {
			return {
				
			};
		},
		methods:{
			back(){
				uni.navigateBack()
			}
		}
	}
</script>

<style lang="scss" scoped>
	.navbar{
		width: 750rpx;
		height: 112rpx;
		position: fixed;
		top: 0;
		left: 0;
		z-index: 99999;
		.btnGroup{
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding: 0 34rpx;
			position: absolute;
			left: 0;
			top:0;
			width: 750rpx;
			height: 112rpx;
		}
	}
	.space{
		width: 750rpx;
		height: 112rpx;
	}
</style>