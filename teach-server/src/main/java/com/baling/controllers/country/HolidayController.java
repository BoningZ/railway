package com.baling.controllers.country;

import com.baling.models.administration.Country;
import com.baling.models.holiday.CountryHoliday;
import com.baling.models.holiday.Holiday;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.holiday.CountryHolidayRepository;
import com.baling.repository.holiday.HolidayRepository;
import com.baling.repository.user.AdminCountryRepository;
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
@RequestMapping("/api/holiday")
public class HolidayController {
    @Autowired
    AdminCountryRepository adminCountryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CountryHolidayRepository countryHolidayRepository;
    @Autowired
    HolidayRepository holidayRepository;


    @PostMapping("/getList")
    @PreAuthorize("hasRole('ADMIN_COUNTRY')")
    public DataResponse getList(@Valid @RequestBody DataRequest dataRequest){
        Country country=adminCountryRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCountry();
        List<CountryHoliday> countryHolidayList=countryHolidayRepository.findByCountry(country);
        List selected=new ArrayList(); for(CountryHoliday ch:countryHolidayList)selected.add(ch.getHoliday().getId());
        List<Holiday> holidayList=holidayRepository.findAll();
        List holidays=new ArrayList();
        for(Holiday h:holidayList){
            Map m=new HashMap<>();
            m.put("id",h.getId());
            m.put("name",h.getName());
            List range=new ArrayList();
            range.add(h.getStart());range.add(h.getEnd());
            m.put("dateRange",range);
            holidays.add(m);
        }
        Map res=new HashMap();
        res.put("holidays",holidays); res.put("selected",selected);
        return CommonMethod.getReturnData(res);
    }
    @PostMapping("/newHoliday")
    @PreAuthorize("hasRole('ADMIN_COUNTRY')")
    public DataResponse newHoliday(@Valid @RequestBody DataRequest dataRequest){
        String id=dataRequest.getString("id");
        if(holidayRepository.existsById(id))return CommonMethod.getReturnMessageError("编号已存在");
        Holiday h=new Holiday();
        h.setId(id); h.setStart(dataRequest.getDate("start")); h.setEnd(dataRequest.getDate("end")); h.setName(dataRequest.getString("name"));
        holidayRepository.save(h);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/submitSelect")
    @PreAuthorize("hasRole('ADMIN_COUNTRY')")
    public DataResponse submitSelect(@Valid @RequestBody DataRequest dataRequest){
        Country country=adminCountryRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCountry();
        List<String> ids=dataRequest.getList("idList");
        countryHolidayRepository.deleteAll(countryHolidayRepository.findByCountry(country));
        for(String id:ids){
            Holiday holiday=holidayRepository.getById(id);
            CountryHoliday ch=new CountryHoliday();
            ch.setHoliday(holiday); ch.setCountry(country);
            countryHolidayRepository.save(ch);
        }
        return CommonMethod.getReturnMessageOK();
    }

}
