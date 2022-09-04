package com.baling.controllers.company;

import com.baling.models.administration.Company;
import com.baling.models.line.*;
import com.baling.models.ticket.ETicketStatus;
import com.baling.models.ticket.Ticket;
import com.baling.models.user.AdminCompany;
import com.baling.models.user.Driver;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.administration.StationRepository;
import com.baling.repository.line.*;
import com.baling.repository.ticket.TicketRepository;
import com.baling.repository.ticket.TicketStatusRepository;
import com.baling.repository.train.TrainRepository;
import com.baling.repository.user.AdminCompanyRepository;
import com.baling.repository.user.DriverRepository;
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
@RequestMapping("/api/line")
public class LineController {
    @Autowired
    AdminCompanyRepository adminCompanyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LineRepository lineRepository;
    @Autowired
    StoppingRepository stoppingRepository;
    @Autowired
    DepartureRepository departureRepository;
    @Autowired
    RunDateRepository runDateRepository;
    @Autowired
    LinePriceRepository linePriceRepository;
    @Autowired
    TrainRepository trainRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    TicketStatusRepository ticketStatusRepository;
    @Autowired
    DriverRepository driverRepository;

    @PostMapping("/getLineTable")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse getLineTable(@Valid @RequestBody DataRequest dataRequest){
        Company company=adminCompanyRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCompany();
        List<Line> lineList=lineRepository.findTopHundredByFeatureLikeAndCompany("%"+dataRequest.getString("lineId")+"%",company.getId());
        List res=new ArrayList();
        for(Line line:lineList)res.add(lineWrapper(line));
        return CommonMethod.getReturnData(res);
    }

    @PostMapping("/getLineInfo")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse getLineInfo(@Valid @RequestBody DataRequest dataRequest){
        Line line=lineRepository.findById(dataRequest.getString("lineId")).get();
        return CommonMethod.getReturnData(lineWrapper(line));
    }

    @PostMapping("/submitLineStopping")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse submitLineStopping(@Valid @RequestBody DataRequest dataRequest){
        Company company=adminCompanyRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCompany();
        Map form=dataRequest.getMap("form");
        String id= (String) form.get("id");
        Line line;
        if(lineRepository.existsById(id)){
            if((boolean) form.get("isNew"))return CommonMethod.getReturnMessageError("该编号已存在");
            else line=lineRepository.getById(id);
        }else line=new Line();
        line.setId(id);
        line.setName((String) form.get("name"));
        line.setRegular((Boolean) form.get("regular"));
        line.setTrain(trainRepository.getById((String) form.get("trainId")));
        line.setCompany(company);
        lineRepository.save(line);
        if((boolean) form.get("isNew")){
            LinePrice linePrice=new LinePrice();
            linePrice.setLine(line);
            linePrice.setConstant(0); linePrice.setPower(1);
            linePrice.setUpdated(new Date(new java.util.Date().getTime()));
            linePrice.setHoliday(false); linePrice.setInDay(127);
            linePrice.setInWeek(16777215);
            linePriceRepository.save(linePrice);
        }
        if((boolean) form.get("rescheduled")) {
            stoppingRepository.deleteAll(stoppingRepository.findByLineOrderByOrderInLine(line));
            int order=0;
            for (Object o : (List)form.get("stoppings")) {
                Map m=(Map)o;
                Stopping stopping=new Stopping();
                stopping.setOrderInLine(order++);
                stopping.setLine(line);
                stopping.setArrival((Integer) m.get("arrival"));
                try{
                    stopping.setConstant((Double) m.get("constant"));
                }catch (Exception e){stopping.setConstant(((Integer)m.get("constant")));}

                stopping.setStay((Integer) m.get("stay"));
                stopping.setStation(stationRepository.getById((String) m.get("stationId")));
                stoppingRepository.save(stopping);
            }
            cancelTickets(line);
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/submitLineDeparture")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse submitLineDeparture(@Valid @RequestBody DataRequest dataRequest){
        Map form=dataRequest.getMap("form");
        Line line=lineRepository.getById((String) form.get("id"));
        if((Boolean)form.get("rescheduled")){
            cancelTickets(line);
            runDateRepository.deleteAll(runDateRepository.findByLine(line));
            departureRepository.deleteAll(departureRepository.findByLine(line));
            for(Object o:(List)form.get("runDates")){
                Map m=(Map)o;
                RunDate runDate=new RunDate();
                runDate.setStart(CommonMethod.toSqlDate(m.get("start")));
                runDate.setEnd(CommonMethod.toSqlDate(m.get("end")));
                runDate.setLine(line);
                runDateRepository.save(runDate);
            }
            for(Object o:(List)form.get("departures")){
                Map m=(Map)o;
                Departure departure=new Departure();
                departure.setDriver(driverRepository.getById((String) m.get("driverId")));
                departure.setLine(line);
                departure.setStart((Integer) m.get("start"));
                departure.setSchedule((Integer) m.get("schedule"));
                departure.setId(line.getId()+departure.getStart());
                departureRepository.save(departure);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/submitLinePrice")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse submitLinePrice(@Valid @RequestBody DataRequest dataRequest){
        Map form=dataRequest.getMap("form");
        Line line=lineRepository.getById((String) form.get("id"));
        linePriceRepository.deleteAll(linePriceRepository.getByLine(line));
        for(Object o:(List)form.get("prices")){
            Map m=(Map)o;
            LinePrice lp=new LinePrice();
            lp.setInWeek(CommonMethod.getStatus((List<Integer>) m.get("inWeek")));
            lp.setInDay(CommonMethod.getStatus((List<Integer>) m.get("inDay")));
            lp.setLine(line);
            lp.setHoliday((Boolean) m.get("holiday"));
            lp.setConstant(CommonMethod.parseNum(m.get("constant")));
            lp.setPower(CommonMethod.parseNum(m.get("power")));
            lp.setUpdated(CommonMethod.toSqlDate(m.get("updated")));
            linePriceRepository.save(lp);
        }
        return CommonMethod.getReturnMessageOK();
    }

    private void cancelTickets(Line line){
        List<Ticket> canceled=ticketRepository.findByLineAndTicketStatus(line.getId(), ticketStatusRepository.getByName(ETicketStatus.SUCCEEDED).getId());
        canceled.addAll(ticketRepository.findByLineAndTicketStatus(line.getId(), ticketStatusRepository.getByName(ETicketStatus.QUEUEING).getId()));
        for(Ticket t:canceled){t.setTicketStatus(ticketStatusRepository.getByName(ETicketStatus.CANCELED));ticketRepository.save(t);}
    }

    private Map lineWrapper(Line line){
        Map res=new HashMap();
        res.put("id",line.getId());
        res.put("name",line.getName());
        res.put("regular",line.isRegular());
        res.put("trainId",line.getTrain().getId());
        res.put("train",line.getTrain().getName());

        List stoppingList=new ArrayList();
        for(Stopping stp:stoppingRepository.findByLineOrderByOrderInLine(line)){
            Map stpMap=new HashMap();
            stpMap.put("stationId",stp.getStation().getId());
            stpMap.put("station",stp.getStation().getName());
            stpMap.put("arrival",stp.getArrival());
            stpMap.put("stay",stp.getStay());
            stpMap.put("constant",String.format("%.2f",stp.getConstant()));
            stoppingList.add(stpMap);
        }
        res.put("stoppings",stoppingList);

        List departureList=new ArrayList();
        for(Departure d:departureRepository.findByLine(line)){
            Map m=new HashMap();
            m.put("driverId",d.getDriver().getId());
            m.put("driver",d.getDriver().getName());
            m.put("start",d.getStart());
            m.put("startTime",CommonMethod.minToHourString(d.getStart()));
            m.put("schedule",d.getSchedule());
            departureList.add(m);
        }
        res.put("departures",departureList);

        List runDateList=new ArrayList();
        for(RunDate rd:runDateRepository.findByLine(line)){
            Map m=new HashMap();
            m.put("start",rd.getStart());
            m.put("end",rd.getEnd());
            runDateList.add(m);
        }
        res.put("runDates",runDateList);

        List priceList=new ArrayList();
        for(LinePrice lp:linePriceRepository.getByLine(line)){
            Map m=new HashMap();
            m.put("inDay",CommonMethod.getNum(lp.getInDay()));
            m.put("inWeek",CommonMethod.getNum(lp.getInWeek()));
            m.put("holiday",lp.isHoliday());
            m.put("constant",lp.getConstant());
            m.put("power",lp.getPower());
            m.put("updated",lp.getUpdated());
            priceList.add(m);
        }
        res.put("prices",priceList);

        return res;
    }
}
