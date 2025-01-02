Quasar.lang.set(Quasar.lang.zhHans)
var appVM = new Vue({
	el : '#q-app',
	data : function() {
		return {
			ipAddr : '',
			tableDataLoading : false,
			tablePagination : {
				page : 1,
				rowsPerPage : 10,
				rowsNumber : 10
			},
			tableColumns : [ {
				align : 'left',
				name : 'ipAddr',
				field : 'ipAddr',
				label : 'IP地址'
			}, {
				align : 'left',
				name : 'createTime',
				field : 'createTime',
				label : '创建时间'
			}, {
				align : 'left',
				name : 'action',
				label : '操作'
			} ],
			tableData : [],
		}
	},
	mounted : function() {
		var that = this;
		this.loadTableData({});
	},
	methods : {

		addIpBlackList : function() {
			var that = this;
			that.$q.dialog({
				title : '请输入要封禁的IP地址',
				ok : '确定',
				cancel : '取消',
				prompt : {
					model : '',
					isValid : function(val) {
						return val != null && val != '';
					},
					type : 'text'
				},
				persistent : true
			}).onOk(function(data) {
				that.$http.post('/ipBlackList/addIpBlackList', {
					ipAddr : data
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

		delIpBlackList : function(ipAddr) {
			var that = this;
			that.$q.dialog({
				title : '提示',
				message : '确定要删除吗?',
				cancel : true,
				persistent : true
			}).onOk(function() {
				that.$http.get('/ipBlackList/delIpBlackList', {
					params : {
						ipAddr : ipAddr
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
			that.$http.get('/ipBlackList/findIpBlackList', {
				params : {
					ipAddr : that.ipAddr
				}
			}).then(function(res) {
				var data = res.body.data;
				that.tableData = data;
				that.tableDataLoading = false;
			});
		}

	},
});