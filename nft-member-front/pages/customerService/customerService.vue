<template>
	<view>
		<web-view :src="customerServiceUrl"></web-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				customerServiceUrl : '',
			}
		},
		onLoad(option) {
			this.customerServiceUrl = option.customerServiceUrl;
		},
		methods: {
			
		}
	}
</script>

<style>

</style>
