<template>
	<view>
		<view class="navbar" :style="{backgroundColor:bgColor}">
			<view class="btnGroup">
				<view class="left btn" @click="common.back()">
					<view class="icon">
						<u-image src="/static/img/common/leftArrow.png" width="64" height="64" mode="widthFix"></u-image>
					</view>
					<!-- <view class="space" :class="{transparent:bgColor=='rgba(0,0,0,0)'}"></view> -->
				</view>
				
				<view class="title">
					{{title}}
				</view>
				<view class="right btn">
					<view class="icon">
						<u-image src="/static/img/common/share.png" width="64" height="64"></u-image>
					</view>
					<!-- <view class="space" :class="{transparent:bgColor=='rgba(0,0,0,0)'}"></view> -->
				</view>
			</view>
		</view>
		<view v-if="spaceFlag" class="space"></view>
	</view>
</template>

<script>
	export default {
		name: "custom-navbar",
		props: {
			spaceFlag: {
				type: Boolean,
				default: true
			},
			bgColor: {
				type: String,
				default: 'rgba(0,0,0,0)',
			},
			title: {
				type: String,
				default: ''
			}
		},
		data() {
			return {

			};
		},
		methods: {
			
		}
	}
</script>

<style lang="scss" scoped>
	.navbar {
		width: 750rpx;
		height: 112rpx;
		position: fixed;
		top: var(--status-bar-height);;
		left: 0;
		z-index: 99999;

		.btnGroup {
			display: flex;
			align-items: center;
			justify-content: space-between;
			padding: 0 34rpx;
			position: absolute;
			left: 0;
			top: 0;
			width: 750rpx;
			height: 112rpx;
			
			.btn{
				width: 64rpx;
				height: 64rpx;
				position: relative;
				display: flex;
				align-items: center;
				justify-content: center;
				.icon{
					position: absolute;
				}
				.transparent {
					background-color: rgba(255, 255, 255, 0.2);
				}
				.space{
					width: 64rpx;
					height: 64rpx;
					border-radius: 100%;
				}
			}
		}
	}

	.space {
		width: 750rpx;
		height: 112rpx;
	}
</style>