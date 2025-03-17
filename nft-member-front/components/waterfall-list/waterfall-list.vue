<template>
	<view class="list">
		<view class="secList left">
			<view class="item" v-for="(item,index) in leftList" :key="index">
				<slot name="leftItem">
					<market-item v-if="isMarket" :item="item" @itemClick="latestCollectionDetailPage"></market-item>
					<waterfall-item v-else :item="item" @itemClick="latestCollectionDetailPage"></waterfall-item>
				</slot>
			</view>
		</view>
		
		<view class="secList right">
			<view class="item" v-for="(item,index) in rightList" :key="index">
				<slot name="rightItem">
					<market-item v-if="isMarket" :item="item" @itemClick="latestCollectionDetailPage"></market-item>
					<waterfall-item v-else :item="item" @itemClick="latestCollectionDetailPage"></waterfall-item>
				</slot>
			</view>
		</view>
	</view>
</template>

<script>
	import waterfallItem from "@/components/waterfall-item/waterfall-item.vue"
	import marketItem from "@/components/market-item/market-item.vue"
	export default {
		name:"waterfall-list",
		props:{
			list:{
				type:Array,
				default:[]
			},
			isMarket:{
				type:Boolean,
				default:false
			}
		},
		components:{
			waterfallItem,
			marketItem,
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
		methods:{
			latestCollectionDetailPage(id) {
				this.$emit('itemClick',id)
			},
		}
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