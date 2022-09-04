<template>
  <Navi/>
  <el-card v-for="(item,index) in dataList" v-bind:key="item" style="width: 80%;margin-left: 10%;margin-top: 5px" v-loading="loading">
    <template #header>
      <el-row>
        <el-col :span="2">
          <el-tag size="large" type="danger" v-show="item.status==='UNPAID'">{{"行程"+(index+1)+":未支付"}}</el-tag>
          <el-tag size="large" type="info" v-show="item.status==='QUEUEING'">{{"行程"+(index+1)+":候补中"}}</el-tag>
          <el-tag size="large" type="success" v-show="item.status==='SUCCEEDED'">{{"行程"+(index+1)+":成功 "+item.price.toFixed(2)+"元"}}</el-tag>
          <el-tag size="large" type="danger" v-show="(item.status==='SUCCEEDED'||item.status==='QUEUEING')&&item.altered>0">{{"改签次数："+item.altered}}</el-tag>
          <el-tag size="large" type="warning" v-show="item.status==='CANCELED'">{{"行程"+(index+1)+":调度变更"}}</el-tag>
          <el-tag size="large"  v-show="item.status==='REFUNDED'">{{"行程"+(index+1)+":已退款"}}</el-tag>
          <el-tag size="large"  v-show="item.status==='USED'">{{"行程"+(index+1)+":已使用"}}</el-tag>
        </el-col>
        <el-col :span="22">
          <div>
            <el-tag size="small">{{"出发："+item.startStation+" "+item.startTime}}</el-tag>
            <span><el-tag type="info" size="large">{{ item.lineName }}</el-tag></span>
            <el-tag type="success" size="small">{{"到达："+item.endStation+" "+item.endTime}}</el-tag>
          </div>
        </el-col>
      </el-row>
    </template>
    <el-row>
        <el-col :span="12"><div><el-label>出发日期:</el-label><el-date-picker disabled v-model="item.startDate"/></div></el-col>
        <el-col :span="12"><div>
          <el-popover placement="left" :width="300" trigger="hover">
            <template #reference>
              <el-button size="mini">查看经停</el-button>
            </template>
            <el-table :data="item.via" size="mini">
              <el-table-column label="车站" prop="name" width="100"/>
              <el-table-column label="到站" prop="arrival" width="100"/>
              <el-table-column label="经停" prop="stay" width="100"/>
            </el-table>
          </el-popover>
        </div></el-col>
    </el-row>
    <h3></h3>
        <div v-show="this.status===0">
          <el-card v-for="(item2) in item.seats" v-bind:key="item2" style="width: 80%;margin-left: 10%">
              <el-row>
                <el-col :span="2">
                  <el-radio-group v-model="seatSingle[index]">
                    <el-radio :label="item2.id" size="large" border>{{item2.name}}</el-radio>
                  </el-radio-group>
                </el-col>
                <el-col :span="8">
                  <div><span>
                    <el-tag type="info">{{ "剩余："+item2.rest}}</el-tag>
                    <el-tag type="warning">{{ "价格："+item2.price+item2.currency}}</el-tag>
                    <el-tag type="info" v-show="item2.rest<=0">{{ "候补："+item2.queueing}}</el-tag>
                  </span></div>
                </el-col>
                <el-col :span="12">
                  <el-container v-for="item3 in item2.cols" v-bind:key="item3">
                    <el-radio-group v-for="item4 in item3" v-bind:key="item4" v-model="colChoice[index]" size="large" style="margin-left: 20px;margin-top: 10px">
                      <el-radio-button v-for="item5 in item4" v-bind:key="item5" :label="item5" />
                    </el-radio-group>
                  </el-container>
                </el-col>
              </el-row>
          </el-card>
        </div>
        <div v-show="item.status==='SUCCEEDED'">
          <el-row>
            <el-col :span="18">
              <el-tag size="large">{{item.coachNum+"车  "+item.row+item.col}}</el-tag>
            </el-col>
            <el-col :span="6">
              <el-tag size="large" type="warning">{{item.seatName}}</el-tag>
            </el-col>
          </el-row>
        </div>
        <div v-show="item.status==='QUEUEING'">
        <el-tag type="info" size="large">候补中</el-tag>
      </div>
        <div v-show="item.status==='SUCCEEDED'||item.status==='QUEUEING'||item.status==='CANCELED'">
          <el-divider/>
          <el-row style="margin-top: 10px">
            <el-col :span="16"></el-col>
            <el-col :span="4">
              <el-button type="warning" size="large" style="width: 80%;" @click="alter(item.id)" plain v-show="item.status!=='CANCELED'">改签</el-button>
            </el-col>
            <el-col :span="4">
              <el-button type="danger" size="large" style="width: 80%;" @click="refund(item.id)" plain>退票</el-button>
            </el-col>
          </el-row>
        </div>
  </el-card>
  <div v-show="status===0">
    <el-table :data="fellowList" style="width: 60%;margin-left: 20%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" />
      <el-table-column property="name" label="姓名"  width="200" />
      <el-table-column property="licence" label="证件号"  />
      <el-table-column property="country" label="国家" width="150"  />
    </el-table>
    <el-row style="width: 60%;margin-left: 20%">
      <el-col :span="18">
        <el-container>
          <el-label>证件号：</el-label>
          <el-input v-model="fellow.licence" style="width: 75%" size="small"></el-input>
        </el-container>
      </el-col>
      <el-col :span="6">
        <el-label >国家：</el-label>
        <el-select style="width: 75%" v-model="fellow.countryId" size="small" placeholder="Select">
          <el-option
              v-for="item in countries"
              :key="item.id"
              :label="item.name"
              :value="item.id">
            <span style="float: left">{{ item.name }}</span>
            <span style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{ item.id }}</span>
          </el-option>
        </el-select>
      </el-col>
    </el-row>
    <el-button size="mini" style="width: 60%;" @click="addFellow">添加到同行人列表</el-button>
  </div>
  <div style="width: 20%;margin: auto" v-show="this.status===0"><el-button type="success" style="width: 80%;margin-top: 10px" @click="goPay">{{this.price()+"元 支付"}}</el-button></div>


  <el-dialog v-model="refundDialog" title="退款信息" width="30%">
    <span>{{refundMsg}}</span>
    <template #footer>
      <span>
        <el-button @click="refundDialog=false">取消</el-button>
        <el-button type="primary" @click="confirmRefund" v-show="okToRefund">确认</el-button>
      </span>
    </template>
  </el-dialog>

</template>

<script>
import {getTravelInfo, payTravel, getFellowList, getCountries, addFellow, getRefundInfo,refund} from "@/service/genServ";
import Navi from '@/components/Navi'
import {ElMessage} from "element-plus";
export default {
  name: "EditTravel",
  components:{Navi},
  data(){
    return{
      fellow:{
        licence:"",
        countryId:"",
      },
      travelId:this.$route.query.travelId,
      status:13322,
      dataList:[],
      colChoice:[],
      seatSingle:[],
      fellowList:[],
      fellowChoice:[],
      priceList:[],
      countries:[],
      loading:false,

      refundDialog:false,
      refundMsg:"",
      okToRefund:false,
      refundingId:""
    }
  },
  created() {
    this.doQuery();
    this.getFellows();
  },
  methods:{
    addFellow(){
      addFellow({'licence':this.fellow.licence,'countryId':this.fellow.countryId}).then(res=>{
        if(res.code!=='0')ElMessage.error(res.msg);
        else {
          ElMessage.success('添加成功');
          this.getFellows();
        }
      })
    },
    handleSelectionChange(res){
      this.fellowChoice=res
    },
    getFellows(){
      getFellowList().then(res=>{
        this.fellowList=res.data;
      })
    },
    price(){
      var res=0;
      for(var i=0;i<this.dataList.length;i++)
        for(var j=0;j<this.dataList[i].seats.length;j++)
          if(this.dataList[i].seats[j].id===this.seatSingle[i]){
            var price=Number(this.dataList[i].seats[j].price);
            res+=price;
            this.priceList[i]=price;
          }
      res*=(this.fellowChoice.length+1);
      return res.toFixed(2);
    },
    doQuery(){
      this.loading=true;
      getTravelInfo({'travelId':this.travelId}).then(res=>{
        this.status=res.data.status;
        this.dataList=res.data.tickets;
        this.colChoice[this.dataList.length]="占位"
      });
      getCountries().then(res=>{
        this.countries=res.data;
      })
      this.loading=false;
    },
    goPay(){
      for(var i=0;i<this.dataList.length;i++)
        if(this.seatSingle[i]==null){
          ElMessage.error('请为所有车次选择坐席');
          return;
        }
      var processedFellows=[]
      for(var j=0;j<this.fellowChoice.length;j++)processedFellows[j]=this.fellowChoice[j].id;
      payTravel({'seatList':this.seatSingle,
                      'colList':this.colChoice,
                      'travelId':this.travelId,
                      'priceList':this.priceList,
                      'fellowList':processedFellows}).then(res=>{
        if(res.code!=='0')
          ElMessage.error(res.msg)
        else {
          ElMessage.success(res.msg);
          this.doQuery()
        }
      })
    },
    alter(ticketId){
      this.$router.push({path:'/AlterTicket',query:{ticketId:ticketId}});
    },
    refund(ticketId){
      getRefundInfo({'ticketId':ticketId}).then(res=>{
        this.refundMsg=res.data.refundMsg;
        this.okToRefund=res.data.okToRefund;
        this.refundingId=ticketId;
        this.refundDialog=true;
      })
    },
    confirmRefund(){
      refund({'ticketId':this.refundingId}).then(res=>{
        if(res.code==='0'){
          ElMessage.success('退款成功');
          this.$router.go(0);
        }else ElMessage.error(res.msg)
        this.refundDialog=false;
      })
    }
  }

}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
