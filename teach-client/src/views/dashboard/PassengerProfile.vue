<template>
  <Navi/>
  <div  class="form-div">
    <el-form ref="editForm" :model="editForm" :rules="editRules" label-position="left" label-width="100px" status-icon style="margin-top: 15px;">
      <br/>
      <el-row align="middle">
        <el-col :span="4">
          &nbsp;
        </el-col>
        <el-col :span="10">
          <el-form-item label="姓名" >
            <el-input v-model="name" maxlength="20" show-word-limit style="width: 500px;background-color: #f4f4f5"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="4">
          &nbsp;
        </el-col>
        <el-col :span="10">
          <el-form-item label="用户编号" >
            <el-input v-model="id" :disabled="true"  maxlength="20"  style="width: 500px;background-color: #f4f4f5"/>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="4">
          &nbsp;
        </el-col>
        <el-col :span="10">
          <el-form-item label="国家" >
            <el-input v-model="country" :disabled="true"  maxlength="20"  style="width: 500px;background-color: #f4f4f5"/>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="4">
          &nbsp;
        </el-col>
        <el-col :span="10">
          <el-form-item label="电话" >
            <el-input v-model="tel"  maxlength="20"  style="width: 500px;background-color: #f4f4f5"/>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="4">
          &nbsp;
        </el-col>
        <el-col :span="10">
          <el-form-item label="生日" >
            <el-date-picker
                style="width: 500px;background-color: #f4f4f5"
                v-model="birthday"
                type="date"
                placeholder="请选择生日"/>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row >
        <el-col :span="4">
          &nbsp;
        </el-col>
        <el-col :span="10">
          <el-form-item align="center">
            <el-button type="primary" @click="submit">提交</el-button>
          </el-form-item>
        </el-col>
      </el-row>


    </el-form>
  </div>
</template>

<script>
import Navi from '@/components/Navi'
import {getProfile,submitProfile} from "@/service/genServ";

export default {
  name: "StudentProfile",
  components:{Navi},
  data(){
    return{
      id:"",
      name:"",
      country:"",
      tel:"",
      birthday:null,
    }
  },
  created() {
    this.doQuery();
  },
  methods:{
    doQuery(){
      getProfile().then(res=>{
        this.id=res.data.id;
        this.name=res.data.name;
        this.country=res.data.country;
        this.birthday=res.data.birthday;
        this.tel=res.data.tel;
      })
    },
    submit(){
      submitProfile({"id":this.id,"name":this.name,"tel":this.tel,"birthday":this.birthday,"country":this.country}).then(res=>{
        if(res.code==='0'){
          this.$message({
            message:  '成功',
            type: 'success',
          })
        }else{
          this.$message({
            message:  res.msg,
            type: 'warning',
          })
        }
      })
    },

  }
}
</script>

