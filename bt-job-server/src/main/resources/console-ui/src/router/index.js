import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    name: 'home',
    path: '/',
    meta: {
      title: '首页'
    }
  },
  {
    name: 'distribute-info',
    path: '/distribute/info',
    component: () => import('../views/distribute/info.vue'),
    meta: {
      title: '集群信息'
    }
  },
  {
    name: 'user-manager',
    path: '/user/manager',
    component: () => import('@/views/user/manager.vue'),
    meta: {
      title: '管理任务'
    }
  },
  {
    name: 'task-new',
    path: '/task/new',
    component: () => import('@/views/task/task.vue'),
    meta: {
      title: '管理任务'
    }
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

export default router
