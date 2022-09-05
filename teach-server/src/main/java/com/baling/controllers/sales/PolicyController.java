package com.baling.controllers.sales;

import com.baling.models.administration.Company;
import com.baling.models.administration.CompanyRefund;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.administration.CompanyRefundRepository;
import com.baling.repository.administration.CompanyRepository;
import com.baling.repository.user.AdminCompanyRepository;
import com.baling.repository.user.UserRepository;
import com.baling.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Access;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/policy")
public class PolicyController {
    @Autowired
    AdminCompanyRepository adminCompanyRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CompanyRefundRepository companyRefundRepository;
    @Autowired
    CompanyRepository companyRepository;



    @PostMapping("/submitRefund")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse submitRefund(@Valid @RequestBody DataRequest dataRequest) {
        Company company = adminCompanyRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCompany();
        Double hour=CommonMethod.parseNum(dataRequest.get("hour")); Double ratio=CommonMethod.parseNum(dataRequest.get("ratio"));
        if(companyRefundRepository.existsByLeftHour(hour))return CommonMethod.getReturnMessageError("同一小时数只能有一个比率");
        CompanyRefund companyRefund=new CompanyRefund();
        companyRefund.setLeftHour(hour); companyRefund.setRatio(ratio); companyRefund.setCompany(company); companyRefund.setId(company.getName()+String.format("-%.0f",hour));
        companyRefundRepository.save(companyRefund);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/submitAlter")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse submitAlter(@Valid @RequestBody DataRequest dataRequest) {
        Company company = adminCompanyRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCompany();
        Integer maxAlter=dataRequest.getInteger("maxAlter");
        company.setMaxAlter(maxAlter);
        companyRepository.save(company);
        return CommonMethod.getReturnMessageOK();
    }

    @PostMapping("/getRefund")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse getRefund(@Valid @RequestBody DataRequest dataRequest) {
        Company company = adminCompanyRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCompany();
        List<CompanyRefund> companyRefunds=companyRefundRepository.findByCompanyOrderByLeftHour(company);
        List res=new ArrayList();
        for(CompanyRefund cf:companyRefunds){
            Map m=new HashMap<>();
            m.put("hour",cf.getLeftHour());
            m.put("ratio",cf.getRatio());
            res.add(m);
        }
        return CommonMethod.getReturnData(res);
    }

    @PostMapping("/getAlter")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse getAlter(@Valid @RequestBody DataRequest dataRequest) {
        Company company = adminCompanyRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCompany();
        Map res=new HashMap(); res.put("maxAlter",company.getMaxAlter());
        return CommonMethod.getReturnData(res);
    }

    @PostMapping("/deleteRefund")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse deleteRefund(@Valid @RequestBody DataRequest dataRequest) {
        Company company = adminCompanyRepository.getByUser(userRepository.findByUserId(CommonMethod.getUserId()).get()).getCompany();
        if(companyRefundRepository.findAll().size()==1)return CommonMethod.getReturnMessageError("至少要有一项政策");
        Double hour=CommonMethod.parseNum(dataRequest.get("hour"));
        companyRefundRepository.deleteAll(companyRefundRepository.findByLeftHourAndCompany(hour,company));
        return CommonMethod.getReturnMessageOK();
    }
}
