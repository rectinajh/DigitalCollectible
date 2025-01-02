Quasar.lang.set(Quasar.lang.zhHans)
var appVM = new Vue({
	el : '#q-app',
	data : function() {
		return {
			mobile : '',
			riskCause : '',
			riskCauseDictItems : [],
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
				name : 'risk_account',
				label : '风控账号'
			}, {
				align : 'left',
				name : 'riskCauseName',
				field : 'riskCauseName',
				label : '风控原因'
			}, {
				align : 'left',
				name : 'hitCount',
				field : 'hitCount',
				label : '命中次数',
				format : function(val) {
					return val + '次';
				},
			}, {
				align : 'left',
				name : 'riskPunishName',
				field : 'riskPunishName',
				label : '处罚'
			}, {
				align : 'left',
				name : 'time_info',
				label : '时间'
			} ],
			tableData : [],
		}
	},
	mounted : function() {
		var that = this;
		that.findRiskCauseDictItem();
		that.loadTableData({
			pagination : this.tablePagination
		});
	},
	methods : {

		findRiskCauseDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'riskCause'
				}
			}).then(function(res) {
				this.riskCauseDictItems = res.body.data;
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
			that.$http.get('/risk/findRiskRecordByPage', {
				params : {
					pageSize : param.pagination.rowsPerPage,
					pageNum : param.pagination.page,
					mobile : that.mobile,
					riskCause : that.riskCause,
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