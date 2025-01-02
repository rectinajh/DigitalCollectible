Quasar.lang.set(Quasar.lang.zhHans)
var appVM = new Vue({
	el : '#q-app',
	data : function() {
		return {
			tabs : [ {
				name : 'system',
				label : '系统设置',
			}, {
				name : 'operation',
				label : '运营设置',
			}, {
				name : 'risk',
				label : '风控设置',
			}, {
				name : 'chain',
				label : '区块链',
			} ],
			tab : 'system',
			systemSetting : '',
			operationSetting : '',
			riskSetting : '',
			currentInUseChain : '',
			chainTypes : [ {
				name : '暂不上链',
				value : 'noneChain'
			}, {
				name : '文昌链',
				value : 'wenChangChain'
			} ],
			wenChangChainSetting : '',
			wenChangChainDialogFlag : false,
		}
	},
	watch : {
		tab : {
			handler : function(newVal, oldVal) {
				if (newVal == 'system') {
					this.getSystemSetting();
				} else if (newVal == 'chain') {
					this.getCurrentInUseChain();
				} else if (newVal == 'operation') {
					this.getOperationSetting();
				} else if (newVal == 'risk') {
					this.getRiskSetting();
				}
			},
			immediate : false,
			deep : true
		}
	},
	mounted : function() {
		this.getSystemSetting();
	},
	methods : {

		updateRiskSetting : function() {
			var that = this;
			var riskSetting = that.riskSetting;
			if (riskSetting.orderUnpaidTimeRange === null || riskSetting.orderUnpaidTimeRange === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入时间范围'
				});
				return;
			}
			if (riskSetting.orderUnpaidCount === null || riskSetting.orderUnpaidCount === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入下单未付款次数'
				});
				return;
			}
			if (riskSetting.orderUnpaidPunish === null || riskSetting.orderUnpaidPunish === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入限制下单分钟数'
				});
				return;
			}
			that.$http.post('/setting/updateRiskSetting', riskSetting, {
				emulateJSON : true
			}).then(function(res) {
				that.$q.notify({
					progress : true,
					timeout : 1000,
					type : 'positive',
					message : '操作成功',
				});
				that.getRiskSetting();
			});
		},

		getRiskSetting : function() {
			var that = this;
			that.$http.get('/setting/getRiskSetting').then(function(res) {
				that.riskSetting = res.body.data;
			});
		},

		updateOperationSetting : function() {
			var that = this;
			var operationSetting = that.operationSetting;
			if (operationSetting.primaryMarketFun === null || operationSetting.primaryMarketFun === '') {
				that.$q.notify({
					type : 'negative',
					message : '请设置是否开放一级市场'
				});
				return;
			}
			if (operationSetting.giveFun === null || operationSetting.giveFun === '') {
				that.$q.notify({
					type : 'negative',
					message : '请设置是否开放转赠功能'
				});
				return;
			}
			if (operationSetting.consignmentFun === null || operationSetting.consignmentFun === '') {
				that.$q.notify({
					type : 'negative',
					message : '请设置是否开放寄售功能'
				});
				return;
			}
			if (operationSetting.giveLimitHoldDay === null || operationSetting.giveLimitHoldDay === '') {
				that.$q.notify({
					type : 'negative',
					message : '请设置转赠限制(持有天数)'
				});
				return;
			}
			if (operationSetting.consignmentLimitHoldDay === null || operationSetting.consignmentLimitHoldDay === '') {
				that.$q.notify({
					type : 'negative',
					message : '请设置寄售限制(持有天数)'
				});
				return;
			}
			if (operationSetting.consignmentMinAmount === null || operationSetting.consignmentMinAmount === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入寄售限额'
				});
				return;
			}
			if (operationSetting.consignmentMaxAmount === null || operationSetting.consignmentMaxAmount === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入寄售限额'
				});
				return;
			}
			if (operationSetting.consignmentMinAmount > operationSetting.consignmentMaxAmount) {
				that.$q.notify({
					type : 'negative',
					message : '请输入寄售金额范围不正确'
				});
				return;
			}
			if (operationSetting.payOrderDeadline === null || operationSetting.payOrderDeadline === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入支付订单有效时长(分钟)'
				});
				return;
			}
			that.$http.post('/setting/updateOperationSetting', operationSetting, {
				emulateJSON : true
			}).then(function(res) {
				that.$q.notify({
					progress : true,
					timeout : 1000,
					type : 'positive',
					message : '操作成功',
				});
				that.getOperationSetting();
			});
		},

		getOperationSetting : function() {
			var that = this;
			that.$http.get('/setting/getOperationSetting').then(function(res) {
				that.operationSetting = res.body.data;
			});
		},

		showChainSettingDialg : function() {
			if (this.currentInUseChain == 'wenChangChain') {
				this.wenChangChainDialogFlag = true;
				this.getWenChangChainSetting();
			}
		},

		updateWenChangChainSetting : function() {
			var that = this;
			var wenChangChainSetting = that.wenChangChainSetting;
			if (wenChangChainSetting.apiGateway === null || wenChangChainSetting.apiGateway === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入API网关'
				});
				return;
			}
			if (wenChangChainSetting.apiKey === null || wenChangChainSetting.apiKey === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入API Key'
				});
				return;
			}
			if (wenChangChainSetting.apiSecret === null || wenChangChainSetting.apiSecret === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入API Secret'
				});
				return;
			}
			if (wenChangChainSetting.chainAddrSuper === null || wenChangChainSetting.chainAddrSuper === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入归集账号(区块链地址)'
				});
				return;
			}
			that.$http.post('/setting/updateWenChangChainSetting', wenChangChainSetting, {
				emulateJSON : true
			}).then(function(res) {
				that.$q.notify({
					progress : true,
					timeout : 1000,
					type : 'positive',
					message : '操作成功',
				});
				this.wenChangChainDialogFlag = false;
			});
		},

		getWenChangChainSetting : function() {
			var that = this;
			that.$http.get('/setting/getWenChangChainSetting').then(function(res) {
				that.wenChangChainSetting = res.body.data;
			});
		},

		updateCurrentInUseChain : function() {
			var that = this;
			if (that.currentInUseChain === null || that.currentInUseChain === '') {
				that.$q.notify({
					type : 'negative',
					message : '请选择要上的链'
				});
				return;
			}
			that.$http.post('/setting/updateCurrentInUseChain', {
				currentInUseChain : that.currentInUseChain
			}, {
				emulateJSON : true
			}).then(function(res) {
				that.$q.notify({
					progress : true,
					timeout : 1000,
					type : 'positive',
					message : '操作成功',
				});
				that.getCurrentInUseChain();
			});
		},

		getCurrentInUseChain : function() {
			var that = this;
			that.$http.get('/setting/getCurrentInUseChain').then(function(res) {
				that.currentInUseChain = res.body.data;
			});
		},

		updateSystemSetting : function() {
			var that = this;
			var systemSetting = that.systemSetting;
			if (systemSetting.appVersion === null || systemSetting.appVersion === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入APP最新版本号'
				});
				return;
			}
			if (systemSetting.appUrl === null || systemSetting.appUrl === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入APP下载地址'
				});
				return;
			}
			if (systemSetting.appSchema === null || systemSetting.appSchema === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入APP Schema'
				});
				return;
			}
			if (systemSetting.h5Gateway === null || systemSetting.h5Gateway === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入h5网关'
				});
				return;
			}
			if (systemSetting.localStoragePath === null || systemSetting.localStoragePath === '') {
				that.$q.notify({
					type : 'negative',
					message : '请输入图片资源存放目录'
				});
				return;
			}
			that.$http.post('/setting/updateSystemSetting', systemSetting, {
				emulateJSON : true
			}).then(function(res) {
				that.$q.notify({
					progress : true,
					timeout : 1000,
					type : 'positive',
					message : '操作成功',
				});
				that.getSystemSetting();
			});
		},

		getSystemSetting : function() {
			var that = this;
			that.$http.get('/setting/getSystemSetting').then(function(res) {
				that.systemSetting = res.body.data;
			});
		}

	},
});