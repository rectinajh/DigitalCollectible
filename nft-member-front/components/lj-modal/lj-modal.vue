<template>
	<view>
		<u-popup v-model="showPopup" mode="center" border-radius="28" @close="close">
			<slot name="popup">
				<view class="popup">
					<view class="title">
						{{title}}
					</view>
					<view class="content">
						{{content}}
					</view>
					<slot name="img"></slot>
					<view class="btnGroup double" v-if="leftBtn">
						<view class="left" @click="leftClick">
							{{leftBtn}}
						</view>
						<view class="right" @click="rightClick">
							{{rightBtn}}
						</view>
					</view>
					
					<view class="btnGroup single" v-else>
						<view class="left" @click="leftClick">
							{{leftBtn}}
						</view>
						<view class="right" @click="rightClick">
							{{rightBtn}}
						</view>
					</view>
				</view>
			</slot>
		</u-popup>
	</view>
</template>

<script>
	export default {
		name:"lj-modal",
		props:{
			show:{
				type:Boolean,
				default:false
			},
			title:{
				type:String,
				default:""
			},
			content:{
				type:String,
				default:""
			},
			leftBtn:{
				type:String,
				default:""
			},
			rightBtn:{
				type:String,
				default:""
			},
		},
		data() {
			return {
				showPopup:false,
			};
		},
		mounted() {
			this.showPopup = this.show
		},
		watch:{
			show(val){
				this.showPopup = val
			}
		},
		methods:{
			leftClick(){
				this.$emit("leftClick")
			},
			rightClick(){
				this.$emit("rightClick")
			},
			close() {
				this.$emit("close")
			}
		}
	}
</script>

<style lang="scss" scoped>
	.popup{
		width: 540rpx;
		background: #181819;
		background-blend-mode: color-dodge;
		background: #181819;
		.title{
			font-size: 34rpx;
			width: 100%;
			text-align: center;
			padding-top: 38rpx;
		}
		.content{
			font-size:28rpx;
			width: 100%;
			text-align: center;
			padding: 20rpx 0;
		}
		.btnGroup{
			width: 100%;
			height: 88rpx;
			display: flex;
			align-items: center;
			font-size: 34rpx;
			text-align: center;
			line-height: 88rpx;
			border-top: 1rpx solid #FFFFFF14;
			box-sizing: border-box;
			.left{
				height: 88rpx;
			}
			.right{
				color: #FCE6B7;
				height: 88rpx;
				box-sizing: border-box;
			}
		}
		.double{
			.left{
				width: 50%;
			}
			.right{
				width: 50%;
				border-left: 1rpx solid #FFFFFF14
			}
		}
		.single{
			.right{
				width: 100%;
				text-align: center;
			}
		}
	}
</style>