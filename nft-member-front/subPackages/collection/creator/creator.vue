<template>
	<view class="page-content">
		<view class="creator-info">
			
			<view class="creator-content">
				<view class="avatar">
					<u-image width="128" height="128" :src='creator.avatar' shape="circle"></u-image>
				</view>
				<view class="text">
					<view class="name">
						{{creator.name}}
					</view>
					<view class="description">
						创作者
					</view>
				</view>
			</view>
			<view class="border">
				<u-image src="/static/img/creator/gradientBg.png" width="100%" mode="widthFix"></u-image>
			</view>
			
		</view>
		<view class="main">
			<view class="tabs">
				<view class="tabItem" v-for="(item,index) in tabs" :key="index" 
				:class="{checked:currentTab===item.value}" @click="changeTab(item.value)">
					{{item.name}}
				</view>
			</view>
			<view class="list">
				<view class="no-data" v-show="noDataFlag">
					<u-empty text="暂无数据" mode="favor"></u-empty>
				</view>
				<view v-show="currentTab == 'collection'" class="collection">
					<waterfall-list :list="collections" @itemClick="latestCollectionDetailPage"></waterfall-list>
				</view>
				<view v-show="currentTab == 'mysteryBox'" class="mysteryBox">
					<waterfall-list :list="mysteryBoxs" @itemClick="latestCollectionDetailPage"></waterfall-list>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import waterfallList from "@/components/waterfall-list/waterfall-list.vue"
	export default {
		data() {
			return {
				creatorId: '',
				creator: '',
				tabs: [{
					name: '藏品',
					value:'collection'
				}, {
					name: '盲盒',
					value:'mysteryBox'
				}],
				currentTab: 'collection',
				collections: [],
				mysteryBoxs: [],
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
			};
		},
		components:{
			waterfallList,
		},
		onLoad(option) {
			this.creatorId = option.id;
			this.findCreator();
			this.findByPage();
		},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods:{
			findCreator() {
				var that = this;
				this.$u.get('/collection/findCreatorById', {
					id: that.creatorId
				}).then(res => {
					that.creator = res.data;
				});
			},
			findByPage() {
				if (this.currentTab == 'collection') {
					this.findLatestCollectionByPage();
				} else if (this.currentTab == 'mysteryBox') {
					this.findLatestMysteryBoxByPage();
				}
			},
			nextPage() {
				if (this.loadingState == 'nomore') {
					return;
				}
				this.pageNum = this.pageNum + 1;
				this.findByPage();
			},
			refreshData() {
				this.pageNum = 1;
				this.loadingState = 'loading';
				this.findByPage();
			},
			changeTab(val){
				if(this.currentTab!==val){
					this.currentTab=val;
					switch(val){
						case 'collection':
							if(this.collections.length<=0){
								this.findLatestCollectionByPage();
							}
							break;
						case 'mysteryBox':
							if(this.mysteryBoxs.length<=0){
								this.findLatestMysteryBoxByPage();
							}
							break;
					}
				}
				else{
					return
				}
			},
			findLatestCollectionByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.collections = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					creatorId: that.creatorId
				};
				that.loadingState = 'loading';
				this.$u.get('/collection/findLatestCollectionByPage', queryParam).then(res => {
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
					var oldRecords = that.collections;
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
			
			findLatestMysteryBoxByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.mysteryBoxs = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					creatorId: that.creatorId
				};
				that.loadingState = 'loading';
				this.$u.get('/collection/findLatestMysteryBoxByPage', queryParam).then(res => {
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
					var oldRecords = that.mysteryBoxs;
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
			latestCollectionDetailPage(id) {
				uni.navigateTo({
					url: `/subPackages/collection/collectionDetail/collectionDetail?id=${id}`
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	@import 'creator.scss'
</style>
