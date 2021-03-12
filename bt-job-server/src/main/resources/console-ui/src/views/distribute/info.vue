<template>
  <div>
   <el-container>
     <el-header style="text-align: left;padding:0;height:5vh">
       <span class="title">集群信息</span>
     </el-header>
     <el-main>
       <el-table
       :header-cell-style="{background:'#eef1f6',color:'#606266'}"
        :data="data"
        style="width: 100%">
          <el-table-column
            prop="host"
            label="节点ip"
            >
          </el-table-column>
          <el-table-column
            prop="status"
            label="节点状态">
            <template slot-scope="scope">
               <el-tag type="success" :closable="false" effect="dark" size="medium" v-if="scope.row.status === 'UP'">{{scope.row.status}}</el-tag>
               <el-tag type="danger" :closable="false" effect="dark" size="medium" v-else>{{scope.row.status}}</el-tag>
            </template>
          </el-table-column>
       </el-table>
     </el-main>
   </el-container>
  </div>
</template>

<style>
  .title{
    font-size: 3vh;
    line-height: 5vh;
    font-family: 'Times New Roman', Times, serif;
  }
</style>

<script>
export default {
  data() {
    return {
      data: [
        {
          host: '127.0.0.1:10010',
          status: 'UP'
        },
        {
          host: '127.0.0.1:10010',
          status: 'DOWN'
        }
      ]
    }
  }
}
</script>
