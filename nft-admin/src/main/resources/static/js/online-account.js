Quasar.lang.set(Quasar.lang.zhHans)
var appVM = new Vue({
	el : '#q-app',
	data : function() {
		return {
			userName : '',
			ipAddr : '',
			timeStart : '',
			timeEnd : '',
			tableDataLoading : false,
			tablePagination : {
				page : 1,
				rowsPerPage : 10,
				rowsNumber : 10
			},
			tableColumns : [ {
				align : 'left',
				name : 'userName',
				field : 'userName',
				label : '登录账号'
			}, {
				align : 'left',
				name : 'ipAddr',
				field : 'ipAddr',
				label : 'ip地址'
			}, {
				align : 'left',
				name : 'subSystemName',
				field : 'subSystemName',
				label : '子系统'
			}, {
				align : 'left',
				name : 'browser',
				field : 'browser',
				label : '浏览器'
			}, {
				align : 'left',
				name : 'os',
				field : 'os',
				label : '操作系统'
			}, {
				align : 'left',
				name : 'loginTime',
				field : 'loginTime',
				label : '登录时间'
			}, {
				align : 'left',
				name : 'action',
				label : '操作'
			} ],
			tableData : []
		}
	},
	mounted : function() {
		this.loadTableData({
			pagination : this.tablePagination
		});
	},
	methods : {

		forceLogout : function(token) {
			var that = this;
			that.$q.dialog({
				title : '提示',
				message : '确定要强制退出登录吗?',
				cancel : true,
				persistent : true
			}).onOk(function() {
				that.$http.post('/onlineAccount/forceLogout', {
					token : token
				}, {
					emulateJSON : true
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
			that.$http.get('/onlineAccount/findOnlineAccountByPage', {
				params : {
					pageSize : param.pagination.rowsPerPage,
					pageNum : param.pagination.page,
					userName : that.userName,
					ipAddr : that.ipAddr,
					timeStart : that.timeStart ? that.timeStart + ' 00:00:00' : '',
					timeEnd : that.timeEnd ? that.timeEnd + ' 23:59:59' : ''
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