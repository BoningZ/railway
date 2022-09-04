package com.baling.controllers.info;

import com.baling.models.administration.City;
import com.baling.models.administration.Company;
import com.baling.models.administration.Country;
import com.baling.models.administration.Station;
import com.baling.models.train.Coach;
import com.baling.models.train.Train;
import com.baling.models.train.TrainType;
import com.baling.models.user.Driver;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.administration.CityRepository;
import com.baling.repository.administration.CompanyRepository;
import com.baling.repository.administration.CountryRepository;
import com.baling.repository.administration.StationRepository;
import com.baling.repository.train.CoachRepository;
import com.baling.repository.train.TrainRepository;
import com.baling.repository.train.TrainTypeRepository;
import com.baling.repository.user.DriverRepository;
import com.baling.util.CommonMethod;
import com.baling.util.DataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/info")
public class InfoController {
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    CityRepository cityRepository;
    @Autowired
    CoachRepository coachRepository;
    @Autowired
    TrainTypeRepository trainTypeRepository;
    @Autowired
    TrainRepository trainRepository;
    @Autowired
    DriverRepository driverRepository;


    @PostMapping("/coach")
    public DataResponse coach(@Valid @RequestBody DataRequest dataRequest){
        List<Coach> coaches=coachRepository.findAll();
        List data=new ArrayList();
        for(Coach c:coaches){
            Map m=new HashMap();
            m.put("id",c.getId());
            m.put("name",c.getName());
            data.add(m);
        }
        return CommonMethod.getReturnData(data);
    }

    @PostMapping("/trainType")
    public DataResponse trainType(@Valid @RequestBody DataRequest dataRequest){
        List<TrainType> trainTypes=trainTypeRepository.findAll();
        List data=new ArrayList();
        for(TrainType tt:trainTypes)
            data.add(CommonMethod.convertToMap(tt));
        return CommonMethod.getReturnData(data);
    }

    @PostMapping("/country")
    public DataResponse country(@Valid @RequestBody DataRequest dataRequest){
        List<Country> countries=countryRepository.getAllBy();
        List data=new ArrayList();
        for(Country c:countries)
            data.add(CommonMethod.convertToMap(c));
        return CommonMethod.getReturnData(data);
    }

    @PostMapping("/company")
    public DataResponse company(@Valid @RequestBody DataRequest dataRequest){
        List<Company> companies=companyRepository.getAllBy();
        List data=new ArrayList();
        for(Company c:companies)
            data.add(CommonMethod.convertToMap(c));
        return CommonMethod.getReturnData(data);
    }

    @PostMapping("/cityWithPos")
    public DataResponse cityWithPos(@Valid @RequestBody DataRequest dataRequest){
        String name=dataRequest.getString("name");
        List<City> cities=cityRepository.findByNameLike("%"+name+"%");
        List data=new ArrayList();
        for(City c:cities){
            Map m=new HashMap();
            m.put("id",c.getId());
            m.put("name",c.getName());
            m.put("pos",c.getProvince().getName()+","+c.getProvince().getCountry().getName());
            data.add(m);
        }
        return CommonMethod.getReturnData(data);
    }

    @PostMapping("/station")
    public DataResponse station(@Valid @RequestBody DataRequest dataRequest){
        List<Station> stations= (stationRepository.getAllBy());
        List data=new ArrayList();
        for(Station s:stations)
            data.add(CommonMethod.convertToMap(s));
        return CommonMethod.getReturnData(data);
    }
    @PostMapping("/driver")
    public DataResponse driver(@Valid @RequestBody DataRequest dataRequest){
        List<Driver> drivers=driverRepository.findAll();
        List data=new ArrayList();
        for(Driver s:drivers)
            data.add(CommonMethod.convertToMap(s));
        return CommonMethod.getReturnData(data);
    }

    @PostMapping("/stationLike")
    public DataResponse stationLike(@Valid @RequestBody DataRequest dataRequest){
        String name=dataRequest.getString("name");
        List<Station> stations=(stationRepository.getStationsByNameLike("%"+name+"%"));
        List data=new ArrayList();
        for(Station s:stations)
            data.add(CommonMethod.convertToMap(s));
        return CommonMethod.getReturnData(data);
    }

    @PostMapping("/trainLike")
    public DataResponse trainLike(@Valid @RequestBody DataRequest dataRequest){
        String name=dataRequest.getString("name");
        List<Train> trains=trainRepository.findByNameLike("%"+name+"%");
        List data=new ArrayList();
        for(Train t:trains){
            Map m=new HashMap();
            m.put("id",t.getId());
            m.put("name",t.getName());
            data.add(m);
        }
        return CommonMethod.getReturnData(data);
    }

    @PostMapping("/stationWithPos")
    public DataResponse stationWithPos(@Valid @RequestBody DataRequest dataRequest){
        String name=dataRequest.getString("name");
        List<Station> stations=(stationRepository.getStationsByNameLike("%"+name+"%"));
        List data=new ArrayList();
        for(Station s:stations){
            Map m=new HashMap();
            m.put("id",s.getId());
            m.put("name",s.getName());
            m.put("pos",s.getCity().getName()+","+s.getCity().getProvince().getName()+","+s.getCity().getProvince().getCountry().getName());
            data.add(m);
        }
        return CommonMethod.getReturnData(data);
    }
}
