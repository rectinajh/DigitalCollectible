<template>
	<view class="cell">
		<view class="title">
			<slot name="icon">
				<view class="icon" v-if="icon">
					<u-image :src="icon" width="40" height="40"></u-image>
				</view>
			</slot>
			<view class="text">
				{{title}}
			</view>
		</view>
		<view class="content">
			<slot name="description">
				<view v-if="description" class="description">
					{{description}}
				</view>
			</slot>
			<view class="arrow" v-if="isClick">
				<u-image src="/static/img/common/cell/arrow.png" width="12" mode="widthFix"></u-image>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		props:{
			title:{
				type:String,
				default:'敬请期待'
			},
			description:{
				type:String,
			},
			isClick:{
				type:Boolean,
				default:true
			},
			icon:{
				type:String
			}
		},
		data() {
			return {
				
			};
		},
		methods:{
			cellClick(e){
				
			}
		}
	}
</script>

<style lang="scss" scoped>
	.cell{
		width: 100%;
		font-size: 30rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		.title{
			display: flex;
			align-items: center;
			.icon{
				padding-right: 24rpx;
			}
			.text{}
		}
		.content{
			display: flex;
			align-items: center;
			.description{
				color: #999;
			}
			.arrow{
				padding-left: 16rpx;
			}
		}
	}
</style>
