Quasar.lang.set(Quasar.lang.zhHans)
var appVM = new Vue({
	el : '#q-app',
	data : function() {
		return {
			tabs : [ {
				name : 'task',
				label : '优先购任务',
			}, {
				name : 'record',
				label : '发放记录',
			} ],
			tab : 'task',
			taskName : '',
			collectionName : '',
			taskState : '',
			taskStateDictItems : [],

			bizType : '',
			bizTypeDictItems : [],
			preSaleTaskId : '',
			tmpPreSaleTaskId : '',
			preSaleTasks : [],
			memberMobile : '',
			recordState : '',
			recordStateDictItems : [],
			tableDataLoading : false,
			tablePagination : {
				page : 1,
				rowsPerPage : 10,
				rowsNumber : 10
			},
			tableColumns : [ {
				align : 'left',
				name : 'taskName',
				field : 'taskName',
				label : '任务名称',
			}, {
				align : 'left',
				name : 'collection_info',
				label : '空投的艺术品'
			}, {
				align : 'left',
				name : 'condition_info',
				label : '优先购条件'
			}, {
				align : 'left',
				name : 'pre_minute_info',
				label : '优先时间'
			},{
				align : 'left',
				name : 'stateName',
				field : 'stateName',
				label : '状态',
			}, {
				align : 'left',
				name : 'executeTime',
				field : 'executeTime',
				label : '执行时间',
			}, {
				align : 'left',
				name : 'createTime',
				field : 'createTime',
				label : '创建时间',
			}, {
				align : 'left',
				name : 'action',
				label : '操作'
			} ],
			recordTableColumns : [ {
				align : 'left',
				name : 'biz_info',
				label : '业务类型'
			}, {
				align : 'left',
				name : 'member_info',
				label : '会员信息'
			}, {
				align : 'left',
				name : 'collection_info',
				label : '艺术品'
			}, {
				align : 'left',
				name : 'stateName',
				field : 'stateName',
				label : '状态',
			}, {
				align : 'left',
				name : 'dealTime',
				field : 'dealTime',
				label : '处理时间',
			}, {
				align : 'left',
				name : 'grantTime',
				field : 'grantTime',
				label : '发放时间',
			}, {
				align : 'left',
				name : 'action',
				label : '操作'
			} ],
			tableData : [],
			collections : [],
			showAddDialogFlag : false,
			selectedRecord : '',
			selectedRecordId : '',
			memberMobile : '',
			snapshotParamDictItems : [],
			conditions : [],
			condDictItems : [ {
				dictItemCode : '大于等于',
				dictItemName : '大于等于'
			}, {
				dictItemCode : '大于',
				dictItemName : '大于'
			}, {
				dictItemCode : '等于',
				dictItemName : '等于'
			}, {
				dictItemCode : '小于等于',
				dictItemName : '小于等于'
			}, {
				dictItemCode : '小于',
				dictItemName : '小于'
			} ],
			logicalOperationDictItems : [ {
				dictItemCode : '且',
				dictItemName : '且'
			}, {
				dictItemCode : '或',
				dictItemName : '或'
			} ],
			memberSnapshots : [],
			memberSnapshotDialogFlag : false,
			conditionErrorFalg : false,
		}
	},
	watch : {
		tab : {
			handler : function(newVal, oldVal) {
				if (newVal == 'task') {
					this.switchTab();
				} else if (newVal == 'record') {
					this.switchTab();
				}
			},
			immediate : false,
			deep : true
		}
	},
	mounted : function() {
		var that = this;
		that.findAllCollection();
		that.findSnapshotParamItem();
		that.findTaskStateDictItem();
		that.findBizTypeDictItem();
		that.findRecordStateDictItem();
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

		formatFieldLabel : function(condition) {
			if (condition.fieldName.startsWith('holdArtwork')) {
				var holdArtworkId = condition.fieldName.split('_')[1];
				for (var i = 0; i < this.collections.length; i++) {
					var collection = this.collections[i];
					if (collection.id == holdArtworkId) {
						return collection.name;
					}
				}
			}
			return condition.fieldLabel;
		},

		getFieldType : function(fieldName) {
			if (fieldName == 'paidCount' || fieldName == 'paidAmount' || fieldName == 'effectiveInvitedCount' || fieldName.startsWith('holdArtwork')) {
				return 'number';
			}
			return 'date';
		},

		showRecordWithPreSaleTaskId : function(preSaleTaskId) {
			this.tmpPreSaleTaskId = preSaleTaskId;
			this.tab = 'record';
		},

		switchTab : function() {
			this.tableDataLoading = false;
			this.tablePagination = {
				page : 1,
				rowsPerPage : 10,
				rowsNumber : 10
			};
			if (this.tab == 'task') {
				this.taskName = '';
				this.collectionName = '';
				this.taskState = '';
				this.loadTableData({
					pagination : this.tablePagination
				});
			} else if (this.tab == 'record') {
				this.bizType = '';
				if (this.tmpPreSaleTaskId) {
					this.preSaleTaskId = this.tmpPreSaleTaskId;
					this.tmpPreSaleTaskId = '';
				} else {
					this.preSaleTaskId = '';
				}
				this.memberMobile = '';
				this.collectionName = '';
				this.recordState = '';
				this.findAllPreSaleTask();
				this.loadTableData2({
					pagination : this.tablePagination
				});
			}
		},

		findAllPreSaleTask : function() {
			var that = this;
			that.$http.get('/preSale/findAllPreSaleTask', {
				params : {}
			}).then(function(res) {
				this.preSaleTasks = res.body.data;
			});
		},

		showMemberSnapshotDialog2 : function(effectiveConditions) {
			var that = this;
			that.findMemberSnapshot(effectiveConditions);
			that.memberSnapshotDialogFlag = true;
		},

		showMemberSnapshotDialog1 : function() {
			var that = this;
			var effectiveConditions = that.getEffectiveCondition();
			if (that.conditionErrorFalg) {
				return;
			}
			that.findMemberSnapshot(effectiveConditions);
			that.memberSnapshotDialogFlag = true;
		},

		findMemberSnapshot : function(effectiveConditions) {
			var that = this;
			that.$http.post('/member/findMemberSnapshot', effectiveConditions).then(function(res) {
				that.memberSnapshots = res.body.data;
			});
		},

		getEffectiveCondition : function() {
			var that = this;
			var conditions = that.conditions;
			var effectiveConditions = [];
			for (var i = 0; i < conditions.length; i++) {
				var airDropCondition = conditions[i];
				var checkedFlag = airDropCondition.checkedFlag;
				if (checkedFlag) {
					effectiveConditions.push({
						fieldName : airDropCondition.fieldName,
						cond : airDropCondition.cond,
						fieldValue : airDropCondition.fieldValue,
						logicalOperation : airDropCondition.logicalOperation
					});
				}
			}
			for (var i = 0; i < effectiveConditions.length; i++) {
				var condition = effectiveConditions[i];
				if (condition.cond === null || condition.cond === '') {
					that.$q.notify({
						type : 'negative',
						message : '请选择条件'
					});
					that.conditionErrorFalg = true;
					return;
				}
				if (condition.fieldValue === null || condition.fieldValue === '') {
					that.$q.notify({
						type : 'negative',
						message : '请输入参数值'
					});
					that.conditionErrorFalg = true;
					return;
				}
			}
			that.conditionErrorFalg = false;
			return effectiveConditions;
		},

		findSnapshotParamItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'snapshotParam'
				}
			}).then(function(res) {
				this.snapshotParamDictItems = res.body.data;
			});
		},

		findTaskStateDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'preSaleTaskState'
				}
			}).then(function(res) {
				this.taskStateDictItems = res.body.data;
			});
		},

		findBizTypeDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'preSaleBizType'
				}
			}).then(function(res) {
				this.bizTypeDictItems = res.body.data;
			});
		},

		findRecordStateDictItem : function() {
			var that = this;
			that.$http.get('/dictconfig/findDictItemInCache', {
				params : {
					dictTypeCode : 'preSaleQualifyState'
				}
			}).then(function(res) {
				this.recordStateDictItems = res.body.data;
			});
		},

		addAirDropHoldArtworkCondition : function(dictItem, collection) {
			console.log(this.$refs.addConditionDropdown);
			this.$refs.addConditionDropdown.hide();
			this.conditions.push({
				fieldName : dictItem.dictItemCode + '_' + collection.id,
				fieldLabel : collection.name,
				cond : '',
				fieldValue : '',
				logicalOperation : '',
				checkedFlag : true
			});
			if (this.conditions.length > 1) {
				this.conditions[this.conditions.length - 2].logicalOperation = '且';
			}
		},

		addAirDropCondition : function(dictItem) {
			this.conditions.push({
				fieldName : dictItem.dictItemCode,
				fieldLabel : dictItem.dictItemName,
				cond : '',
				fieldValue : '',
				logicalOperation : '',
				checkedFlag : true
			});
			if (this.conditions.length > 1) {
				this.conditions[this.conditions.length - 2].logicalOperation = '且';
			}
		},

		del : function(id) {
			var that = this;
			that.$q.dialog({
				title : '提示',
				message : '确定要删除吗?',
				cancel : true,
				persistent : true
			}).onOk(function() {
				that.$http.get('/composeActivity/del', {
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
			var selectedRecord = that.selectedRecord;
			if (selectedRecord.taskName === null || selectedRecord.taskName === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入任务名称'
				});
				return;
			}
			if (selectedRecord.collectionId === null || selectedRecord.collectionId === '') {
				that.$q.notify({
					type : 'negative',
					message : '请选择优先购的艺术品'
				});
				return;
			}
			if (selectedRecord.preMinute === null || selectedRecord.preMinute === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入优先时间(分钟)'
				});
				return;
			}
			if (!selectedRecord.executeImmediatelyFlag) {
				if (selectedRecord.executeTime === null || selectedRecord.executeTime === '') {
					that.$q.notify({
						type : 'negative',
						message : '请输入执行时间'
					});
					return;
				}
			}
			var effectiveConditions = that.getEffectiveCondition();
			if (that.conditionErrorFalg) {
				return;
			}
			selectedRecord.preSaleConditions = effectiveConditions;
			that.$http.post('/preSale/addPreSaleTask', selectedRecord, {}).then(function(res) {
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
			this.selectedRecord = {
				taskName : '',
				collectionId : '',
				preMinute : '',
				executeTime : '',
				executeImmediatelyFlag : false,
			};
			this.conditions = [];
			this.memberSnapshots = [];
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
			that.$http.get('/preSale/findPreSaleTaskByPage', {
				params : {
					pageSize : param.pagination.rowsPerPage,
					pageNum : param.pagination.page,
					taskName : that.taskName,
					collectionName : that.collectionName,
					state : that.taskState
				}
			}).then(function(res) {
				var data = res.body.data;
				that.tablePagination.rowsNumber = data.total;
				that.tableData = data.content;
				that.tablePagination.page = param.pagination.page;
				that.tablePagination.rowsPerPage = param.pagination.rowsPerPage;
				that.tableDataLoading = false;
			});
		},

		refreshTable2 : function() {
			this.loadTableData2({
				pagination : {
					page : 1,
					rowsPerPage : this.tablePagination.rowsPerPage,
					rowsNumber : 10
				}
			});
		},

		loadTableData2 : function(param) {
			var that = this;
			that.tableDataLoading = true;
			that.$http.get('/preSale/findPreSaleQualifyByPage', {
				params : {
					pageSize : param.pagination.rowsPerPage,
					pageNum : param.pagination.page,
					bizType : that.bizType,
					preSaleTaskId : that.preSaleTaskId,
					memberMobile : that.memberMobile,
					collectionName : that.collectionName,
					state : that.recordState
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