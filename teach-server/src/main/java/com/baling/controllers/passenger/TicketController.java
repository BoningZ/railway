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
    public DataResponse getProfile(@Valid @RequestBody DataRequest dataRequest){
        String fromRange=dataRequest.getString("fromRange"),toRange=dataRequest.getString("toRange");
        switch (fromRange){
            case "city":from=stationRepository.findByCity(cityRepository.getById(dataRequest.getString("fromCityId")));break;
            case "station":from= Collections.singletonList(stationRepository.getById(dataRequest.getString("fromStationId")));break;
        }
        switch (toRange){
            case "city":to=stationRepository.findByCity(cityRepository.getById(dataRequest.getString("toCityId")));break;
            case "station":to= Collections.singletonList(stationRepository.getById(dataRequest.getString("toStationId")));break;
        }
        startDate=dataRequest.getDate("startDate");
        List<Route> routes=getRoutes();
        if(routes.size()==0)return CommonMethod.getReturnMessageError("无法找到路径");

        List dataList=new ArrayList();
        for(Route r:routes){
            Map mRoute=new HashMap();
            mRoute.put("start",CommonMethod.minToHourString(r.startList.get(0).getArrival()+r.departureList.get(0).getStart()));
            mRoute.put("duration",CommonMethod.minToHourString(r.minSpent+r.daySpent*24*60-(r.startList.get(0).getArrival()+r.departureList.get(0).getStart())));
            mRoute.put("end",(r.daySpent>0?("+"+r.daySpent+"天  "):"")+CommonMethod.minToHourString(r.minSpent));
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
                lineInfo.put("startStationTransfer",start.getStation().getTransferId());
                lineInfo.put("startId",start.getStation().getId());

                lineInfo.put("duration",CommonMethod.minToHourString(end.getArrival()-start.getArrival()));

                int lastAllMin=departure.getStart()+end.getArrival();
                lineInfo.put("endTime",(lastAllMin>24*60?("+"+lastAllMin/(24*60)+"天 "):"")+CommonMethod.minToHourString(lastAllMin%(24*60)));
                lineInfo.put("endStation",end.getStation().getName());
                lineInfo.put("endStationTransfer",end.getStation().getTransferId());
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
        return CommonMethod.getReturnData(dataList);
    }






    //transfer algorithm
    private final static int MAX_TRANSFER=7,MAX_ROUTES=100,MAX_TRANS_TIME=240,MIN_IN_CITY=60;
    private Map<Station,Integer> toDest=new HashMap<>();//distance to destination
    private List<Station> from,to;
    private java.sql.Date startDate;
    private int routeFound;
    private static class Route{
        private List<Departure> departureList=new ArrayList<>();
        private List<Date> departureDateList=new ArrayList<>();
        private List<Stopping> startList=new ArrayList<>();
        private List<Stopping> endList=new ArrayList<>();
        private List<Integer> dateList=new ArrayList<>();
        private int daySpent=0,minSpent=0;
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
        }
        void append(Departure departure,Date date,Stopping stop){
            departureList.add(departure);
            departureDateList.add(date);
            startList.add(stop);
            int todayMin=(departure.getStart()+stop.getArrival())%(24*60);
            if(todayMin<minSpent)daySpent++;
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
        List<Route> res=new ArrayList<>(),ready=new ArrayList<>();
        expand(to);expand(from);
        initToDest();
        for(Station s:from) {//init today's departure
            List<Stopping> stoppings = stoppingRepository.findByStation(s);
            for(Stopping stop:stoppings){
                Line line=stop.getLine();
                List<Departure> departures=departureRepository.findByLine(line);
                for(Departure d:departures)
                    if(isValid(d,stop,startDate))ready.add(new Route(d,CommonMethod.addByDay(startDate,-(stop.getArrival()+d.getStart())/(24*60)),stop));
            }
        }
        for(Route rr:ready)
            res.addAll(dfs(rr,1));
        return res;
    }
    private List<Route> dfs(Route route,int depth){
        if(depth>MAX_TRANSFER||routeFound>=MAX_ROUTES)return new ArrayList<>();
        List<Route> res=new ArrayList<>();
        Stopping last=route.getLastStart();
        List<Stopping> stoppings=stoppingRepository.findByLineOrderByOrderInLine(last.getLine());
        for(Stopping stp:stoppings){
            if(stp.getOrderInLine()<=last.getOrderInLine())continue;//previous stopping
            Integer curDis=toDest.get(stp);
            if(curDis!=null&&curDis<toDest.get(last)){//make advance to destination
                route.pushEnd(stp);
                if(curDis==1){res.add(route); return res;}//this branch ended by arriving
                else {//more needed
                    List<DepartureNStopping> nxt=getValidDepartureInCity(stp.getStation(),CommonMethod.addByDay(startDate,route.daySpent),route.minSpent,route.minSpent+MAX_TRANS_TIME);
                    for(DepartureNStopping ds:nxt){
                        Route tmp=new Route(route); tmp.append(ds.departure,ds.date,ds.stopping);
                        res.addAll(dfs(tmp,depth+1));
                    }
                }
                break;
            }
        }
        return res;
    }

    private static class DepartureNStopping{
        Departure departure; Date date; Stopping stopping;
        DepartureNStopping(Departure departure, Date date,Stopping stopping) {
            this.departure = departure;
            this.date=date;
            this.stopping = stopping;
        }
    }
    private List<DepartureNStopping> getValidDepartureInCity(Station station,java.sql.Date date,int startMin,int endMin){
        List<Station> stations=stationRepository.findByCity(station.getCity());
        List<DepartureNStopping> res=new ArrayList<>();
        for(Station s:stations)
            if (s.equals(station) || (station.getTransferId() != null && s.getTransferId().equals(station.getTransferId())))
                res.addAll(getValidDeparture(s,date,startMin,endMin));
            else if (startMin + MIN_IN_CITY <= endMin)
                res.addAll(getValidDeparture(s,date,startMin+MIN_IN_CITY,endMin));
        return res;
    }
    private List<DepartureNStopping> getValidDeparture(Station station,java.sql.Date date,int startMin,int endMin){
        if(endMin>24*60){//inter day
            List<DepartureNStopping> tmp=getValidDeparture(station,date,startMin,24*60);
            tmp.addAll(getValidDeparture(station,CommonMethod.addByDay(date,1),0,endMin));
            return tmp;
        }
        List<DepartureNStopping> res=new ArrayList<>();
        List<Stopping> everyStopping=stoppingRepository.findByStation(station);//get all stoppings at this station
        for(Stopping stp:everyStopping){
            List<Departure> curDept=departureRepository.findByLine(stp.getLine());
            for(Departure d:curDept) {
                if (!isValid(d, stp, date)) continue;
                int curMin = (d.getStart() + stp.getArrival()) % (24 * 60);
                if (curMin >= startMin && curMin <= endMin) res.add(new DepartureNStopping(d,CommonMethod.addByDay(date,-(d.getStart()+stp.getArrival())/(24*60)),stp));
            }
        }
        return res;
    }
    private void initToDest(){
        toDest.clear();
        for(Station s:to)toDest.put(s,1);
        List<Station> cur=new ArrayList<>(to),nxt;
        for(int i=2;i<=MAX_TRANSFER;i++){
            nxt=new ArrayList<>();
            for(Station s:cur){//spread from every station
                List<Stopping> stoppings=stoppingRepository.findByStation(s);
                List<Line> lines=new ArrayList<>();
                for(Stopping stop:stoppings)//init all lines via current station
                    if(!lines.contains(stop.getLine()))lines.add(stop.getLine());
                for(Line l:lines){//spread by lines
                    List<Stopping> stops=stoppingRepository.findByLineOrderByOrderInLine(l);
                    for(Stopping stop:stops){//spread if not visited
                        Station curStation=stop.getStation();
                        if(!toDest.containsKey(curStation)&&!nxt.contains(curStation))nxt.add(curStation);
                    }
                }
            }
            cur=nxt;
            expand(cur);
            for(Station s:cur)toDest.put(s,i);
        }
    }

    private Station toMain(Station station){return station.getTransferId()==null?station:stationRepository.getById(station.getTransferId());}
    private void expand(List<Station> stations){
        List<Station> willAdd=new ArrayList<>();
        for(Station s:stations)
            if(s.getTransferId()!=null)
                for(Station stat:stationRepository.findByTransferId(s.getTransferId()))
                    if(!willAdd.contains(stat)&&!stations.contains(stat))willAdd.add(stat);
        stations.addAll(willAdd);
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
        for(CountryHoliday ch:countryHolidayRepository.findByCountry(stoppingRepository.findByLineAndOrderInLine(line,1).getStation().getCity().getProvince().getCountry())){
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
