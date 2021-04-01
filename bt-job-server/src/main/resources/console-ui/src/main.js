import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import * as api from './http/api'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import '@/assets/main.css'
import JsonViewer from 'vue-json-viewer'

Vue.config.productionTip = false
Vue.use(ElementUI);
Vue.use(JsonViewer);
Vue.prototype.$api = api

new Vue({
  router,
  store,
  render: function (h) { return h(App) }
}).$mount('#app')
