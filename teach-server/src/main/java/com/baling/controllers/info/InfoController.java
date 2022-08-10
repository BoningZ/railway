package com.baling.controllers.info;

import com.baling.models.administration.Company;
import com.baling.models.administration.Country;
import com.baling.models.administration.Station;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.administration.CompanyRepository;
import com.baling.repository.administration.CountryRepository;
import com.baling.repository.administration.StationRepository;
import com.baling.util.CommonMethod;
import com.baling.util.DataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/station")
    public DataResponse station(@Valid @RequestBody DataRequest dataRequest){
        List<Station> stations= DataProcessor.filterStation(stationRepository.getAllBy());
        List data=new ArrayList();
        for(Station s:stations)
            data.add(CommonMethod.convertToMap(s));
        return CommonMethod.getReturnData(data);
    }

    @PostMapping("/stationLike")
    public DataResponse stationLike(@Valid @RequestBody DataRequest dataRequest){
        String name=dataRequest.getString("name");
        List<Station> stations=DataProcessor.filterStation(stationRepository.getStationsByNameLike("%"+name+"%"));
        List data=new ArrayList();
        for(Station s:stations)
            data.add(CommonMethod.convertToMap(s));
        return CommonMethod.getReturnData(data);
    }
}
