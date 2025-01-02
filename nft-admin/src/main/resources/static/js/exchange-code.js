Quasar.lang.set(Quasar.lang.zhHans)
var appVM = new Vue({
	el : '#q-app',
	data : function() {
		return {
			tableDataLoading : false,
			tablePagination : {
				page : 1,
				rowsPerPage : 10,
				rowsNumber : 10
			},
			tableColumns : [ {
				align : 'left',
				name : 'collection_info',
				label : '艺术品'
			}, {
				align : 'left',
				name : 'totalCount',
				field : 'totalCount',
				label : '兑换码数量',
			}, {
				align : 'left',
				name : 'unused_count',
				label : '未使用',
			}, {
				align : 'left',
				name : 'usedCount',
				field : 'usedCount',
				label : '已使用'
			}, {
				align : 'left',
				name : 'invalidCount',
				field : 'invalidCount',
				label : '已作废',
			}, {
				align : 'left',
				name : 'expiredCount',
				field : 'expiredCount',
				label : '已过期',
			}, {
				align : 'left',
				name : 'action',
				label : '操作'
			} ],
			tableData : [],
			collections : [],
			selectedRecordId : '',
			showAddDialogFlag : false,
			generateExchangeCode : '',

			exchangeCodes : [],
			exchangeCodeDialogFlag : false,
			
			showExchangeRecordDialogFlag : false,
			exchangeRecords : [],
			memberMobile : '',
		}
	},
	mounted : function() {
		var that = this;
		that.findAllCollection();
		that.loadTableData({
			pagination : this.tablePagination
		});
	},
	methods : {
		
		invalid : function(id) {
			var that = this;
			that.$q.dialog({
				title : '提示',
				message : '确定要将未使用的兑换码作废吗?',
				cancel : true,
				persistent : true
			}).onOk(function() {
				that.$http.get('/exchangeCode/invalid', {
					params : {
						collectionId : id
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
		
		showExchangeRecordDialog : function(id) {
			var that = this;
			that.memberMobile = '';
			that.selectedRecordId = id;
			that.findExchangeRecord();
			that.showExchangeRecordDialogFlag = true;
		},

		findExchangeRecord : function() {
			var that = this;
			that.$http.get('/exchangeCode/findExchangeRecord', {
				params : {
					collectionId : that.selectedRecordId,
					memberMobile : that.memberMobile
				}
			}).then(function(res) {
				that.exchangeRecords = res.body.data;
			});
		},

		showExchangeCodeDialog : function(id) {
			var that = this;
			that.$http.get('/exchangeCode/findExchangeCode', {
				params : {
					collectionId : id,
				}
			}).then(function(res) {
				that.exchangeCodes = res.body.data;
				that.exchangeCodeDialogFlag = true;
			});
		},

		findAllCollection : function() {
			var that = this;
			that.$http.get('/collection/findAllCollection', {
				params : {}
			}).then(function(res) {
				this.collections = res.body.data;
			});
		},

		add : function() {
			var that = this;
			var generateExchangeCode = that.generateExchangeCode;
			if (generateExchangeCode.collectionId === null || generateExchangeCode.collectionId === '') {
				that.$q.notify({
					type : 'negative',
					message : '请选择要兑换的艺术品'
				});
				return;
			}
			if (generateExchangeCode.quantity === null || generateExchangeCode.quantity === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入兑换码数量'
				});
				return;
			}
			if (generateExchangeCode.effectiveDays != null && generateExchangeCode.effectiveDays != '') {
				if (generateExchangeCode.effectiveDays <= 0) {
					that.$q.notify({
						type : 'negative',
						message : '有效期(天数)必须大于0'
					});
					return;
				}
			}
			that.$http.post('/exchangeCode/generateExchangeCode', generateExchangeCode, {
				emulateJSON : true
			}).then(function(res) {
				that.$q.notify({
					progress : true,
					timeout : 1000,
					type : 'positive',
					message : '操作成功',
				});
				that.showAddDialogFlag = false;
				that.refreshTable();
			});
		},

		showAddDialog : function() {
			this.showAddDialogFlag = true;
			this.generateExchangeCode = {
				collectionId : '',
				quantity : '',
				effectiveDays : ''
			};
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
			that.$http.get('/exchangeCode/findExchangeCodeSummaryByPage', {
				params : {
					pageSize : param.pagination.rowsPerPage,
					pageNum : param.pagination.page
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