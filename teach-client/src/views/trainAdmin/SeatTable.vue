<template>
  <Navi/>
  <el-row style="width: 80%;margin-left: 10%;margin-top: 5px">
    <el-col :span="6">
      <el-container>
        <el-label>编号：</el-label>
        <el-input v-model="seat.id" style="width: 75%" size="small"></el-input>
      </el-container>
    </el-col>
    <el-col :span="6">
      <el-container>
        <el-label>名称：</el-label>
        <el-input v-model="seat.name" style="width: 75%" size="small"></el-input>
      </el-container>
    </el-col>
    <el-col :span="4">
      <el-container>
        <el-label>常数：</el-label>
        <el-input v-model="seat.constant" style="width: 75%" size="small"></el-input>
      </el-container>
    </el-col>
    <el-col :span="4">
      <el-container>
        <el-label>倍率：</el-label>
        <el-input v-model="seat.power" style="width: 75%" size="small"></el-input>
      </el-container>
    </el-col>
    <el-col :span="4">
      <el-container>
        <el-button size="small" type="success" @click="newSeat">新建</el-button>
      </el-container>
    </el-col>
  </el-row>
  <el-table :data="dataList" style="width: 80%;margin-top: 5px;margin-left: 10%;">
    <el-table-column prop="id" sortable label="编号"/>
    <el-table-column prop="name" label="名称"/>
    <el-table-column prop="constant" label="原常数" width="70"/>
    <el-table-column label="新常数">
      <template #default="scope">
        <el-input v-show="editing===scope.row.id" v-model="newConst"/>
      </template>
    </el-table-column>
    <el-table-column prop="power" label="原倍率" width="70"/>
    <el-table-column label="新倍率">
      <template #default="scope">
        <el-input v-show="editing===scope.row.id" v-model="newPower"/>
      </template>
    </el-table-column>
    <el-table-column label="操作">
      <template #default="scope">
        <el-button v-show="editing!==scope.row.id" link type="primary" @click="startEdit(scope.row.id)">编辑</el-button>
        <el-button v-show="editing===scope.row.id" link type="info" @click="editing=null">取消</el-button>
        <el-button v-show="editing===scope.row.id" link type="success" @click="submitEdit(scope.row.id)">保存</el-button>
      </template>
    </el-table-column>
  </el-table>



</template>

<script>
import Navi from '@/components/Navi'
import {getSeatTable, newSeat, submitSeat} from "@/service/genServ";
import {ElMessage} from "element-plus";
export default {
  name: "SeatTable",
  components:{Navi},
  data(){
    return{
      dataList:[],
      newConst:null,
      newPower:null,
      editing:null,
      seat:{
        id:null,
        name:null,
        constant:null,
        power:null
      }
    }
  },
  created() {
    this.doQuery()
  },
  methods:{
    newSeat(){
      if(this.seat.id==null||this.seat.name==null||this.seat.constant==null||this.seat.power==null){ElMessage.error('请输入所有参数');return;}
      this.seat.constant=Number(this.seat.constant); this.seat.power=Number(this.seat.power);
      if(isNaN(this.seat.constant)||isNaN(this.seat.power)){ElMessage.error('常数与倍率需为数字！');return;}
      newSeat({
        'id':this.seat.id,
        'name':this.seat.name,
        'constant':this.seat.constant,
        'power':this.seat.power
      }).then(res=>{
        if(res.code!=='0')ElMessage.error(res.msg);
        else {ElMessage.success('成功');this.doQuery();}
      })
    },
    doQuery(){
      getSeatTable().then(res=>{
        this.dataList=res.data
      })
    },
    startEdit(seatId){
      this.newConst=null; this.newPower=null;
      this.editing=seatId;
    },
    submitEdit(seatId){
      this.newConst=Number(this.newConst); this.newPower=Number(this.newPower);
      if(this.newConst==null||this.newPower==null){ElMessage.error('请输入所有参数！');return;}
      if(isNaN(this.newConst)||isNaN(this.newPower)){ElMessage.error('请输入数字！');return;}
      if(this.newConst<0||this.newPower<0){ElMessage.error('请输入正数！');return;}
      submitSeat({'seatId':seatId,'constant':this.newConst,'power':this.newPower}).then(res=>{
        if(res.code==='0')ElMessage.success('提交成功');
        this.doQuery();
        this.editing=null;
      })
    }

  }

}
</script>

<style scoped>

</style>
