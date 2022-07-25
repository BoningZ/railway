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
            m.put("name",passenger.getName());
        }
        return CommonMethod.getReturnData(m);
    }

    @PostMapping("/submitProfile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
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
            passenger.setId(dataRequest.getString("id"));
            passenger.setName(dataRequest.getString("name"));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date birth=null;
            Calendar cal=Calendar.getInstance();
            try {
                birth=format.parse(dataRequest.getString("birthday"));
                cal.setTime(birth);
                cal.add(Calendar.DATE,1);
                birth=cal.getTime();
            } catch (ParseException e) {e.printStackTrace();}
            passengerRepository.save(passenger);
        }
        return CommonMethod.getReturnMessageOK();
    }

}

