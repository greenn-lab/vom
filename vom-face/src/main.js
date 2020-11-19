import Vue from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'

import vuetify from './plugins/vuetify'
import 'roboto-fontface/css/roboto/roboto-fontface.css'
import '@mdi/font/css/materialdesignicons.css'
import '@kfonts/nanum-barun-gothic';

import Chart from 'chart.js'

Vue.config.productionTip = false

Chart.defaults.global.defaultFontFamily = '"Roboto", "나눔바른고딕", "nanum-barun-gothic"'

new Vue({
  router,
  vuetify,
  store,
  render: h => h(App)
}).$mount('#app')
