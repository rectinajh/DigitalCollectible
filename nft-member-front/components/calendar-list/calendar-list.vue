<template>
	<view class="list">
		<view class="day" v-for="(dateItem,dateIndex) in list" :key="dateIndex">
			<view class="piece">
				<p class="dot"></p>
				<p class="date OPPOSans-M">{{dateItem.date}}</p>
			</view>
			<view class="dayList">
				<view class="dayItem" v-for="(dayItem,dayIndex) in dateItem.timeCollections">
					<view class="time OPPOSans-L">
						{{dayItem.time}}
					</view>
					<view class="item" v-for="(item,index) in dayItem.collections" :key="index">
						<presale-item :item="item" @clickItem="latestCollectionDetailPage"></presale-item>
					</view>
				</view>
			</view>
		</view>
		
	</view>
</template>

<script>
	import presaleItem from "@/components/presale-item/presale-item.vue"
	export default {
		name:"calendar",
		props:{
			list:{
				type:Array,
				default:()=>[]
			}
		},
		components:{
			presaleItem
		},
		data() {
			return {
				
			};
		},
		methods:{
			latestCollectionDetailPage(id) {
				uni.navigateTo({
					url: '../latestCollectionDetail/latestCollectionDetail?id=' + id
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.list{
		width: 100%;
		border-left: 1rpx solid #FCE6B7;
		margin-top: 40rpx;
		.day{
			padding-bottom: 40rpx;
			.piece{
				display: flex;
				align-items: center;
				padding-bottom: 8rpx;
				.dot{
					width: 16rpx;
					height: 16rpx;
					background-color: #FCE6B7;
					border-radius: 50%;
					margin-left: -8rpx;
				}
				.date{
					margin-left: 24rpx;
					font-size: 40rpx;
					color: #FCE6B7;
				}
			}
			.dayList{
				width: 646rpx;
				margin-left: 16rpx;
				.dayItem{
					margin-left: 16rpx;
					.time{
						margin-bottom: 20rpx;
						color: #FCE6B7;
						font-size: 28rpx;
					}
				}
			}
		}
		
	}
</style>