<template>
  <div id="app">
    <el-container>
      <el-aside class="aside">
        <el-menu
          text-color="#fff"
          background-color="#545c64"
          active-text-color="#ffd04b"
          :unique-opened="true"
          :collapse="collapse"
          :router="true">
          <router-link style="text-decoration: none" to="/">
            <div class="icon-title">
              <span>
                <i>BT-JOB</i>
              </span>
            </div>
          </router-link>
          <el-submenu index="1">
            <template slot="title">
              <i class="el-icon-cpu"></i>
              <span>任务管理</span>
            </template>
            <el-menu-item index="/task/new">
              <i class="el-icon-first-aid-kit"/>
              <span slot="title">管理任务</span>
            </el-menu-item>
          </el-submenu>

          <el-submenu index="2">
            <template slot="title">
              <i class="el-icon-connection"></i>
              <span>集群管理</span>
            </template>
            <el-menu-item index="/distribute/info">
              <i class="el-icon-link"/>
              <span slot="title">集群信息</span>
            </el-menu-item>
          </el-submenu>

          <el-submenu index="3">
            <template slot="title">
              <i class="el-icon-s-custom"></i>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/user/manager">
              <i class="el-icon-user"/>
              <span slot="title">用户信息管理</span>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="border">
          <div class="collapse-icon" @click="collapsed">
            <i class="el-icon-s-unfold"/>
          </div>
          <el-tabs style="width:100%;" closable v-model="currentName" @tab-click='clickTab' @tab-remove="tabRemove">
            <el-tab-pane v-for="(item,index) in tabs" :label="item.label" :name="item.name" :key="index"></el-tab-pane>
          </el-tabs>
        </el-header>
        <el-main class="main">
          <keep-alive>
            <router-view/>
          </keep-alive>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        collapse: false,
        tabs: [],
        currentName: ''
      }
    },
    methods: {
      collapsed() {
        this.collapse = !this.collapse;
      },
      addTab (route) {
        if (route.path === '/') {
           this.currentName = '';
          return;
        }
        this.tabs.forEach(item => {
          if (item.name === route.meta.title) {
            item.routeName = route.name;
            item.path = route.path;
          }
        })
        var t = this.tabs.find(e => e.name === route.meta.title);
        if (!t) {
          this.tabs.push({ label: route.meta.title, name: route.meta.title, path: route.path, routeName: route.name });
        }
        this.currentName = route.meta.title;
      },
      clickTab (tab, event) {
        var t = this.tabs.find(e => e.name === tab.name);
        if (t) {
          this.currentName = t.name;
          this.$router.push(t.path)
        }
      },
      tabRemove (name) {
        this.tabs = this.tabs.filter((item, index, arr) => {
          if (item.name === name) {
            var t = arr[index - 1] || arr[index + 1];
            if (t) {
              this.currentName = t.name;
              this.$router.push(t.path)
            } else {
              this.currentName = '';
              this.$router.push('/')
            }
          }
          return item.name !== name;
        })
      }
    },
    watch: {
      $route(to, from) {
        this.addTab(to)
      }
    }
  }
</script>

<style scope>
  #app {
    font-family: Avenir, Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
  }
  html,body,#app{
    height: auto;
    margin: 0;
    padding: 0;
  }
  .el-menu{
    height: calc(100% - 40px);
    width: 14rem;
  }
  .border{
    height: 40px !important;
    display: flex;
    padding-left: 0 !important;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
  }
  .aside{
    height: 100vh;
    width: auto !important;
    /*background-color: #545c64;*/
  }
  .icon-title{
    height: 40px;
    line-height: 40px;
    font-family: 'Gabriola',serif;
    text-align: center;
    font-size: 28px;
    font-weight: bolder;
    letter-spacing: 4px;
    color: #ffffff !important;
    cursor: pointer;
  }
  .collapse-icon{
    cursor: pointer;
    height: 40px;
    padding: 0;
    width: 40px;
    text-align: center;
    font-size: 20px;
    line-height: 40px;
  }
  .main{
    height: calc(100% - 40px);
    padding: 0 !important;
    margin-top: 10px;
  }
.el-tabs{
  margin-left: 20px;
}
.el-tabs__item{
  font-size: 15px !important;
  height: 40px !important;
  line-height: 40px !important;
}
.el-tabs__nav-wrap::after{
  content: none !important;
}
</style>
