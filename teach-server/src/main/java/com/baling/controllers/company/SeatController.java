package com.baling.controllers.company;

import com.baling.models.train.Seat;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.train.SeatRepository;
import com.baling.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/seat")
public class SeatController {
    @Autowired
    SeatRepository seatRepository;

    @PostMapping("/getSeatTable")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse getSeatTable(@Valid @RequestBody DataRequest dataRequest){
        List<Seat> seats=seatRepository.findAll();
        List dataList=new ArrayList();
        for(Seat seat:seats)dataList.add(CommonMethod.convertToMap(seat));
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/submitSeat")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse submitSeat(@Valid @RequestBody DataRequest dataRequest){
        Seat seat=seatRepository.getById(dataRequest.getString("seatId"));
        seat.setConstant(dataRequest.getDouble("constant"));
        seat.setPower(dataRequest.getDouble("power"));
        seatRepository.save(seat);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/newSeat")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse newSeat(@Valid @RequestBody DataRequest dataRequest){
        String id=dataRequest.getString("id");
        if(seatRepository.existsById(id))return CommonMethod.getReturnMessageError("该编号已存在！");
        if(id.length()>20)return CommonMethod.getReturnMessageError("编号长度不得超过20！");
        Seat seat=new Seat();
        seat.setId(id);
        seat.setName(dataRequest.getString("name"));
        seat.setPower(dataRequest.getDouble("power"));
        seat.setConstant(dataRequest.getDouble("constant"));
        seatRepository.save(seat);
        return CommonMethod.getReturnMessageOK();
    }




}
