package com.baling.controllers.sales;

import com.baling.models.administration.Company;
import com.baling.models.administration.Currency;
import com.baling.models.line.Departure;
import com.baling.models.line.Line;
import com.baling.models.line.Stopping;
import com.baling.models.ticket.ETicketStatus;
import com.baling.models.ticket.Ticket;
import com.baling.models.train.Seat;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.line.DepartureRepository;
import com.baling.repository.line.LineRepository;
import com.baling.repository.line.StoppingRepository;
import com.baling.repository.ticket.TicketRepository;
import com.baling.repository.ticket.TicketStatusRepository;
import com.baling.repository.train.SeatRepository;
import com.baling.repository.user.AdminCompanyRepository;
import com.baling.repository.user.UserRepository;
import com.baling.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/sales")
public class SalesController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AdminCompanyRepository adminCompanyRepository;
    @Autowired
    LineRepository lineRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    DepartureRepository departureRepository;
    @Autowired
    TicketStatusRepository ticketStatusRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    StoppingRepository stoppingRepository;

    @PostMapping("/stat")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse stat(@Valid @RequestBody DataRequest dataRequest){
        Company company=adminCompanyRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCompany();
        List<Line> lineList=lineRepository.findTopHundredByFeatureLikeAndCompany("%"+dataRequest.getString("lineId")+"%",company.getId());
        Date start,end;
        if(dataRequest.getDate("start")==null){
            Date today=new Date(new java.util.Date().getTime());
            start=CommonMethod.addByDay(today,-7);
            end=CommonMethod.addByDay(today,7);
        }else{
            start=dataRequest.getDate("start");
            end=dataRequest.getDate("end");
        }
        List res=new ArrayList();
        for(Line line:lineList){
            Map lineM=new HashMap();
            lineM.put("id",line.getId());
            lineM.put("name",line.getName());
            List<Seat> seats=seatRepository.findByTrain(line.getTrain().getId());
            Map<Seat,Integer> seatMap=new HashMap<>();
            for(Seat s:seats)seatMap.put(s,0);
            Map<Seat,Integer> seatCountMapLine=new HashMap<>(seatMap);
            Map<Seat,Double> seatPriceMapLine=new HashMap();
            for(Seat s:seats)seatPriceMapLine.put(s,0.0);
            List<String> stations=new ArrayList<>();
            List<Stopping> stoppings=stoppingRepository.findByLineOrderByOrderInLine(line);
            Currency currency=stoppings.size()==0?null:stoppings.get(0).getStation().getCity().getProvince().getCountry().getCurrency();
            for(Stopping s:stoppings)stations.add(s.getStation().getName());
            lineM.put("stations",stations);
            int stationCount=stations.size();
            Map<Seat, int[]> seatStationTemplate=new HashMap<>();
            for(Seat s:seats)seatStationTemplate.put(s,new int[stationCount+2]);
            int count=0;double price=0;
            List details=new ArrayList();
            List<Departure> departures=departureRepository.findByLine(line);
            for(Departure d:departures){
                List<Ticket> valid=ticketRepository.findByDepartureAndTicketStatusAndStartDateBetween(d,ticketStatusRepository.getByName(ETicketStatus.SUCCEEDED),start,end);
                valid.addAll(ticketRepository.findByDepartureAndTicketStatusAndStartDateBetween(d,ticketStatusRepository.getByName(ETicketStatus.QUEUEING),start,end));
                valid.addAll(ticketRepository.findByDepartureAndTicketStatusAndStartDateBetween(d,ticketStatusRepository.getByName(ETicketStatus.USED),start,end));
                if(valid.size()>0){
                    valid.sort(Comparator.comparing(Ticket::getStartDate));
                    Date curStart=valid.get(0).getStartDate();
                    Map detailM=new HashMap();
                    Map<Seat,int[]> seatDetailM=null;
                    for(Ticket v:valid){
                        if(!v.getStartDate().equals(curStart)||valid.indexOf(v)==0){
                            curStart=v.getStartDate();
                            if (seatDetailM != null) {
                                List seatDetail=new ArrayList();
                                for(Seat s:seats){
                                    List curRes=new ArrayList();
                                    int ss=0;
                                    for(int i=1;i<=stationCount;i++){
                                        ss+=((int[])seatDetailM.get(s))[i];
                                        curRes.add(ss);
                                    }
                                    seatDetail.add(curRes);
                                }
                                detailM.put("seatDetail",seatDetail);
                                details.add(detailM);
                            }
                            detailM=new HashMap();
                            for(Seat s:seats)seatStationTemplate.replace(s,new int[stationCount+2]);
                            seatDetailM=new HashMap<>(seatStationTemplate);
                            detailM.put("startDate",v.getStartDate());
                            detailM.put("departureId",d.getId());
                            detailM.put("startTime",CommonMethod.minToHourString(d.getStart()));
                        }
                        Seat vs=v.getSeat();
                        seatDetailM.get(vs)[v.getStart()]++;
                        seatDetailM.get(vs)[v.getEnd()+1]--;
                        seatCountMapLine.replace(vs,seatCountMapLine.get(vs)+1);
                        double realPrice=v.getPriceInCurrency(currency);
                        seatPriceMapLine.replace(vs,seatPriceMapLine.get(vs)+realPrice);
                        count++; price+=realPrice;
                    }
                    if (seatDetailM != null) {
                        List seatDetail=new ArrayList();
                        for(Seat s:seats){
                            List curRes=new ArrayList();
                            int ss=0;
                            for(int i=1;i<=stationCount;i++){
                                ss+=((int[])seatDetailM.get(s))[i];
                                curRes.add(ss);
                            }
                            seatDetail.add(curRes);
                        }
                        detailM.put("seatDetail",seatDetail);
                        details.add(detailM);
                    }
                }
            }
            lineM.put("details",details);
            List<String> seatName=new ArrayList<>();
            List seatPrices=new ArrayList(),seatCounts=new ArrayList();
            for(Seat s:seats){
                seatName.add(s.getName());
                Map tmp=new HashMap();
                tmp.put("name",s.getName());
                tmp.put("num",seatCountMapLine.get(s));
                seatCounts.add(tmp);
                tmp=new HashMap();
                tmp.put("name",s.getName());
                tmp.put("num",seatPriceMapLine.get(s));
                seatPrices.add(tmp);
            }
            lineM.put("seats",seatName);
            lineM.put("seatPrices",seatPrices);
            lineM.put("seatCounts",seatCounts);
            lineM.put("count",count);
            lineM.put("price",price);
            res.add(lineM);
        }
        return CommonMethod.getReturnData(res);
    }
}
