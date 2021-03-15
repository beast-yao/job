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
        v-loading="loading"
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
          <el-table-column
            label="meta">
            <template slot-scope="scope">
              <json-viewer :value="scope.row.metaData" :expand-depth=0 :boxed="true" sort></json-viewer>
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
      data: [],
      loading: false
    }
  },
  mounted: function() {
    this.getServices();
  },
  methods: {
    getServices: function() {
      this.loading = true;
      this.$api.getServices().then(({ data: res }) => {
        console.log(res)
        this.data = res.data;
        // this.data = res.data.map(item => {
        //   if (item.metaData) {
        //     item.metaData = JSON.stringify(item.metaData)
        //   }
        //   return item;
        // });
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>
