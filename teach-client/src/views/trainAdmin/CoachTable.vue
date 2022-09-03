<template>
<Navi/>
  <el-form :inline="true" :data="form">
    <el-form-item label="车厢编号">
      <el-input placeholder="请输入车厢编号" v-model="form.coachId" size="small"/>
    </el-form-item>
    <el-form-item>
      <el-button size="small" type="primary" @click="doQuery">查询</el-button>
    </el-form-item>
    <el-form-item>
      <el-button size="small" type="success" @click="doCreate">新建</el-button>
    </el-form-item>
  </el-form>

  <el-table :data="dataList" style="width: 80%;margin-top: 5px;margin-left: 10%;">
    <el-table-column prop="id" label="编号" width="300"/>
    <el-table-column prop="name" label="名称" width="300"/>
    <el-table-column label="是否制冷" width="100">
      <template #default="scope">
        <el-tag size="small" type="danger" v-show="!scope.row.refrigerated">否</el-tag>
        <el-tag size="small" type="success" v-show="scope.row.refrigerated">是</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="maxFreeCount" label="自由席数" width="150"/>
    <el-table-column label="包含坐席">
      <template #default="scope">
        <el-tag size="small" v-for="item in scope.row.seats" v-bind:key="item">{{item}}</el-tag>
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
import Navi from '@/components/Navi'
import {getCoachTable} from "@/service/genServ";
export default {
  name: "CoachTable",
  components:{Navi},
  data(){
    return{
      form:{
        coachId:""
      },
      dataList:[],

    }
  },
  created() {
    this.doQuery()
  },
  methods:{
    doQuery(){
      getCoachTable({'coachId':this.form.coachId}).then(res=>{
        this.dataList=res.data
      })
    },
    doCreate(){this.$router.push('/CoachEdit');},
    doEdit(coachId){this.$router.push({path:'/CoachEdit',query:{coachId:coachId}})}
  }
}
</script>

<style scoped>

</style>
