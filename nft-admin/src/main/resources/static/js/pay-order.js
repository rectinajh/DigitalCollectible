Quasar.lang.set(Quasar.lang.zhHans)
var appVM = new Vue({
	el : '#q-app',
	data : function() {
		return {
			memberMobile : '',
			collectionName : '',
			bizMode : '',
			bizModeDictItems : [],
			stateDictItems : [],
			state : '',
			timeStart : dayjs().format('YYYY-MM-DD'),
			timeEnd : dayjs().format('YYYY-MM-DD'),
			tableDataLoading : false,
			tablePagination : {
				page : 1,
				rowsPerPage : 10,
				rowsNumber : 10
			},
			tableColumns : [ {
				align : 'left',
				name : 'member_info',
				label : '会员信息'
			}, {
				align : 'left',
				name : 'collection_info',
				label : '商品信息'
			}, {
				align : 'left',
				name : 'pay_biz',
				label : '支付业务'
			}, {
				align : 'left',
				name : 'stateName',
				field : 'stateName',
				label : '状态',
			}, {
				align : 'left',
				name : 'amount',
				field : 'amount',
				label : '金额',
			}, {
				align : 'left',
				name : 'time_info',
				label : '时间',
			} ],
			tableData : [],
		}
	},
	mounted : function() {
		var that = this;
		that.findStateDictItem();
		that.findBizModeDictItem();
		that.loadTableData({
			pagination : this.tablePagination
		});
		new ClipboardJS('.copy-btn', {
			text : function(trigger) {
				return trigger.getAttribute('data-clipboard-text');
			}
		}).on('success', function(e) {
			that.$q.notify({
				type : 'positive',
				message : '复制成功'
			});
			return;
		});
	},
	methods : {

		findStateDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'payOrderState'
				}
			}).then(function(res) {
				this.stateDictItems = res.body.data;
			});
		},

		findBizModeDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'payOrderBizMode'
				}
			}).then(function(res) {
				this.bizModeDictItems = res.body.data;
			});
		},

		viewImage : function(imagePath) {
			var image = new Image();
			image.src = imagePath;
			var viewer = new Viewer(image, {
				hidden : function() {
					viewer.destroy();
				},
			});
			viewer.show();
		},

		refreshTable : function() {
			this.loadTableData({
				pagination : {
					page : 1,
					rowsPerPage : this.tablePagination.rowsPerPage,
					rowsNumber : 10
				}
			});
		},

		loadTableData : function(param) {
			var that = this;
			that.tableDataLoading = true;
			that.$http.get('/payOrder/findPayOrderByPage', {
				params : {
					pageSize : param.pagination.rowsPerPage,
					pageNum : param.pagination.page,
					memberMobile : that.memberMobile,
					collectionName : that.collectionName,
					bizMode : that.bizMode,
					state : that.state,
					createTimeStart : that.timeStart ? that.timeStart + ' 00:00:00' : '',
					createTimeEnd : that.timeEnd ? that.timeEnd + ' 23:59:59' : ''
				}
			}).then(function(res) {
				var data = res.body.data;
				that.tablePagination.rowsNumber = data.total;
				that.tableData = data.content;
				that.tablePagination.page = param.pagination.page;
				that.tablePagination.rowsPerPage = param.pagination.rowsPerPage;
				that.tableDataLoading = false;
			});
		}

	},
});