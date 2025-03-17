<template>
	<view class="content">
		<view class="tabs">
			<view class="item" v-for="(item,index) in tabs" :key="index" 
			:class="{active:currentTab==index}" @click="switchTab(index)">
				{{item.name}}
			</view>
		</view>
		<view class="list">
			<view class="no-data" v-show="noDataFlag">
				<u-empty text="暂无数据"color="#fff" icon-size="240" 
				src="/static/img/common/default/noData.png"></u-empty>
			</view>
			
			<order-list :list="giveRecords"></order-list>
			
			<!-- <view class="item" v-for="(giveRecord,index) in giveRecords" :key="index">
				<view class="top">
					<view class="code">
						订单编号：{{giveRecord.orderNo}}
					</view>
					<view class="type" :class="{given:giveRecord.giveDirection == 'from'}">
						{{giveRecord.giveDirection == 'from' ? '已转出' : '已转入'}}
					</view> 
				</view>
				<view class="main">
					<view class="left">
						<u-image class="collection-cover" width="160rpx" height="160rpx" mode="aspectFill" 
						border-radius="16" :src="giveRecord.collectionCover">
						</u-image>
					</view>
					<view class="right">
						<view class="name">
							{{giveRecord.collectionName}}
						</view>
						<view class="user">
							{{giveRecord.giveDirection == 'from' ? `接受者:${giveRecord.giveToMobile}` : `转赠者:${giveRecord.giveFromMobile}`}}
						</view>
						<view class="date">
							{{giveRecord.giveTime}}
						</view>
					</view>
				</view>
			</view> -->
		</view>
	</view>
</template>

<script>
	import orderList from '@/subPackages/mine/components/order-list/order-list.vue'
	export default {
		data() {
			return {
				tabs: [{
					name: '全部'
				}, {
					name: '转出'
				}, {
					name: '转入',
				}],
				currentTab: 0,
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
				giveRecords: [],
			}
		},
		components:{
			orderList
		},
		onLoad() {
			this.findByPage();
		},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods: {
			switchTab(index) {
				if(this.currentTab===index) return
				this.currentTab = index;
				this.refreshData();
			},
	
			refreshData() {
				this.pageNum = 1;
				this.loadingState = 'loading';
				this.findByPage();
			},
	
			nextPage() {
				if (this.loadingState == 'nomore') {
					return;
				}
				this.pageNum = this.pageNum + 1;
				this.findByPage();
			},
	
			findByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.giveRecords = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					giveDirection: that.currentTab == 0 ? '' : that.currentTab == 1 ? 'from' : 'to'
				};
				that.loadingState = 'loading';
				this.$u.get('/transaction/findMyGiveRecordByPage', queryParam).then(res => {
					var newRecords = res.data.content;
					var totalPage = res.data.totalPage;
					if (that.pullDownRefreshFlag) {
						that.pullDownRefreshFlag = false;
						uni.stopPullDownRefresh();
					}
					if (newRecords.length == 0) {
						that.loadingState = 'nomore';
					}
					if (totalPage == that.pageNum) {
						that.loadingState = 'nomore';
					}
					var oldRecords = that.giveRecords;
					for (var i = 0; i < newRecords.length; i++) {
						var flag = true;
						for (var j = 0; j < oldRecords.length; j++) {
							if (newRecords[i].id == oldRecords[j].id) {
								flag = false;
								break;
							}
						}
						if (flag) {
							oldRecords.push(newRecords[i]);
						}
					}
					that.noDataFlag = oldRecords.length == 0;
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
@import 'giveRecord.scss'
</style>
