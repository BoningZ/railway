<template>
  <Navi/>
  <el-form :data="form" style="width: 90%;margin-left: 5%;margin-top: 5px">
    <el-form-item label="编号">
      <el-input v-model="form.id" style="width: 80%" :disabled="true"/>
    </el-form-item>
    <el-form-item label="名称">
      <el-input v-model="form.name" style="width: 80%" :disabled="true" />
    </el-form-item>


    <el-form v-for="(price,index) in form.prices" v-bind:key="price" :data="price">
      <el-divider/>
      <el-form-item label="天内价格">
        <el-checkbox-group v-model="price.inDay" size="small">
          <el-checkbox-button v-for="o in 24" v-bind:key="o" :label="o"> {{ o-1}}</el-checkbox-button>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="周内价格">
        <el-checkbox-group v-model="price.inWeek" size="small">
          <el-checkbox-button v-for="o in 7" v-bind:key="o" :label="o"> {{ o}}</el-checkbox-button>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="节假日专属">
        <el-switch v-model="price.holiday"/>
      </el-form-item>
      <el-form-item label="常数">
        <el-input v-model="price.constant"/>
      </el-form-item>
      <el-form-item label="倍率">
        <el-input v-model="price.power"/>
      </el-form-item>
      <el-form-item label="启用日期">
        <el-date-picker v-model="price.updated"/>
      </el-form-item>

      <el-form-item>
        <el-button type="danger" @click="deletePrice(index)" >删除计划外</el-button>
      </el-form-item>
    </el-form>
    <el-divider/>
    <el-form-item>
      <el-button type="success" @click="addPrice">添加计划外</el-button>
    </el-form-item>

    <el-divider/>
    <el-form-item>
      <el-button @click="submit">提交</el-button>
    </el-form-item>
  </el-form>

</template>

<script>
import Navi from '@/components/Navi'
import {getLineInfo, submitLinePrice} from "@/service/genServ";
import {ElMessage} from "element-plus";
export default {
  name: "LineEditDeparture",
  components:{Navi},
  data(){
    return{
      form:{
        id:this.$route.query.lineId,
        name:null,
        prices:[]
      },
    }
  },
  created() {
    this.doQuery()

  },
  methods:{
    doQuery(){
      getLineInfo({'lineId':this.form.id}).then(res=>{
        this.form=res.data
      });
    },
    addPrice(){
      this.form.prices[this.form.prices.length]={};
      var cur=this.form.prices.length-1;
      this.form.prices[cur].inDay=[];
      this.form.prices[cur].inWeek=[];
    },
    deletePrice(index){
      this.form.prices.splice(index,1)
    },
    submit(){
      var prices=this.form.prices;
      if(prices.length<1){ElMessage.error('最少需要有一个价格修正');return;}
      for(var i=0;i<prices.length;i++){
        var price=prices[i];
        if(!price.inDay||!price.inWeek||(!price.constant&&price.constant!==0)||(!price.power&&price.power!==0)||!price.updated){ElMessage('请完善修正信息');return;}
        if(isNaN(price.power)||isNaN(price.constant)){ElMessage('请注意价格格式');return;}
        this.form.prices[i].power=Number(price.power); this.form.prices[i].constant=Number(price.constant);
      }
      submitLinePrice({'form':this.form}).then(res=>{
        if(res.code!=='0')ElMessage(res.msg);
        else this.$router.go(-1)
      })
    }
  }
}
</script>

<style scoped>

</style>
