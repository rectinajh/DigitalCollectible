<template>
	<view>
		<web-view :src="link"></web-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				link: '',
			}
		},
		onLoad(option) {
			this.link = option.link;
		},
		methods: {

		}
	}
</script>

<style>

</style>
