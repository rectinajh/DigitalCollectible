Quasar.lang.set(Quasar.lang.zhHans)
var appVM = new Vue({
	el : '#q-app',
	data : function() {
		return {
			mobile : '',
			state : '',
			stateDictItems : [],
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
				name : 'amount',
				field : 'amount',
				label : '首单消费金额',
			}, {
				align : 'left',
				name : 'paidTime',
				field : 'paidTime',
				label : '首单付款时间',
			}, {
				align : 'left',
				name : 'collectionName',
				field : 'collectionName',
				label : '奖励的艺术品',
			}, {
				align : 'left',
				name : 'stateName',
				field : 'stateName',
				label : '状态',
			}, {
				align : 'left',
				name : 'createTime',
				field : 'createTime',
				label : '创建时间',
			}, {
				align : 'left',
				name : 'dealTime',
				field : 'dealTime',
				label : '处理时间',
			} ],
			tableData : [],
			showSettingDialogFlag : false,
			collections : [],
			setting : '',
		}
	},
	mounted : function() {
		var that = this;
		that.findStateDictItem();
		that.findAllCollection();
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
					dictTypeCode : 'rewardGrantState'
				}
			}).then(function(res) {
				this.stateDictItems = res.body.data;
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

		updateSetting : function() {
			var that = this;
			var setting = that.setting;
			if (setting.rewardFun === null || setting.rewardFun === '') {
				that.$q.notify({
					type : 'negative',
					message : '请设置是否开启奖励功能'
				});
				return;
			}
			if (setting.rewardArtworkId === null || setting.rewardArtworkId === '') {
				that.$q.notify({
					type : 'negative',
					message : '请选择要奖励的艺术品'
				});
				return;
			}
			that.$http.post('/firstOrderReward/updateFirstOrderRewardSetting', setting, {
				emulateJSON : true
			}).then(function(res) {
				that.$q.notify({
					progress : true,
					timeout : 1000,
					type : 'positive',
					message : '操作成功',
				});
				that.showSettingDialogFlag = false;
			});
		},

		showSettingDialog : function(id) {
			var that = this;
			that.$http.get('/firstOrderReward/getFirstOrderRewardSetting', {
				params : {}
			}).then(function(res) {
				that.setting = res.body.data;
				that.showSettingDialogFlag = true;
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
			that.$http.get('/firstOrderReward/findRewardRecordByPage', {
				params : {
					pageSize : param.pagination.rowsPerPage,
					pageNum : param.pagination.page,
					memberMobile : that.mobile,
					state : that.state
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