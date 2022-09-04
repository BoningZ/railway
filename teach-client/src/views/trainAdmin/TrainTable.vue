<template>
  <Navi/>
  <el-form :inline="true" :data="form">
    <el-form-item label="列车编号">
      <el-input placeholder="请输入列车编号" v-model="form.trainId" size="small"/>
    </el-form-item>
    <el-form-item>
      <el-button size="small" type="primary" @click="doQuery">查询</el-button>
    </el-form-item>
    <el-form-item>
      <el-button size="small" type="success" @click="doCreate">新建</el-button>
    </el-form-item>
  </el-form>

  <el-table :data="dataList" style="width: 80%;margin-top: 5px;margin-left: 10%;">
    <el-table-column prop="id" label="编号" width="250"/>
    <el-table-column prop="name" label="名称" width="250"/>
    <el-table-column label="车辆类型" width="150">
      <template #default="scope">
        <el-tag size="small" :type="scope.row.speed>165?'success':'primary'">{{scope.row.trainType}}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="speed" label="速度" width="150"/>
    <el-table-column label="挂载车厢">
      <template #default="scope">
        <el-tag size="small" v-for="item in scope.row.coaches" v-bind:key="item">{{item}}</el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作" width="150">
      <template #default="scope">
        <el-button size="small" @click="doEdit(scope.row.id)">编辑</el-button>
      </template>
    </el-table-column>
  </el-table>

</template>

<script>
import  Navi from '@/components/Navi'
import {getTrainTable} from "@/service/genServ";
export default {
  name: "TrainTable",
  components:{Navi},
  data(){
    return{
      dataList:[],
      form:{
        trainId:""
      }
    }
  },
  created() {
    this.doQuery();
  },
  methods:{
    doQuery(){
      getTrainTable({'trainId':this.form.trainId}).then(res=>{
        this.dataList=res.data
      })
    },
    doCreate(){
      this.$router.push('/TrainEdit');
    },
    doEdit(trainId){
      this.$router.push({path:'/TrainEdit',query:{trainId:trainId}});
    }

  }
}
</script>

<style scoped>

</style>
