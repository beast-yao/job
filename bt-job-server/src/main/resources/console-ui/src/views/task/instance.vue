<template>
    <el-container>
      <el-header class="header">
        <span class="title">任务执行记录</span>
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
            </el-col>
          </el-row>
        </el-header>
        <el-main>
          <el-table :data="data" v-loading="loading" max-height="580px" :cell-style="{'text-align':'center'}" :header-cell-style="{'text-align':'center','background-color':'#f5f7fa'}" >
            <template slot="empty">
              <span>暂无任务信息</span>
            </template>
            <el-table-column prop="taskName" label="任务名" min-width="120" />
            <el-table-column prop="des" label="任务描述" min-width="120" >
              <template slot-scope="scope">
                <el-tooltip :content="scope.row.des" placement="bottom">
                  <p>{{scope.row.des}}</p>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column prop="appName" label="服务名" min-width="120" />
            <el-table-column prop="serveHost" label="当前触发server" min-width="100" />
            <el-table-column prop="executeType" label="执行类型" min-width="100" />
            <el-table-column prop="timeType" label="触发方式" min-width="100" />
            <el-table-column prop="timeVal" label="时间表达式" min-width="100" />
            <el-table-column prop="executeStatue" label="执行状态" min-width="100" />
            <el-table-column prop="taskType" label="任务类型" min-width="100" />
            <el-table-column prop="exceptTriggerTime" label="期望触发时间" min-width="150" />
            <el-table-column prop="triggerTime" label="实际触发时间" min-width="150" />
            <el-table-column prop="executeEndTime" label="执行结束时间" min-width="150" />
            <el-table-column fixed="right" label="操作">
              <template slot-scope="scope">
                <el-button type="text" size="mini" @click="showLogView(scope.row)">查看日志</el-button>
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
       <Log :instanceId="instanceId" :visable="showLog"></Log>
    </el-container>
</template>
<script>
import Log from './instance-log';
export default {
  data () {
      return {
          params: {},
          data: [],
          loading: false,
          page: 1,
          pageSize: 10,
          total: 0,
          instanceId: '',
          showLog: false
      }
  },
  components: { Log },
  mounted () {
    this.search();
  },
  watch: {
    '$route' () {
      this.page = 1;
      this.pageSize = 10;
      this.search();
    }
  },
  methods: {
    search () {
      this.loading = true;
      this.$api.getTaskInstance({ ...this.params, current: this.page - 1, pageSize: this.pageSize, taskId: this.$route.query.taskId }).then((result) => {
        this.total = result.data.total;
        this.data = result.data.data;
      }).catch((err) => {
        console.log(err)
      }).finally(() => {
        this.loading = false;
      });
    },
    reload () {
      this.page = 1;
      this.search();
    },
    handleCurrentChange(val) {
      this.search();
    },
    handleSizeChange (val) {
      this.page = 0;
      this.search();
    },
    showLogView (row) {
      console.table(row)
      this.instanceId = row.instanceId;
      this.showLog = true;
    }
  }
}
</script>

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
