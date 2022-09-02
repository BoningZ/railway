<template>
  <Navi/>
  <div style="margin-top: 25px">
    <el-row :gutter="3">
      <el-col :span="12">
        <el-header :height="80"><el-label >允许市内换乘：</el-label></el-header>
        <el-main><el-switch v-model="transferInCity"  /></el-main>
      </el-col>
      <el-col :span="12">
        <el-header :height="80"><el-label >策略：</el-label></el-header>
        <el-main>
          <el-radio-group v-model="priority">
            <el-radio label="time" border>时间短</el-radio>
            <el-radio label="money" border>花费少</el-radio>
          </el-radio-group>
        </el-main>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="14">
        <el-label>出发日期：</el-label>
        <el-date-picker
            style="width: 40%;background-color: #f4f4f5"
            v-model="startDate"
            type="date"
            placeholder="请选择出发日期"/>
      </el-col>
      <el-col :span="10">
        <el-label>出发时间：</el-label>
        <el-select v-model="startTime" class="m-2" placeholder="选择出发时间" size="large">
          <el-option
              v-for="o in 24"
              :key="o-1"
              :label="(o-1)+'时'"
              :value="o-1"/>
        </el-select>
      </el-col>
    </el-row>
    <h3></h3>
    <el-button style="width: 50%;" type="primary" @click="doQuery" :disabled="loadingTable">查询车票</el-button>
  </div>

  <el-table v-loading="loadingTable" class="table-content" :data="dataList" border style="width: 100%;"  height="1000">

    <el-table-column label="序号" fixed="left" width="50" align="center" color="black">
      <template v-slot="scope">
        {{ scope.$index+1 }}
      </template>
    </el-table-column><!--第一列-->

    <el-table-column label="出发时间" align="center" color="black" sortable prop="start" width="120" /><!--courseNum-->
    <el-table-column label="历时" align="center" color="black" sortable prop="duration" width="120"/><!--courseName-->
    <el-table-column label="到达时间" align="center" color="black" sortable prop="end" width="120"/>
    <el-table-column label="路线" align="center" color="black" sortable prop="name" />
    <el-table-column prop="tag" label="车辆" :filters="typeList" :filter-method="filterTag" width="240" filter-placement="bottom-end">
      <template #default="scope">
        <el-tag :type="scope.row.type === '普通客运火车' ? '' : 'success'" disable-transitions>{{ scope.row.type }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column type="expand" >
      <template #default="prop">
        <el-card v-for="item in prop.row.lines" v-bind:key="item" style="width: 90%;margin-left: 5%;margin-top: 5px" >
          <template #header>
            <el-row>
              <el-col :span="8"></el-col>
              <el-col :span="10">
                <div>
                  <el-tag size="small">{{"出发："+item.startStation+" "+item.startTime}}</el-tag>
                  <span><el-tag type="info" size="large">{{ item.lineName }}</el-tag></span>
                  <el-tag type="success" size="small">{{"到达："+item.endStation+" "+item.endTime}}</el-tag>
                </div>
              </el-col>
              <el-col :span="6">
                <el-popover placement="right" :width="300" trigger="hover">
                  <template #reference>
                    <el-button size="mini">查看经停</el-button>
                  </template>
                  <el-table :data="item.via" size="mini">
                    <el-table-column label="车站" prop="name" width="100"/>
                    <el-table-column label="到站" prop="arrival" width="100"/>
                    <el-table-column label="经停" prop="stay" width="100"/>
                  </el-table>
                </el-popover>
              </el-col>
            </el-row>
          </template>
          <h3></h3>
          <div >
            <el-card v-for="(item2) in item.seats" v-bind:key="item2" style="width: 90%;margin-left: 5%">
              <el-row>
                <el-col :span="4">
                  <el-radio-group v-model="seatId">
                    <el-radio @change="clearPos" :label="item2.id" size="large" border>{{item2.name}}</el-radio>
                  </el-radio-group>
                </el-col>
                <el-col :span="8">
                  <div><span>
                    <el-tag type="info">{{ "剩余："+item2.rest}}</el-tag>
                    <el-tag type="warning">{{ "价格："+item2.price+item2.currency}}</el-tag>
                    <el-tag type="info" v-show="item2.rest<=0">{{ "候补："+item2.queueing}}</el-tag>
                  </span></div>
                </el-col>
                <el-col :span="10">
                  <el-container v-for="item3 in item2.cols" v-bind:key="item3">
                    <el-radio-group v-for="item4 in item3" v-bind:key="item4" v-model="col" size="large" style="margin-left: 20px;margin-top: 10px">
                      <el-radio-button v-for="item5 in item4" v-bind:key="item5" :label="item5" />
                    </el-radio-group>
                  </el-container>
                </el-col>
              </el-row>
            </el-card>
          </div>
          <div >
            <el-divider/>
            <el-row style="margin-top: 10px">
              <el-col :span="16"></el-col>
              <el-col :span="8">
                <el-button type="warning" size="large" style="width: 80%;" @click="goAlter(prop.row.routeId)" :disabled="okForRoute(prop.row.routeId)" plain>{{price(prop.row.routeId)+" 选择"}}</el-button>
              </el-col>
            </el-row>
          </div>
        </el-card>

      </template>
    </el-table-column><!--编辑删除按钮-->
  </el-table>

</template>

<script>
import Navi from '@/components/Navi'
import {getAlterRoutes,alterTicket} from "@/service/genServ";
import {ElMessage} from "element-plus";
export default {
  name: "AlterTicket",
  components:{Navi},
  data(){
    return{
      dataList:[],
      typeList:[],
      transferInCity:false,
      priority:'time',
      startDate:null,
      startTime:0,
      loadingTable:false,
      ticketId:this.$route.query.ticketId,
      seatId:null,
      col:null,
      curPrice:0,
      newPrice:0
    }
  },
  methods:{
    price(routeId){
      var res=0;
      for(var j=0;j<this.dataList[routeId].lines[0].seats.length;j++)
        if(this.dataList[routeId].lines[0].seats[j].id===this.seatId){
          var price=Number(this.dataList[routeId].lines[0].seats[j].price);
          res+=price;
        }
      this.newPrice=res;
      if(res>this.curPrice)return "补"+(res-this.curPrice).toFixed(2)+"元";
      else return "退"+(this.curPrice-res).toFixed(2)+"元";
    },
    okForRoute(routeId){//actually not ok
      if(this.seatId==null)return true;
      for(var j=0;j<this.dataList[routeId].lines[0].seats.length;j++)
        if (this.dataList[routeId].lines[0].seats[j].id === this.seatId) return false;

      return true;
    },
    clearPos(){ this.col=null;},
    filterTag(value,row){
      return row.type===value;
    },
    doQuery(){
      this.loadingTable=true;
      getAlterRoutes({
        'ticketId':this.ticketId,
        'transferInCity':this.transferInCity,
        'priority':this.priority,
        'startTime':this.startTime,
        'startDate':this.startDate}).then(res=>{
        this.loadingTable=false;
        if(res.code!=='0')
          ElMessage.error(res.msg);
        else {
          this.dataList = res.data.table;
          this.typeList=res.data.type;
          this.curPrice=res.data.price
        }
      })
    },
    goAlter(routeId){
      if(this.seatId===null){ElMessage.error("请选择坐席");return;}
      alterTicket({
        'routeId':routeId,
        'seatId':this.seatId,
        'col':this.col,
        'ticketId':this.ticketId,
        'price':this.newPrice}).then(res=>{
        if(res.code==='0')this.$router.go(-1)
        else ElMessage.error(res.msg)
      })
    }
  }


}
</script>

<style scoped>

</style>
