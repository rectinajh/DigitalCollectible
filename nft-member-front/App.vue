<script>
	export default {
		globalData: {},
		onLaunch() {
		},
		onShow() {
			// #ifdef APP-PLUS
			// var args = plus.runtime.arguments;
			// if (args) {
			// 	setTimeout(function() {
			// 		uni.navigateTo({
			// 			url: '../transfer/transfer?c2bOrderNo=' + args.split('://')[1]
			// 		});
			// 	}, 1400);
			// }
			// #endif
		},
		onHide() {
			// #ifdef APP-PLUS
			// plus.runtime.arguments = '';
			// #endif
		}
	}
</script>

<style lang="scss">
	@import "uview-ui/index.scss";
	@import '/common/css/common.scss';
	@font-face {
		font-family: OPPOSans-M;
		src: url('@/static/fonts/OPPOSans-M.ttf');
	}
	@font-face {
		font-family: OPPOSans-L;
		src: url('@/static/fonts/OPPOSans-L.ttf');
	}
	page{
		background-color: #0C0C0D;
		color: #fff;
	}
</style>
