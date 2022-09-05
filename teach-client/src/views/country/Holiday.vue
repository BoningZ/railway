<template>
  <Navi/>
  <el-form :data="form" style="margin-top: 10px" :inline="true">
    <el-form-item label="编号">
      <el-input v-model="form.id"/>
    </el-form-item>
    <el-form-item label="名称">
      <el-input v-model="form.name"/>
    </el-form-item>
    <el-form-item label="起止时间">
      <el-date-picker
          v-model="form.dateRange" type="daterange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"/>
    </el-form-item>
    <el-form-item>
      <el-button @click="submitNew" type="warning">新增</el-button>
    </el-form-item>
  </el-form>
  <el-divider/>
  <el-button type="success" size="large" @click="submitSelect">提交更改</el-button>
  <el-table  ref="myref" :data="dataList" style="width: 80%;margin-left: 10%" @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55" />
    <el-table-column prop="id" label="编号"/>
    <el-table-column prop="name" label="名称"/>
    <el-table-column label="起止时间">
      <template #default="scope">
        <el-date-picker
            v-model="scope.row.dateRange" type="daterange"
            :disabled="true"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"/>
      </template>
    </el-table-column>

  </el-table>

</template>

<script>
import Navi from '@/components/Navi'
import {getHolidayList, newHoliday, submitHolidaySelect} from "@/service/genServ";
import {ElMessage} from "element-plus";
export default {
  name: "Holiday",
  components:{Navi},
  data(){
    return{
      form:{
        id:"",
        name:"",
        dateRange:[]
      },
      dataList:[],
      selected:[]
    }
  },
  created() {
    this.doQuery()
  },
  methods:{
    doQuery(){
      getHolidayList().then(res=>{
        this.dataList=res.data.holidays;
        this.selected=res.data.selected;
        this.$nextTick(()=>{
          this.$nextTick(()=>{
            this.dataList.forEach((row)=>this.changeStatus(row.id,false));
            this.selected.forEach((id)=>this.changeStatus(id,true));
          })
        })
      })
    },
    submitNew(){
      newHoliday({'id':this.form.id,'name':this.form.name,'start':this.form.dateRange[0],'end':this.form.dateRange[1]}).then(res=>{
        if(res.code!=='0')ElMessage.error(res.msg);
        else{
          ElMessage.success('成功');
          this.doQuery();
          this.$nextTick(()=>{
            this.$nextTick(()=>{
              this.dataList.forEach((row)=>this.changeStatus(row.id,false));
              this.selected.forEach((id)=>this.changeStatus(id,true));
            })
          })
        }
      })
    },
    submitSelect(){
      submitHolidaySelect({'idList':this.selected}).then(res=>{
        if(res.code!=='0')ElMessage.error(res.msg);
        else{
          ElMessage.success('成功');
          this.doQuery();
          this.$nextTick(()=>{
            this.$nextTick(()=>{
              this.dataList.forEach((row)=>this.changeStatus(row.id,false));
              this.selected.forEach((id)=>this.changeStatus(id,true));
            })
          })
        }
      })
    },
    changeStatus(id,to){
      for(var i=0;i<this.dataList.length;i++){
        if(this.dataList[i].id===id)this.$refs.myref.toggleRowSelection(this.dataList[i], to)
      }
    },
    handleSelectionChange(val){
      this.selected=[]
      val.forEach((item)=>{
        this.selected[this.selected.length]=item.id;
      })
    }
  }
}
</script>

<style scoped>

</style>
