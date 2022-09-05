<template>
  <Navi/>
  <el-form :inline="true" :data="form" style="margin-top: 5px;">
    <el-form-item label="车次编号或名称">
      <el-input placeholder="请输入车次编号或名称" v-model="form.lineId" size="small"/>
    </el-form-item>
    <el-form-item label="日期范围">
      <el-date-picker
                      v-model="form.dateRange" type="daterange"
                      range-separator="至"
                      start-placeholder="开始时间"
                      end-placeholder="结束时间"/>
    </el-form-item>
    <el-form-item>
      <el-button size="small" type="primary" @click="doQuery">查询</el-button>
    </el-form-item>
  </el-form>

  <el-table :data="dataList" style="width: 90%;margin-top: 5px;margin-left: 5%;">
    <el-table-column prop="id" label="编号" width="300"/>
    <el-table-column prop="name" label="名称" width="300"/>
    <el-table-column prop="count" label="售出总数" />
    <el-table-column label="售出细节" >
      <template #default="scope">
        <el-popover placement="left" :width="200" trigger="hover">
          <template #reference>
            <el-button size="mini">售出</el-button>
          </template>
          <el-table :data="scope.row.seatCounts" size="mini">
            <el-table-column label="坐席" prop="name" width="100"/>
            <el-table-column label="张数" prop="num" width="100"/>
          </el-table>
        </el-popover>
      </template>
    </el-table-column>
    <el-table-column prop="price" label="总营业额" />
    <el-table-column label="营业额细节" >
      <template #default="scope">
        <el-popover placement="left" :width="200" trigger="hover">
          <template #reference>
            <el-button size="mini">营业额</el-button>
          </template>
          <el-table :data="scope.row.seatPrices" size="mini">
            <el-table-column label="坐席" prop="name" width="100"/>
            <el-table-column label="营业额" prop="num" width="100"/>
          </el-table>
        </el-popover>
      </template>
    </el-table-column>
    <el-table-column type="expand" >
      <template #default="scope">
        <el-table :data="scope.row.details" style="width: 90%;margin-top: 5px;margin-left: 5%;">
          <el-table-column prop="startDate" label="发车日期"/>
          <el-table-column prop="startTime" label="发车时间"/>
          <el-table-column type="expand" >
            <template #default="scope2">
              <div>
                <div :id="scope2.row.departureId+scope2.row.startTime"/>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-table-column>
  </el-table>

</template>

<script>
import Navi from '@/components/Navi'
import echarts from "echarts";
import {getSalesStat} from "@/service/genServ";
export default {
  name:"SalesStat",
  components:{Navi},
  data(){
    return{
      form:{
        lineId:"",
        dateRange:[]
      },
      dataList:[]
    }
  },
  created() {
    this.doQuery()
  },
  methods:{
    doQuery(){
      getSalesStat({'lineId':this.form.lineId,'start':this.form.dateRange[0],'end':this.form.dateRange[1]}).then(res=>{
        this.dataList=res.data
        var list=this.dataList
        for(var i=0;i<list.length;i++){
          for(var j=0;j<list[i].details.length;j++){
            var cur=list[i].details[j];
            var chartDom= document.getElementById(cur.departureId+cur.startTime);
            var wrapper=[];
            for(var k=0;k<cur.seatDetail.length;k++)wrapper[k]={
              name: list[i].seats[k],type: 'line',stack: 'Total',areaStyle: {},
              emphasis: {focus: 'series'},
              data: cur.seatDetail[k]
            }
            var myChart=echarts.init(chartDom);
            var option = {
              title: {text: '单次发车统计'},
              tooltip: {trigger: 'axis',axisPointer: {type: 'cross',label: { backgroundColor: '#6a7985'}}},
              legend: {data: list[i].seats},
              toolbox: {feature: {saveAsImage: {}}},
              grid: {left: '3%',right: '4%',bottom: '3%',containLabel: true},
              xAxis: [{type: 'category',boundaryGap: false,data: list[i].stations}],
              yAxis: [{type: 'value'}],
              series: wrapper
            };
            option && myChart.setOption(option);
          }
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
