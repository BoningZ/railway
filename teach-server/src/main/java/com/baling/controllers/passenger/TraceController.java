package com.baling.controllers.passenger;

import com.baling.models.line.Line;
import com.baling.models.line.Stopping;
import com.baling.models.ticket.Ticket;
import com.baling.models.ticket.Travel;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.line.LineRepository;
import com.baling.repository.line.StoppingRepository;
import com.baling.repository.ticket.TicketRepository;
import com.baling.repository.ticket.TravelRepository;
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
@RequestMapping("/api/travel")
public class TraceController {
    @Autowired
    TravelRepository travelRepository;
    @Autowired
    LineRepository lineRepository;
    @Autowired
    StoppingRepository stoppingRepository;
    @Autowired
    TicketRepository ticketRepository;

    @PostMapping("/trace")
    public DataResponse trace(@Valid @RequestBody DataRequest dataRequest) {
        Travel travel=travelRepository.getById(dataRequest.getString("travelId"));
        List<Ticket> tickets=ticketRepository.findByTravelOrderByOrderInTravel(travel);
        List res=new ArrayList();
        double preLng=0,preLat=0;
        for(Ticket t:tickets){
            Line line=t.getDeparture().getLine();
            List<Stopping> stoppings=stoppingRepository.findByLineOrderByOrderInLine(line);
            for(Stopping s:stoppings){
                if(s.getOrderInLine()<t.getStart()||s.getOrderInLine()>t.getEnd())continue;
                Map m=new HashMap<>();
                double lng=s.getStation().getLon(),lat=s.getStation().getLat();
                if((inBeijing(lng,lat)&&(Math.abs(lng-preLng)>2||Math.abs(lat-preLat)>2))||(preLat!=0&&(Math.abs(lng-preLng)>5||Math.abs(lat-preLat)>5)))continue;
                preLng=lng; preLat=lat;
                m.put("lng",lng);
                m.put("lat",lat);
                res.add(m);
            }
        }
        return CommonMethod.getReturnData(res);
    }

    private boolean inBeijing(double lng,double lat){
        return (lng<=117&&lng>=115&&lat<=41&&lat>=39);
    }
}
