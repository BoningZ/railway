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
          <el-label>出发日期：</el-label>
          <el-date-picker
              style="width: 40%;background-color: #f4f4f5"
              v-model="startDate"
              type="date"
              placeholder="请选择出发日期"/>
          <h3></h3>
          <el-button style="width: 50%;" type="primary" @click="doQuery">查询车票</el-button>
        </div>
      </el-col>
    </el-row>
  </el-form>

</template>

<script>
import Navi from '@/components/Navi'
import {ref} from "vue";
import {getStationWithPos,getCityWithPos} from "@/service/genServ";
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
      startDate:null
    }
  },
  methods:{
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

    }
  }
}
</script>

<style scoped>

</style>
