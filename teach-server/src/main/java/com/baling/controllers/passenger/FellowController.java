package com.baling.controllers.passenger;

import com.baling.models.administration.Country;
import com.baling.models.user.FellowPassenger;
import com.baling.models.user.Passenger;
import com.baling.models.user.User;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.administration.CountryRepository;
import com.baling.repository.user.FellowPassengerRepository;
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
@RequestMapping("/api/fellow")
public class FellowController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    FellowPassengerRepository fellowPassengerRepository;
    @Autowired
    CountryRepository countryRepository;

    @PostMapping("/getFellow")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse getFellow(@Valid @RequestBody DataRequest dataRequest){
        Integer userId= CommonMethod.getUserId();
        User u=userRepository.findByUserId(userId).get();
        List<FellowPassenger> fps=fellowPassengerRepository.getByHost(passengerRepository.findByUser(u));
        List dataList=new ArrayList();
        for(FellowPassenger fp:fps){
            Passenger p=fp.getFellow();
            Map m=new HashMap<>();
            m.put("id",p.getId());
            m.put("name",p.getName());
            m.put("licence",p.getLicence());
            m.put("country",p.getCountry().getName());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/addFellow")
    @PreAuthorize("hasRole('PASSENGER')")
    public DataResponse addFellow(@Valid @RequestBody DataRequest dataRequest){
        Integer userId= CommonMethod.getUserId();
        User u=userRepository.findByUserId(userId).get();
        Passenger host=passengerRepository.findByUser(u);
        String licence=dataRequest.getString("licence");
        Country country=countryRepository.getById(dataRequest.getString("countryId"));
        Passenger fellow=passengerRepository.findByCountryAndLicence(country,licence);
        if(fellow==null)return CommonMethod.getReturnMessageError("该用户未在本系统注册");
        if(fellowPassengerRepository.getByHostAndFellow(host,fellow)!=null)return CommonMethod.getReturnMessageError("已在同行人列表中！");
        FellowPassenger fellowPassenger=new FellowPassenger();
        fellowPassenger.setFellow(fellow); fellowPassenger.setHost(host);
        fellowPassengerRepository.save(fellowPassenger);
        return CommonMethod.getReturnMessageOK();
    }



}
