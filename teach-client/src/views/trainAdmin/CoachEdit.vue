<template>
  <Navi/>
  <el-form :data="form" style="width: 90%;margin-left: 5%;margin-top: 5px">
    <el-form-item label="编号">
      <el-input v-model="coachId" style="width: 80%" :disabled="!isNew"/>
    </el-form-item>
    <el-form-item label="名称">
      <el-input v-model="form.name" style="width: 80%" />
    </el-form-item>
    <el-form-item label="是否制冷">
      <el-switch v-model="form.refrigerated" />
    </el-form-item>
    <el-form v-for="(seat,index) in form.seats" v-bind:key="seat" :data="seat">
      <el-divider/>
      <el-form-item label="坐席">
        <el-select style="width: 40%;"  v-model="seat.seatId" filterable placeholder="选择坐席">
          <el-option
              v-for="item in seats"
              :key="item.id"
              :label="item.name+'  '+item.id"
              :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-radio-group v-model="seat.type">
          <el-radio label="normal" size="large" border>一般席</el-radio>
          <el-radio label="order" size="large" border>顺序编号席</el-radio>
          <el-radio label="free" size="large" border>自由席</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="安装行" v-show="seat.type==='normal'">
        <el-checkbox-group v-model="seat.rows" size="small">
          <el-checkbox-button v-for="o in 29" v-bind:key="o" :label="o"> {{ o}}</el-checkbox-button>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="分组数" v-show="seat.type==='normal'">
        <el-input-number v-model="seat.numOfColGroups" :min="1" :max="5"  />
      </el-form-item>
      <el-form-item v-for="cnt in seat.numOfColGroups" :label="'第'+(cnt)+'组'" v-show="seat.type==='normal'" v-bind:key="cnt">
        <el-input v-model="seat.cols[cnt-1]" placeholder="以逗号分隔 例：A,B,C" />
      </el-form-item>
      <el-form-item label="安装总数" v-show="seat.type==='order'">
        <el-input-number v-model="seat.total" :min="1" :max="1000"  />
      </el-form-item>
      <el-form-item>
        <el-button type="danger" @click="deleteSeats(index)">删除坐席</el-button>
      </el-form-item>
    </el-form>
    <el-divider/>
    <el-form-item>
      <el-button type="success" @click="addSeats">添加坐席</el-button>
    </el-form-item>
    <el-divider/>
    <el-form-item>
      <el-button @click="submit">提交</el-button>
    </el-form-item>
  </el-form>

</template>

<script>

import Navi from '@/components/Navi'
import {getCoachInfo, getSeatTable, submitCoach} from "@/service/genServ";
import {ElMessage} from "element-plus";
export default {
  name: "CoachEdit",
  components:{Navi},
  data(){
    return{
      coachId:this.$route.query.coachId,
      isNew:true,
      form:{
        id:null,
        name:null,
        refrigerated:true,
        maxFreeCount:0,
        seats:[]
      },
      seats:[]
    }
  },
  created() {
    if(this.$route.query.coachId!=null){
      this.isNew=false;
      this.doQuery()
    }
    this.getSeats();
  },
  methods:{
    doQuery(){
      getCoachInfo({'coachId':this.coachId}).then(res=>{
        this.form=res.data
      })
    },
    getSeats(){
      getSeatTable().then(res=>{this.seats=res.data})
    },
    addSeats(){
      this.form.seats[this.form.seats.length]={};
      var cur=this.form.seats.length-1;
      this.form.seats[cur].cols=[];
      this.form.seats[cur].rows=[];
    },
    deleteSeats(index){
      this.form.seats.splice(index,1)
    },
    submit(){
      var numOfFree=0,noTotal=false;
      var emptyCols=0,noCol=false;
      var conflictRow=-1,noRow=false;
      var occupied=0,noType=false,noId=false;
      var seats=this.form.seats;
      for(var i=0;i<seats.length;i++){
        if(seats[i].seatId==null){noId=true;continue;}
        if(seats[i].type==null){noType=true;continue;}
        if(seats[i].type==='free'){numOfFree++;continue;}
        if(seats[i].type==='normal'){

          if(seats[i].rows.length===0)noRow=true;
          for(var j=0;j<seats[i].rows.length;j++)
            if((occupied&(1<<seats[i].rows[j]))>0)conflictRow=seats[i].rows[j];
            else occupied|=(1<<seats[i].rows[j]);

          if(seats[i].cols.length===0)noCol=true;
          for(var k=0;k<seats[i].cols.length;k++)
            if(seats[i].cols[k]==null||seats[i].cols[k]==='')emptyCols++;

          continue;
        }
        if(seats[i].type==='order'&&seats[i].total==null)noTotal=true;
      }
      var flag=true;
      if(!this.coachId){flag=false;ElMessage.error('请输入编号');}
      if(!this.form.name){flag=false;ElMessage.error('请输入名称');}
      if(noId){flag=false;ElMessage.error('请选择所有坐席');}
      if(noType){flag=false;ElMessage.error('请选择所有坐席种类');}
      if(noTotal){flag=false;ElMessage.error('请为顺序编号席分配数量');}
      if(noCol){flag=false;ElMessage.error('请为一般席分配至少一列');}
      if(noRow){flag=false;ElMessage.error('请为一般席分配至少一行');}
      if(numOfFree>1){flag=false;ElMessage.error('最多有一种自由席');}
      if(emptyCols>0){flag=false;ElMessage.error('共'+emptyCols+'组列号为空');}
      if(conflictRow>=0){flag=false;ElMessage.error('第'+conflictRow+'行分配冲突');}
      if(flag){
        this.form.id=this.coachId;
        this.form.new=this.isNew;
        submitCoach({'form':this.form}).then(res=>{
          if(res.code!=='0')ElMessage.error(res.msg);
          else this.$router.go(-1);
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
