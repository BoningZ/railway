package com.baling.controllers.company;

import com.baling.models.train.Coach;
import com.baling.models.train.CoachSeat;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.train.CoachRepository;
import com.baling.repository.train.CoachSeatRepository;
import com.baling.repository.train.SeatRepository;
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
@RequestMapping("/api/coach")
public class CoachController {
    @Autowired
    CoachRepository coachRepository;
    @Autowired
    CoachSeatRepository coachSeatRepository;
    @Autowired
    SeatRepository seatRepository;

    @PostMapping("/getCoachTable")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse getCoachTable(@Valid @RequestBody DataRequest dataRequest){
        String coachId=dataRequest.getString("coachId");
        List<Coach> coaches=coachRepository.findByIdLike("%"+coachId+"%");
        List dataList=new ArrayList();
        for(Coach coach:coaches){
            Map m= CommonMethod.convertToMap(coach);
            List seats=new ArrayList();
            for(CoachSeat cs:coachSeatRepository.findByCoach(coach))
                if(!seats.contains(cs.getSeat().getName()))seats.add(cs.getSeat().getName());
            m.put("seats",seats);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getCoachInfo")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse getCoachInfo(@Valid @RequestBody DataRequest dataRequest){
        Coach coach=coachRepository.findById(dataRequest.getString("coachId")).get();//单例对象会携带额外属性，故不能用get
        Map res=new HashMap();
        res.putAll(CommonMethod.convertToMap(coach));
        List<CoachSeat> coachSeats=coachSeatRepository.findByCoach(coach);
        List seatList=new ArrayList();
        for(CoachSeat cs:coachSeats){
            Map seatMap=new HashMap();
            seatMap.put("seatId",cs.getSeat().getId());
            if(cs.getCols()==null){
                seatMap.put("type","free");//free
                seatMap.put("cols",new ArrayList<>());
                seatMap.put("rows",new ArrayList<>());
            }
            else if(cs.getCols().equals("顺序编号")){//order
                seatMap.put("type","order");
                seatMap.put("total",cs.getRowsPosition());
                seatMap.put("cols",new ArrayList<>());
                seatMap.put("rows",new ArrayList<>());
            }
            else {//normal
                seatMap.put("type","normal");
                seatMap.put("rows",CommonMethod.getNum(cs.getRowsPosition()));
                List<String> colList=cs.getSoundColList();
                seatMap.put("numOfColGroups",colList.size());
                seatMap.put("cols",colList);
            }
            seatList.add(seatMap);
        }
        res.put("seats",seatList);
        return CommonMethod.getReturnData(res);
    }

    @PostMapping("/submitCoach")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse submitCoach(@Valid @RequestBody DataRequest dataRequest){
        Map form=dataRequest.getMap("form"); String id=(String) form.get("id");
        Coach coach;
        if(coachRepository.existsById(id)){
            if(!(Boolean)form.get("new"))coach=coachRepository.getById(id);
            else return CommonMethod.getReturnMessageError("该编号已存在");
        }
        else {coach=new Coach(); coach.setId(id);}
        coach.setName((String) form.get("name"));
        coach.setRefrigerated((Boolean) form.get("refrigerated"));
        coach.setMaxFreeCount((Integer) form.get("maxFreeCount"));
        coachSeatRepository.deleteAll(coachSeatRepository.findByCoach(coach));
        coachRepository.save(coach);
        for(Object oSeatMap:(ArrayList)form.get("seats")){
            Map seatMap=(Map)oSeatMap;
            CoachSeat cs=new CoachSeat(); cs.setCoach(coach); cs.setSeat(seatRepository.getById((String) seatMap.get("seatId")));
            switch ((String) seatMap.get("type")){
                case "free":
                    cs.setRowsPosition(-1);
                    break;
                case "order":
                    cs.setRowsPosition((Integer) seatMap.get("total"));
                    cs.setCols("顺序编号");
                    break;
                case "normal":
                    cs.setRowsPosition(CommonMethod.getStatus((List<Integer>) seatMap.get("rows")));
                    String tmp="";
                    for(String col:(List<String>)seatMap.get("cols"))tmp+=(col+"/");
                    cs.setCols(tmp.substring(0,tmp.length()-1));
            }
            coachSeatRepository.save(cs);
        }
        return CommonMethod.getReturnMessageOK();
    }
}
