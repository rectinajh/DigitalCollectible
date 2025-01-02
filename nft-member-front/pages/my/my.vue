<template>
	<view class="page-content">
		<view class="top-content">
			<view class="avatar-nick-name">
				<view class="member-avatar">
					<u-image height="80rpx" width="80rpx" :src="getAvatar()" shape="circle"></u-image>
				</view>
				<view class="nick-name-mobile">
					<view v-show="isLogin()">
						<view class="member-nick-name">{{personalInfo.nickName}}</view>
						<view class="member-mobile">{{personalInfo.mobile}}</view>
					</view>
					<view class="login" v-show="!isLogin()" @click="loginPage">
						<view class="login-t">登录/注册</view>
						<view class="login-b">点击登录 享受更多精彩内容</view>
					</view>
				</view>
				<view class="setting" @click="gotoPage('../setting/setting')">
					<u-icon name="setting" size="48"></u-icon>
				</view>
			</view>
			<view class="block-chain-addr" v-show="isLogin()"><text
					class="block-chain-addr-l">区块链地址：{{getBlockChainAddr()}}</text>
				<u-image height="32rpx" width="32rpx" src="/static/img/copy_my.png" v-show="personalInfo.blockChainAddr"
					@click="copyData(personalInfo.blockChainAddr)"></u-image>
			</view>
			<view class="grid-navs">
				<view class="grid-nav" @click="gotoPage('../balanceChangeLog/balanceChangeLog')">
					<u-icon name="rmb-circle" size="48"></u-icon>
					<view>我的钱包</view>
				</view>
				<view class="grid-nav" @click="gotoPage('../payOrder/payOrder')">
					<u-icon name="order" size="48"></u-icon>
					<view>我的订单</view>
				</view>
				<view class="grid-nav" @click="gotoPage('../myGiveRecord/myGiveRecord')">
					<u-icon name="zhuanfa" size="48"></u-icon>
					<view>转赠记录</view>
				</view>
				<view class="grid-nav" @click="gotoPage('../msg/msg')">
					<view class="msg-read" :class="{'msg-unread-num' : unreadMsgIds.length > 0}">
					</view>
					<u-icon name="chat" size="48">
					</u-icon>
					<view>消息通知
					</view>
				</view>
			</view>
			<view class="grid-navs">
				<view class="grid-nav" @click="gotoPage('../invite/invite')">
					<u-icon name="man-add" size="48"></u-icon>
					<view>邀请好友</view>
				</view>
				<view class="grid-nav" @click="gotoPage('../composeActivity/composeActivity')">
					<u-icon name="bag" size="48"></u-icon>
					<view>合成藏品</view>
				</view>
				<view class="grid-nav" @click="gotoPage('../exchangeCode/exchangeCode')">
					<u-icon name="gift" size="48">
					</u-icon>
					<view>空投兑换</view>
				</view>
				<view class="grid-nav" @click="contactCustomerService">
					<u-icon name="kefu-ermai" size="48"></u-icon>
					<view>联系客服</view>
				</view>
			</view>
		</view>
		<view>
			<view class="custom-tabs">
				<view class="custom-tabs-l">
					<u-tabs :list="tabs" :is-scroll="false" :current="currentTab" @change="switchTab"></u-tabs>
				</view>
			</view>
			<view>
				<view class="no-data" v-show="noDataFlag">
					<u-empty text="暂无数据" mode="favor"></u-empty>
				</view>
				<view class="custom-tab-content">
					<view v-show="currentTab == 0">
						<u-row gutter="8">
							<u-col span="6" v-for="collection in collections">
								<view class="collection">
									<view class="collection-content" @click="holdCollectionDetailPage(collection.id)">
										<image class="collection-cover" style="height: 280rpx; width: 100%;"
											:src="collection.collectionCover">
										</image>
										<view class="collection-name u-line-1">{{collection.collectionName}}</view>
										<view class="collection-hold-date">收藏于 {{collection.holdDate}}</view>
									</view>
								</view>
							</u-col>
						</u-row>
					</view>
					<view v-show="currentTab == 1">
						<u-row gutter="8">
							<u-col span="6" v-for="collection in mysteryBoxs">
								<view class="collection">
									<view class="collection-content" @click="holdCollectionDetailPage(collection.id)">
										<image class="collection-cover" style="height: 280rpx; width: 100%;"
											:src="collection.collectionCover">
										</image>
										<view class="collection-name u-line-1">{{collection.collectionName}}</view>
										<view class="collection-hold-date">收藏于 {{collection.holdDate}}</view>
									</view>
								</view>
							</u-col>
						</u-row>
					</view>
					<view v-show="currentTab == 2">
						<u-row gutter="8">
							<u-col span="6" v-for="collection in soldCollections">
								<view class="collection">
									<view class="collection-content">
										<image class="collection-cover" style="height: 280rpx; width: 100%;"
											:src="collection.collectionCover">
										</image>
										<view class="collection-name u-line-1">{{collection.collectionName}}</view>
										<view class="collection-hold-date">卖出于 {{collection.soldDate}}</view>
										<view class="resale-price">￥{{collection.soldPrice}}</view>
									</view>
								</view>
							</u-col>
						</u-row>
					</view>
					<view v-show="currentTab == 3">
						<u-row gutter="8">
							<u-col span="6" v-for="collection in resaleCollections">
								<view class="collection">
									<view class="collection-content"
										@click="myResaleCollectionDetailPage(collection.id)">
										<image class="collection-cover" style="height: 280rpx; width: 100%;"
											:src="collection.collectionCover">
										</image>
										<view class="collection-name u-line-1">{{collection.collectionName}}</view>
										<view class="collection-hold-date">发布于 {{collection.resaleDate}}</view>
										<view class="resale-price">￥{{collection.resalePrice}}</view>
									</view>
								</view>
							</u-col>
						</u-row>
					</view>
				</view>
				<view @click="nextPage" v-show="!noDataFlag">
					<u-loadmore margin-top="40" margin-bottom="40" :status="loadingState"></u-loadmore>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				personalInfo: '',
				tabs: [{
					name: '藏品'
				}, {
					name: '盲盒'
				}, {
					name: '已卖出',
				}, {
					name: '已发布',
				}],
				currentTab: 0,
				collections: [],
				mysteryBoxs: [],
				resaleCollections: [],
				soldCollections: [],
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
				unreadMsgIds: [],
				customerServiceUrl: '',
			}
		},
		onLoad(option) {
			var tab = option.tab;
			if (tab) {
				this.currentTab = tab;
			}
		},
		onShow() {
			this.getCustomerServiceUrl();
			this.getMyPersonalInfo();
			this.findByPage();
		},
		onReachBottom() {
			this.nextPage();
		},
		onPullDownRefresh() {
			if (!this.isLogin()) {
				uni.stopPullDownRefresh();
				return;
			}
			this.pullDownRefreshFlag = true;
			this.refreshData();
		},
		methods: {

			loginPage() {
				uni.reLaunch({
					url: "../login/login"
				});
			},

			isLogin() {
				var tokenName = uni.getStorageSync('tokenName');
				return tokenName != null && tokenName != '';
			},

			getBlockChainAddr() {
				if (this.personalInfo) {
					if (this.personalInfo.bindRealNameTime === null || this.personalInfo.bindRealNameTime === '') {
						return '实名认证后生成区块链地址...';
					} else {
						return this.personalInfo.blockChainAddr ? this.personalInfo.blockChainAddr : '等待创建区块链地址...';
					}
				}
			},

			getCustomerServiceUrl() {
				var that = this;
				this.$u.get('/setting/getCustomerServiceUrl').then(res => {
					that.customerServiceUrl = res.data;
				});
			},

			contactCustomerService() {
				if (this.customerServiceUrl) {
					uni.navigateTo({
						url: '../customerService/customerService?customerServiceUrl=' + this.customerServiceUrl
					});
				} else {
					uni.showToast({
						title: "客服太繁忙了",
						icon: "none"
					});
					return;
				}
			},

			switchTab(index) {
				this.currentTab = index;
				this.refreshData();
			},

			copyData(data) {
				var that = this
				uni.setClipboardData({
					data: '' + data,
					success: function() {
						uni.showToast({
							title: '复制成功',
							duration: 2000,
							icon: 'none'
						});
					},
					fail: function(err) {
						uni.showToast({
							title: '复制失败' + err,
							duration: 2000,
							icon: 'none'
						});
					}
				});
			},

			myResaleCollectionDetailPage(id) {
				uni.navigateTo({
					url: '../myResaleCollectionDetail/myResaleCollectionDetail?id=' + id
				});
			},

			holdCollectionDetailPage(id) {
				uni.navigateTo({
					url: '../holdCollectionDetail/holdCollectionDetail?id=' + id
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
				if (!this.isLogin()) {
					this.noDataFlag = true;
					return;
				}
				if (this.currentTab == 0) {
					this.findMyHoldCollectionByPage();
				} else if (this.currentTab == 1) {
					this.findMyHoldMysteryBoxByPage();
				} else if (this.currentTab == 2) {
					this.findMySoldCollectionByPage();
				} else if (this.currentTab == 3) {
					this.findMyResaleCollectionByPage();
				}
			},

			findMyResaleCollectionByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.resaleCollections = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum
				};
				that.loadingState = 'loading';
				this.$u.get('/myArtwork/findMyResaleCollectionByPage', queryParam).then(res => {
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

			findMyHoldMysteryBoxByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.mysteryBoxs = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum
				};
				that.loadingState = 'loading';
				this.$u.get('/myArtwork/findMyHoldMysteryBoxByPage', queryParam).then(res => {
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

			findMyHoldCollectionByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.collections = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum
				};
				that.loadingState = 'loading';
				this.$u.get('/myArtwork/findMyHoldCollectionByPage', queryParam).then(res => {
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

			findMySoldCollectionByPage() {
				var that = this;
				if (that.pageNum == 1) {
					that.soldCollections = [];
				}
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum
				};
				that.loadingState = 'loading';
				this.$u.get('/myArtwork/findMySoldCollectionByPage', queryParam).then(res => {
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
					var oldRecords = that.soldCollections;
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

			gotoPage(path) {
				if (this.isLogin()) {
					uni.navigateTo({
						url: path
					});
				} else {
					this.loginPage();
				}
			},

			getAvatar() {
				return this.personalInfo.avatar ? this.baseUrl + '/storage/fetch/' + this.personalInfo.avatar :
					'/static/img/avatar.png';
			},

			getMyPersonalInfo() {
				if (!this.isLogin()) {
					return;
				}
				var that = this;
				this.$u.get('/member/getMyPersonalInfo').then(res => {
					that.personalInfo = res.data;
				});
			},
		}
	}
</script>

<style>
	page {
		height: 100% !important;
	}

	.msg-unread-num {
		background-color: #d10e0e;
	}

	.setting {
		color: white;
		padding-right: 32rpx;
		position: relative;
		top: -40rpx;
	}

	.msg-read {
		height: 24rpx;
		width: 24rpx;
		border-radius: 24rpx;
		line-height: 24rpx;
		text-align: center;
		font-size: 20rpx;
		transform: scale(0.8);
		position: absolute;
		top: 0rpx;
		left: 46rpx;
	}

	.div-line {
		width: 100%;
		height: 20rpx;
		background: #f0f0f0;
	}

	.resale-price {
		padding-left: 16rpx;
	}

	.custom-tabs {
		padding-bottom: 32rpx;
		display: flex;
		align-items: center;
	}

	.custom-tabs-l {
		flex: 1;
	}

	.compose-activity {
		background: linear-gradient(to right, #2979ff, #909399);
		height: 56rpx;
		line-height: 56rpx;
		border-radius: 44rpx 0rpx 0rpx 44rpx;
		font-size: small;
		color: white;
		padding-left: 24rpx;
		padding-right: 24rpx;
		margin-left: 12rpx;
	}


	.custom-tab-content {
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.collections {}

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
		font-size: 26rpx;
		padding-left: 16rpx;
		padding-top: 16rpx;
		color: #000000;
	}

	.collection-hold-date {
		font-size: 24rpx;
		padding-left: 16rpx;
	}

	.no-data {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 400rpx;
	}

	.grid-navs {
		display: flex;
		justify-content: space-around;
		align-items: center;
	}

	.grid-nav {
		color: white;
		text-align: center;
		padding-top: 20rpx;
		padding-bottom: 20rpx;
		position: relative;
	}

	.page-content {
		padding-bottom: 140rpx;

	}

	.top-content {
		padding-top: 64rpx;
		background: linear-gradient(to right, #2f2f2f, #747171);
	}

	.avatar-nick-name {
		display: flex;
		align-items: center;
		padding-bottom: 16rpx;
		padding-left: 32rpx;
	}

	.nick-name-mobile {
		flex: 1;
	}

	.login {}

	.login-t {
		padding-bottom: 12rpx;
		color: white;
	}

	.login-b {
		color: #888;
		font-size: smaller;
	}

	.member-avatar {
		padding-right: 20rpx;
	}

	.member-nick-name {
		font-size: larger;
		padding-bottom: 12rpx;
		color: white;
	}

	.member-mobile {
		font-size: small;
		color: #888;
	}

	.block-chain-addr {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-bottom: 16rpx;
	}

	.block-chain-addr-l {
		color: white;
		font-size: small;
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
		padding-right: 60rpx;
	}

	.block-chain-addr-r {}
</style>
