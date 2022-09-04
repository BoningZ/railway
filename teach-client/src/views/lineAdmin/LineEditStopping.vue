<template>
  <Navi/>
  <el-form :data="form" style="width: 90%;margin-left: 5%;margin-top: 5px">
    <el-form-item label="编号">
      <el-input v-model="form.id" style="width: 80%" :disabled="!form.isNew"/>
    </el-form-item>
    <el-form-item label="名称">
      <el-input v-model="form.name" style="width: 80%" />
    </el-form-item>
    <el-form-item label="承运车辆">
      <el-select style="width: 75%" filterable v-model="form.trainId"
                 remote
                 reserve-keyword
                 placeholder="请输入火车名"
                 :remote-method="searchTrains">
        <el-option
            v-for="item in trains"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          <span style="float: left">{{ item.name }}</span>
          <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="更改调度">
      <el-switch v-model="form.rescheduled" />
    </el-form-item>
    <el-form-item label="是否常开">
      <el-switch v-model="form.regular" :disabled="!form.rescheduled"/>
    </el-form-item>

    <el-form v-for="(stopping,index) in form.stoppings" v-bind:key="stopping" :data="stopping">
      <el-divider/>
      <el-form-item label="车站">
        <el-select style="width: 75%" filterable v-model="stopping.stationId"
                   remote
                   reserve-keyword
                   placeholder="请输入车站名"
                   :remote-method="searchStations">
          <el-option
              v-for="item in stations"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="距发车运行时间(分)">
        <el-input-number v-model="stopping.arrival" :min="0" :max="50000" :disabled="!form.rescheduled" />
      </el-form-item>
      <el-form-item label="停留时长(分)">
        <el-input-number v-model="stopping.stay" :min="1" :max="50000" :disabled="!form.rescheduled" />
      </el-form-item>
      <el-form-item label="距发车站费用">
        <el-input v-model="stopping.constant" :disabled="!form.rescheduled"/>
      </el-form-item>
      <el-form-item>
        <el-button type="danger" @click="deleteItem(index)" v-show="form.isNew">删除经停</el-button>
      </el-form-item>
    </el-form>
    <el-divider/>
    <el-form-item>
      <el-button type="success" @click="addItem" v-show="form.isNew">添加经停</el-button>
    </el-form-item>
    <el-divider/>
    <el-form-item>
      <el-button @click="submit">提交</el-button>
    </el-form-item>
  </el-form>

</template>

<script>
import Navi from '@/components/Navi'
import {getLineInfo, getStationsLike, getTrainsLike, submitLineStopping} from "@/service/genServ";
import {ElMessage} from "element-plus";
export default {
  name: "LineEditStopping",
  components:{Navi},
  data(){
    return{
      form:{
        id:this.$route.query.lineId,
        name:null,
        isNew:true,
        rescheduled:false,
        regular:true,
        trainId:null,
        stoppings:[]
      },
      trains:[],
      stations:[]
    }
  },
  created() {
    if(this.$route.query.lineId!=null){
      this.form.isNew=false;
      this.doQuery();
    }else this.form.rescheduled=true;
  },
  methods:{
    doQuery(){
      getLineInfo({'lineId':this.form.id}).then(res=>this.form=res.data);
    },
    searchTrains(query){
      if(query){
        setTimeout(() => {
          getTrainsLike({'name':query}).then(res=>{this.trains=res.data})
        }, 200)
      }else this.trains=[];
    },
    searchStations(query){
      if(query){
        setTimeout(() => {
          getStationsLike({'name':query}).then(res=>{this.stations=res.data})
        }, 200)
      }else this.stations=[];
    },
    addItem(){
      this.form.stoppings[this.form.stoppings.length]={};
    },
    deleteItem(index){
      this.form.stoppings.splice(index,1)
    },
    submit(){
      if(!this.form.id||!this.form.name||!this.form.trainId){ElMessage.error("请输入编号名称与车辆");return;}
      var stps=this.form.stoppings,curArrive=0,curConst=0;
      for(var i=0;i<stps.length;i++){
        var stp=stps[i];
        if(!stp.stationId||(!stp.arrival&&stp.arrival!==0)||!stp.stay||(!stp.constant&&stp.constant!==0)){ElMessage.error("请完善经停信息");return;}
        if(stp.arrival<curArrive){ElMessage.error("到达时间须递增");return;}
        if(isNaN(stp.constant)){ElMessage.error("费用须为数字");return;}
        this.form.stoppings[i].constant=Number(this.form.stoppings[i].constant)
        if(stp.constant<curConst){ElMessage.error("费用须递增");return;}
        curConst=stp.constant; curArrive=stp.arrival;
      }
      submitLineStopping({'form':this.form}).then(res=>{
        if(res.code!=='0')ElMessage.error(res.msg);
        else this.$router.go(-1)
      })
    }
  }
}
</script>

<style scoped>

</style>
