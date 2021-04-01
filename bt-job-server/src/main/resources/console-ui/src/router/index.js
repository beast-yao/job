import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    name: 'home',
    path: '/'
  },
  {
    name: 'distribute-info',
    path: '/distribute/info',
    component: () => import('../views/distribute/info.vue')
  },
  {
    name: 'user-manager',
    path: '/user/manager',
    component: () => import('@/views/user/manager.vue')
  },
  {
    name: 'task-new',
    path: '/task/new',
    component: () => import('@/views/task/task.vue')
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

export default router
