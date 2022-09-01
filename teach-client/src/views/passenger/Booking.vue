<template>
  <Navi/>
  <el-form :inline="true">
    <el-row :gutter="10">
      <el-col :span="7">
        <div style="margin-top: 25px">
          <el-label>出发：</el-label>
          <el-radio-group v-model="fromForm.fromRange" size="small" @change="clearFromForm">
            <el-radio-button label="city" >城市</el-radio-button>
            <el-radio-button label="station">车站</el-radio-button>
          </el-radio-group>
          <h3></h3>
          <el-select style="width: 60%" filterable v-model="fromForm.fromId"
                     remote
                     reserve-keyword
                     :placeholder="fromForm.fromRange==='city'?'请输入城市名':'请输入车站名'"
                     :remote-method="fromForm.fromRange==='city'?searchCities:searchStations"
                     :loading="loading">
            <el-option
                v-for="item in results"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.pos }}</span>
            </el-option>
          </el-select>
        </div>
      </el-col>
      <el-col :span="7">
        <div style="margin-top: 25px">
          <el-label>到达：</el-label>
          <el-radio-group v-model="toForm.toRange" size="small" @change="clearToForm">
            <el-radio-button label="city" >城市</el-radio-button>
            <el-radio-button label="station">车站</el-radio-button>
          </el-radio-group>
          <h3></h3>
          <el-select style="width: 60%" filterable v-model="toForm.toId"
                     remote
                     reserve-keyword
                     :placeholder="toForm.toRange==='city'?'请输入城市名':'请输入车站名'"
                     :remote-method="toForm.toRange==='city'?searchCitiesTo:searchStationsTo"
                     :loading="loading">
            <el-option
                v-for="item in resultsTo"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            >
              <span style="float: left">{{ item.name }}</span>
              <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.pos }}</span>
            </el-option>
          </el-select>
        </div>
      </el-col>
      <el-col :span="10">
        <div style="margin-top: 25px">
          <el-row :gutter="3">
            <el-col :span="8">
              <el-container>
                <el-header :height="80"><el-label >最大换乘次数：</el-label></el-header>
                <el-main><el-slider style="width: 200px;margin-top:0" v-model="maxTransfer" :max="3" show-input/></el-main>
              </el-container>
            </el-col>
            <el-col :span="8">
              <el-header :height="80"><el-label >允许市内换乘：</el-label></el-header>
              <el-main><el-switch v-model="transferInCity"  /></el-main>
            </el-col>
            <el-col :span="8">
              <el-header :height="80"><el-label >策略：</el-label></el-header>
              <el-main>
              <el-radio-group v-model="priority">
                <el-radio label="time" border>时间短</el-radio>
                <el-radio label="money" border>花费少</el-radio>
              </el-radio-group>
              </el-main>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="14">
              <el-label>出发日期：</el-label>
              <el-date-picker
                  style="width: 40%;background-color: #f4f4f5"
                  v-model="startDate"
                  type="date"
                  placeholder="请选择出发日期"/>
            </el-col>
            <el-col :span="10">
              <el-label>出发时间：</el-label>
              <el-select v-model="startTime" class="m-2" placeholder="选择出发时间" size="large">
                <el-option
                    v-for="o in 24"
                    :key="o-1"
                    :label="(o-1)+'时'"
                    :value="o-1"/>
              </el-select>
            </el-col>
          </el-row>
          <h3></h3>
          <el-button style="width: 50%;" type="primary" @click="doQuery" :disabled="loadingTable">查询车票</el-button>
        </div>
      </el-col>
    </el-row>
  </el-form>


  <el-table v-loading="loadingTable" class="table-content" :data="dataList" border style="width: 100%;"  height="1000">

    <el-table-column label="序号" fixed="left" width="50" align="center" color="black">
      <template v-slot="scope">
        {{ scope.$index+1 }}
      </template>
    </el-table-column><!--第一列-->

    <el-table-column label="出发时间" align="center" color="black" sortable prop="start" width="120" /><!--courseNum-->
    <el-table-column label="历时" align="center" color="black" sortable prop="duration" width="120"/><!--courseName-->
    <el-table-column label="到达时间" align="center" color="black" sortable prop="end" width="120"/>
    <el-table-column label="路线" align="center" color="black" sortable prop="name" />
    <el-table-column prop="tag" label="车辆" :filters="typeList" :filter-method="filterTag" width="240" filter-placement="bottom-end">
      <template #default="scope">
        <el-tag :type="scope.row.type === '普通客运火车' ? '' : 'success'" disable-transitions>{{ scope.row.type }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column type="expand" >
      <template #default="prop">

        <el-table :data="prop.row.lines" border size="mini">
          <el-table-column label="路线" prop="lineName" width="150"/>
          <el-table-column label="出发日期" prop="startDate" width="150"/>
          <el-table-column label="出发时间" prop="startTime" width="100"/>
          <el-table-column label="出发站" prop="startStation" width="100"/>
          <el-table-column label="到达时间" prop="endTime" width="100"/>
          <el-table-column label="到达站" prop="endStation" width="100"/>
          <el-table-column label="经停" width="80">
            <template #default="vias">
              <el-popover placement="left" :width="300" trigger="hover">
                <template #reference>
                  <el-button size="mini">查看</el-button>
                </template>
                <el-table :data="vias.row.via" size="mini">
                  <el-table-column label="车站" prop="name" width="100"/>
                  <el-table-column label="到站" prop="arrival" width="100"/>
                  <el-table-column label="经停" prop="stay" width="100"/>
                </el-table>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column label="余票">
            <template #default="theLine">
              <el-button size="mini" v-for="item in theLine.row.seats" v-bind:key="item">
                {{item.name+" 剩余:"+item.rest+"  价格："+item.price+item.currency}}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="width: 20%;margin: auto"><el-button type="success" style="width: 80%;" @click="goPay(prop.row.routeId)">下单</el-button></div>

      </template>
    </el-table-column><!--编辑删除按钮-->
  </el-table>



</template>

<script>
import Navi from '@/components/Navi'
import {ref} from "vue";
import {getStationWithPos,getCityWithPos,queryRoute,getTravel} from "@/service/genServ";
import {ElMessage} from "element-plus";
export default {
  name: "Booking",
  components:{Navi},
  data(){
    return{
      fromForm:{
        fromRange:"city",
        fromId:null
      },
      toForm: {
        toRange: "city",
        toId: null,
      },
      loading:ref(false),
      results:[],
      resultsTo:[],
      startDate:null,
      startTime:0,
      maxTransfer:0,
      transferInCity:false,
      priority:"time",
      dataList:[],
      typeList:[],
      loadingTable:false
    }
  },
  methods:{
    goPay(routeId){
      getTravel({'routeId':routeId}).then(res=>{
        this.$router.push({path:'/EditTravel',query:{travelId:res.data.travelId}})
      })
    },
    filterTag(value,row){
      return row.type===value;
    },
    clearFromForm(value){
      console.log(value)
      this.fromForm.fromId=null;
      this.results=[];
    },
    searchCities(query){
      if(query){
        this.loading=ref(true)
        setTimeout(() => {
          this.loading=ref(false)
          getCityWithPos({'name':query}).then(res=>{
            this.results=res.data
          })
        }, 200)
      }else this.results=[];
    },
    searchStations(query){
      if(query){
        this.loading=ref(true)
        setTimeout(() => {
          this.loading=ref(false)
          getStationWithPos({'name':query}).then(res=>{
            this.results=res.data
          })
        }, 200)
      }else this.results=[];
    },
    clearToForm(value){
      console.log(value)
      this.toForm.toId=null;
      this.resultsTo=[];
    },
    searchCitiesTo(query){
      if(query){
        this.loading=ref(true)
        setTimeout(() => {
          this.loading=ref(false)
          getCityWithPos({'name':query}).then(res=>{
            this.resultsTo=res.data
          })
        }, 200)
      }else this.resultsTo=[];
    },
    searchStationsTo(query){
      if(query){
        this.loading=ref(true)
        setTimeout(() => {
          this.loading=ref(false)
          getStationWithPos({'name':query}).then(res=>{
            this.resultsTo=res.data
          })
        }, 200)
      }else this.resultsTo=[];
    },
    doQuery(){
      this.loadingTable=true;
      queryRoute({
        'fromRange':this.fromForm.fromRange,
        'toRange':this.toForm.toRange,
        'fromId':this.fromForm.fromId,
        'toId':this.toForm.toId,
        'startDate':this.startDate,
        'maxTransfer':this.maxTransfer,
        'transferInCity':this.transferInCity,
        'priority':this.priority,
        'startTime':this.startTime}).then(res=>{
          this.loadingTable=false;
          if(res.code!=='0')
            ElMessage.error(res.msg);
          else {
          this.dataList = res.data.table;
          this.typeList=res.data.type;
          }
      })
    }
  }
}
</script>

<style scoped>
</style>
