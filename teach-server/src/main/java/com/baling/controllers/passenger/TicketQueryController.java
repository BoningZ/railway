package com.baling.controllers.passenger;

import com.baling.models.administration.City;
import com.baling.models.administration.CompanyRefund;
import com.baling.models.administration.Currency;
import com.baling.models.administration.Station;
import com.baling.models.holiday.CountryHoliday;
import com.baling.models.holiday.Holiday;
import com.baling.models.line.*;
import com.baling.models.ticket.ETicketStatus;
import com.baling.models.ticket.Ticket;
import com.baling.models.ticket.Travel;
import com.baling.models.train.*;
import com.baling.models.user.Passenger;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.administration.CityRepository;
import com.baling.repository.administration.CompanyRefundRepository;
import com.baling.repository.administration.StationRepository;
import com.baling.repository.holiday.CountryHolidayRepository;
import com.baling.repository.line.*;
import com.baling.repository.ticket.TicketRepository;
import com.baling.repository.ticket.TicketStatusRepository;
import com.baling.repository.ticket.TravelRepository;
import com.baling.repository.train.CoachSeatRepository;
import com.baling.repository.train.SeatRepository;
import com.baling.repository.train.TrainCoachRepository;
import com.baling.repository.user.PassengerRepository;
import com.baling.repository.user.UserRepository;
import com.baling.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ticket")
public class TicketQueryController {
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
    @Autowired
    UserRepository userRepository;
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    TravelRepository travelRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    LinePriceRepository linePriceRepository;
    @Autowired
    CompanyRefundRepository companyRefundRepository;

    @PostMapping("/getTransferInfo")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse getTransferInfo(@Valid @RequestBody DataRequest dataRequest){
        Passenger passenger=passengerRepository.findByUser(userRepository.findByUserId(CommonMethod.getUserId()).get());
        maxTrans=dataRequest.getInteger("maxTransfer");
        startTime=dataRequest.getInteger("startTime");
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


        routes=getRoutes();
        if(routes.size()==0)return CommonMethod.getReturnMessageError("无法找到路径");
        return CommonMethod.getReturnData(routeWrapper(passenger));
    }

    @PostMapping("/getAlterRoutes")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse getAlterRoutes(@Valid @RequestBody DataRequest dataRequest){
        Passenger passenger=passengerRepository.findByUser(userRepository.findByUserId(CommonMethod.getUserId()).get());
        maxTrans=0;fromCity=toCity=true;
        startDate=dataRequest.getDate("startDate");
        startTime=dataRequest.getInteger("startTime");
        allowedInCity=dataRequest.getBoolean("transferInCity");
        priMoney=dataRequest.getString("priority").equals("money");
        Ticket ticket=ticketRepository.getById(dataRequest.getString("ticketId"));
        City cityFrom=stoppingRepository.findByLineAndOrderInLine(ticket.getDeparture().getLine(),ticket.getStart()).getStation().getCity(),
                cityTo=stoppingRepository.findByLineAndOrderInLine(ticket.getDeparture().getLine(),ticket.getEnd()).getStation().getCity();
        from=stationRepository.findByCity(cityFrom); to=stationRepository.findByCity(cityTo);
        fromId=cityFrom.getId(); toId=cityTo.getId();

        routes=getRoutes();
        if(routes.size()==0)return CommonMethod.getReturnMessageError("无法找到路径");
        Map routeInfos=routeWrapper(passenger);
        routeInfos.put("price",ticket.getPrice());
        return CommonMethod.getReturnData(routeInfos);
    }

    @PostMapping("/getTravel")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse getTravel(@Valid @RequestBody DataRequest dataRequest){
        Route route=routes.get(dataRequest.getInteger("routeId"));
        Passenger cur=passengerRepository.findByUser(userRepository.findByUserId(CommonMethod.getUserId()).get());
        Travel travel=new Travel();
        travel.setPassenger(cur);
        travel.setId((cur.getId().substring(0,4)+(new java.util.Date()).toString().substring(7)).substring(0,19).replace(' ','-'));
        travel.setBuyTime(new java.util.Date());
        travelRepository.save(travel);
        for(int i=0;i<route.departureList.size();i++){
            Ticket ticket=new Ticket();
            ticket.setId((cur.getId().substring(0,4)+(new java.util.Date()).toString().substring(12,14)+route.departureList.get(i).getId().substring(3)+"endOfQuoteRndOfQuoteEndOfQuote").substring(0,19).replace(' ','-'));
            ticket.setTravel(travel);
            ticket.setTicketStatus(ticketStatusRepository.getByName(ETicketStatus.UNPAID));
            ticket.setAltered(0);
            ticket.setOrderInTravel(i);
            ticket.setDeparture(route.departureList.get(i));
            ticket.setStart(route.startList.get(i).getOrderInLine());
            ticket.setEnd(route.endList.get(i).getOrderInLine());
            ticket.setStartDate(route.departureDateList.get(i));
            ticket.setPrice(0);

            assignSeat(ticket,seatRepository.getById("GLO-NULL"),0,"无效",0);
            ticketRepository.save(ticket);
        }

        Map m=new HashMap(); m.put("travelId",travel.getId());
        return CommonMethod.getReturnData(m);
    }

    @PostMapping("/payTravel")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse payTravel(@Valid @RequestBody DataRequest dataRequest){
        Passenger passenger=passengerRepository.findByUser(userRepository.findByUserId(CommonMethod.getUserId()).get());
        Currency pCurrency=passenger.getCountry().getCurrency();
        List<Double> priceList=dataRequest.getList("priceList");
        List<String> seatIdList=dataRequest.getList("seatList");
        List<Seat> seatList=new ArrayList<>();
        for(String seatId:seatIdList){seatList.add(seatRepository.getById(seatId));}
        List<String> colList=dataRequest.getList("colList");
        List<String> fellowIdList=dataRequest.getList("fellowList");
        Travel travel=travelRepository.getById(dataRequest.getString("travelId"));

        List<Ticket> readyToAdd=ticketRepository.findByTravelOrderByOrderInTravel(travel);
        if(fellowIdList!=null&&fellowIdList.size()>0){
            List<Ticket> ref=new ArrayList<>(readyToAdd);
            for(String passengerId:fellowIdList){
                Passenger cur=passengerRepository.getById(passengerId);
                Travel newT=new Travel();
                newT.setPassenger(cur);
                newT.setId((cur.getId().substring(0,4)+(new java.util.Date()).toString().substring(7)).substring(0,19).replace(' ','-'));
                newT.setBuyTime(new java.util.Date());
                travelRepository.save(newT);
                for(Ticket t:ref){
                    Ticket curTicket=t.clone();
                    curTicket.setId(t.getId().replace(travel.getPassenger().getId().substring(0,4),passengerId.substring(0,4)));
                    curTicket.setTravel(newT);
                    ticketRepository.save(curTicket);
                    readyToAdd.add(curTicket);
                }
            }
        }
        boolean notMatched=false,failed=false;
        for(Ticket t:readyToAdd){
            int order=t.getOrderInTravel();

            Currency tCurrency=t.getTravel().getPassenger().getCountry().getCurrency();
            t.setPrice(tCurrency.equals(pCurrency)?priceList.get(order):pCurrency.toAnother(tCurrency,priceList.get(order)));

            ETicketStatus result;
            if(assignForTicket(t,seatList.get(order),colList.get(order),0))result=ETicketStatus.SUCCEEDED;
            else {
                notMatched=true;
                if(assignForTicket(t,seatList.get(order),null,0))result=ETicketStatus.SUCCEEDED;
                    else {failed=true; result=ETicketStatus.QUEUEING;}
            }
            t.setTicketStatus(ticketStatusRepository.getByName(result));
            ticketRepository.save(t);
        }
        String returnMessage="";
        if(!(notMatched||failed))returnMessage="您所选所有座位余票充足";
        if(notMatched)returnMessage="您有所选座位余票不足，已自动分配";
        if(failed)returnMessage+="   有订单需要候补";
        return CommonMethod.getReturnMessageOK(returnMessage);
    }

    @PostMapping("/getTravelInfo")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse getTravelInfo(@Valid @RequestBody DataRequest dataRequest){
        Travel travel=travelRepository.getById(dataRequest.getString("travelId"));
        int status=1;
        List<Ticket> tickets=ticketRepository.findByTravelOrderByOrderInTravel(travel);
        List ticketList=new ArrayList();
        for(Ticket t:tickets){
            Map curT=new HashMap();
            curT.put("id",t.getId());
            curT.put("price",t.getPrice());
            curT.put("coachNum",t.getCoachNum());
            curT.put("altered",t.getAltered());
            if(t.getCol()!=null&&!t.getCol().equals("自由席")) {
                curT.put("row", t.getRowPosition());
                curT.put("col", t.getCol());
            }else{
                curT.put("row","自由席");
                curT.put("col","自由席");
            }
            curT.put("seatName",t.getSeat().getName());
            curT.put("status",t.getTicketStatus().getName().name());
            if(t.getTicketStatus().equals(ticketStatusRepository.getByName(ETicketStatus.UNPAID)))status=0;
            Departure departure=t.getDeparture();  Line line=departure.getLine(); Date departureDate=t.getStartDate();
            Stopping start=stoppingRepository.findByLineAndOrderInLine(line,t.getStart()), end=stoppingRepository.findByLineAndOrderInLine(line,t.getEnd());
            curT.putAll(getLineInfo(departure,start,end,departureDate,travel.getPassenger().getCountry().getCurrency()));
            ticketList.add(curT);
        }
        Map resM=new HashMap();
        resM.put("status",status);resM.put("tickets",ticketList);
        return CommonMethod.getReturnData(resM);
    }

    @PostMapping("/alterTicket")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse alterTicket(@Valid @RequestBody DataRequest dataRequest){
        Route route=routes.get(dataRequest.getInteger("routeId"));
        Double price=dataRequest.getDouble("price");
        Seat seat=seatRepository.getById(dataRequest.getString("seatId"));
        Ticket ticket=ticketRepository.getById(dataRequest.getString("ticketId"));
        String col=dataRequest.getString("col");

        if(ticket.getAltered()>=ticket.getDeparture().getLine().getCompany().getMaxAlter())return CommonMethod.getReturnMessageError("已超过该公司改签上限！");
        if(!okToEdit(ticket))return CommonMethod.getReturnMessageError("超过发车时间，不允许改签");
        ticket.setAltered(ticket.getAltered()+1);
        ticket.setDeparture(route.departureList.get(0));
        ticket.setStart(route.startList.get(0).getOrderInLine());
        ticket.setEnd(route.endList.get(0).getOrderInLine());
        ticket.setStartDate(route.departureDateList.get(0));
        ticket.setPrice(price);

        boolean notMatched=false,failed=false;
        ETicketStatus result;
        if(assignForTicket(ticket,seat,col,0))result=ETicketStatus.SUCCEEDED;
        else {
            notMatched=true;
            if(assignForTicket(ticket,seat,null,0))result=ETicketStatus.SUCCEEDED;
            else {failed=true; result=ETicketStatus.QUEUEING;}
        }
        ticket.setTicketStatus(ticketStatusRepository.getByName(result));
        ticketRepository.save(ticket);

        String returnMessage="";
        if(!(notMatched||failed))returnMessage="您所选座位余票充足";
        if(notMatched)returnMessage="您所选座位余票不足，已自动分配";
        if(failed)returnMessage+="   订单需要候补";
        return CommonMethod.getReturnMessageOK(returnMessage);
    }

    @PostMapping("/getRefundInfo")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse getRefundInfo(@Valid @RequestBody DataRequest dataRequest){
        Ticket ticket=ticketRepository.getById(dataRequest.getString("ticketId"));
        Map res=new HashMap();
        if(ticket.getTicketStatus().equals(ticketStatusRepository.getByName(ETicketStatus.CANCELED))){
            res.put("refundMsg","可全额退款");
            res.put("okToRefund",true);
            return CommonMethod.getReturnData(res);
        }
        if(!okToEdit(ticket)){
            res.put("refundMsg","已过开车时间，不可退票");
            res.put("okToRefund",false);
            return CommonMethod.getReturnData(res);
        }
        List<CompanyRefund> policies=companyRefundRepository.findByCompanyOrderByLeftHour(ticket.getDeparture().getLine().getCompany());
        int leftHour=CommonMethod.hourBetween(new java.util.Date(),getDeadline(ticket));
        double ratio=0;
        for(CompanyRefund cr:policies)
            if(cr.getLeftHour()<leftHour)ratio=cr.getRatio();
            else break;
        res.put("refundMsg","可退金额："+String.format("%.2f",ticket.getPrice()*ratio));
        res.put("okToRefund",true);
        return CommonMethod.getReturnData(res);
    }

    @PostMapping("/refund")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse refund(@Valid @RequestBody DataRequest dataRequest) {
        Ticket ticket = ticketRepository.getById(dataRequest.getString("ticketId"));
        ticket.setTicketStatus(ticketStatusRepository.getByName(ETicketStatus.REFUNDED));
        ticketRepository.save(ticket);
        return CommonMethod.getReturnMessageOK();
    }




    //get info
    private Map routeWrapper(Passenger passenger){
        List dataList=new ArrayList();
        List<String> typeList=new ArrayList();
        List typeFilter=new ArrayList();
        for(Route r:routes){
            Map mRoute=new HashMap();
            int startTime=(r.startList.get(0).getArrival()+r.departureList.get(0).getStart())%(24*60);
            mRoute.put("start",CommonMethod.minToHourString(startTime));
            mRoute.put("duration",CommonMethod.minToHourChinese(r.minSpent+r.daySpent*24*60-startTime));
            mRoute.put("end",(r.daySpent>0?("+"+r.daySpent+"天  "):"")+CommonMethod.minToHourString(r.minSpent));
            mRoute.put("name",r.getName());
            mRoute.put("routeId",routes.indexOf(r));
            String type=r.getType();
            mRoute.put("type",type);
            if(!typeList.contains(type)){
                typeList.add(type);
                Map mmm=new HashMap();
                mmm.put("text",type); mmm.put("value",type);
                typeFilter.add(mmm);
            }
            List inRoute=new ArrayList();
            for(int i=0;i<r.departureList.size();i++){
                Departure departure=r.departureList.get(i);
                Date departureDate=r.departureDateList.get(i);
                Stopping start=r.startList.get(i),end=r.endList.get(i);
                inRoute.add(getLineInfo(departure,start,end,departureDate,passenger.getCountry().getCurrency()));
            }
            mRoute.put("lines",inRoute);
            dataList.add(mRoute);
        }
        Map finalRes=new HashMap();
        finalRes.put("table",dataList); finalRes.put("type",typeFilter);
        return finalRes;
    }
    private Map getLineInfo(Departure departure, Stopping start, Stopping end, Date date, Currency currency){
        Map lineInfo=new HashMap();
        lineInfo.put("via",getVia(start,end,departure));

        Train train=departure.getLine().getTrain();

        lineInfo.put("lineId",departure.getLine().getId());
        lineInfo.put("lineName",departure.getLine().getName());

        lineInfo.put("trainName",train.getName());
        lineInfo.put("trainType",train.getTrainType().getName());

        lineInfo.put("startDate",CommonMethod.addByDay(date,(departure.getStart()+start.getArrival())/(24*60)));
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
        Map<Seat,List<List<List<String>>>> arrange=new HashMap<>();
        for(TrainCoach tc:trainCoaches){
            Coach coach=tc.getCoach();
            int numOfCoach=CommonMethod.numOfOnes(tc.getPosition());
            List<CoachSeat> coachSeats=coachSeatRepository.findByCoach(coach);
            for(CoachSeat cs:coachSeats){
                Seat seat=cs.getSeat();
                if(!enumSeat.contains(seat)){enumSeat.add(seat);cntSeat.put(seat,0);arrange.put(seat,new ArrayList<>());}
                List<List<String>> curArrange=cs.getColList();
                if(!arrange.get(seat).contains(curArrange))arrange.get(seat).add(curArrange);
                int extra=cs.numOfSeats();
                cntSeat.replace(seat,cntSeat.get(seat)+extra*numOfCoach);
            }
        }
        for(Seat seat:enumSeat){
            Map m=new HashMap();
            m.put("id",seat.getId());
            m.put("name",seat.getName());
            m.put("rest",cntSeat.get(seat)-ticketRepository.countByDepartureAndStartDateAndTicketStatusAndSeatAndStartLessThanAndEndGreaterThan(departure,date, ticketStatusRepository.getByName(ETicketStatus.SUCCEEDED),seat, end.getOrderInLine() ,start.getOrderInLine()));
            m.put("queueing",ticketRepository.countByDepartureAndStartDateAndTicketStatusAndSeatAndStartLessThanAndEndGreaterThan(departure,date,ticketStatusRepository.getByName(ETicketStatus.QUEUEING),seat, end.getOrderInLine(),start.getOrderInLine()));
            m.put("price",String.format("%.2f",start.getStation().getCity().getProvince().getCountry().getCurrency().toAnother(currency,getPrice(departure,start,end,seat,date))));
            m.put("currency",currency.getName());
            m.put("cols",arrange.get(seat));
            seats.add(m);
        }
        lineInfo.put("seats",seats);

        return lineInfo;
    }
    private double getPrice(Departure departure,Stopping start,Stopping end,Seat seat,Date date){
        Line line=departure.getLine();
        int week=1<<(CommonMethod.getWeekDay(date)-1),day=1<<(departure.getStart()/60);
        List<LinePrice> lps=null;
        if(judgeHoliday(line,date))lps=linePriceRepository.getByLineAndDayAndWeekAndHoliday(line.getId(),day,week,true);
        if(lps==null)lps=linePriceRepository.getByLineAndDayAndWeekAndHoliday(line.getId(),day,week,false);
        double kLine=1,bLine=0,bSeat,kSeat,bStop;
        for(LinePrice lp:lps)
            if(lp.getUpdated().before(date)){kLine=lp.getPower();bLine=lp.getConstant();break;}
        kSeat=seat.getPower(); bSeat=seat.getConstant();
        bStop=end.getConstant()-start.getConstant();
        return CommonMethod.priceFormula(kLine,bLine,kSeat,bSeat,bStop);
    }
    private List getVia(Stopping start,Stopping end,Departure departure){
        List l=new ArrayList();
        List<Stopping> via=stoppingRepository.findBetween(departure.getLine().getId(),start.getOrderInLine(),end.getOrderInLine());
        int dTime=departure.getStart();
        for(Stopping s:via){
            Map m=new HashMap();
            m.put("name",s.getStation().getName());
            m.put("arrival",CommonMethod.minToHourString((s.getArrival()+dTime)%(24*60)));
            m.put("stay",s.getStay()+"分");
            l.add(m);
        }
        return l;
    }


    //assign seat algorithm
    private boolean assignForTicket(Ticket ticket,Seat seat,String col,int coachNum){
        List<TrainCoach> trainCoaches=trainCoachRepository.findByTrain(ticket.getDeparture().getLine().getTrain());
        for(TrainCoach tc:trainCoaches){
            if(coachNum>0)if((tc.getPosition()&(1<<(coachNum-1)))==0)continue;
            List<Integer> nums=coachNum>0?Collections.singletonList(coachNum):CommonMethod.getNum(tc.getPosition());
            Coach coach=tc.getCoach();
            List<CoachSeat> coachSeats=coachSeatRepository.findByCoach(coach);
            for(CoachSeat cs:coachSeats){
                if(!cs.getSeat().equals(seat))continue;
                for(Integer num:nums){
                    List<List<String>> splits=col==null?cs.getColList():Collections.singletonList(Collections.singletonList(col));
                    for(List<String> split:splits)
                        for(String c:split){
                            if(c.equals("自由席")){assignSeat(ticket,seat,num,c,-1);return true;}
                            if(cs.numOfSeats()>getBought(ticket,seat,c,num)){assignSeat(ticket,seat,num,c,getAvailableRow(ticket,seat,c,num));return true;}
                        }
                }
            }
        }
        return false;
    }
    private void assignSeat(Ticket ticket,Seat seat,int coachNum,String col,int row){
        ticket.setSeat(seat);
        ticket.setCoachNum(coachNum);
        ticket.setCol(col);
        ticket.setRowPosition(row);
    }
    private int getAvailableRow(Ticket ticket,Seat seat,String col,int coachNum){
        Coach curCoach=trainCoachRepository.nativeFindByTrainIdAndPositionBit(ticket.getDeparture().getLine().getTrain().getId(),1<<(coachNum-1)).getCoach();
        List<CoachSeat> coachSeat=coachSeatRepository.findByCoachAndSeatAndAndColsContains(curCoach,seat,col);
        List<Integer> availableRow=new ArrayList<>();
        for(CoachSeat cs:coachSeat)availableRow.addAll(CommonMethod.getNum(cs.getRowsPosition()));
        List<Ticket> tickets=ticketRepository.findByDepartureAndStartDateAndTicketStatusAndSeatAndColAndCoachNumAndStartLessThanAndEndGreaterThanOrderByRowPosition(ticket.getDeparture(),ticket.getStartDate(),ticketStatusRepository.getByName(ETicketStatus.SUCCEEDED),seat,col,coachNum,ticket.getEnd(),ticket.getStart());
        if(col.equals("顺序编号")){
            int res=0;
            for(Ticket t:tickets)
                if(t.getRowPosition()>res+1)return res+1;
                else res=t.getRowPosition();
            return 1;
        }
        int occupied=0;
        for(Ticket t:tickets)occupied|=(1<<(t.getRowPosition()-1));
        for(int pos:availableRow)if((occupied&(1<<(pos-1)))==0)return pos;
        return 0;
    }
    private int getBought(Ticket ticket,Seat seat,String col,int coachNum){
        int res=0;
        List<Ticket> tickets=ticketRepository.findByDepartureAndStartDateAndTicketStatusAndSeatAndStartLessThanAndEndGreaterThan(ticket.getDeparture(),ticket.getStartDate(),ticketStatusRepository.getByName(ETicketStatus.SUCCEEDED),seat,ticket.getEnd(),ticket.getStart());
        tickets.addAll(ticketRepository.findByDepartureAndStartDateAndTicketStatusAndSeatAndStartLessThanAndEndGreaterThan(ticket.getDeparture(),ticket.getStartDate(),ticketStatusRepository.getByName(ETicketStatus.QUEUEING),seat,ticket.getEnd(),ticket.getStart()));
        for(Ticket t:tickets){
            if(col!=null)if(!t.getCol().equals(col))continue;
            if(coachNum>0)if(t.getCoachNum()!=coachNum)continue;
            res++;
        }
        return res;
    }

    //refund or alter check
    private boolean okToEdit(Ticket ticket){
        return (new java.util.Date()).before(getDeadline(ticket));
    }
    private java.util.Date getDeadline(Ticket ticket){
        Stopping first=stoppingRepository.findByLineAndOrderInLine(ticket.getDeparture().getLine(),ticket.getStart());
        return CommonMethod.addByMin(ticket.getStartDate(),ticket.getDeparture().getStart()+first.getArrival());
    }








    //transfer algorithm
    private final Comparator<DepartureNStopping> moneyPri= (o1, o2) -> (int) (o2.moneySpent()-o1.moneySpent()),
                                                 timePri= (o1, o2) -> (int) (o2.timeSpent()-o1.timeSpent());
    private final static int MAX_TRANSFER=3,MAX_ROUTES=30,MAX_TRANS_TIME=240,MIN_IN_CITY=60;
    private List<Line> lineVisited=new ArrayList<>();
    private Map<Station,Integer> toDest=new HashMap<>();//distance to destination
    private Map<Station,List<Station>> mapDest=new HashMap<>();
    private List<Station> from,to;
    private java.sql.Date startDate;
    private int startTime;
    private int routeFound;
    private List<Route> routes;
    private boolean fromCity=false,toCity=false;
    private String fromId,toId;
    private Boolean priMoney=false,allowedInCity=false;
    private Integer maxTrans=1;



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
                    if((d.getStart()+start.getArrival())%(24*60)<startTime*60)continue;
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
        nxt=fromCity?getValidDepartureInCity(okOne,startDate,startTime*60,24*60):getValidDeparture(from.get(0),startDate,startTime*60,24*60);
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

    private boolean judgeHoliday(Line line,Date date){
        for(CountryHoliday ch:countryHolidayRepository.findByCountry(stoppingRepository.findFirstInLine(line.getId()).getStation().getCity().getProvince().getCountry())){
            Holiday h=ch.getHoliday();
            if(date.before(h.getEnd())&&date.after(h.getStart()))return true;
        }
        return false;
    }
    private boolean judgeWeekend(Date date){
        Calendar c=Calendar.getInstance();
        c.setTime(date);
        int weekDay=c.get(Calendar.DAY_OF_WEEK);
        return weekDay == 1 || weekDay == 7;
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
        boolean isHoliday=judgeHoliday(line,curDate),isWeekend=false;
        if(!isHoliday)isWeekend=judgeWeekend(curDate);
        int symbol;
        if(isHoliday)symbol=1;
        else if(isWeekend)symbol=2;
        else symbol=4;
        return (departure.getSchedule()&symbol)>0;
    }
    private boolean isValid(Departure d,Stopping stop,java.sql.Date curDate){
        return isValid(d, CommonMethod.addByDay(curDate,-((stop.getArrival()+d.getStart())/(60*24))));
    }

    private static class Route{
        private List<Departure> departureList=new ArrayList<>();
        private List<Date> departureDateList=new ArrayList<>();
        private List<Stopping> startList=new ArrayList<>();
        private List<Stopping> endList=new ArrayList<>();
        private List<Integer> dateList=new ArrayList<>();
        private int daySpent=0,minSpent=0;
        String getName(){
            StringBuilder res= new StringBuilder();
            for(int i=0;i<departureList.size();i++){
                if(i!=0)
                    res.append(endList.get(i - 1).getStation().equals(startList.get(i).getStation()) ? "(同站换乘)" : "(同城换乘)");
                res.append(startList.get(i).getStation().getName()).append("--(").append(departureList.get(i).getLine().getName()).append(")->").append(endList.get(i).getStation().getName());
            }
            return res.toString();
        }
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
}
