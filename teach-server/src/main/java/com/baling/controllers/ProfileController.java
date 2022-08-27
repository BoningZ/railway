package com.baling.controllers;

import com.baling.models.user.Driver;
import com.baling.models.user.EUserType;
import com.baling.models.user.Passenger;
import com.baling.models.user.User;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.user.DriverRepository;
import com.baling.repository.user.PassengerRepository;
import com.baling.repository.user.UserRepository;
import com.baling.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teach")
public class ProfileController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    PassengerRepository passengerRepository;

    @PostMapping("/getProfile")
    @PreAuthorize("hasRole('DRIVER') or hasRole('PASSENGER')")
    public DataResponse getProfile(@Valid @RequestBody DataRequest dataRequest){
        Integer userId= CommonMethod.getUserId();
        User user;
        Optional<User> tmp = userRepository.findByUserId(userId);
        user = tmp.get();
        Map m=new HashMap();
        if(user.getUserType().getName()== EUserType.ROLE_DRIVER){
            Driver driver=driverRepository.findByUser(user);
            m.put("id",driver.getId());
            m.put("name",driver.getName());
        }else{
            Passenger passenger=passengerRepository.findByUser(user);
            m.put("id",passenger.getId());
            m.put("tel",passenger.getTel());
            m.put("birthday",passenger.getBirthday());
            m.put("country",passenger.getCountry().getName());
            m.put("name",passenger.getName());
        }
        return CommonMethod.getReturnData(m);
    }

    @PostMapping("/submitProfile")
    @PreAuthorize("hasRole('PASSENGER') or hasRole('ADMIN')")
    public DataResponse submitProfile(@Valid @RequestBody DataRequest dataRequest){
        Integer userId= CommonMethod.getUserId();
        User user;
        Optional<User> tmp = userRepository.findByUserId(userId);
        user = tmp.get();
        if(user.getUserType().getName()== EUserType.ROLE_DRIVER){
            Driver driver=driverRepository.findByUser(user);
            driver.setId(dataRequest.getString("id"));
            driver.setName(dataRequest.getString("name"));
            driverRepository.save(driver);
        }else{
            Passenger passenger=passengerRepository.findByUser(user);
            passenger.setName(dataRequest.getString("name"));
            passenger.setTel(dataRequest.getString("tel"));
            passenger.setBirthday(dataRequest.getDate("birthday"));
            passengerRepository.save(passenger);
        }
        return CommonMethod.getReturnMessageOK();
    }

}

