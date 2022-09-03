<template>
  <body id="poster">
  <el-form class="login-container" label-position="left" label-width="0px">
  <div class="register">
    <h2>注册</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <el-label for="username">用户名</el-label>&nbsp;&nbsp;&nbsp;&nbsp;
        <el-input style="width: 75%" clearable v-model="username"  name="username" placeholder="请输入用户名"></el-input>
      </div>



      <div class="form-group">
        <h2></h2>
        <label htmlFor="password">密码</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <el-input style="width: 75%"  clearable placeholder="请输入密码" v-model="password" show-password></el-input>
      </div>
      <div class="form-group">
        <h2></h2>
        <label htmlFor="password2">重复密码</label>&nbsp;
        <el-input style="width: 75%"  clearable placeholder="请输入重复密码" v-model="password2" show-password></el-input>
      </div>
      <div style="margin-top: 20px" id="register">
        <el-radio-group v-model="type" size="mini" >
          <el-radio label="0" border>我是乘客</el-radio>
          <el-radio label="1" border>我是员工</el-radio>
        </el-radio-group>
      </div>

      <div style="margin-top: 20px" v-show="(type==='1')">
        <el-radio-group v-model="role" size="mini" >
          <el-radio label="ROLE_DRIVER" >司机</el-radio>
          <el-radio label="ROLE_ADMIN_COUNTRY" >国家管理</el-radio>
          <el-radio label="ROLE_ADMIN_STATION" >车站管理</el-radio>
          <el-radio label="ROLE_ADMIN_COMPANY" >运营局管理</el-radio>
        </el-radio-group>
      </div>

      <div class="form-group" v-show="(type==='0')">
        <h2></h2>
        <el-label for="username">身份证号</el-label>&nbsp;
        <el-input style="width: 75%" clearable v-model="sid"  name="name" placeholder="请输入身份证号"></el-input>
      </div>

      <div class="form-group" v-show="(role==='ROLE_DRIVER'||type==='0')">
        <h2></h2>
        <el-label for="username">姓名</el-label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <el-input style="width: 75%" clearable v-model="name"  name="name" placeholder="请输入姓名"></el-input>
      </div>

      <div class="form-group"  v-show="(type==='1')">
        <h2></h2>
        <el-label for="username">工号</el-label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <el-input style="width: 75%" clearable v-model="tid"  name="tid" placeholder="请输入工号"></el-input>
      </div>

      <div class="form-group"  v-show="(role==='ROLE_DRIVER'&&type==='1')">
        <h2></h2>
        <el-label for="username">电话</el-label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <el-input style="width: 75%" clearable v-model="tel"  name="tel" placeholder="请输入电话"></el-input>
      </div>

      <div class="form-group" v-show="(role==='ROLE_ADMIN_COUNTRY'||type==='0')">
        <h2></h2>
        <el-label for="username">国家</el-label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <el-select style="width: 75%" v-model="country" placeholder="Select">
          <el-option
              v-for="item in countries"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          >
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
          </el-option>
        </el-select>
      </div>

      <div class="form-group" v-show="(role==='ROLE_ADMIN_COMPANY'&&type==='1')">
        <h2></h2>
        <el-label for="username">运营局</el-label>&nbsp;&nbsp;&nbsp;&nbsp;
        <el-select style="width: 75%" filterable v-model="company" placeholder="Select">
          <el-option
              v-for="item in companies"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          >
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
          </el-option>
        </el-select>
      </div>

      <div class="form-group" v-show="(role==='ROLE_ADMIN_STATION'&&type==='1')">
        <h2></h2>
        <el-label for="username">车站</el-label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <el-select style="width: 75%" filterable v-model="station"
                   remote
                   reserve-keyword
                   placeholder="请输入车站名"
                   :remote-method="searchStations"
                   :loading="loading">
          <el-option
              v-for="item in stations"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          >
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
          </el-option>
        </el-select>
      </div>



      <div class="form-group"  v-show="(type==='1')">
        <h2></h2>
        <el-label for="username">校验密码</el-label>&nbsp;
        <el-input style="width: 75%" clearable v-model="check"  name="check" placeholder="请输入校验密码"></el-input>
      </div>

      <div style="margin-top: 20px" class="form-group">
        <h2></h2>
        <el-button class="btn btn-primary" type="primary" @click='handleSubmit()' style="width: 50%;background: #409EFF;border: none">注册</el-button>
      </div>
    </form>
  </div>
  </el-form>
  </body>
</template>

<script>
import {register,getCountries,getCompanies,getStationsLike} from '@/service/genServ.js'
import {ref} from "vue";

export default {
  name: "Register",
  data(){
    return{
      sid:'',
      username:'',
      password:'',
      password2:'',
      name:'',
      tid:'',
      tel:'',
      type:'0',
      role:'ROLE_DRIVER',
      check:'',
      countries:[],
      country:'CN',
      companies:[],
      company:'CN-370000',
      stations:[],
      station:null,
      loading:ref(false),
    }
  },
  created() {
    this.getInit();
  },
  methods: {
    getInit(){
      getCountries().then(res=>{
        this.countries=res.data;
      })
      getCompanies().then(res=>{
        this.companies=res.data;
      })
    },
    searchStations(query){
      if(query){
        this.loading=ref(true)
        setTimeout(() => {
          this.loading=ref(false)
          getStationsLike({'name':query}).then(res=>{
            this.stations=res.data
          })
        }, 200)
      }else this.stations=[];
    },
    checkCompleteness(){
      if(!this.username||!this.password)return false;
      if(this.type==='0')
        return !!(this.name && this.country && this.sid);
      else{
        if(this.role==='ROLE_DRIVER')
          return !!(this.tid && this.tel && this.name);
        else if(this.role==='ROLE_ADMIN_STATION')
          return !!(this.tid&&this.station);
        else if(this.role==='ROLE_ADMIN_COMPANY')
          return !!(this.tid&&this.company);
        else if(this.role==='ROLE_ADMIN_COUNTRY')
          return !!(this.tid&&this.country);
      }
    },
    handleSubmit() {
      if(this.password!==this.password2){
        this.$message({
          message:  '口令不一致！',
          type: 'warning',
        })
        return;
      }if(!this.checkCompleteness()){
        this.$message({
          message:  '所有项目均为必填项！',
          type: 'warning',
        })
      }
        else{
        register({'username':this.username,
                        'password':this.password,
                        'role':this.type==='0'?'ROLE_PASSENGER':this.role,
                        'tel':this.tel,
                        'country':this.country,
                        'company':this.company,
                        'station':this.station,
                        'sid':this.sid,
                        'tid':this.tid,
                        'name':this.name,
                        'check':this.check}).then(response=>{
          if (response.code === '0') {
            this.$message({
              message:  '成功，跳转到登录页面',
              type: 'success',
            })
            this.$router.go(-1)
          } else {
            this.$message({
              message: response.msg,
              type: 'warning',
            })
          }
        })
      }
    }
  }
}
</script>

<style>
#poster{
  background:url("../assets/register.jpg") no-repeat;
  background-position: center;
  height: 100%;
  width: 100%;
  background-size: cover;
  position: fixed;
}
body{
  margin: 0px;
}
.login-container {
  border-radius: 30px;
  background-clip: padding-box;
  margin: 80px auto;
  width: 400px;
  padding: 35px 35px 50px 35px;
  background: #fff;
  border: 1px solid #eaeaea;
  box-shadow: 0 0 25px #cac6c6;
}
.login_title {
  margin: 0px auto 40px auto;
  text-align: center;
  color: #505458;
}
</style>
