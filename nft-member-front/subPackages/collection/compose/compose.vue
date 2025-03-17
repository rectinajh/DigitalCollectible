<template>
	<view class="content">
		<view class="header">
			<view class="mainCollection">
				<view class="cover">
					<view class="border">
						<u-image src="/static/img/collection/border.png" width="380" height="380" mode="aspectFill"></u-image>
					</view>
					<view class="cover">
						<u-image :src="activityDetail.collectionCover" border-radius="24"
						width="296" height="296" mode="aspectFill"></u-image>
					</view>
				</view>
				<view class="name">
					{{activityDetail.collectionName}}
				</view>
				<view class="tips">
					集齐以下藏品即可进行合成
				</view>
			</view>
		</view>
		
		<view class="secCollections">
			<view class="item" v-for="(material, index) in materials" :key="index">
				<view class="bg">
					<u-image src="/static/img/collection/secCollectionBg.png" width="686" height="248"></u-image>
				</view>
				<view class="main">
					<view class="title">
						请添加{{material.quantity}}个以下藏品
					</view>
					<view class="imgGroup" v-show="calcSelectedMaterialQuantity(material.materialId, myHolds) == 0">
						<view class="img" @click="showSelectMaterialModal(material)">
							<view class="mask" v-show="calcSelectedMaterialQuantity(material.materialId, myHolds) == 0">
								<u-image src="/static/img/collection/plus.png" width="32" height="32"></u-image>
							</view>
							<view class="check" v-show="calcSelectedMaterialQuantity(material.materialId, myHolds) > 0">
								<view class="icon">
									<u-image src="/static/img/collection/check.png" width="40" height="40"></u-image>
								</view>
							</view>
							<u-image :src="material.materialCover" border-radius="12" width="140" height="140" mode="aspectFill"></u-image>
						</view>
					</view>
					<view class="imgGroup" v-show="calcSelectedMaterialQuantity(material.materialId, myHolds) > 0">
						<view class="img" @click="showSelectMaterialModal(material)"
						v-for="(selectedMaterial, index2) in filterSelectedMaterial(material.materialId)">
							<view class="mask" v-show="calcSelectedMaterialQuantity(material.materialId, myHolds) == 0">
								<u-image src="/static/img/collection/plus.png" width="32" height="32"></u-image>
							</view>
							<view class="check" v-show="calcSelectedMaterialQuantity(material.materialId, myHolds) > 0">
								<view class="icon">
									<u-image src="/static/img/collection/check.png" width="40" height="40"></u-image>
								</view>
							</view>
							<u-image :src="material.materialCover" border-radius="12" width="140" height="140" mode="aspectFill"></u-image>
						</view>
					</view>
				</view>
				<view class="mission ZiHunBianTaoTi">
					任务{{index+1}}
				</view>
			</view>
		</view>
		
		<view class="fixed-bottom">
			<view class="market" @click="marketPage">
				<u-image src="/static/img/collection/market.png" width="48" height="48"></u-image>
			</view>
			<view class="compose-btn" @click="composeStep1">
				立即合成
			</view>
		</view>
		
		
		<u-modal v-model="composeConfirmFlag" title="提示" :show-cancel-button="true" cancel-text="取消" confirm-text="确定"
			confirm-color="#FCE6B7" cancel-color="#fff" backgroundColor="#181819" :title-style="{color:'#fff'}" :content-style="{color:'#fff'}"
			@confirm="compose">
			<view class="slot-content">
				<view class="compose-confirm-content">
					您选择了{{calcRequireMaterialQuantity()}}个藏品参与"{{activityDetail.title}}"活动，一旦合成，该批藏品将进行销毁，是否确认合成？
				</view>
			</view>
		</u-modal>
		<u-modal v-model="composeResultFlag" :show-title="false" :show-cancel-button="false" confirm-text="我知道了"
			confirm-color="#FCE6B7" cancel-color="#fff" backgroundColor="#181819" :title-style="{color:'#fff'}" :content-style="{color:'#fff'}"
			@confirm="myPage">
			<view class="slot-content">
				<view class="compose-result-content">
					<view class="compose-result-title">恭喜获得</view>
					<view class="compose-result-name">{{activityDetail.collectionName}}</view>
					<image style="width: 200rpx; height: 200rpx;" :src="activityDetail.collectionCover">
					</image>
				</view>
			</view>
		</u-modal>
		<u-modal v-model="noAvailableMaterialFlag" title="温馨提示" :show-cancel-button="true" cancel-text="取消"
			confirm-color="#FCE6B7" cancel-color="#fff" backgroundColor="#181819" :title-style="{color:'#fff'}" :content-style="{color:'#fff'}"
			confirm-text="去购买" content="你没有当前藏品，是否去购买" @confirm="marketPage">
		</u-modal>
		<u-popup v-model="selectMaterialModalFlag" mode="bottom" border-radius="14">
			<view class="common-modal">
				<view class="modal-title">
					<view>
						<u-icon name="arrow-leftward" @click="hideSelectMaterialModal"></u-icon>
					</view>
					<view class="modal-title-txt">选择原料</view>
					<view class="close-buy-txt" @click="hideSelectMaterialModal">关闭</view>
				</view>
				<view>
					<view class="select-material-name">{{selectMaterial.materialName}}</view>
					<scroll-view scroll-y="true" style="height: 300rpx;">
						<view class="optional-material" v-for="myHold in copayMyHolds"
							v-show="myHold.collectionId == selectMaterial.materialId"
							@click="toggleCopayMyHold(myHold)">
							<view class="optional-material-id">
								#{{myHold.collectionSerialNumber}}
							</view>
							<view>
								<!-- <u-checkbox v-model="checkboxTrue" shape="circle" :disabled="true"
									v-show="myHold.selectedFlag"> </u-checkbox>
								<u-checkbox v-model="checkboxFalse" shape="circle" :disabled="true"
									v-show="!myHold.selectedFlag">
								</u-checkbox> -->
								<view class="check" v-show="myHold.selectedFlag">
									<u-image src="/static/img/login/checkbox.png" width="30" height="30"></u-image>
								</view>
								<view class="uncheck" v-show="!myHold.selectedFlag">
									<u-image src="/static/img/login/uncheckbox.png" width="30" height="30"></u-image>
								</view>
							</view>
						</view>
					</scroll-view>
					<view class="confirmBtn" @click="confirmSelected">
						确定(已选{{calcSelectedMaterialQuantity(selectMaterial.materialId, copayMyHolds)}}个)
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				id: '',
				materials: [],
				activityDetail: '',
				myHolds: [],
				copayMyHolds: [],
				checkboxTrue: true,
				checkboxFalse: false,
				selectMaterialModalFlag: false,
				selectMaterial: '',
				noAvailableMaterialFlag: false,
				composeConfirmFlag: false,
				composeResultFlag: false,
				composeRequireFlag: false,
			}
		},
		onNavigationBarButtonTap(e) {
			this.composeRequireFlag = true;
		},
		onLoad(option) {
			this.id = option.id;
			this.findActiveComposeActivityDetail();
			this.findAllMyHold();
		},
		methods: {

			myPage() {
				uni.reLaunch({
					url: "../my/my"
				});
			},

			marketPage() {
				uni.reLaunch({
					url: "/pages/market/market"
				});
			},

			toggleCopayMyHold(myHold) {
				myHold.selectedFlag = !myHold.selectedFlag;
			},

			calcRequireMaterialQuantity() {
				var quantity = 0;
				for (var i = 0; i < this.materials.length; i++) {
					quantity = quantity + this.materials[i].quantity;
				}
				return quantity;
			},

			compose() {
				var that = this;
				var holdCollectionIds = [];
				for (var i = 0; i < this.myHolds.length; i++) {
					if (this.myHolds[i].selectedFlag) {
						holdCollectionIds.push(this.myHolds[i].id);
					}
				}
				this.$u.post('/composeActivity/compose', {
					activityId: that.id,
					holdCollectionIds: holdCollectionIds
				}).then(res => {
					that.composeResultFlag = true;
				});
			},

			composeStep1() {
				var that = this;
				for (var i = 0; i < that.materials.length; i++) {
					var material = that.materials[i];
					var selectedQuantity = that.calcSelectedMaterialQuantity(material.materialId, that.myHolds);
					if (selectedQuantity != material.quantity) {
						uni.showToast({
							title: "原料数量不正确",
							icon: "none"
						});
						return;
					}
				}
				that.composeConfirmFlag = true;
			},

			filterSelectedMaterial(materialId) {
				if (materialId === null || materialId === '') {
					return [];
				}
				var selectedMaterials = [];
				for (var i = 0; i < this.myHolds.length; i++) {
					if (materialId == this.myHolds[i].collectionId && this.myHolds[i].selectedFlag) {
						selectedMaterials.push(this.myHolds[i]);
					}
				}
				return selectedMaterials;
			},

			calcAvailableMaterialQuantity(materialId) {
				if (materialId === null || materialId === '') {
					return 0;
				}
				var quantity = 0;
				for (var i = 0; i < this.myHolds.length; i++) {
					if (materialId == this.myHolds[i].collectionId) {
						quantity++;
					}
				}
				return quantity;
			},

			calcSelectedMaterialQuantity(materialId, myHolds) {
				if (materialId === null || materialId === '') {
					return 0;
				}
				var quantity = 0;
				for (var i = 0; i < myHolds.length; i++) {
					if (materialId == myHolds[i].collectionId && myHolds[i].selectedFlag) {
						quantity++;
					}
				}
				return quantity;
			},

			confirmSelected() {
				this.selectMaterialModalFlag = false;
				var myHolds = this.$u.deepClone(this.copayMyHolds);
				this.myHolds = myHolds;
			},

			hideSelectMaterialModal() {
				this.selectMaterialModalFlag = false;
			},

			showSelectMaterialModal(material) {
				this.selectMaterial = material;
				var availableMaterialQuantity = this.calcAvailableMaterialQuantity(material.materialId);
				if (availableMaterialQuantity == 0) {
					this.noAvailableMaterialFlag = true;
					return;
				}
				this.selectMaterialModalFlag = true;
				var copayMyHolds = this.$u.deepClone(this.myHolds);
				this.copayMyHolds = copayMyHolds;
			},

			findActiveComposeActivityDetail() {
				var that = this;
				this.$u.get('/composeActivity/findActiveComposeActivityDetail', {
					id: that.id
				}).then(res => {
					that.activityDetail = res.data;
					that.materials = res.data.materials;
				});
			},

			findAllMyHold() {
				var that = this;
				this.$u.get('/myArtwork/findAllMyHold').then(res => {
					var result = res.data;
					for (var i = 0; i < result.length; i++) {
						result[i].selectedFlag = false;
					}
					that.myHolds = result;
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
@import 'compose.scss'
</style>