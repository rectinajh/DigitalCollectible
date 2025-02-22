<template>
	<view>
		<u-popup v-model="show" mode="bottom" length="80%" border-radius="32"
		:closeable="true" close-icon-color="#999999" close-icon-size="20rpx" @close="closePopup">
			<view class="agreementPopup">
				
				<view class="title">
					{{title}}
				</view>
				
				<scroll-view scroll-y="true" style="height: 1000rpx;">
					<view class="content">
						{{content}}
					</view>
				</scroll-view>
				<view class="space"></view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	export default {
		name:"agreementPopup",
		props: {
			showAgreement: {
				type: Boolean,
				default: false
			},
			type:{
				type: String,
				default: 'user'
			}
		},
		data() {
			return {
				show:false,
				title:'用户协议',
				content:'用户协议内容'
			};
		},
		watch:{
			showAgreement(val){
				this.show=val;
			},
			type(val){
				if(val=='user'){
					this.title='用户协议';
					this.content='用户协议内容';
				}else{
					this.title='隐私政策';
					this.content='一、定义1.1 灵境：是指我们开发运营并向您提供数字资产购买、收藏、创作与分享等相关服务的平台，包括灵境App、灵境支付宝小程序、灵境抽赏支付宝小程序及后续我们不时开发的其他App、小程序或网页等。1.2 数字资产：是指可收藏、交换、使用且以数字形式记录、呈现的商品或权益。数字资产包括数字藏品、数字权益、数字凭证、数字空间等多种类型。在本协议中数字资产特指数字藏品和数字卡牌。数字藏品：是指使用蚂蚁链区块链技术进行唯一标识的经数字化的特定虚拟作品、艺术品和商品，包括但不限于数字图片、音乐、视频、3D模型等各种形式的虚拟数字藏品。数字卡牌：是指以数字化形式呈现且具有标识性的虚拟卡牌。灵境的数字卡牌分为上链存证的数字卡牌（以下简称“特殊卡牌”），及未上链存证的数字卡牌（以下简称“一般卡牌”），具体以数字卡牌详情页展示为准。1.3 发行方：是指在灵境发行数字藏品和数字卡牌并授权发售方销售数字藏品和数字卡牌的实体或个人。如果您的数字藏品或数字卡牌是通过与灵境合作的第三方（例如天猫）兑换、购买或获得，并在灵境进行展示、应用、使用或获得服务的，则发行方指在该第三方发行数字藏品或数字卡牌的实体或个人。1.4 发售方：是指经发行方授权，通过灵境向您销售数字藏品和数字卡牌并进行收款和开票的实体。在本协议中特指灵境。1.5 创作者：是指数字藏品或数字卡牌的原始作品的相关著作权利人，包括但不限于原始作品的自然人作者，以及其他依照法律、合同或授权享有原始作品相关著作权利的自然人、法人或者非法人组织等。具体以数字藏品或数字卡牌详情页向您展示的信息为准。1.6 服务：是指在本协议有效期内我们及我们的关联方和合作方基于互联网和区块链，通过灵境（包括灵境在存续期间发展出的各种新业务形态）向您提供的各项服务，包括但不限于：(i)数字藏品和数字卡牌的展示、浏览、购买、抽取、转让、争议处理、订单管理、信息查看等相关的信息技术服务；(ii)数字藏品和数字卡牌在灵境以及应用场景内的相关应用服务，包括但不限于在社区提供与数字藏品或数字卡牌相关的社区交互和信息交流服务；以及(iii)将数字藏品和特殊卡牌在蚂蚁链进行存证、管理、簿记及交收等存管服务。具体服务内容以灵境应用场景页面向您实际展示的信息为准。1.7 规则：是指已在或后续将在灵境发布的各类规则、规范、公告、说明、解读、声明、指引或其他类似内容，具体以实际展示为准。1.8 应用场景：是指提供数字藏品或数字卡牌信息展示、交互和其他应用活动的各类App、小程序和网页等应用软件。';
				}
			}
		},
		mounted() {
			this.show=this.showAgreement;
		},
		methods:{
			closePopup(){
				this.$emit('close')
			},
		}
	}
</script>

<style lang="scss" scoped>
	.agreementPopup{
		background-color: #181819;
		height: 100%;
		.title{
			text-align: center;
			padding-top: 48rpx;
			padding-bottom: 48rpx;
			box-sizing: border-box;
		}
		.content{
			padding: 0rpx 48rpx;
		}
		.space{
			width: 100%;
			height: 34rpx;
		}
	}
</style>
