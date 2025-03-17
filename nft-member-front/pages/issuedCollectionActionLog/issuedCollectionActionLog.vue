<template>
	<view>
		<view class="orders">
			<template v-for="(record,index) in actionLogs">
				<view class="order" :key="index">
					<view class="order-section1">
						<view class="order-section1-l">{{record.memberNickName}}</view>
						<view class="order-section1-r">{{record.actionDesc}}</view>
					</view>
					<view class="order-section2">
						<text>{{record.actionTime}}</text>
					</view>
				</view>
				<u-line v-if="index!==record" style="margin-left: -40rpx;" color="#FFFFFF14" length="750rpx" />
			</template>
		</view>
		<!-- <view class="no-data">我是有底线的~</view> -->
	</view>
</template>

<script>
	export default {
		data() {
			return {
				issuedCollectionId: '',
				actionLogs: []
			}
		},
		onLoad(option) {
			this.issuedCollectionId = option.id;
			this.findIssuedCollectionActionLog();
		},
		methods: {
			findIssuedCollectionActionLog() {
				var that = this;
				this.$u.get('/collection/findIssuedCollectionActionLog', {
					issuedCollectionId: that.issuedCollectionId
				}).then(res => {
					that.actionLogs = res.data;
				});
			},
		}
	}
</script>

<style lang="scss" scoped> 
	.no-data {
		text-align: center;
		line-height: 3;
	}

	.orders {
		padding: 32rpx 40rpx 40rpx 40rpx;
		
		.order {
			
			padding-top: 12rpx;
			padding-bottom: 12rpx;
			
			.order-section1 {
				display: flex;
				justify-content: space-between;
				align-items: center;
				font-size: 28rpx;
				.order-section1-l {
					line-height: 1.5;
				}
				
				.order-section1-r {
					line-height: 2;
				}
			}
			
			.order-section2 {
				color: #666666;
				font-size: 24rpx;
				
			}
			
			// .order-section2-l {
			// 	padding-right: 8rpx;
			// }
		}
	}

	
</style>
