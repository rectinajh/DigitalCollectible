<template>
	<view class="page-content">
		<u-popup v-model="showSearchModalFlag" mode="top" height="auto">
			<view class="search-modal-content">
				<view class="top-search">
					<view class="top-search-l">
						<u-search placeholder="请输入您想查找的内容" :show-action="false" v-model="keyword2"
							@search="showSearchResult" @clear="clearSearchResult"></u-search>
					</view>
					<view class="top-search-r">
						<text class="top-search-r-cancel" @click="showSearchModalFlag = false;">取消</text>
					</view>
				</view>
				<view v-show="showSearchResultFlag">
					<view class="no-search-result" v-show="searchResults.length == 0">
						<u-empty text="搜索不到内容" mode="search"></u-empty>
					</view>
					<view class="search-result" v-for="searchResult in searchResults"
						@click="findBySearchKeywordDict(searchResult)">
						{{searchResult.label}}
					</view>
				</view>
				<view>
					<view class="hot-word-title">
						大家都在搜
					</view>
					<view class="hot-word-items">
						<u-row gutter="8">
							<u-col span="6" v-for="(dict, index) in allCollectionDicts" v-show="index <= 7">
								<view class="hot-word-item u-line-1" @click="findBySearchKeywordDict(dict)">
									{{dict.label}}
								</view>
							</u-col>
						</u-row>
					</view>
				</view>
			</view>
		</u-popup>
		<u-popup v-model="showFilterModalFlag" mode="right" width="85%">
			<view class="filter-modal-content">
				<view class="category-title">
					<view class="category-title-l">
						<u-icon name="grid"></u-icon>
					</view>
					<view class="category-title-r">品牌</view>
				</view>
				<view class="category-items">
					<u-row gutter="8">
						<u-col span="6" v-for="dict in brandDicts">
							<view class="category-item u-line-1"
								:class="{'category-item-active':selectedBrandDict.id == dict.id}"
								@click="clickBrandDict(dict)">{{dict.label}}</view>
						</u-col>
					</u-row>
				</view>
				<view v-show="collectionDicts.length > 0">
					<view class="category-title">
						<view class="category-title-l">
							<u-icon name="photo"></u-icon>
						</view>
						<view class="category-title-r">作品</view>
					</view>
					<view class="category-items">
						<u-row gutter="8">
							<u-col span="6" v-for="dict in collectionDicts">
								<view class="category-item u-line-1"
									:class="{'category-item-active':selectedCollectionDict.id == dict.id}"
									@click="clickCollectionDict(dict)">{{dict.label}}</view>
							</u-col>
						</u-row>
					</view>
				</view>
				<view class="fixed-bottom">
					<view class="fixed-bottom-content">
						<view class="reset-filter-btn" @click="resetFilter">重置</view>
						<view class="confirm-filter-btn" @click="onlySelectBrandDict">确定筛选</view>
					</view>
				</view>
			</view>
		</u-popup>
		<view class="sticky" :class="{'sticky-fixed':stickyFixedFlag}">
			<view class="top-search">
				<view class="top-search-l">
					<u-search placeholder="请输入您想查找的内容" :show-action="false" v-model="keyword"
						@focus="showSearchModalFlag = true;"></u-search>
				</view>
				<view class="top-search-r">
					<u-icon class="filter-icon" name="hourglass" size="36"
						:color="getSelectedDictFlag() ? '#007aff' : '#888'" @click="showFilterModalFlag = true;">
					</u-icon>
				</view>
			</view>
			<view class="query-cond-content">
				<view class="query-cond-content-l">
					<u-dropdown ref="marketDropdown">
						<u-dropdown-item v-model="commodityType" :title="showCommodityTypeLabel()"
							:options="commodityTypeOptions" @change="refreshData"></u-dropdown-item>
					</u-dropdown>
				</view>
				<view class="query-cond-content-r">
					<view class="query-cond-content-r-item"
						:class="{ 'query-cond-content-r-item-active': orderByWay == 'resalePrice_desc' }"
						@click="changeOrderByWay('resalePrice_desc')">价格<u-icon name="arrow-downward"></u-icon>
					</view>
					<view class="query-cond-content-r-item"
						:class="{ 'query-cond-content-r-item-active': orderByWay == 'resalePrice_asc' }"
						@click="changeOrderByWay('resalePrice_asc')">价格<u-icon name="arrow-upward"></u-icon>
					</view>
					<view class="query-cond-content-r-item"
						:class="{ 'query-cond-content-r-item-active': orderByWay == 'resaleTime_desc' }"
						@click="changeOrderByWay('resaleTime_desc')">最新</view>
				</view>
			</view>
		</view>

		<view class="no-data" v-show="noDataFlag">
			<u-empty text="暂无数据" mode="favor"></u-empty>
		</view>
		<u-row gutter="8">
			<u-col span="6" v-for="collection in resaleCollections">
				<view class="collection" @click="resaleCollectionDetailPage(collection.id)">
					<view class="collection-content" @click="">
						<image class="collection-cover" style="height: 280rpx; width: 100%;"
							:src="collection.collectionCover">
						</image>
						<view class="collection-name u-line-1">{{collection.collectionName}}</view>
						<view class="collection-num">
							<view class="collection-num-l">
								#{{collection.collectionSerialNumber}}/{{collection.quantity}}</view>
							<view class="resale-price">￥{{collection.resalePrice}}</view>
						</view>
					</view>
				</view>
			</u-col>
		</u-row>
		<view @click="nextPage" v-show="!noDataFlag">
			<u-loadmore margin-top="40" margin-bottom="40" :status="loadingState" />
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				stickyFixedFlag: false,
				enableStickyFlag: true,
				keyword: '',
				commodityType: '1',
				commodityTypeOptions: [{
					label: '藏品',
					value: '1'
				}, {
					label: '盲盒',
					value: '2'
				}],
				orderByWay: 'resaleTime_desc',
				resaleCollections: [],
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
				showFilterModalFlag: false,
				brandDicts: [],
				selectedBrandDict: '',
				selectedBrandId: '',
				collectionDicts: [],
				selectedCollectionDict: '',
				selectedCollectionId: '',
				allCollectionDicts: [],
				showSearchModalFlag: false,
				keyword2: '',
				searchResults: [],
				showSearchResultFlag: false,
				searchKeywordDict: '',
			}
		},
		onLoad() {
			this.findPublishedBrandAndCollectionDict();
			this.findByPage();
		},
		onShow() {
			this.enableStickyFlag = true;
		},
		onHide() {
			this.enableStickyFlag = false;
			if (this.$refs.marketDropdown && this.$refs.marketDropdown.showDropdown) {
				this.$refs.marketDropdown.close();
			}
		},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods: {

			findBySearchKeywordDict(dict) {
				this.searchKeywordDict = dict;
				this.keyword = dict.label;
				this.keyword2 = dict.label;
				this.showSearchModalFlag = false;
				this.refreshData();
			},

			clearSearchResult() {
				this.keyword = '';
				this.keyword2 = '';
				this.searchResults = [];
				this.showSearchResultFlag = false;
				this.searchKeywordDict = '';
				this.refreshData();
			},

			showSearchResult() {
				var searchResults = [];
				for (var i = 0; i < this.allCollectionDicts.length; i++) {
					var collectionDict = this.allCollectionDicts[i];
					if (collectionDict.label.indexOf(this.keyword2) != -1) {
						searchResults.push(collectionDict);
					}
				}
				this.searchResults = searchResults;
				this.showSearchResultFlag = true;
			},

			resetFilter() {
				this.selectedBrandDict = '';
				this.selectedBrandId = '';
				this.selectedCollectionDict = '';
				this.selectedCollectionId = '';
				this.showFilterModalFlag = false;
				this.refreshData();
			},

			getSelectedDictFlag() {
				if (this.selectedBrandId) {
					return true;
				}
				if (this.selectedCollectionId) {
					return true;
				}
				return false;
			},

			clickBrandDict(dict) {
				this.selectedBrandDict = dict;
				var collectionDicts = [];
				for (var i = 0; i < dict.collections.length; i++) {
					var collectionDict = dict.collections[i];
					if (collectionDict.type == this.commodityType) {
						collectionDicts.push(collectionDict);
					}
				}
				this.collectionDicts = collectionDicts;
				this.selectedCollectionDict = '';
			},

			clickCollectionDict(dict) {
				this.selectedCollectionDict = dict;
				this.selectedCollectionId = dict.id;
				this.showFilterModalFlag = false;
				this.refreshData();
			},

			onlySelectBrandDict() {
				this.selectedCollectionId = '';
				this.selectedBrandId = this.selectedBrandDict.id;
				this.showFilterModalFlag = false;
				this.refreshData();
			},

			findPublishedBrandAndCollectionDict() {
				var that = this;
				this.$u.get('/collection/findPublishedBrandAndCollectionDict', {}).then(res => {
					that.brandDicts = res.data;
					var allCollectionDicts = [];
					for (var i = 0; i < that.brandDicts.length; i++) {
						var brandDict = that.brandDicts[i];
						for (var j = 0; j < brandDict.collections.length; j++) {
							var collectionDict = brandDict.collections[j];
							allCollectionDicts.push(collectionDict);
						}
					}
					that.allCollectionDicts = allCollectionDicts;
				});
			},

			changeOrderByWay(orderByWay) {
				this.orderByWay = orderByWay;
				this.refreshData();
			},

			showCommodityTypeLabel() {
				for (var i = 0; i < this.commodityTypeOptions.length; i++) {
					var commodityTypeOption = this.commodityTypeOptions[i];
					if (this.commodityType == commodityTypeOption.value) {
						return commodityTypeOption.label;
					}
				}
			},

			resaleCollectionDetailPage(id) {
				uni.navigateTo({
					url: '../resaleCollectionDetail/resaleCollectionDetail?id=' + id
				});
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
					that.resaleCollections = [];
				}
				var orderArr = that.orderByWay.split('_');
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					commodityType: that.commodityType,
					creatorId: that.selectedCollectionId ? '' : that.selectedBrandId,
					collectionId: that.selectedCollectionId,
					propertie: orderArr[0],
					direction: orderArr[1]
				};
				if (that.searchKeywordDict) {
					queryParam.collectionId = that.searchKeywordDict.id;
				}
				that.loadingState = 'loading';
				this.$u.get('/collection/findResaleCollectionByPage', queryParam).then(res => {
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
					var oldRecords = that.resaleCollections;
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

<style>
	::v-deep .u-dropdown__content {
		min-width: 200rpx;
	}

	::v-deep .u-dropdown__content__mask {
		background: unset !important;
	}

	page {
		height: 100% !important;
	}

	.no-search-result {
		padding-top: 32rpx;
	}

	.search-result {
		height: 64rpx;
		line-height: 64rpx;
		font-size: small;
		color: #000;
	}

	.hot-word-title {
		line-height: 3;
		color: #097fff;
	}

	.hot-word-items {}

	.hot-word-item {
		text-align: center;
		height: 64rpx;
		line-height: 64rpx;
		margin-bottom: 8rpx;
		margin-top: 8rpx;
		background: #e8e8e8;
		color: #888;
		font-size: small;
		padding-left: 12rpx;
		padding-right: 12rpx;
	}

	.search-modal-content {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 16rpx;
		padding-bottom: 16rpx;
	}

	.fixed-bottom {
		position: fixed;
		bottom: 0rpx;
		left: 0rpx;
		width: 100%;
		background: #ffffff;
	}

	.fixed-bottom-content {
		display: flex;
	}

	.reset-filter-btn {
		background: linear-gradient(to right, #969696, #bcbcbc);
		color: white;
		height: 80rpx;
		line-height: 80rpx;
		padding-left: 80rpx;
		padding-right: 80rpx;
	}

	.confirm-filter-btn {
		background: linear-gradient(to right, #2979ff, #909399);
		color: white;
		height: 80rpx;
		line-height: 80rpx;
		flex: 1;
		text-align: center;
	}



	.category-title {
		display: flex;
		align-items: center;
		padding-left: 32rpx;
		line-height: 2;
	}

	.category-title-l {
		font-size: large;
		color: #007aff;
	}

	.category-title-r {
		font-weight: bold;
		padding-left: 4rpx;
	}

	.category-items {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.category-item {
		background: #e8e8e8;
		color: #888;
		text-align: center;
		margin-bottom: 12rpx;
		margin-top: 12rpx;
		padding: 12rpx;
		font-size: small;
	}

	.category-item-active {
		background: #007aff;
		color: white;
	}

	.filter-modal-close {
		position: fixed;
		width: 100%;
		background: white;
		z-index: 9999;
	}

	.filter-modal-close-inner {
		color: rgb(144, 147, 153);
		padding-right: 20rpx;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
		display: flex;
		justify-content: flex-end;
	}

	.filter-modal-content {
		padding-top: 64rpx;
	}

	.sticky {
		background-color: #ffffff;
	}

	.sticky-fixed {
		padding-top: 28rpx;
	}

	.top-search {
		display: flex;
		align-items: center;
	}

	.top-search-l {
		flex: 1;
	}

	.top-search-r {
		padding-left: 16rpx;
	}

	.filter-icon {}

	.top-search-r-cancel {
		font-size: small;
	}

	.query-cond-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-bottom: 12rpx;
	}

	.query-cond-content-l {}

	.query-cond-content-r {
		display: flex;
		align-items: center;
	}

	.query-cond-content-r-item {
		font-size: small;
		padding: 8rpx 24rpx;
		min-width: 128rpx;
		text-align: center;
	}

	.query-cond-content-r-item-active {
		background: #007aff;
		color: white;
	}

	.page-content {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-bottom: 140rpx;
	}


	.collection {
		padding-bottom: 10rpx;
	}

	.collection-content {
		background: #e7e7e7;
		border-radius: 20rpx;
		padding-bottom: 20rpx;
	}

	.collection-cover {
		border-radius: 20rpx 20rpx 0rpx 0rpx;
	}

	.collection-name {
		font-size: smaller;
		padding-left: 16rpx;
		color: #060606;
		font-weight: bold;
	}

	.resale-price {
		font-weight: bold;
		color: #ff9900;
		font-size: larger;
	}

	.collection-num {
		padding-left: 16rpx;
		padding-top: 8rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-right: 16rpx;
	}

	.collection-num-l {
		font-size: smaller;
		color: #888;
	}

	.no-data {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 400rpx;
	}
</style>
