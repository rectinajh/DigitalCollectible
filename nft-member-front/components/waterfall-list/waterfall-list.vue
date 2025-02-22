<template>
	<view class="list">
		<view class="secList left">
			<view class="item" v-for="(item,index) in leftList" :key="index">
				<waterfall-item :item="item"></waterfall-item>
			</view>
		</view>
		
		<view class="secList right">
			<view class="item" v-for="(item,index) in rightList" :key="index">
				<waterfall-item :item="item"></waterfall-item>
			</view>
		</view>
	</view>
</template>

<script>
	import waterfallItem from "@/components/waterfall-item/waterfall-item.vue"
	export default {
		name:"waterfall-list",
		props:{
			list:{
				type:Array,
				default:[]
			}
		},
		components:{
			waterfallItem,
		},
		data() {
			return {
				leftList:[],
				rightList:[],
			};
		},
		watch:{
			list(newVal,oldVal){
				this.leftList=[]
				this.rightList=[]
				if(this.list.length>0){
					for(let i=0;i<this.list.length;i++){
						if((i+1)%2!=0){
							this.leftList.push(this.list[i])
						}else{
							this.rightList.push(this.list[i])
						}
					}
				}
			}
		},
	}
</script>

<style lang="scss">
	.list{
		display: flex;
		justify-content: space-between;
		
		.secList{
			width: 332rpx;
		}
		.left{
			
		}
		.right{
			
		}
	}
</style>