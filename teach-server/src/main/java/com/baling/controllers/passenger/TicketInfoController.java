package com.baling.controllers.passenger;

import com.baling.models.line.Line;
import com.baling.models.line.Stopping;
import com.baling.models.ticket.Ticket;
import com.baling.models.ticket.Travel;
import com.baling.models.user.Passenger;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.line.LineRepository;
import com.baling.repository.line.StoppingRepository;
import com.baling.repository.ticket.TicketRepository;
import com.baling.repository.ticket.TicketStatusRepository;
import com.baling.repository.ticket.TravelRepository;
import com.baling.repository.user.PassengerRepository;
import com.baling.repository.user.UserRepository;
import com.baling.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ticket")
public class TicketInfoController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    TravelRepository travelRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    StoppingRepository stoppingRepository;

    @PostMapping("/getTravelList")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse getTravelList(@Valid @RequestBody DataRequest dataRequest){
        Passenger host=passengerRepository.findByUser(userRepository.findByUserId(CommonMethod.getUserId()).get());
        List travelList=new ArrayList();
        List<Travel> travels=travelRepository.findByPassenger(host);
        for(Travel travel:travels){
            Map travelMap=new HashMap();
            List<Ticket> tickets=ticketRepository.findByTravelOrderByOrderInTravel(travel);
            if(tickets.size()==0)continue;
            Ticket first=tickets.get(0),last=tickets.get(tickets.size()-1);
            Stopping firstStop=stoppingRepository.findByLineAndOrderInLine(first.getDeparture().getLine(),first.getStart()),
                    endStop=stoppingRepository.findByLineAndOrderInLine(last.getDeparture().getLine(), last.getEnd());
            travelMap.put("id",travel.getId());
            travelMap.put("date",CommonMethod.addByDay(first.getStartDate(),(first.getDeparture().getStart()+firstStop.getArrival())/(24*60)));
            travelMap.put("time",CommonMethod.minToHourString(first.getDeparture().getStart()+firstStop.getArrival()));
            travelMap.put("from",firstStop.getStation().getName());
            travelMap.put("to",endStop.getStation().getName());
            List ticketList=new ArrayList();
            for(Ticket ticket:tickets){
                Map ticketMap=new HashMap();
                Line curLine=ticket.getDeparture().getLine();
                ticketMap.put("from",stoppingRepository.findByLineAndOrderInLine(curLine,ticket.getStart()).getStation().getName());
                ticketMap.put("name",curLine.getName());
                ticketMap.put("to",stoppingRepository.findByLineAndOrderInLine(curLine,ticket.getEnd()).getStation().getName());
                ticketMap.put("status",ticket.getTicketStatus().getName());
                ticketMap.put("seat",ticket.getSeat().getName()+" "+ticket.getCoachNum()+"车 "+ticket.getRowPosition()+ticket.getCol());
                ticketList.add(ticketMap);
            }
            travelMap.put("ticket",ticketList);
            travelList.add(travelMap);
        }
        return CommonMethod.getReturnData(travelList);
    }

}
