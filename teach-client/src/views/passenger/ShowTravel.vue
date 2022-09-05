<template>
  <Navi/>
  <div id="mymap" style="height: 1000px;" />
</template>

<script>

import BMap from 'BMap'
import Navi from '@/components/Navi'
import {getTravelTrace} from "@/service/genServ";
export default {
  name: "ShowTravel",
  components:{Navi},
  data(){
    return{
      travelId:this.$route.query.travelId,
      trace:[]
    }
  },
  created() {
    this.doQuery()
  },
  mounted() {
    this.initMap()
  },
  methods:{
    doQuery(){
      getTravelTrace({'travelId':this.travelId}).then(res=>{
        this.trace=res.data
        this.$nextTick(()=>this.initMap())
      }
      )
    },
    initMap(){
      var map = new BMap.Map("mymap");    // 创建Map实例
      map.centerAndZoom(new BMap.Point(116.404, 29.915), 6);  // 初始化地图,设置中心点坐标和地图级别
      map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
      // eslint-disable-next-line no-undef
      var sy = new BMap.Symbol(BMap_Symbol_SHAPE_BACKWARD_OPEN_ARROW, {
        scale: 0.6,//图标缩放大小
        strokeColor:'#fff',//设置矢量图标的线填充颜色
        strokeWeight: '2',//设置线宽
      });
      var icons = new BMap.IconSequence(sy, '10', '30');
// 创建polyline对象
      var tmp=[];
      this.trace.forEach((item)=>tmp[tmp.length]=new BMap.Point(item.lng,item.lat));
      var pois =  tmp;
      var polyline =new BMap.Polyline(pois, {
        enableEditing: false,//是否启用线编辑，默认为false
        enableClicking: true,//是否响应点击事件，默认为true
        icons:[icons],
        strokeWeight:'8',//折线的宽度，以像素为单位
        strokeOpacity: 0.8,//折线的透明度，取值范围0 - 1
        strokeColor:"#18a45b" //折线颜色
      });
      map.addOverlay(polyline);
    }
  }
}
</script>

<style scoped>
.anchorBL{
  display:none;
}
</style>
