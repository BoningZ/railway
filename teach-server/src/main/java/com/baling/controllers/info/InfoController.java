package com.baling.controllers.info;

import com.baling.models.administration.City;
import com.baling.models.administration.Company;
import com.baling.models.administration.Country;
import com.baling.models.administration.Station;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.administration.CityRepository;
import com.baling.repository.administration.CompanyRepository;
import com.baling.repository.administration.CountryRepository;
import com.baling.repository.administration.StationRepository;
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

    @PostMapping("/stationLike")
    public DataResponse stationLike(@Valid @RequestBody DataRequest dataRequest){
        String name=dataRequest.getString("name");
        List<Station> stations=(stationRepository.getStationsByNameLike("%"+name+"%"));
        List data=new ArrayList();
        for(Station s:stations)
            data.add(CommonMethod.convertToMap(s));
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
