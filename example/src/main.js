import Vue from "vue"
import App from "./App.vue"

Vue.config.productionTip = false

document.addEventListener("deviceready", () => {
    // alert('window.device:' + JSON.stringify(window.device))
    console.log("window.device:" + JSON.stringify(window.device))
})

new Vue({
    render: h => h(App)
}).$mount("#app")