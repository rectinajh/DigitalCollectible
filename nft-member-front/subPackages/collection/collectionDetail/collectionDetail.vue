<template>
	<view class="page-content">
		<custom-navbar :spaceFlag="false"></custom-navbar>
		<view class="topBg" :style="{background:coverColor}"></view>
		<view class="top">
			<view class="collection-cover">
				<view class="imgBorder">
					<u-image src="/static/img/detail/border.png" width="400rpx" height="400rpx"></u-image>
					<view class="imgContent">
						<u-image class="cover" width="356" height="356" mode="aspectFill"
							:src="collectionDetail.cover||collectionDetail.collectionCover"
							@load="coverOnload"></u-image>
					</view>
				</view>
			</view>
			<view class="img">
				<u-image src="/static/img/detail/foundation.png" width="750rpx" mode="widthFix"></u-image>
			</view>
		</view>

		<view class="information">
			
			<view class="infoHead">
				<view class="infoTopBg"></view>
				<view class="topInfo">
					<view class="arrow">
						<u-image src="/static/img/detail/arrow.png" width="30rpx" height="26rpx"></u-image>
					</view>
					<view class="name">
						{{collectionDetail.name || collectionDetail.collectionName}}
					</view>
					<view class="stock">
						<view v-if="type==='home'">
							<view class="left">
								限量
							</view>
							<view class="right">
								{{collectionDetail.quantity}}份
							</view>
						</view>
						<view v-else-if="collectionDetail.collectionSerialNumber" class="codeNum">
							#{{collectionDetail.collectionSerialNumber}}/{{collectionDetail.quantity}}
						</view>
					</view>
					<view class="codeNum" v-else-if="">
						
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
			</view>
			

			<view class="giftInfo" v-if="collectionDetail.subCommoditys && collectionDetail.subCommoditys.length > 0">
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

			<view class="person-info">
				<view class="tabs" v-if="type!=='home'">
					<view class="item" :class="{checked:tabChecked==='holder'}" @click="checkPerson('holder')">
						持有者
					</view>
					<view class="item" :class="{checked:tabChecked==='creator'}" @click="checkPerson('creator')">
						创作者
					</view>
				</view>
				<view v-show="tabChecked==='holder'" class="holder-info">
					<view class="avatar">
						<u-image class="holder-avatar" width="80rpx" height="80rpx" shape="circle"
							:src="collectionDetail.holderAvatar||'/static/img/avatar.png'"></u-image>
					</view>
					<view class="info">
						<view class="name">
							{{collectionDetail.holderNickName}}
						</view>
						<view class="hash">
							区块链地址：{{collectionDetail.holderBlockChainAddr}}
						</view>
					</view>
				</view>
				<view v-show="tabChecked==='creator'" class="creator-info">
					<view class="left">
						<view class="creator-avatar">
							<u-image width="80rpx" height="80rpx" shape="circle" :src="collectionDetail.creatorAvatar">
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
			</view>

			<view v-if="type!=='home'" class="tech-info">
				<view class="title">技术信息</view>
				<view class="content">
					<view class="item">
						<text class="item-label">唯一标识</text>
						<text
							class="item-value">{{collectionDetail.uniqueId ? collectionDetail.uniqueId : '上链确认中'}}</text>
					</view>
					<view class="item">
						<text class="item-label">作品HASH</text>
						<text class="item-value">{{collectionDetail.collectionHash}}</text>
					</view>
					<view class="item">
						<text class="item-label">交易HASH</text>
						<text
							class="item-value">{{collectionDetail.transactionHash ? collectionDetail.transactionHash : '上链确认中'}}</text>
					</view>
				</view>
			</view>


			<view class="story">
				<view class="title">
					<view class="left">
						<u-image src="/static/img/detail/storyLeftArrow.png" width="14" mode="widthFix"></u-image>
					</view>
					<view class="text">
						作品故事
					</view>
					<view class="right">
						<u-image src="/static/img/detail/storyRightArrow.png" width="14" mode="widthFix"></u-image>
					</view>
				</view>
				<view class="storyContent">
					<view class="imgItem" v-for="(item,index) in collectionDetail.storyPicLinks" :key="index">
						<u-image width="100%" mode="widthFix" :src="item"></u-image>
					</view>
				</view>
			</view>

			<view class="about">
				<view class="title">
					购买须知
				</view>
				<view class="content">
					<view class="blindBox" v-if="collectionDetail.commodityType == '2'">
						<view>盲盒是平台基于精选IP所推出的数字藏品新玩法，每个盲盒内都有一个或多个数字藏品。</view>
						<view>所有藏品都是通过区块链技术发行，盲盒的抽取也是完全随机，在盲盒没有打开之前，没有人可以知道盲盒里的数字藏品是什么。</view>
						<view>每一期盲盒都会设置不同级别的数字藏品，让用户充分感受开盲盒的乐趣。</view>
					</view>
					<view class="nft">
						<view>数字藏品为虚拟数字商品，而非实物，仅限实名认证为年满14周岁的中国大陆用户购买。</view>
						<view>数字藏品的版权由发行方或原创者拥有，除另外取得版权拥有者书面同意外，用户不得将数字藏品用于任何商业用途。</view>
						<view>本商品一经售出，不支持退换。本商品源文件不支持本地下载。</view>
						<view>请勿对数字藏品进行炒作、场外交易、欺诈、或以任何其他非法方式进行使用。</view>
					</view>
				</view>
			</view>

			<view v-if="type!=='home'" class="record" @click="issuedCollectionActionLogPage">
				<view class="text">
					查看流转记录
				</view>
				<view class="rightArrow">
					<u-image src="/static/img/detail/rightArrow.png" width="10rpx" mode="widthFix"></u-image>
				</view>
			</view>

			<!-- <view class="space"
				:style="{height:collectionDetail.preSaleFlag&&collectionDetail.surplusSecond > 0?'300rpx':'150rpx'}">
			</view> -->
			<view class="space" :style="{height:spaceHeight}">
			</view>
		</view>


		<view class="fixed-bottom">
			<view class="presaleBar" v-if="collectionDetail.preSaleFlag&&collectionDetail.surplusSecond > 0">
				<view class="top">
					<view class="imgText">
						<u-image src="/static/img/detail/presaleText.png" width="214rpx" mode="widthFix"></u-image>
					</view>
					<view class="btn" @click="latestCollectionCreateOrder">
						<p v-if="!hasPreSale">暂无优先购买特权</p>
						<p v-else>拥有优先购买特权</p>
						<view class="arrow">
							<u-image src="/static/img/detail/presaleArrow.png" width="10rpx" mode="widthFix"></u-image>
						</view>
					</view>
				</view>
				<view class="bottom">
					<view class="fri step"
						:class="[collectionDetail.surplusSecond > 0 && !hasPreSale ? 'underway' : 'normal']">
						<view class="number">1</view>
						<view class="text">预告</view>
					</view>
					<view class="sec step" :class="[hasPreSale ? 'underway' : 'normal']">
						<view class="number">2</view>
						<view class="text">优先购</view>
					</view>
					<view class="thr step" :class="[collectionDetail.surplusSecond <= 0 ? 'underway' : 'normal']">
						<view class="number">3</view>
						<view class="text">公售</view>
					</view>
				</view>
			</view>

			<view class="handleBar">

				<view v-if="type!='hold'" class="normalBar">
					<view class="price OPPOSans-H">
						<span class="sign">￥</span>
						{{common.moneyFormat(collectionDetail.price||collectionDetail.resalePrice)}}
					</view>

					<view class="btn" :style="{background:btnBgColor,color:btnTextColor,border:btnBorder}"
						@click="commonBtnClick()">
						<view class="top" :class="{single:!btnBottomText}">
							{{btnTopText}}
						</view>
						<view v-if="btnBottomText" class="stock">
							{{btnBottomText}}
						</view>
					</view>
				</view>
				<view class="holdBar" v-else>
					<view class="download btn" @click="previewImage">
						<view class="icon">
							<u-image src="/static/img/detail/download.png" width="48rpx" height="48rpx"></u-image>
						</view>
						<view class="text">
							下载
						</view>
					</view>
					<view class="share btn" @click="collectionGivePage">
						<view class="icon">
							<u-image src="/static/img/detail/share.png" width="48rpx" height="48rpx"></u-image>
						</view>
						<view class="text">
							转赠
						</view>
					</view>
					<view class="sale btn" @click="collectionResalePage">
						<view class="icon">
							<u-image src="/static/img/detail/sale.png" width="48rpx" height="48rpx"></u-image>
						</view>
						<view class="text">
							寄售
						</view>
					</view>
					
					<view class="open" @click="openMysteryBoxFlag=true"
					v-if="collectionDetail.subCommoditys && collectionDetail.subCommoditys.length > 0">
						开盲盒
					</view>
				</view>

			</view>

		</view>

		<u-popup v-model="showPayModalFlag" mode="bottom" length="80%" border-radius="32" close-icon-color="#999999"
			:closeable="true" close-icon-size="20rpx">
			<view class="payPopup">
				<view class="title">
					支付订单
				</view>
				<view class="info">
					<view class="description">
						实付金额
					</view>
					<view class="price OPPOSans-M">
						<view class="sign">
							￥
						</view>
						<view class="num">
							{{common.moneyFormat(collectionDetail.price)}}
						</view>
					</view>
				</view>
				<view class="payMethods">
					<view class="tips">
						选择支付方式
					</view>
					<view class="list">
						<view class="item" v-for="(item,index) in payList" :key="index"
							:class="{borderBottom:index!=payList.length-1}" @click="changePaytype(index)">
							<view class="left">
								<view class="icon">
									<u-image :src="item.icon" width="52rpx" height="52rpx"></u-image>
								</view>
								<view class="name">
									{{item.name}}
								</view>
							</view>
							<view class="right">
								<view class="uncheck" v-show="payType!=item.value">
									<u-image src="/static/img/login/uncheckbox.png" width="40rpx" height="40rpx" />
								</view>
								<view class="checked" v-show="payType==item.value">
									<u-image src="/static/img/login/checkbox.png" width="40rpx" height="40rpx" />
								</view>
							</view>
						</view>
					</view>
				</view>
				<view class="btn" @click="confirmPay">
					{{payName}}支付￥{{common.moneyFormat(collectionDetail.price)}}
				</view>
			</view>
		</u-popup>
		
		<lj-modal :show="showCancelModal" title="提示" content="是否要取消发布" leftBtn="暂时不要" rightBtn="取消发布"
		@close="showCancelModal=false" @leftClick="showCancelModal=false" @rightClick="cancelResale"></lj-modal>
		
		<lj-modal :show="openMysteryBoxFlag" title="提示" content="是否要开盲盒" leftBtn="暂时不要" rightBtn="打开盲盒"
		@close="openMysteryBoxFlag=false" @leftClick="openMysteryBoxFlag=false" @rightClick="openMysteryBox"></lj-modal>
		
		<lj-modal :show="mysteryBoxResultFlag" title="恭喜获得" :content="mysteryBoxResult.name" rightBtn="我知道了"
		@close="mysteryBoxResultFlag=false" @rightClick="toMine()">
			<template slot="img">
				<view style="padding: 20rpx 170rpx;">
					<u-image width="200rpx" height="200rpx" mode="aspectFill" :src="mysteryBoxResult.cover"></u-image>
				</view>
			</template>
		</lj-modal>
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
				coverColor: 'radial-gradient(circle at center 10% ,rgba(153, 153, 153, 1.0) 0%,#0C0C0D 80%)',
				iconList: [{
						value: 'medal',
						url: '/static/img/detail/medal.png',
						text: '商家权益',
					},
					{
						value: 'AR',
						url: '/static/img/detail/AR.png',
						text: 'AR体验',
					},
					{
						value: 'box',
						url: '/static/img/detail/box.png',
						text: '数字展馆',
					},
					{
						value: 'avatar',
						url: '/static/img/detail/avatar.png',
						text: '生成头像',
					},
					{
						value: 'wait',
						url: '/static/img/detail/avatar.png',
						text: '敬请期待',
					},
					{
						value: 'wait',
						url: '/static/img/detail/avatar.png',
						text: '敬请期待',
					},
				],
				btnTopText: '',
				btnBottomText: '',
				btnBgColor: '',
				btnTextColor: '',
				btnBorder: null,
				type: 'home', //默认为首页进入
				buystatus: '',
				tabChecked: 'holder',
				payList: [{
					name: '余额',
					icon: '/static/img/pay/card.png',
					value: 'account'
				}],
				payType: 'account',
				payName: '余额',
				spaceHeight: '150rpx',
				showCancelModal:false,
				openMysteryBoxFlag:false,
				mysteryBoxResultFlag: false,
				mysteryBoxResult: '',
			}
		},
		onLoad(option) {
			this.collectionId = option.id;
			let type = 'home'
			if (option.type) {
				type = option.type
				this.type = type
			}
			this.getCollectionDetail(type);
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
				});
			},

			creatorPage() {
				uni.navigateTo({
					url: `/subPackages/collection/creator/creator?id=${this.collectionDetail.creatorId}`
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
									url: "/pages/my/my?tab=" + (that.collectionDetail
										.commodityType == '1' ? '0' : '1')
								});
							}, 2000);
						}
					});
				});
			},
			getCollectionDetail(type) {
				var that = this;
				let url = ''
				switch (type) {
					case 'market':
						url = '/collection/findResaleCollectionDetail'
						break;
					case 'resale':
						url = '/myArtwork/findMyResaleCollectionDetail'
						break;
					case 'hold':
						url = '/myArtwork/findMyHoldCollectionDetail'
						break;
					default:
						url = '/collection/findLatestCollectionDetailById';
						this.tabChecked = 'creator'
						break;
				}
				this.$u.get(url, {
					id: that.collectionId
				}).then(res => {
					that.collectionDetail = res.data;
					// uni.setNavigationBarTitle({
					// 	title: that.collectionDetail.name
					// });

					if (that.collectionDetail.preSaleFlag && that.isLogin()) {
						that.checkHasPreSale();
					}
					this.initBtn(type)
					// 获取fixed-bottom的高度并赋值给spaceHeight
					if(this.collectionDetail.preSaleFlag&&this.collectionDetail.surplusSecond > 0){
						this.spaceHeight = '340rpx'
					}else{
						const query = uni.createSelectorQuery().in(this);
						query.select('.fixed-bottom').boundingClientRect(data => {
							this.spaceHeight = data.height * 2 + 60 + 'rpx';
						}).exec();
					}
				});
			},
			initBtn(type) {
				switch (type) {
					case 'resale':
						this.btnTopText = '取消发布'
						this.btnBgColor = 'rgba(0,0,0,0)'
						this.btnTextColor = '#CCCCCC'
						this.btnBottomText = null
						this.btnBorder = "1rpx solid #FFFFFF66"
						break;
					case 'market':
						this.btnTopText = '购买'
						this.btnBgColor = '#FCE6B7'
						this.btnTextColor = '#644205'
						this.btnBottomText = null
						break;
					default:
						if (this.collectionDetail.surplusSecond > 0) {
							this.btnTopText = '即将开售'
							this.btnBottomText = `${this.collectionDetail.saleTime}开启`
							this.btnBgColor = 'linear-gradient(90deg, #FFFFA9 0%, #6ADFFF 100%)'
							this.btnTextColor = '#2D3A2F'
							this.buystatus = 'presale'
						} else if (this.collectionDetail.stock === 0) {
							this.btnTopText = '已售罄'
							this.btnBottomText = '剩余0份'
							this.btnBgColor = '#333333'
							this.btnTextColor = '#999999'
							this.buystatus = 'sellout'
						} else {
							this.btnTopText = '购买'
							this.btnBottomText = `剩余${this.collectionDetail.stock}份`
							this.btnBgColor = '#FCE6B7'
							this.btnTextColor = '#644205'
							this.buystatus = 'selling'
						}

						break;
				}
			},
			commonBtnClick() {
				if (this.type != 'home') {
					switch (this.type) {
						case 'resale':
							this.showCancelModal=true
							break;
						case 'market':
							this.resaleCollectionCreateOrder()
							break;
						default:
							break;
					}
				} else {
					switch (this.buystatus) {
						case 'selling':
							this.latestCollectionCreateOrder()
							break;
						case 'presale':
							if (this.hasPreSale) {
								this.latestCollectionCreateOrder()
							}
							break;
						default:
							break;
					}
				}
			},
			checkPerson(e) {
				if (e !== this.tabChecked) {
					this.tabChecked = e
				}
			},
			issuedCollectionActionLogPage() {
				uni.navigateTo({
					url: '/pages/issuedCollectionActionLog/issuedCollectionActionLog?id=' + this.collectionDetail
						.issuedCollectionId
				});
			},
			changePaytype(index) {
				this.payType = this.payList[index].value
				this.payName = this.payList[index].name
			},
			previewImage() {
				uni.previewImage({
					urls: [this.collectionDetail.collectionCover],
				});
			},
			collectionGivePage() {
				// uni.navigateTo({
				// 	url: '/pages/collectionGive/collectionGive?id=' + this.collectionId
				// });
				uni.navigateTo({
					url: `/subPackages/collection/collectionExport/collectionExport?id=${this.collectionId}&type=give`
				});
			},
			collectionResalePage() {
				// uni.navigateTo({
				// 	url: '/pages/collectionResale/collectionResale?id=' + this.collectionId
				// });
				uni.navigateTo({
					url: `/subPackages/collection/collectionExport/collectionExport?id=${this.collectionId}&type=resale`
				});
			},
			cancelResale() {
				var that = this;
				this.$u.post('/transaction/cancelResale', {
					resaleCollectionId: that.collectionId,
				}).then(res => {
					uni.showToast({
						icon: 'success',
						title: '取消成功!',
						duration: 2000,
						mask: true,
						complete: function() {
							setTimeout(() => {
								uni.reLaunch({
									url: "/pages/my/my"
								});
							}, 2000);
						}
					});
				});
			},
			openMysteryBox() {
				var that = this;
				this.$u.post('/transaction/openMysteryBox', {
					holdCollectionId: that.collectionId,
				}).then(res => {
					that.mysteryBoxResult = res.data;
					that.mysteryBoxResultFlag = true;
				});
			},
			toMine(){
				uni.reLaunch({
					url:'/pages/my/my'
				})
			},
			resaleCollectionCreateOrder() {
				var that = this;
				this.$u.post('/transaction/resaleCollectionCreateOrder', {
					resaleCollectionId: that.collectionId,
				}).then(res => {
					that.orderId = res.data;
					that.showPayModalFlag = true;
				});
			},
			

			coverOnload(e) {
				// return
				try {
					let colorThief = new ColorThief();
					let img = document.querySelector('.cover > img')
					let rgbColor = colorThief.getColor(img)
					console.log(rgbColor);
					// 将数组rgbColor转换为字符串
					let colorString = `rgba(${rgbColor[0]}, ${rgbColor[1]}, ${rgbColor[2]},.9)`;
					// 将字符串colorString赋值给data中的变量
					this.coverColor = 'radial-gradient(circle,' + colorString + ',rgba(0,0,0,0))';
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