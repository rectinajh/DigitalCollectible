<template>
	<view class="page-content">
		<u-modal v-model="composeConfirmFlag" title="提示" :show-cancel-button="true" cancel-text="取消" confirm-text="确定"
			@confirm="compose">
			<view class="slot-content">
				<view class="compose-confirm-content">
					您选择了{{calcRequireMaterialQuantity()}}个藏品参与"{{activityDetail.title}}"活动，一旦合成，该批藏品将进行销毁，是否确认合成？
				</view>
			</view>
		</u-modal>
		<u-modal v-model="composeResultFlag" :show-title="false" :show-cancel-button="false" confirm-text="我知道了"
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
			confirm-text="去购买" content="你没有当前藏品，是否去购买" @confirm="marketPage">
		</u-modal>
		<u-popup v-model="composeRequireFlag" mode="center" width="80%">
			<view class="compose-require">
				<view class="compose-require-title"><text>合成规则</text>
					<u-icon class="compose-require-title-close" name="close" @click="composeRequireFlag = false;">
					</u-icon>
				</view>
				<view class="compose-require-content">
					<view class="compose-require-item" v-for="(material, index) in materials">
						{{material.materialName}}*{{material.quantity}}
					</view>
				</view>
			</view>
		</u-popup>
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
								<u-checkbox v-model="checkboxTrue" shape="circle" :disabled="true"
									v-show="myHold.selectedFlag"> </u-checkbox>
								<u-checkbox v-model="checkboxFalse" shape="circle" :disabled="true"
									v-show="!myHold.selectedFlag">
								</u-checkbox>
							</view>
						</view>
					</scroll-view>
					<u-button type="primary" @click="confirmSelected">
						确定(已选{{calcSelectedMaterialQuantity(selectMaterial.materialId, copayMyHolds)}}个)</u-button>
				</view>
			</view>
		</u-popup>
		<view v-for="(material, index) in materials">
			<view class="condition">
				<view class="condition-title">
					<view class="condition-title-l">条件{{index + 1}} （请选择{{material.quantity}}个藏品）:</view>
					<view class="condition-title-r"
						v-show="calcSelectedMaterialQuantity(material.materialId, myHolds) > 0">
						<u-button type="primary" size="mini" plain @click="showSelectMaterialModal(material)">重新选择
						</u-button>
					</view>
				</view>
				<u-row gutter="8">
					<u-col span="4" v-show="calcSelectedMaterialQuantity(material.materialId, myHolds) == 0">
						<view class="require-material">
							<view class="require-material-cover">
								<image class="require-material-cover-img"
									style="height: 200rpx; width: 100%; opacity: 0.4;" :src="material.materialCover">
								</image>
								<view class="require-material-cover-add" @click="showSelectMaterialModal(material)">
									<u-icon name="plus" color="white" size="60"></u-icon>
								</view>
							</view>
							<view class="require-material-name u-line-1">
								{{material.materialName}}
							</view>
						</view>
					</u-col>
					<u-col span="4" v-for="(selectedMaterial, index2) in filterSelectedMaterial(material.materialId)">
						<view class="selected-material">
							<view class="selected-material-cover">
								<image class="selected-material-cover-img" style="height: 200rpx; width: 100%;"
									:src="selectedMaterial.collectionCover">
								</image>
								<view class="selected-material-num">
									原料{{index2 + 1}}
								</view>
							</view>
							<view class="selected-material-name u-line-1">
								{{selectedMaterial.collectionName}}
							</view>
							<view class="selected-material-id">
								<text
									class="selected-material-id-l">#{{selectedMaterial.collectionSerialNumber}}</text><text>/{{selectedMaterial.quantity}}</text>
							</view>
						</view>
					</u-col>
				</u-row>
			</view>
			<view class="div-line"></view>
		</view>

		<view class="compose-after">
			<image class="compose-arrow" style="height: 60rpx; width: 60rpx;" src="/static/img/arrow-down.png">
			</image>
			<view class="compose-after-collection">
				<image class="compose-after-collection-img" style="height: 240rpx; width: 240rpx;"
					:src="activityDetail.collectionCover">
				</image>
				<view class="compose-after-collection-name">
					{{activityDetail.collectionName}}
				</view>
			</view>
		</view>

		<view class="fixed-bottom">
			<view class="fixed-bottom-content">
				<view class="fixed-bottom-content-inner">
					<view class="fixed-bottom-content-inner-l" @click="marketPage">
						<image style="width: 40rpx; height: 40rpx;" src="/static/img/market.png">
						</image>
						<view>去市场</view>
					</view>
					<view class="fixed-bottom-content-inner-r">
						<view class="fixed-bottom-tip">集齐以上藏品即可进行合成</view>
						<view class="compose-btn" @click="composeStep1">立即合成</view>
					</view>
				</view>
			</view>
		</view>
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
					url: "../market/market"
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

<style>
	::v-deep .u-checkbox__icon-wrap--disabled {
		color: transparent !important;
		background-color: transparent !important;
		border-color: #c8c9cc !important;
	}

	::v-deep .u-checkbox__icon-wrap--disabled--checked {
		color: #fff !important;
		background-color: #2979ff !important;
		border-color: #2979ff !important;
	}

	.page-content {
		padding-bottom: 280rpx;
	}

	.compose-require {
		padding: 24rpx;
	}

	.compose-require-title {
		text-align: center;
	}

	.compose-require-title-close {
		float: right;
	}

	.compose-require-content {}

	.compose-require-item {
		color: #000000;
		padding-top: 4rpx;
		padding-bottom: 4rpx;
	}

	.compose-after {
		text-align: center;
	}

	.div-line {
		width: 100%;
		height: 20rpx;
		background: #f3f3f3;
	}

	.compose-arrow {}

	.compose-after-collection {
		padding-top: 16rpx;
	}

	.compose-after-collection-img {}

	.compose-after-collection-name {}

	.compose-result-content {
		text-align: center;
		padding-top: 20px;
		padding-bottom: 20px;
	}

	.compose-result-name {
		line-height: 2;
		color: #888;
	}

	.compose-result-title {
		line-height: 2;
		font-size: larger;
	}

	.compose-confirm-content {
		font-size: smaller;
		padding: 32rpx;
	}

	.select-material-name {
		padding-top: 32rpx;
		padding-bottom: 32rpx;
	}

	.optional-material {
		display: flex;
		justify-content: space-between;
		align-items: center;
		line-height: 3;
		color: #888;
	}

	.optional-material-id {}

	.common-modal {
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-top: 32rpx;
		padding-bottom: 32rpx;
	}

	.modal-title {
		display: flex;
		justify-content: space-between;
	}

	.modal-title-txt {
		font-weight: bold;
	}

	.close-modal-txt {
		color: #909399;
	}

	.fixed-bottom-content-inner {
		display: flex;
		align-items: center;
	}

	.fixed-bottom-content-inner-l {
		text-align: center;
	}

	.fixed-bottom-content-inner-r {
		text-align: center;
		flex: 1;
		padding-left: 64rpx;
	}

	.fixed-bottom-tip {
		color: #888;
		padding-bottom: 32rpx;
	}

	.compose-btn {
		background: #2979ff;
		font-size: large;
		font-weight: bold;
		color: white;
		padding: 20rpx 60rpx;
	}

	.fixed-bottom-content {
		padding-top: 32rpx;
		padding-bottom: 32rpx;
	}

	.fixed-bottom {
		position: fixed;
		bottom: 0rpx;
		left: 0rpx;
		width: 100%;
		background: #ffffff;
		padding-left: 32rpx;
		padding-right: 32rpx;
	}

	.condition {
		background: white;
		padding-left: 32rpx;
		padding-right: 32rpx;
		padding-bottom: 8rpx;
	}

	.condition-title {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.condition-title-l {
		color: #888;
		line-height: 3;
		font-size: smaller;
	}

	.condition-title-r {}

	.selected-material {
		padding-bottom: 8rpx;
	}

	.selected-material-cover {
		position: relative;
	}

	.selected-material-cover-img {}

	.selected-material-num {
		position: absolute;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		background-color: #ecf5ff;
		height: 40rpx;
		width: 80rpx;
		font-size: smaller;
		color: #353535;
	}

	.selected-material-name {
		font-size: small;
		padding-left: 4rpx;
	}

	.selected-material-id {
		font-size: small;
		padding-left: 4rpx;
	}

	.selected-material-id-l {
		color: #888;
	}

	.require-material {}

	.require-material-cover {
		position: relative;
	}

	.require-material-cover-img {}

	.require-material-cover-add {
		position: absolute;
		top: 0;
		right: 0;
		bottom: 0;
		left: 0;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		background-color: rgb(0 0 0 / 17%);
		height: 200rpx;
	}

	.require-material-name {
		font-size: small;
		padding-left: 4rpx;
	}

	page {
		height: 100% !important;
		background-color: #f3f3f3;
	}
</style>
