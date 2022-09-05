<template>
  <Navi/>
  <el-table :data="dataList" style="width: 80%;margin-left: 10%">
    <el-table-column prop="date" sortable label="出发日期"/>
    <el-table-column prop="time" label="出发时间"/>
    <el-table-column prop="from" label="出发站"/>
    <el-table-column prop="to" label="到达站"/>
    <el-table-column label="查看">
      <template #default="scope">
        <el-button size="small" @click="goTravel(scope.row.id)">查看</el-button>
        <el-button size="small" type="primary" @click="trace(scope.row.id)">轨迹</el-button>
      </template>
    </el-table-column>
    <el-table-column type="expand">
      <template #default="props">
        <el-table :data="props.row.ticket" style="width: 60%;margin-left: 20%" border>
          <el-table-column prop="from" label="出发站"/>
          <el-table-column prop="to" label="到达站"/>
          <el-table-column label="状态">
            <template #default="props3">
              <el-tag size="large" type="danger" v-show="props3.row.status==='UNPAID'">未支付</el-tag>
              <el-tag size="large" type="info" v-show="props3.row.status==='QUEUEING'">候补中</el-tag>
              <el-tag size="large" type="success" v-show="props3.row.status==='SUCCEEDED'">成功</el-tag>
              <el-tag size="large" type="warning" v-show="props3.row.status==='CANCELED'">被取消</el-tag>
              <el-tag size="large" type="warning" v-show="props3.row.status==='REFUNDED'">已退款</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="座次">
            <template #default="props2">
              <el-tag size="large" v-show="props2.row.status==='SUCCEEDED'">{{props2.row.seat}}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-table-column>

  </el-table>

</template>

<script>

import Navi from '@/components/Navi'
import {getTravelList} from "@/service/genServ";
export default {
  name: "MyTravel",
  components:{Navi},
  data(){
    return{
      dataList:[]
    }
  },
  created() {
    this.doQuery();
  },
  methods:{
    doQuery(){
      getTravelList().then(res=>{
        this.dataList=res.data;
      })
    },
    goTravel(id){
      this.$router.push({path:'/EditTravel',query:{travelId:id}})
    },
    trace(id){
      this.$router.push({path:'/ShowTravel',query:{travelId:id}})
    }
  }
}
</script>

<style scoped>

</style>
