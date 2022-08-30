package com.baling.controllers.passenger;

import com.baling.models.administration.Station;
import com.baling.models.holiday.CountryHoliday;
import com.baling.models.holiday.Holiday;
import com.baling.models.line.Departure;
import com.baling.models.line.Line;
import com.baling.models.line.RunDate;
import com.baling.models.line.Stopping;
import com.baling.models.ticket.ETicketStatus;
import com.baling.models.train.*;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.administration.CityRepository;
import com.baling.repository.administration.StationRepository;
import com.baling.repository.holiday.CountryHolidayRepository;
import com.baling.repository.line.DepartureRepository;
import com.baling.repository.line.LineRepository;
import com.baling.repository.line.RunDateRepository;
import com.baling.repository.line.StoppingRepository;
import com.baling.repository.ticket.TicketRepository;
import com.baling.repository.ticket.TicketStatusRepository;
import com.baling.repository.train.CoachSeatRepository;
import com.baling.repository.train.TrainCoachRepository;
import com.baling.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    @Autowired
    LineRepository lineRepository;
    @Autowired
    StoppingRepository stoppingRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    RunDateRepository runDateRepository;
    @Autowired
    CountryHolidayRepository countryHolidayRepository;
    @Autowired
    DepartureRepository departureRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    TrainCoachRepository trainCoachRepository;
    @Autowired
    CoachSeatRepository coachSeatRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    TicketStatusRepository ticketStatusRepository;

    @PostMapping("/getTransferInfo")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse getTransferInfo(@Valid @RequestBody DataRequest dataRequest){
        maxTrans=dataRequest.getInteger("maxTransfer");
        allowedInCity=dataRequest.getBoolean("transferInCity");
        priMoney=dataRequest.getString("priority").equals("money");
        String fromRange=dataRequest.getString("fromRange"),toRange=dataRequest.getString("toRange");
        fromId=dataRequest.getString("fromId"); toId=dataRequest.getString("toId");
        switch (fromRange){
            case "city":from=stationRepository.findByCity(cityRepository.getById(fromId));fromCity=true;break;
            case "station":from= Collections.singletonList(stationRepository.getById(fromId));fromCity=false;break;
        }
        switch (toRange){
            case "city":to=stationRepository.findByCity(cityRepository.getById(toId));toCity=true;break;
            case "station":to= Collections.singletonList(stationRepository.getById(toId));toCity=false;break;
        }
        startDate=dataRequest.getDate("startDate");
        List<Route> routes=getRoutes();
        if(routes.size()==0)return CommonMethod.getReturnMessageError("无法找到路径");

        List dataList=new ArrayList();
        List<String> typeList=new ArrayList();
        List typeFliter=new ArrayList();
        for(Route r:routes){
            Map mRoute=new HashMap();
            int startTime=(r.startList.get(0).getArrival()+r.departureList.get(0).getStart())%(24*60);
            mRoute.put("start",CommonMethod.minToHourString(startTime));
            mRoute.put("duration",CommonMethod.minToHourString(r.minSpent+r.daySpent*24*60-startTime));
            mRoute.put("end",(r.daySpent>0?("+"+r.daySpent+"天  "):"")+CommonMethod.minToHourString(r.minSpent));
            String type=r.getType();
            mRoute.put("type",type);
            if(!typeList.contains(type)){
                typeList.add(type);
                Map mmm=new HashMap();
                mmm.put("text",type); mmm.put("value",type);
                typeFliter.add(mmm);
            }
            List inRoute=new ArrayList();
            for(int i=0;i<r.departureList.size();i++){
                Map lineInfo=new HashMap();
                Departure departure=r.departureList.get(i);
                Date departureDate=r.departureDateList.get(i);
                int datePassed=r.dateList.get(i);
                Stopping start=r.startList.get(i),end=r.endList.get(i);
                Train train=departure.getLine().getTrain();
                lineInfo.put("lineId",departure.getLine().getId());
                lineInfo.put("lineName",departure.getLine().getName());

                lineInfo.put("trainName",train.getName());
                lineInfo.put("trainType",train.getTrainType().getName());

                lineInfo.put("startDate",CommonMethod.addByDay(startDate,datePassed));
                lineInfo.put("startTime",CommonMethod.minToHourString((departure.getStart()+start.getArrival())%(24*60)));
                lineInfo.put("startStation",start.getStation().getName());
                lineInfo.put("startId",start.getStation().getId());

                lineInfo.put("duration",CommonMethod.minToHourString(end.getArrival()-start.getArrival()));

                int lastAllMin=departure.getStart()+end.getArrival();
                lineInfo.put("endTime",(lastAllMin>24*60?("+"+lastAllMin/(24*60)+"天 "):"")+CommonMethod.minToHourString(lastAllMin%(24*60)));
                lineInfo.put("endStation",end.getStation().getName());
                lineInfo.put("endId",end.getStation().getId());

                List seats=new ArrayList();
                List<Seat> enumSeat=new ArrayList<>();
                Map<Seat,Integer> cntSeat=new HashMap<>();
                List<TrainCoach> trainCoaches=trainCoachRepository.findByTrain(train);
                for(TrainCoach tc:trainCoaches){
                    Coach coach=tc.getCoach();
                    int numOfCoach=CommonMethod.numOfOnes(tc.getPosition());
                    List<CoachSeat> coachSeats=coachSeatRepository.findByCoach(coach);
                    for(CoachSeat cs:coachSeats){
                        Seat seat=cs.getSeat();
                        if(!enumSeat.contains(seat)){enumSeat.add(seat);cntSeat.put(seat,0);}
                        int extra=cs.numOfSeats()>0?cs.numOfSeats():coach.getMaxFreeCount();//free seat handler
                        cntSeat.replace(seat,cntSeat.get(seat)+extra*numOfCoach);
                    }
                }
                for(Seat seat:enumSeat){
                    Map m=new HashMap();
                    m.put("id",seat.getId());
                    m.put("name",seat.getName());
                    m.put("rest",cntSeat.get(seat)-ticketRepository.countByDepartureAndStartDateAndTicketStatus(departure,departureDate, ticketStatusRepository.getByName(ETicketStatus.SUCCEEDED)));
                    seats.add(m);
                }
                lineInfo.put("seats",seats);
                inRoute.add(lineInfo);
            }
            mRoute.put("lines",inRoute);
            dataList.add(mRoute);
        }
        Map finalRes=new HashMap();
        finalRes.put("table",dataList); finalRes.put("type",typeFliter);
        return CommonMethod.getReturnData(finalRes);
    }






    //transfer algorithm
    private final Comparator<DepartureNStopping> moneyPri=new Comparator<DepartureNStopping>() {public int compare(DepartureNStopping o1, DepartureNStopping o2){return (int) (o1.moneySpent()-o2.moneySpent());}},
                                                 timePri=new Comparator<DepartureNStopping>() {public int compare(DepartureNStopping o1, DepartureNStopping o2){return (int) (o1.timeSpent()-o2.timeSpent());}};
    private final static int MAX_TRANSFER=3,MAX_ROUTES=30,MAX_TRANS_TIME=240,MIN_IN_CITY=60;
    private List<Line> lineVisited=new ArrayList<>();
    private Map<Station,Integer> toDest=new HashMap<>();//distance to destination
    private Map<Station,List<Station>> mapDest=new HashMap<>();
    private List<Station> from,to;
    private java.sql.Date startDate;
    private int routeFound;
    private boolean fromCity=false,toCity=false;
    private String fromId,toId;
    private Boolean priMoney=false,allowedInCity=false;
    private Integer maxTrans=1;
    private static class Route{
        private List<Departure> departureList=new ArrayList<>();
        private List<Date> departureDateList=new ArrayList<>();
        private List<Stopping> startList=new ArrayList<>();
        private List<Stopping> endList=new ArrayList<>();
        private List<Integer> dateList=new ArrayList<>();
        private int daySpent=0,minSpent=0;
        String getType(){
            List<TrainType> good=new ArrayList<>();
            for(Departure d:departureList){
                TrainType cur=d.getLine().getTrain().getTrainType();
                if(!good.contains(cur))good.add(cur);
                if(good.size()>1)return "混合";
            }
            return good.get(0).getName();
        }
        Route(Departure departure,Date date,Stopping stop){
            this.departureList.add(departure);
            this.departureDateList.add(date);
            this.startList.add(stop);
            dateList.add(0);
            minSpent=(departure.getStart()+stop.getArrival())%(24*60);
        }
        Route(Route another){//deep copy
            departureList=new ArrayList<>(another.departureList);
            departureDateList=new ArrayList<>(another.departureDateList);
            startList=new ArrayList<>(another.startList);
            endList=new ArrayList<>(another.endList);
            dateList=new ArrayList<>(another.dateList);
            daySpent=another.daySpent; minSpent=another.minSpent;
        }
        void append(Departure departure,Date date,Stopping stop){
            departureList.add(departure);
            departureDateList.add(date);
            startList.add(stop);
            int todayMin=(departure.getStart()+stop.getArrival())%(24*60);
            if(todayMin<minSpent)daySpent++;
            minSpent=todayMin;
            dateList.add(daySpent);
        }
        void pushEnd(Stopping stop){
            endList.add(stop);
            int deltaMin=stop.getArrival()-getLastStart().getArrival();
            minSpent+=deltaMin;
            daySpent+=minSpent/(24*60);
            minSpent%=24*60;
        }
        Stopping getLastStart(){return startList.get(startList.size()-1);}
        Stopping getLastEnd(){return endList.get(endList.size()-1);}
        int getDaySpent(){return daySpent;}
    }
    private List<Route> getRoutes(){
        routeFound=0;
        List<Route> res=new ArrayList<>();

        List<Line> trySingle;
        if(fromCity&&toCity)trySingle=lineRepository.findFromCityToCity(fromId,toId);
        else if(fromCity)trySingle=lineRepository.findFromCityToStation(fromId,toId);
        else if(toCity)trySingle=lineRepository.findFromStationToCity(fromId,toId);
        else trySingle=lineRepository.findFromStationToStation(fromId,toId);
        if(trySingle!=null){
            double remain=1; Random random=new Random();
            if(trySingle.size()>1.2*MAX_ROUTES)remain=(double)MAX_ROUTES*1.2/trySingle.size();
            List<DepartureNStopping> ready=new ArrayList<>();
            for(Line l:trySingle){
                if(random.nextDouble()>remain)continue;
                List<Departure> curDept=departureRepository.findByLine(l);
                Stopping start=fromCity?stoppingRepository.findFirstByLineAndCity(l.getId(),fromId):stoppingRepository.findFirstByLineAndStation(l.getId(),fromId),
                        end=toCity?stoppingRepository.findLastByLineAndCity(l.getId(),toId):stoppingRepository.findLastByLineAndStation(l.getId(),toId);
                for(Departure d:curDept) {
                    if (!isValid(d, start, startDate)) continue;
                    ready.add(new DepartureNStopping(d,CommonMethod.addByDay(startDate,-(d.getStart()+start.getArrival())/(24*60)),end,start));
                }
            }
            if(ready.size()>0){
                ready.sort(priMoney?moneyPri:timePri);
                for(DepartureNStopping ds:ready){
                    Route tmp=new Route(ds.departure,ds.date,ds.start); tmp.pushEnd(ds.stopping);
                    res.add(tmp);
                    if(++routeFound>MAX_ROUTES)return res;
                }
                return res;
            }
        }
        if(maxTrans==0)return res;


        lineVisited.clear();
        Station okOne=initToDest();
        if(okOne==null)return res;
        List<DepartureNStopping> nxt;
        nxt=fromCity?getValidDepartureInCity(okOne,startDate,0,24*60):getValidDeparture(from.get(0),startDate,0,24*60);
        nxt.sort(priMoney?timePri:moneyPri);
        for(DepartureNStopping ds:nxt){
            if(routeFound>MAX_ROUTES)break;
            Stopping start=ds.start,end=ds.stopping; Departure dep=ds.departure;
            Route tmp=new Route(dep,ds.date,start); tmp.pushEnd(end);
            if(toDest.get(end.getStation())==1){res.add(tmp);routeFound++;continue;}
            res.addAll(dfs2(tmp,1));
        }
        return res;
    }
    private List<Route> dfs2(Route route,int depth){
        if(depth>MAX_TRANSFER||routeFound>=MAX_ROUTES)return new ArrayList<>();
        List<Route> res=new ArrayList<>();
        Stopping last=route.getLastEnd();
        List<DepartureNStopping> nxt=allowedInCity?getValidDepartureInCity(last.getStation(),CommonMethod.addByDay(startDate,route.daySpent),route.minSpent,route.minSpent+MAX_TRANS_TIME):
                getValidDeparture(last.getStation(),CommonMethod.addByDay(startDate,route.daySpent),route.minSpent,route.minSpent+MAX_TRANS_TIME);
        nxt.sort(priMoney?timePri:moneyPri);
        for(DepartureNStopping ds:nxt){
            Stopping start=ds.start,end=ds.stopping; Departure dep=ds.departure;
            Route tmp=new Route(route);
            tmp.append(dep,ds.date,start); tmp.pushEnd(end);
            if(toDest.get(end.getStation())==1){res.add(tmp);routeFound++;lineVisited.add(ds.departure.getLine());return(res);}
            List<Route> rest=dfs2(tmp,depth+1);
            if(rest.size()!=0) {
                res.addAll(rest);
                lineVisited.add(ds.departure.getLine());
            }
        }
        return res;
    }



    private static class DepartureNStopping{
        Departure departure; Date date; Stopping stopping,start;
        DepartureNStopping(Departure departure, Date date,Stopping stopping,Stopping start) {
            this.departure = departure;
            this.date=date;
            this.stopping = stopping;
            this.start=start;
        }
        double moneySpent(){return stopping.getConstant()-start.getConstant();}
        int timeSpent(){return stopping.getArrival()-start.getArrival();}
    }
    private List<DepartureNStopping> getValidDepartureInCity(Station station,java.sql.Date date,int startMin,int endMin){
        List<Station> stations=stationRepository.findByCity(station.getCity());
        List<DepartureNStopping> res=new ArrayList<>();
        int curDis=toDest.get(station);
        for(Station s:stations) {
            Integer thisDis=toDest.get(s);
            if(thisDis==null)continue; if(thisDis>curDis)continue;
            if (s.equals(station))
                res.addAll(getValidDeparture(s, date, startMin, endMin));
            else if (startMin + MIN_IN_CITY <= endMin)
                res.addAll(getValidDeparture(s, date, startMin + MIN_IN_CITY, endMin));
        }
        return res;
    }
    private List<DepartureNStopping> getValidDeparture(Station station,java.sql.Date date,int startMin,int endMin){
        if(endMin>24*60){//inter day
            List<DepartureNStopping> tmp=getValidDeparture(station,date,startMin,24*60);
            tmp.addAll(getValidDeparture(station,CommonMethod.addByDay(date,1),0,endMin%(24*60)));
            return tmp;
        }
        List<DepartureNStopping> res=new ArrayList<>();
        for(Station nxtSta:mapDest.get(station)){//find next lines with direction
            List<Line> lines=lineRepository.findByOrderedStations(station.getId(),nxtSta.getId());
            for(Line l:lines){
                if(lineVisited.contains(l))continue;
                List<Departure> curDept=departureRepository.findByLine(l);
                Stopping stp=stoppingRepository.findLastByLineAndStation(l.getId(), nxtSta.getId()),cur=stoppingRepository.findFirstByLineAndStation(l.getId(),station.getId());
                for(Departure d:curDept) {
                    //if(lineVisited.contains(l))break;
                    if (!isValid(d, cur, date)) continue;
                    int curMin = (d.getStart() + cur.getArrival()) % (24 * 60);
                    if (curMin >= startMin && curMin <= endMin){
                        //lineVisited.add(l);
                        res.add(new DepartureNStopping(d,CommonMethod.addByDay(date,-(d.getStart()+stp.getArrival())/(24*60)),stp,cur));
                    }
                }
            }
        }
        return res;
    }
    private Station initToDest(){
        toDest.clear();
        mapDest.clear();
        for(Station s:to)toDest.put(s,1);
        List<Station> cur=new ArrayList<>(to),nxt;
        for(int i=2;i<=1+maxTrans;i++){//spread from destination
            nxt=new ArrayList<>();
            for(Station s:cur){//spread from every station
                List<Station> stations=stationRepository.findDirectStations(s.getId());
                for(Station curStation:stations) {
                    if(!toDest.containsKey(curStation)){
                        if(!mapDest.containsKey(curStation))mapDest.put(curStation,new ArrayList<>());
                        mapDest.get(curStation).add(s);
                        if(!nxt.contains(curStation))nxt.add(curStation);
                    }
                }
            }
            cur=nxt;
            int flag=-1;
            for(Station s:cur){toDest.put(s,i);if(flag==-1&&from.contains(s))flag=from.indexOf(s);}
            if(flag!=-1)return from.get(flag);
        }
        Station ready=null;
        for(Station s:from){//spread from origin
            List<Station> stations=stationRepository.findDirectStations(s.getId());
            for(Station sta:stations) {
                if(from.contains(sta))continue;
                if (toDest.containsKey(sta)) {
                    ready = s;
                    if (!toDest.containsKey(s)) {
                        toDest.put(s, toDest.get(sta) + 1);
                        mapDest.put(s, new ArrayList<>());
                    }
                    mapDest.get(s).add(sta);
                }
            }
        }
        return ready;
    }



    private boolean isValid(Departure departure,java.sql.Date curDate){
        Line line=departure.getLine();
        //Run date
        List<RunDate> runDates=runDateRepository.findByLine(line);
        for(RunDate rd:runDates)
            if(curDate.before(rd.getEnd())&&curDate.after(rd.getStart())) {
                if (line.isRegular()) return false;
            }else if(!line.isRegular())return false;
        //Holiday & Weekend
        boolean isHoliday=false,isWeekend=false;
        for(CountryHoliday ch:countryHolidayRepository.findByCountry(stoppingRepository.findFirstInLine(line.getId()).getStation().getCity().getProvince().getCountry())){
            Holiday h=ch.getHoliday();
            if(curDate.before(h.getEnd())&&curDate.after(h.getStart())){isHoliday=true;break;}
        }
        if(!isHoliday){
            Calendar c=Calendar.getInstance();
            c.setTime(curDate);
            int weekDay=c.get(Calendar.DAY_OF_WEEK);
            if(weekDay==1||weekDay==7)isWeekend=true;
        }
        int symbol;
        if(isHoliday)symbol=1;
        else if(isWeekend)symbol=2;
        else symbol=4;
        return (departure.getSchedule()&symbol)>0;
    }
    private boolean isValid(Departure d,Stopping stop,java.sql.Date curDate){
        return isValid(d, CommonMethod.addByDay(curDate,-((stop.getArrival()+d.getStart())/(60*24))));
    }

}
