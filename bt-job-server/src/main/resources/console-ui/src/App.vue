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
            <el-menu-item index="1.1">
              <i class="el-icon-first-aid-kit"/>
              <span slot="title">新建任务</span>
            </el-menu-item>
            <el-menu-item index="1.2">
              <i class="el-icon-coordinate"/>
              <span slot="title">任务状态</span>
            </el-menu-item>
          </el-submenu>

          <el-submenu index="2">
            <template slot="title">
              <i class="el-icon-connection"></i>
              <span>集群管理</span>
            </template>
            <el-menu-item index="2.1">
              <i class="el-icon-link"/>
              <span slot="title">集群信息</span>
            </el-menu-item>
          </el-submenu>

          <el-submenu index="3">
            <template slot="title">
              <i class="el-icon-s-custom"></i>
              <span>用户管理</span>
            </template>
            <el-menu-item index="3.1">
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
        </el-header>
        <el-main style="height: 95vh;">
          <router-view/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        collapse: false
      }
    },
    methods: {
      collapsed() {
        this.collapse = !this.collapse;
      }
    }
  }
</script>

<style>
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
    height: 5vh !important;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
  }
  .aside{
    height: 100vh;
    width: auto !important;
    /*background-color: #545c64;*/
  }
  .icon-title{
    height: 5vh;
    line-height: 5vh;
    font-family: 'Gabriola',serif;
    text-align: center;
    font-size: 5vh;
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
    margin-left: -20px;
    line-height: 5vh;
  }

</style>
