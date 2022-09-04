<template>
  <Navi/>
  <el-form :data="form" style="width: 90%;margin-left: 5%;margin-top: 5px">
    <el-form-item label="编号">
      <el-input v-model="trainId" style="width: 80%" :disabled="!isNew"/>
    </el-form-item>
    <el-form-item label="名称">
      <el-input v-model="form.name" style="width: 80%" />
    </el-form-item>
    <el-form-item label="车辆类型">
      <el-select style="width: 40%;"  v-model="form.trainTypeId" filterable placeholder="选择车辆类型">
        <el-option
            v-for="item in trainTypes"
            :key="item.id"
            :label="item.name+'  '+item.id"
            :value="item.id">
          <span style="float: left">{{ item.name }}</span>
          <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="最大速度(km/h)">
      <el-input-number v-model="form.speed" :min="1" :max="5000"  />
    </el-form-item>

    <el-form v-for="(coach,index) in form.coaches" v-bind:key="coach" :data="coach">
      <el-divider/>
      <el-form-item label="车厢">
        <el-select style="width: 40%;"  v-model="coach.coachId" filterable placeholder="选择车厢">
          <el-option
              v-for="item in coaches"
              :key="item.id"
              :label="item.name+'  '+item.id"
              :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="挂载位置">
        <el-checkbox-group v-model="coach.position" size="small">
          <el-checkbox-button v-for="o in 29" v-bind:key="o" :label="o"> {{ o}}</el-checkbox-button>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item>
        <el-button type="danger" @click="deleteCoaches(index)">删除车厢</el-button>
      </el-form-item>
    </el-form>
    <el-divider/>
    <el-form-item>
      <el-button type="success" @click="addCoaches">添加车厢</el-button>
    </el-form-item>
    <el-divider/>
    <el-form-item>
      <el-button @click="submit">提交</el-button>
    </el-form-item>
  </el-form>

</template>

<script>
import Navi from '@/components/Navi'
import {getCoaches, getTrainInfo, getTrainTypes, submitTrain} from "@/service/genServ";
import {ElMessage} from "element-plus";
export default {
  name: "TrainEdit",
  components:{Navi},
  data(){
    return{
      trainId:this.$route.query.trainId,
      isNew:true,
      form:{
        id:"",
        name:"",
        speed:null,
        trainTypeId:null,
        coaches:[]
      },
      trainTypes:[],
      coaches:[]
    }
  },
  created() {
    if(this.$route.query.trainId!=null){
      this.isNew=false;
      this.doQuery();
    }
    getTrainTypes().then(res=>{this.trainTypes=res.data});
    getCoaches().then(res=>{this.coaches=res.data});
  },
  methods:{
    doQuery(){
      getTrainInfo({'trainId':this.trainId}).then(res=>{
        this.form=res.data
      })
    },
    addCoaches(){
      this.form.coaches[this.form.coaches.length]={};
      var cur=this.form.coaches.length-1;
      this.form.coaches[cur].position=[];
    },
    deleteCoaches(index){
      this.form.coaches.splice(index,1)
    },
    submit(){
      this.form.id=this.trainId; this.form.isNew=this.isNew;
      var idFlag=false, posFlag=false, posConflict=-1,idConflict=false;
      var coaches=this.form.coaches;
      for(var ii=0;ii<coaches.length;ii++)
        for(var jj=ii+1;jj<coaches.length;jj++)
          if(coaches[ii].coachId===coaches[jj].coachId)idConflict=true;
      var occupied=0;
      for(var i=0;i<coaches.length;i++){
        var coach=coaches[i];
        if(!coach.coachId)idFlag=true;
        if(coach.position.length===0)posFlag=true;
        for(var j=0;j<coach.position.length;j++)
          if((occupied&(1<<coach.position[j]))>0)posConflict=coach.position[j];
          else occupied|=(1<<coach.position[j]);
      }
      var flag=false;
      if(!this.trainId){flag=true; ElMessage.error('请输入编号')}
      if(!this.form.name){flag=true; ElMessage.error('请输入名称')}
      if(!this.form.trainTypeId){flag=true; ElMessage.error('请选择车辆类型')}
      if(!this.form.speed){flag=true; ElMessage.error('请输入最大时速')}
      if(idConflict){flag=true; ElMessage.error('每种车厢只能挂载一次（可挂载在多个位置）')}
      if(idFlag){flag=true; ElMessage.error('请选择所有车厢类型')}
      if(posFlag){flag=true; ElMessage.error('请为所有车厢至少选择一个挂载位置')}
      if(posConflict!==-1){flag=true; ElMessage.error('挂载位置在'+posConflict+'处冲突')}
      if(!flag){
        submitTrain({'form':this.form}).then(res=>{
          if(res.code!=='0')ElMessage.error(res.msg);
          else this.$router.go(-1)
        })
      }
    }
  }


}
</script>

<style scoped>

</style>
