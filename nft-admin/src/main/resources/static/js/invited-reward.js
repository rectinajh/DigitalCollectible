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
				name : 'level',
				field : 'level',
				label : '奖励级别',
			}, {
				align : 'left',
				name : 'invitedCount',
				field : 'invitedCount',
				label : '要求有效邀请人数',
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
			rewardFun : '',
			invitedRequires : [],
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

		addInvitedRequire : function() {
			this.invitedRequires.push({
				invitedCount : '',
				rewardArtworkId : '',
			});
		},

		removeInvitedRequire : function(index) {
			this.invitedRequires.splice(index, 1);
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
			var invitedRequires = that.invitedRequires;
			for (var i = 0; i < invitedRequires.length; i++) {
				var invitedCount = invitedRequires[i].invitedCount;
				if (invitedCount === null || invitedCount === '') {
					that.$q.notify({
						type : 'negative',
						message : '请输入有效邀请人数'
					});
					return;
				}
				if (invitedCount <= 0) {
					that.$q.notify({
						type : 'negative',
						message : '有效邀请人数必须大于0'
					});
					return;
				}
				var rewardArtworkId = invitedRequires[i].rewardArtworkId;
				if (rewardArtworkId === null || rewardArtworkId === '') {
					that.$q.notify({
						type : 'negative',
						message : '请选择奖励的艺术品'
					});
					return;
				}
			}
			if (invitedRequires.length == 0) {
				that.$q.notify({
					type : 'negative',
					message : '最少添加一项'
				});
				return;
			}

			if (that.rewardFun === null || that.rewardFun === '') {
				that.$q.notify({
					type : 'negative',
					message : '请设置是否开启奖励功能'
				});
				return;
			}
			that.$http.post('/invitedReward/updateInvitedRewardSetting', {
				rewardFun : that.rewardFun,
				invitedRequires : that.invitedRequires
			}, {}).then(function(res) {
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
			that.$http.get('/invitedReward/getInvitedRewardSetting', {
				params : {}
			}).then(function(res) {
				that.rewardFun = res.body.data.rewardFun;
				that.invitedRequires = res.body.data.invitedRequires;
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
			that.$http.get('/invitedReward/findRewardRecordByPage', {
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