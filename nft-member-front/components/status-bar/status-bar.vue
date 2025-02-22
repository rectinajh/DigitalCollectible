<template>
	<view>
		<view class="status" :style="{backgroundColor:statusColor}"></view>
		<view class="fixed" :style="{backgroundColor:statusColor}"></view>
	</view>
	
</template>

<script>
	export default {
		name:"status-bar",
		props:{
			statusColor:{
				type:String,
				default:"#000"
			},
		},
		data() {
			return {
				
			};
		}
	}
</script>

<style lang="scss" scoped>
	.status{
		height: var(--status-bar-height);
		width: 100%;
	}
	.fixed{
		position: fixed;
		top: 0;
		left: 0;
		width: 100%;
		height:var(--status-bar-height);;
		z-index: 99999;
	}
</style>