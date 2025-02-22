<template>
	<view class="page-content">
		<custom-navbar :spaceFlag="false"></custom-navbar>
		<view class="topBg" :style="{background:coverColor}" style="background-position: top;">
			<view class="collection-cover">
				<view class="imgBorder">
					<u-image src="/static/img/detail/border.png" width="400rpx" height="400rpx"></u-image>
					<view class="imgContent">
						<u-image class="cover" style="width: 100%;" mode="widthFix" :src="collectionDetail.cover" @load="coverOnload"></u-image>
					</view>
				</view>
				<!-- <image class="cover" style="width: 100%;" mode="widthFix" src="/static/img/common/original.png" @load="coverOnload"></image> -->
			</view>
			<view class="img">
				<u-image src="/static/img/detail/foundation.png" width="750rpx" mode="widthFix"></u-image>
			</view>
		</view>
		
		<!-- <view class="chain-tip">
			<image style="width: 36rpx; height: 30rpx;" src="/static/img/chain_icon.png">
			</image>
			官方授权 • 限量发行
		</view> -->
		<!-- <view class="collection-name">
			<view class="collection-name-inner">{{collectionDetail.name}}</view>
			<view class="limit-block">
				<view class="limit">限量</view>
				<view class="quantity">{{collectionDetail.quantity}}份</view>
			</view>
		</view> -->
		<view class="information">
			<view class="topInfo">
				<view class="arrow">
					<u-image src="/static/img/detail/arrow.png" width="30rpx" height="26rpx"></u-image>
				</view>
				<view class="name">
					{{collectionDetail.name}}
				</view>
				<view class="stock">
					<view class="left">
						限量
					</view>
					<view class="right">
						{{collectionDetail.quantity}}份
					</view>
				</view>
				<view class="iconGroup">
					<view class="title">
						<view class="leftLine"></view>
						<p>购买即可体验内容</p>
						<view class="rightLine"></view>
					</view>
					<view class="scrollBox">
						<scroll-view :scroll-x="true" class="scrollRow">
							<view class="iconItem" v-for="(item,index) in iconList" :key="index">
								<u-image :src="item.url" mode="widthFix" width="88rpx"></u-image>
								<view class="text">
									{{item.text}}
								</view>
							</view>
						</scroll-view>
					</view>
				</view>
			</view>
			
			<view class="giftInfo" v-show="collectionDetail.subCommoditys && collectionDetail.subCommoditys.length > 0">
				<view class="title">
					您可能抽到以下作品中的1个作品
				</view>
				<view class="giftList">
					
					<view class="giftItem" v-for="(item,index) in collectionDetail.subCommoditys" :key="index">
						<view class="giftImg">
							<u-image :src="item.cover" width="104rpx" height="104rpx" mode="aspectFill"></u-image>
						</view>
						<view class="giftText">
							<view class="giftName">
								{{item.name}}
							</view>
							<view class="giftProbability">
								概率:{{item.probability}}%
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<view class="creator-info">
				<view class="left">
					<view class="creator-avatar">
						<u-image width="80rpx" height="80rpx" shape="circle"
							:src="collectionDetail.creatorAvatar">
						</u-image>
					</view>
					<view class="creator-name">
						{{collectionDetail.creatorName}}
					</view>
				</view>
				
				<view class="more" @click="creatorPage">
					<view>更多作品</view>
					<view class="arrow">
						<u-image src="/static/img/detail/rightArrow.png" width="10rpx" mode="widthFix"></u-image>
					</view>
				</view>
			</view>
			
			<view class="story">
				
			</view>
			
		</view>
		
		
		<!-- <view v-show="collectionDetail.subCommoditys && collectionDetail.subCommoditys.length > 0">
			<view class="div-line"></view>
			<view class="mystery-box">
				<view class="mystery-box-title">
					<view class="mystery-box-title-l">作品描述</view>
					<view class="mystery-box-title-r">您可能抽到以下作品中的<text>1</text>个</view>
				</view>
				<view class="mystery-box-commoditys">
					<view class="mystery-box-commodity" v-for="subCommodity in collectionDetail.subCommoditys">
						<view class="mystery-box-commodity-l">
							<u-image width="120rpx" height="120rpx" border-radius="10" :src="subCommodity.cover">
							</u-image>
						</view>
						<view class="mystery-box-commodity-r">
							<view class="mystery-box-commodity-r1 u-line-1">
								{{subCommodity.name}}
							</view>
							<view class="mystery-box-commodity-r2">概率：{{subCommodity.probability}}%</view>
						</view>
					</view>

				</view>
			</view>
		</view> -->
		<view class="div-line"></view>
		<view class="creator-story">
			<view class="story-title">作品故事</view>
			<view class="story-content">
				<image style="width: 100%;" mode="widthFix" v-for="storyPicLink in collectionDetail.storyPicLinks"
					:src="storyPicLink">
				</image>
			</view>
		</view>
		<view class="buy-instructions" v-show="collectionDetail.commodityType == '2'">
			<view class="buy-instructions-title">关于盲盒</view>
			<view class="buy-instructions-content">
				<view>盲盒是平台基于精选IP所推出的数字藏品新玩法，每个盲盒内都有一个或多个数字藏品。</view>
				<view>所有藏品都是通过区块链技术发行，盲盒的抽取也是完全随机，在盲盒没有打开之前，没有人可以知道盲盒里的数字藏品是什么。</view>
				<view>每一期盲盒都会设置不同级别的数字藏品，让用户充分感受开盲盒的乐趣。</view>
			</view>
		</view>
		<view class="buy-instructions">
			<view class="buy-instructions-title">关于数字藏品</view>
			<view class="buy-instructions-content">
				数字藏品为虚拟数字商品，而非实物，仅限实名认证为年满14周岁的中国大陆用户购买。数字藏品的版权由发行方或原创者拥有，除另外取得版权拥有者书面同意外，用户不得将数字藏品用于任何商业用途。本商品一经售出，不支持退换。本商品源文件不支持本地下载。请勿对数字藏品进行炒作、场外交易、欺诈、或以任何其他非法方式进行使用。
			</view>
		</view>
		<view class="fixed-bottom">
			<view class="fixed-bottom-content">
				<view class="price-info">
					<view>￥</view>
					<view>{{moneyFormat(collectionDetail.price)}}</view>
				</view>
				<view>
					<view class="sell-out" v-show="collectionDetail.stock == 0">已售罄</view>
					<view v-show="collectionDetail.stock > 0">
						<view v-show="!hasPreSale">
							<view class="buy-now" v-show="collectionDetail.surplusSecond == 0"
								@click="latestCollectionCreateOrder">立即购买
							</view>
							<view class="future-sale" v-show="collectionDetail.surplusSecond > 86400">
								<view class="future-sale1">敬请期待</view>
								<view class="future-sale2">
									{{collectionDetail.saleTime}}
								</view>
							</view>
							<view class="for-sale"
								v-show="collectionDetail.surplusSecond > 0 && collectionDetail.surplusSecond <= 86400">
								<view class="for-sale1">即将开售</view>
								<view class="for-sale2">
									<u-count-down :show-days="false" color="white" separator-size="26"
										separator-color="white" bg-color="#4c4c4c" font-size="26"
										:timestamp="collectionDetail.surplusSecond">
									</u-count-down>
								</view>
							</view>
						</view>
						<view v-show="hasPreSale">
							<view class="buy-now" @click="latestCollectionCreateOrder">优先购买
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import ColorThief from '@/node_modules/colorthief/dist/color-thief.mjs'
	export default {
		data() {
			return {
				collectionId: '',
				collectionDetail: '',
				showPayModalFlag: false,
				orderId: '',
				hasPreSale: false,
				coverColor:'radial-gradient(circle,rgba(100,0,0,1),rgba(0,0,0,0))',
				iconList:[
					{
						value: 'medal',
						url:'/static/img/detail/medal.png',
						text: '商家权益',
					},
					{
						value: 'AR',
						url:'/static/img/detail/AR.png',
						text: 'AR体验',
					},
					{
						value: 'box',
						url:'/static/img/detail/box.png',
						text: '数字展馆',
					},
					{
						value: 'avatar',
						url:'/static/img/detail/avatar.png',
						text: '生成头像',
					},
					{
						value: 'wait',
						url:'/static/img/detail/avatar.png',
						text: '敬请期待',
					},
					{
						value: 'wait',
						url:'/static/img/detail/avatar.png',
						text: '敬请期待',
					},
				]
			}
		},
		onLoad(option) {
			this.collectionId = option.id;
			this.getCollectionDetail();
		},
		methods: {

			isLogin() {
				var tokenName = uni.getStorageSync('tokenName');
				return tokenName != null && tokenName != '';
			},

			checkHasPreSale() {
				var that = this;
				this.$u.get('/transaction/checkHasPreSale', {
					collectionId: that.collectionId
				}).then(res => {
					that.hasPreSale = res.data;
					console.log(that.hasPreSale);
				});
			},

			creatorPage() {
				uni.navigateTo({
					url: '../creator/creator?id=' + this.collectionDetail.creatorId
				});
			},

			latestCollectionCreateOrder() {
				var that = this;
				this.$u.post('/transaction/latestCollectionCreateOrder', {
					collectionId: that.collectionId,
				}).then(res => {
					that.orderId = res.data;
					that.showPayModalFlag = true;
				});
			},

			confirmPay() {
				var that = this;
				this.$u.post('/transaction/confirmPay', {
					orderId: that.orderId,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '购买成功!',
						duration: 2000,
						mask: true,
						complete: function() {
							setTimeout(() => {
								uni.reLaunch({
									url: "../my/my?tab=" + (that.collectionDetail
										.commodityType == '1' ? '0' : '1')
								});
							}, 2000);
						}
					});
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

			getCollectionDetail() {
				var that = this;
				this.$u.get('/collection/findLatestCollectionDetailById', {
					id: that.collectionId
				}).then(res => {
					that.collectionDetail = res.data;
					// uni.setNavigationBarTitle({
					// 	title: that.collectionDetail.name
					// });
					
					if (that.collectionDetail.preSaleFlag && that.isLogin()) {
						that.checkHasPreSale();
					}
				});
			},
			coverOnload(e){
				// return
				try {
					let colorThief = new ColorThief();
					let img = document.querySelector('.cover > img')
					let rgbColor = colorThief.getColor(img)
					console.log(rgbColor);
					// 将数组rgbColor转换为字符串
					let colorString = `rgba(${rgbColor[0]}, ${rgbColor[1]}, ${rgbColor[2]},.9)`;
					// 将字符串colorString赋值给data中的变量
					this.coverColor = 'radial-gradient(circle,' + colorString +',rgba(0,0,0,0))';
					// this.coverColor = colorString
					
					console.log(this.coverColor);
				} catch (error) {
					//TODO handle the exception
				}
			},
		}
	}
</script>

<style lang="scss" scoped>
	@import '@/subPackages/collection/collectionDetail/collectionDetail.scss';
</style>
