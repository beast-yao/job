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
        <el-main class="main">
          <div class="border">
            <div class="collapse-icon" @click="collapsed">
              <i class="el-icon-s-unfold"/>
            </div>
           <el-tabs closable v-model="currentName">
            <el-tab-pane v-for="(item,index) in tabs" :label="item.label" :name="item.name" :key="index"></el-tab-pane>
          </el-tabs>
          </div>
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
        var t = this.tabs.find(e => e.name === route.meta.title);
        if (!t) {
          this.tabs.push({ label: route.meta.title, name: route.meta.title, path: route.path });
        }
        this.currentName = route.meta.title;
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
    height: calc(100% - 5vh);
    width: 14rem;
  }
  .border{
    height: 5vh;
    display: flex;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
  }
  .aside{
    height: 100vh;
    width: auto !important;
    /*background-color: #545c64;*/
  }
  .icon-title{
    height: 6vh;
    line-height: 6vh;
    font-family: 'Gabriola',serif;
    text-align: center;
    font-size: 4vh;
    font-weight: bolder;
    letter-spacing: 4px;
    color: #ffffff !important;
    cursor: pointer;
  }
  .collapse-icon{
    cursor: pointer;
    height: 100%;
    padding: 0;
    width: 5vh;
    text-align: center;
    font-size: 3vh;
    line-height: 5vh;
  }
  .main{
    height: 95vh;
    padding: 0 !important;
    margin-top: 10px;
  }
.el-tabs__nav-wrap::after {
    content: none !important;
}
.el-tabs__active-bar{
  display: none;
}
.el-tabs{
  margin-left: 20px;
}
.el-tabs__item{
  font-size: 16px !important;
}
</style>
