<template>
	<view class="page-content">
		<status-bar></status-bar>
		<view class="top-content">
			
			<view class="toolBar">
				<view class="setting"  @click="common.toPage('/pages/msg/msg')">
					<u-image src="/static/img/home/msg.png" width="40" height="40"></u-image>
				</view>
				<view class="setting" @click="gotoPage('/pages/setting/setting')">
					<u-image src="/static/img/common/setting.png" width="40" height="40"></u-image>
				</view>
			</view>
			
			<view class="avatar-nick-name">
				<view class="member-avatar">
					<u-image height="112rpx" width="112rpx" :src="getAvatar()" mode="aspectFill" shape="circle"></u-image>
				</view>
				<view class="nick-name-mobile">
					<view v-show="isLogin()">
						<view class="member-nick-name">{{personalInfo.nickName}}</view>
						<view class="block-chain-addr" v-show="isLogin()">
							<view class="left">区块链地址：{{getBlockChainAddr()}}</view>
							<view v-show="personalInfo.blockChainAddr" class="right">
								<u-image height="32rpx" width="32rpx" src="/static/img/common/copy.png"
									@click="copyData(personalInfo.blockChainAddr)"></u-image>
							</view>
						</view>
						<!-- <view class="member-mobile">{{personalInfo.mobile}}</view> -->
					</view>
					<view class="login" v-show="!isLogin()" @click="loginPage">
						<view class="login-t">登录/注册</view>
						<view class="login-b">点击登录 享受更多精彩内容</view>
					</view>
				</view>
			</view>
			
			<view class="menu">
				<view class="chainMenu">
					<view class="item" v-for="(item,index) in chainMenuList" :key="index" @click="gotoPage(item.value)">
						<view class="icon">
							<u-image :src="item.icon" width="80" height="80"></u-image>
						</view>
						<view class="name">
							{{item.name}}
						</view>
					</view>
				</view>
				
				<view class="serviceMenu">
					<view class="title">
						我的服务
					</view>
					<view class="list">
						<view class="item" v-for="(item,index) in serviceMenuList" :key="index" @click="gotoPage(item.value)">
							<view class="icon">
								<u-image :src="item.icon" width="48" height="48"></u-image>
							</view>
							<view class="name">
								{{item.name}}
							</view>
						</view>
					</view>
				</view>
			</view>
			
		</view>
		<view class="content">
			<view class="custom-tabs">
				<view class="custom-tabs-l">
					<!-- <u-tabs :list="tabs" :is-scroll="false" active-color="#FCE6B7" inactive-color="#999999" bg-color="rgba(0,0,0,0)"
					:current="currentTab" @change="switchTab"></u-tabs> -->
					
					<view class="tab" v-for="(item,index) in tabs" :class="{check:currentTab==index}"
					@click="switchTab(index)">
						{{item.name}}
					</view>
				</view>
			</view>
			<view class="list">
				<view class="no-data" v-show="noDataFlag">
					<u-empty text="暂无数据"color="#fff" icon-size="240" 
					src="/static/img/common/default/noData.png"></u-empty>
				</view>
				<view class="custom-tab-content">
					<view v-show="currentTab == 0">
						<u-row gutter="8">
							<u-col span="6" v-for="collection in collections">
								<view class="holdCollection">
									<view class="collection-content" @click="holdCollectionDetailPage(collection.id)">
										<u-image width="322" height="322" mode="aspectFill":src="collection.collectionCover"></u-image>
										<view class="collection-name u-line-1">{{collection.collectionName}}</view>
										<!-- <view class="collection-hold-date">收藏于 {{collection.holdDate}}</view> -->
									</view>
								</view>
							</u-col>
						</u-row>
					</view>
					<view v-show="currentTab == 1">
						<u-row gutter="8">
							<u-col span="6" v-for="collection in mysteryBoxs">
								<view class="holdCollection">
									<view class="collection-content" @click="holdCollectionDetailPage(collection.id)">
										<u-image width="322" height="322" mode="aspectFill":src="collection.collectionCover"></u-image>
										<view class="collection-name u-line-1">{{collection.collectionName}}</view>
										<!-- <view class="collection-hold-date">收藏于 {{collection.holdDate}}</view> -->
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
										<u-image width="322" height="322" mode="aspectFill":src="collection.collectionCover"></u-image>
										<view class="collection-text">
											<view class="collection-name u-line-1">{{collection.collectionName}}</view>
											<view class="collection-hold-date">{{collection.soldDate}}</view>
											<view class="resale-price">￥{{collection.soldPrice}}</view>
										</view>
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
										<u-image width="322" height="322" mode="aspectFill":src="collection.collectionCover"></u-image>
										<view class="collection-text">
											<view class="collection-name u-line-1">{{collection.collectionName}}</view>
											<view class="collection-hold-date">{{collection.resaleDate}}</view>
											<view class="resale-price OPPOSans-M">￥{{collection.resalePrice}}</view>
										</view>
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
					name: '数字藏品'
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
				chainMenuList:[
					{
						name:"我的订单",
						icon:"/static/img/mine/order.png",
						value:"/subPackages/mine/order/order"
						// value:"/pages/payOrder/payOrder"
					},
					{
						name:"转赠记录",
						icon:"/static/img/mine/share.png",
						// value:"/pages/myGiveRecord/myGiveRecord"
						value:"/subPackages/mine/giveRecord/giveRecord"
					},
					{
						name:"合成藏品",
						icon:"/static/img/mine/box.png",
						value:"/pages/composeActivity/composeActivity"
					},
					{
						name:"空投兑换",
						icon:"/static/img/mine/exchange.png",
						value:"/pages/exchangeCode/exchangeCode"
					},
				],
				serviceMenuList:[
					{
						name:"我的钱包",
						icon:"/static/img/mine/wallet.png",
						// value:"/pages/balanceChangeLog/balanceChangeLog"
						value:"/subPackages/wallet/myWallet/myWallet"
					},
					{
						name:"区块链查询",
						icon:"/static/img/mine/chain.png",
						value:"contactCustomerService"
					},
					{
						name:"联系客服",
						icon:"/static/img/mine/staff.png",
						value:"contactCustomerService"
					},
					{
						name:"邀请好友",
						icon:"/static/img/mine/invite.png",
						value:"/pages/invite/invite"
					},
				],
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
				uni.navigateTo({
					url: "/pages/login/login"
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
						url: '/pages/customerService/customerService?customerServiceUrl=' + this.customerServiceUrl
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
				// uni.navigateTo({
				// 	url: '../myResaleCollectionDetail/myResaleCollectionDetail?id=' + id
				// });
				uni.navigateTo({
					url: `/subPackages/collection/collectionDetail/collectionDetail?id=${id}&type=resale`
				});
			},

			holdCollectionDetailPage(id) {
				// uni.navigateTo({
				// 	url: '../holdCollectionDetail/holdCollectionDetail?id=' + id
				// });
				uni.navigateTo({
					url: `/subPackages/collection/collectionDetail/collectionDetail?id=${id}&type=hold`
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
				if(path==='contactCustomerService'){
					this.contactCustomerService()
				}
				else if (this.isLogin()) {
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
					that.$store.dispatch('updateUserInfo',that.personalInfo)
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
@import "my.scss"
</style>
