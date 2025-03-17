<template>
	<view class="content">
		<view class="wallet">
			<view class="card">
				<u-image src="/static/img/wallet/cardBg.png" width="686" height="260"></u-image>
				<view class="balance">
					<view class="title">
						账户余额（元）
					</view>
					<view class="money OPPOSans-H">
						<view class="sign">
							￥
						</view>
						<view class="num">
							{{moneyFormat(balance)}}
						</view>
					</view>
				</view>
			</view>
			
			<view class="btnGroup">
				<view class="btn border" @click="rechagePage">
					<view class="icon">
						<u-image src="/static/img/wallet/recharge.png" width="40" height="40"></u-image>
					</view>
					<view class="text">
						充值
					</view>
				</view>
				<view class="btn" @click="withdrawPage">
					<view class="icon">
						<u-image src="/static/img/wallet/withdraw.png" width="40" height="40"></u-image>
					</view>
					<view class="text">
						提现
					</view>
				</view>
			</view>
		</view>
		
		<view class="list-content">
			<view class="header">
				<view class="title">
					账户明细
				</view>
				<view class="filter" @click="showFilterFlag=!showFilterFlag">
					<u-image src="/static/img/common/filter.png" width="40" height="40"></u-image>
				</view>
			</view>
			
			<view class="no-data" v-show="noDataFlag">
				<u-empty text="暂无数据"color="#fff" icon-size="240" 
				src="/static/img/common/default/noData.png"></u-empty>
			</view>
			
			<view class="list">
				<view class="item" v-for="(record,index) in records" :key="index" :class="{border:index!=records.length-1}">
					<view class="left">
						<view class="type">
							{{record.changeTypeName}}
						</view>
						<view class="date">
							{{record.changeTime}}
						</view>
					</view>
					<view class="right">
						{{record.balanceChange}}
					</view>
				</view>
			</view>
		</view>
		
		<u-action-sheet bgColor="#181819" gabColor="#0A0A0A" border-radius="32" cancelColor="#FFFFFF"
		:list="changeTypes" @click="filterChangeType" v-model="showFilterFlag"></u-action-sheet>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				changeTypes: [{
					text: '全部', 
					color: '#FCE6B7',
					value: 'all',
					borderBottom:'1rpx solid #FFFFFF14'
				}],
				changeType: 'all',
				balance: '',
				records: [],
				pageNum: 1,
				loadingState: 'loadmore',
				pullDownRefreshFlag: false,
				noDataFlag: false,
				showFilterFlag: false,

			}
		},
		onLoad() {
			this.findTypeDictItem();
			this.getBalance();
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

			withdrawPage() {
				// uni.navigateTo({
				// 	url: '/pages/withdraw/withdraw'
				// });
				uni.navigateTo({
					url: '/subPackages/wallet/withdraw/withdraw'
				});
			},

			rechagePage() {
				uni.showToast({
					title: "敬请期待",
					icon: "none"
				});
			},

			moneyFormat(money, len) {
				len = len || 2
				if (!money && money !== 0)
					return ''
				if (isNaN(+money))
					return ''
				if (money === 0 || money === '0')
					return '0.' + '0'.repeat(len)
				var arr = (money + '').split('.')
				var intStr = arr[0] ? arr[0] : 0
				var floatStr = arr[1] ? arr[1] : 0
				if (floatStr === 0) {
					floatStr = '0'.repeat(len)
				} else {
					floatStr = (+('0.' + floatStr)).toFixed(len).split('.')[1]
				}
				money = (intStr + '.' + floatStr).replace(/(\d{1,3})(?=(?:\d{3})+\.)/g, `$1,`);
				return money
			},

			filterChangeType(index) {
				var that = this;
				that.changeTypes.map((val, idx) => {
					if (index == idx) {
						val.color = '#FCE6B7';
						that.changeType = val.value;
					} else {
						val.color = '#FFFFFF';
					}
				});
				that.refreshData();
			},

			findTypeDictItem() {
				var that = this;
				this.$u.get('/dictconfig/findDictItemInCache', {
					dictTypeCode: 'memberBalanceChangeType'
				}).then(res => {
					var lists = res.data;
					for (var i = 0; i < lists.length; i++) {
						var item = lists[i];
						that.changeTypes.push({
							text: item.dictItemName,
							color: '#FFFFFF',
							value: item.dictItemCode,
							borderBottom:'1rpx solid #FFFFFF14',
							padding:'40rpx 0'
						});
					}
				});
			},

			refreshData() {
				this.pageNum = 1;
				this.records = [];
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
				var queryParam = {
					pageSize: 10,
					pageNum: that.pageNum,
					changeType: that.changeType != 'all' ? that.changeType : '',
				};
				that.loadingState = 'loading';
				this.$u.get('/memberBalanceChangeLog/findByPage', queryParam).then(res => {
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
					var oldRecords = that.records;
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
					that.noDataFlag = that.records.length == 0;
				});
			},

			getBalance() {
				var that = this;
				this.$u.get('/member/getBalance').then(res => {
					that.balance = res.data;
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
@import 'myWallet.scss'
</style>