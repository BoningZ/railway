<template>
  <Navi/>
  <el-form :data="form" style="width: 90%;margin-left: 5%;margin-top: 5px">
    <el-form-item label="编号">
      <el-input v-model="form.id" style="width: 80%" :disabled="true"/>
    </el-form-item>
    <el-form-item label="名称">
      <el-input v-model="form.name" style="width: 80%" :disabled="true" />
    </el-form-item>
    <el-form-item label="更改调度">
      <el-switch v-model="form.rescheduled" />
    </el-form-item>


    <el-form v-for="(runDate,index) in form.runDates" v-bind:key="runDate" :data="runDate">
      <el-divider/>
      <el-form-item label="计划外时间范围">
        <el-date-picker :disabled="!form.rescheduled"
            v-model="runDate.dates" type="daterange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"/>
      </el-form-item>
      <el-form-item>
        <el-button type="danger" @click="deleteRunDate(index)" :disabled="!form.rescheduled">删除计划外</el-button>
      </el-form-item>
    </el-form>
    <el-divider/>
    <el-form-item>
      <el-button type="success" @click="addRunDate" :disabled="!form.rescheduled">添加计划外</el-button>
    </el-form-item>




    <el-divider/>
    <el-form v-for="departure in form.departures" v-bind:key="departure" :data="departure">
      <el-divider/>
      <el-form-item label="司机">
        <el-select style="width: 40%;"  v-model="departure.driverId" :disabled="!form.rescheduled" filterable placeholder="选择司机">
          <el-option
              v-for="item in drivers"
              :key="item.id"
              :label="item.name+'  '+item.id"
              :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="发车时间">
        <el-container>
          <el-input-number v-model="departure.startHour" :min="0" :max="23" :disabled="!form.rescheduled" />:
          <el-input-number v-model="departure.startMin" :min="0" :max="59" :disabled="!form.rescheduled" />
        </el-container>
      </el-form-item>
      <el-form-item>
        <el-checkbox-group v-model="departure.schedules" :disabled="!form.rescheduled">
          <el-checkbox :label="1" >节假</el-checkbox>
          <el-checkbox :label="2" >周末</el-checkbox>
          <el-checkbox :label="4" >工作日</el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>
    <el-divider/>
    <el-form-item>
      <el-button type="success" @click="addDeparture" :disabled="!form.rescheduled">添加发车</el-button>
    </el-form-item>
    <el-divider/>
    <el-form-item>
      <el-button @click="submit">提交</el-button>
    </el-form-item>
  </el-form>

</template>

<script>
import Navi from '@/components/Navi'
import {getDrivers, getLineInfo, submitLineDeparture,} from "@/service/genServ";
import {ElMessage} from "element-plus";
export default {
  name: "LineEditDeparture",
  components:{Navi},
  data(){
    return{
      form:{
        id:this.$route.query.lineId,
        name:null,
        rescheduled:false,
        departures:[],
        runDates:[]
      },
      drivers:[]
    }
  },
  created() {
    this.doQuery()
    getDrivers().then(res=>this.drivers=res.data)

  },
  methods:{
    doQuery(){
      getLineInfo({'lineId':this.form.id}).then(res=>{
            for(var i=0;i<res.data.runDates.length;i++){
              res.data.runDates[i].dates=[]
              res.data.runDates[i].dates[0]=res.data.runDates[i].start;
              res.data.runDates[i].dates[1]=res.data.runDates[i].end;
            }
            for(var j=0;j<res.data.departures.length;j++){
              res.data.departures[j].startHour=parseInt(10,(Number(res.data.departures[j].start)/60));
              res.data.departures[j].startMin=(Number(res.data.departures[j].start)%60);
              var s=0;
              res.data.departures[j].schedules=[]
              if((res.data.departures[j].schedule&1)>0)res.data.departures[j].schedules[s++]=1;
              if((res.data.departures[j].schedule&2)>0)res.data.departures[j].schedules[s++]=2;
              if((res.data.departures[j].schedule&4)>0)res.data.departures[j].schedules[s++]=4;
            }
        this.form=res.data
      });
    },
    addRunDate(){
      this.form.runDates[this.form.runDates.length]={};
    },
    deleteRunDate(index){
      this.form.runDates.splice(index,1)
    },
    addDeparture(){
      this.form.departures[this.form.departures.length]={};
      var cur=this.form.departures.length-1;
      this.form.departures[cur].schedules=[]
    },
    submit(){
      var runDates=this.form.runDates;
      for(var i=0;i<runDates.length;i++){
        if(!runDates[i].dates){ElMessage.error('请选择日期范围');return;}
        this.form.runDates[i].start=runDates[i].dates[0];
        this.form.runDates[i].end=runDates[i].dates[1];
      }
      var departures=this.form.departures;
      for(var j=0;j<departures.length;j++){
        var d=departures[j]
        if(!d.driverId||(!d.startHour&&d.startHour!==0)||(!d.startMin&&d.startMin!==0)||!d.schedules){ElMessage.error('请完善发车信息');return;}
        this.form.departures[j].start=d.startHour*60+d.startMin;
        var occupied=0;
        for(var k=0;k<d.schedules.length;k++)occupied|=d.schedules[k];
        this.form.departures[j].schedule=occupied;
      }
      submitLineDeparture({'form':this.form}).then(res=>{
        if(res.code!=='0')ElMessage.error(res.msg);
        else this.$router.go(-1);
      })
    }
  }
}
</script>

<style scoped>

</style>
