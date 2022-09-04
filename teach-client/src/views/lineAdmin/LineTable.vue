<template>
  <Navi/>
  <el-form :inline="true" :data="form">
    <el-form-item label="路线编号或名称">
      <el-input placeholder="请输入路线编号或名称" v-model="form.lineId" size="small"/>
    </el-form-item>
    <el-form-item>
      <el-button size="small" type="primary" @click="doQuery">查询</el-button>
    </el-form-item>
    <el-form-item>
      <el-button size="small" type="success" @click="doCreate">新建</el-button>
    </el-form-item>
  </el-form>

  <el-table :data="dataList" style="width: 80%;margin-top: 5px;margin-left: 10%;" v-loading="loading">
    <el-table-column prop="id" label="编号" sortable width="250"/>
    <el-table-column prop="name" label="名称" sortable width="250"/>
    <el-table-column prop="train" label="承运车辆" sortable width="250"/>
    <el-table-column label="是否常设" width="100">
      <template #default="scope">
        <el-tag size="small" type="danger" v-show="!scope.row.regular">否</el-tag>
        <el-tag size="small" type="success" v-show="scope.row.regular">是</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="经停" width="80">
      <template #default="scope">
        <el-popover placement="left" :width="400" trigger="hover">
          <template #reference>
            <el-button size="mini">经停</el-button>
          </template>
          <el-table :data="scope.row.stoppings" size="mini">
            <el-table-column label="车站" prop="station" width="100"/>
            <el-table-column label="到站(分)" prop="arrival" width="100"/>
            <el-table-column label="经停(分)" prop="stay" width="100"/>
            <el-table-column label="价格" prop="constant" width="100"/>
          </el-table>
        </el-popover>
      </template>
    </el-table-column>

    <el-table-column label="发车" width="80">
      <template #default="scope">
        <el-popover placement="left" :width="500" trigger="hover">
          <template #reference>
            <el-button size="mini">发车</el-button>
          </template>
          <el-table :data="scope.row.departures" size="mini">
            <el-table-column label="司机" prop="driver" width="100"/>
            <el-table-column label="开点" prop="startTime" width="100"/>
            <el-table-column label="编排" width="300">
              <template #default="item">
                <el-tag size="small" type="success" v-show="(item.row.schedule&1)>0">节假</el-tag>
                <el-tag size="small" type="primary" v-show="(item.row.schedule&2)>0">周末</el-tag>
                <el-tag size="small" type="warning" v-show="(item.row.schedule&4)>0">工作日</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-popover>
      </template>
    </el-table-column>

    <el-table-column label="计划外" width="80">
      <template #default="scope">
        <el-popover placement="left" :width="200" trigger="hover">
          <template #reference>
            <el-button size="mini">{{scope.row.regular?"停开":"发车"}}</el-button>
          </template>
          <el-table :data="scope.row.runDates" size="mini">
            <el-table-column label="起始" prop="start" width="100"/>
            <el-table-column label="终止" prop="end" width="100"/>
          </el-table>
        </el-popover>
      </template>
    </el-table-column>

    <el-table-column label="价格修正" width="80">
      <template #default="scope">
        <el-popover placement="left" :width="700" trigger="hover">
          <template #reference>
            <el-button size="mini">修正</el-button>
          </template>
          <el-table :data="scope.row.prices" size="mini">
            <el-table-column label="采用日期" prop="updated" width="100"/>
            <el-table-column label="常数" prop="constant" width="100"/>
            <el-table-column label="倍率" prop="power" width="100"/>
            <el-table-column label="常数" prop="constant" width="100"/>
            <el-table-column label="日中安排" prop="inDay" width="100"/>
            <el-table-column label="周中安排" prop="inWeek" width="100"/>
            <el-table-column label="节假日" prop="holiday" width="100"/>
          </el-table>
        </el-popover>
      </template>
    </el-table-column>


    <el-table-column label="编辑" >
      <template #default="scope">
        <el-button size="small" @click="doEditStopping(scope.row.id)" type="primary" plain>基本与停站</el-button>
        <el-button size="small" @click="doEditDeparture(scope.row.id)" type="success" plain>发车</el-button>
        <el-button size="small" @click="doEditPrice(scope.row.id)" type="warning" plain>价格</el-button>
      </template>
    </el-table-column>
  </el-table>

</template>

<script>
import Navi from '@/components/Navi'
import {getLineTable} from "@/service/genServ";
export default {
  name: "LineTable",
  components:{Navi},
  data(){
    return{
      form:{
        lineId:"",
      },
      dataList:[],
      loading:false
    }
  },
  created() {
    this.doQuery()
  },
  methods:{
    doQuery(){
      this.loading=true;
      getLineTable({'lineId':this.form.lineId}).then(res=>{this.dataList=res.data;this.loading=false;});
    },
    doCreate(){
      this.$router.push('/LineEditStopping');
    },
    doEditStopping(lineId){
      this.$router.push({path:'/LineEditStopping',query:{lineId:lineId}});
    },
    doEditDeparture(lineId){
      this.$router.push({path:'/LineEditDeparture',query:{lineId:lineId}});
    },
    doEditPrice(lineId){
      this.$router.push({path:'/LineEditPrice',query:{lineId:lineId}});
    },
  }
}
</script>

<style scoped>

</style>
