Quasar.lang.set(Quasar.lang.zhHans)
var appVM = new Vue({
	el : '#q-app',
	data : function() {
		return {
			clickDealWayDictItems : [],
			carousels : [],
			selectedRecord : '',
			selectedRecordId : '',
			showAddOrUpdateDialogFlag : false,
			addOrUpdateAction : '',
			adjustOrderNoFlag : false,
		}
	},
	mounted : function() {
		var that = this;
		this.findClickDealWayDictItem();
		this.loadTableData({});
	},
	methods : {

		findClickDealWayDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'carouselClickDealWay'
				}
			}).then(function(res) {
				this.clickDealWayDictItems = res.body.data;
			});
		},

		adjustCarouselOrderNo : function() {
			var that = this;
			var carouselIds = [];
			for (var i = 0; i < that.carousels.length; i++) {
				carouselIds.push(that.carousels[i].id);
			}

			that.$http.post('/carousel/adjustCarouselOrderNo', carouselIds).then(function(res) {
				that.$q.notify({
					progress : true,
					timeout : 1000,
					type : 'positive',
					message : '操作成功',
				});
				that.adjustOrderNoFlag = false;
				that.refreshTable();
			});
		},

		moveUp : function(index) {
			var temp = this.carousels[index - 1];
			Vue.set(this.carousels, index - 1, this.carousels[index])
			Vue.set(this.carousels, index, temp);
		},

		moveDown : function(index) {
			var i = this.carousels[index + 1];
			Vue.set(this.carousels, index + 1, this.carousels[index])
			Vue.set(this.carousels, index, i);
		},

		addOrUpdateCarousel : function() {
			var that = this;
			var selectedRecord = that.selectedRecord;
			if (selectedRecord.cover === null || selectedRecord.cover === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入封面'
				});
				return;
			}
			if (selectedRecord.clickDealWay === null || selectedRecord.clickDealWay === '') {
				that.$q.notify({
					type : 'negative',
					message : '请选择点击处理方式'
				});
				return;
			}
			if (selectedRecord.clickDealWay === '2' || selectedRecord.clickDealWay === '3') {
				if (selectedRecord.link === null || selectedRecord.link === '') {
					that.$q.notify({
						type : 'negative',
						message : '请输入链接'
					});
					return;
				}
			}
			that.$http.post('/carousel/addOrUpdateCarousel', selectedRecord, {
				emulateJSON : true
			}).then(function(res) {
				that.$q.notify({
					progress : true,
					timeout : 1000,
					type : 'positive',
					message : '操作成功',
				});
				that.showAddOrUpdateDialogFlag = false;
				that.refreshTable();
			});
		},

		showEditDialog : function(id) {
			var that = this;
			that.$http.get('/carousel/findCarouselById', {
				params : {
					id : id
				}
			}).then(function(res) {
				that.selectedRecord = res.body.data;
				that.showAddOrUpdateDialogFlag = true;
				that.addOrUpdateAction = 'update';
			});
		},

		showAddDialog : function() {
			this.showAddOrUpdateDialogFlag = true;
			this.addOrUpdateAction = 'add';
			this.selectedRecord = {
				clickDealWay : '1',
				cover : '',
				link : ''
			};
		},

		delById : function(id) {
			var that = this;
			that.$q.dialog({
				title : '提示',
				message : '确定要删除吗?',
				cancel : true,
				persistent : true
			}).onOk(function() {
				that.$http.get('/carousel/delById', {
					params : {
						id : id
					}
				}).then(function(res) {
					that.$q.notify({
						progress : true,
						timeout : 1000,
						type : 'positive',
						message : '操作成功',
					});
					that.refreshTable();
				});
			});
		},

		refreshTable : function() {
			this.loadTableData({});
		},

		loadTableData : function(param) {
			var that = this;
			that.tableDataLoading = true;
			that.$http.get('/carousel/findCarousel', {
				params : {}
			}).then(function(res) {
				that.carousels = res.body.data;
			});
		}

	},
});