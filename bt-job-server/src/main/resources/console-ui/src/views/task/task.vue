<template>
  <div>
    <el-container>
      <el-header class="header">
        <span class="title">任务管理</span>
      </el-header>
       <el-container>
         <el-header>
          <el-row :gutter="24" type="flex">
            <el-col :span="8">
              <div class="label_content">
                <span>任务名</span>
                <el-input placeholder="请输入任务名" v-model="params.taskName" maxlength="25" show-word-limit clearable/>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="label_content">
                <span>服务名</span>
                <el-input placeholder="请输入服务名" v-model="params.appName" maxlength="25" show-word-limit clearable/>
              </div>
            </el-col>
            <el-col :span="8">
              <el-button type="primary" @click="search" :loading="loading" icon="el-icon-search">查询</el-button>
              <el-button type="info" icon="el-icon-circle-plus-outline">增加任务</el-button>
            </el-col>
          </el-row>
        </el-header>
        <el-main>
          <el-table :data="data" v-loading="loading" :cell-style="{'text-align':'center'}" :header-cell-style="{'text-align':'center','background-color':'#f5f7fa'}" >
            <template slot="empty">
              <span>暂无任务信息</span>
            </template>
            <el-table-column prop="taskName" label="任务名" min-width="150" />
            <el-table-column prop="desc" label="任务描述" min-width="150" />
            <el-table-column prop="appName" label="服务名" min-width="150" />
            <el-table-column prop="serveHost" label="当前触发server" min-width="150" />
            <el-table-column prop="executeType" label="执行类型" min-width="120" />
            <el-table-column prop="timeType" label="触发方式" min-width="150" />
            <el-table-column prop="timeVal" label="时间表达式" min-width="120" />
            <el-table-column prop="lastTriggerTime" label="最后触发时间" min-width="150" />
            <el-table-column prop="nextTriggerTime" label="下次触发时间" min-width="150" />
            <el-table-column prop="crt" label="创建时间" min-width="150" />
            <el-table-column>
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="taskInstance(scope.row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination :hide-on-single-page="true"  background :page-size.sync="pageSize"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
          :page-sizes="[10, 20, 50]"
          :disabled="loading"
          :current-page.sync="page" :total="total" layout="total, sizes,prev, pager, next"/>
        </el-main>
       </el-container>
    </el-container>
  </div>
</template>

<style scope>
 .label_content{
   width: 80px;
   display: flex;
   width: 100%;
   align-items: center;
 }
 .label_content>span{
   flex: 1;
   text-align: right;
   margin-right: 10px;
 }
 .label_content .el-input{
   flex: 3.5;
 }
</style>

<script>
export default {
  data() {
    return {
      params: {},
      data: [],
      loading: false,
      page: 0,
      pageSize: 10,
      total: 0
    }
  },
  mounted () {
    this.search();
  },
  methods: {
    search () {
      this.loading = true;
      this.$api.getTask({ ...this.params, current: this.page - 1, pageSize: this.pageSize }).then((result) => {
        this.total = result.data.total;
        this.data = result.data.data;
      }).catch((err) => {
        console.log(err)
      }).finally(() => {
        this.loading = false;
      });
    },
    handleCurrentChange(val) {
      this.search();
    },
    handleSizeChange (val) {
      this.page = 0;
      this.search();
    },
    taskInstance (task) {
      console.log(task)
    }
  }
}
</script>
