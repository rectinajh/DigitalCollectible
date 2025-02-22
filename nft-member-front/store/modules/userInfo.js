export default {
	state: {
		userInfo:null
	},
	getters: {
		userInfo(state){
			return state.userInfo
		}
	},
	mutations: {
		updateUserInfo(state, userInfo){
			state.userInfo = userInfo
		},
		logout(state){
			state.userInfo = null
		}
	},
	actions: {
		updateUserInfo({commit}, userInfo){
			commit('updateUserInfo', userInfo)
		},
		logout({commit}){
			commit('logout')
		}
	}
}