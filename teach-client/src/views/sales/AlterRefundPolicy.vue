<template>
  <Navi/>
  <el-form :inline="true" style="margin: 5px;">
    <el-form-item label="最大改签次数">
      <el-input-number v-model="maxAlter" :min="0" :max="100"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="submitAlter">提交</el-button>
    </el-form-item>
  </el-form>
  <el-divider/>
  <el-button @click="newRefund">新建退款政策</el-button>
  <el-table :data="dataList" style="width: 80%;margin-left: 10%">
    <el-table-column label="距开车小时数" width="250">
      <template #default="scope">
        <el-input-number v-model="scope.row.hour" :min="0" :max="1000"/>
      </template>
    </el-table-column>
    <el-table-column label="退换比例">
      <template #default="scope">
        <el-slider v-model="scope.row.ratio" :format-tooltip="formatTooltip" />
      </template>
    </el-table-column>
    <el-table-column label="操作" width="150">
      <template #default="scope">
        <el-button size="small" type="danger" @click="doDelete(scope.row.hour)">删除</el-button>
        <el-button size="small" type="success" @click="doEdit(scope.row.hour,scope.row.ratio/100)">提交</el-button>
      </template>
    </el-table-column>
  </el-table>

</template>

<script>
import Navi from '@/components/Navi'
import {ElMessage} from "element-plus";
import {deleteRefund, getAlter, getRefund, submitAlter, submitRefund} from "@/service/genServ";
export default {
  name: "AlterRefundPolicy",
  components:{Navi},
  data(){
    return{
      maxAlter:null,
      dataList:[]
    }
  },
  created() {
    this.doQuery()
  },
  methods:{
    formatTooltip(value){return value/100;},
    newRefund(){this.dataList[this.dataList.length]={}},
    doDelete(hour){
      if(hour==null){ElMessage.error('小时数不得为空');return;}
      deleteRefund({'hour':hour}).then(res=>{
        if(res.code!=='0')ElMessage.error(res.msg);
        else {
          this.doQuery();
          ElMessage.success('成功')
        }
      })
    },
    submitAlter(){
      if(this.maxAlter==null){ElMessage.error('次数不得为空');return;}
      submitAlter({'maxAlter':this.maxAlter}).then(res=>{
        if(res.code==='0'){
          this.doQuery();
          ElMessage.success('成功')
        }
      })
    },
    doEdit(hour,ratio){
      if(ratio==null||hour==null){ElMessage.error('任一项不得为空');return;}
      submitRefund({'hour':hour,'ratio':ratio}).then(res=>{
        if(res.code!=='0')ElMessage.error(res.msg);
        else{
          this.doQuery();
          ElMessage.success('成功')
        }
      })
    },
    doQuery(){
      getRefund().then(res=>{
        this.dataList=res.data;
        for(var i=0;i<this.dataList.length;i++)this.dataList[i].ratio*=100;
      });
      getAlter().then(res=>this.maxAlter=res.data.maxAlter)
    }
  }
}
</script>

<style scoped>

</style>
