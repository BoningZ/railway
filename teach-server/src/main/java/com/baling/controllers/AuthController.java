package com.baling.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.baling.models.user.*;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.administration.CompanyRepository;
import com.baling.repository.administration.CountryRepository;
import com.baling.repository.administration.StationRepository;
import com.baling.repository.user.*;
import com.baling.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baling.payload.request.LoginRequest;
import com.baling.payload.response.JwtResponse;
import com.baling.security.jwt.JwtUtils;
import com.baling.security.services.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    UserTypeRepository userTypeRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    StationRepository stationRepository;
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    AdminCompanyRepository adminCompanyRepository;
    @Autowired
    AdminCountryRepository adminCountryRepository;
    @Autowired
    AdminStationRepository adminStationRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        User user=userRepository.findByUsername(userDetails.getUsername()).get();
        user.setLastLoginTime(new Date());
        user.setLoginCount(user.getLoginCount()+1);
        userRepository.save(user);

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                roles.get(0)));
    }

    @PostMapping("/signup")
    public DataResponse registerUser(@Valid @RequestBody DataRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getString("username"))) {
            return /*ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"))*/
                    CommonMethod.getReturnMessageError("该用户名已被占用！");
        }

        // Create new user's account
        User user = new User(signUpRequest.getString("username"),
                encoder.encode(signUpRequest.getString("password")));

        String strRoles = signUpRequest.getString("role");


        if (strRoles == null) {
            UserType userRole = userTypeRepository.findByName(EUserType.ROLE_PASSENGER);
            user.setUserType(userRole);
        } else {
            UserType userRole = userTypeRepository.findByName(EUserType.valueOf(strRoles));
            user.setUserType(userRole);
        }

        switch (user.getUserType().getName()){
            case ROLE_PASSENGER:
                String country=signUpRequest.getString("country");
                String id=signUpRequest.getString("sid");
                if(passengerRepository.existsById(country+id))return CommonMethod.getReturnMessageError("你所在的国家该身份证号已被注册！");
                Passenger passenger=new Passenger();
                passenger.setName(signUpRequest.getString("name"));
                passenger.setId(country+id);
                passenger.setCountry(countryRepository.getById(country));
                passenger.setLicence(id);
                userRepository.save(user);
                passenger.setUser(user);
                passengerRepository.save(passenger);
                break;
            case ROLE_DRIVER:
                if(driverRepository.existsById(signUpRequest.getString("tid")))return CommonMethod.getReturnMessageError("该工号已被注册！");
                if(!checker(signUpRequest.getString("check"),"drive"))return CommonMethod.getReturnMessageError("校验密码错误！");
                Driver driver=new Driver();
                driver.setName(signUpRequest.getString("name"));
                driver.setId(signUpRequest.getString("tid"));
                driver.setTel(signUpRequest.getString("tel"));
                userRepository.save(user);
                driver.setUser(user);
                driverRepository.save(driver);
                break;
            case ROLE_ADMIN_COUNTRY:
                if(adminCountryRepository.existsById(signUpRequest.getString("tid")))return CommonMethod.getReturnMessageError("该工号已被注册！");
                String countryId=signUpRequest.getString("country");
                if(!checker(signUpRequest.getString("check"),countryId))return CommonMethod.getReturnMessageError("校验密码错误！");
                AdminCountry adminCountry=new AdminCountry();
                adminCountry.setId(signUpRequest.getString("tid"));
                adminCountry.setCountry(countryRepository.getById(countryId));
                userRepository.save(user);
                adminCountry.setUser(user);
                adminCountryRepository.save(adminCountry);
            case ROLE_ADMIN_COMPANY:
                if(adminCompanyRepository.existsById(signUpRequest.getString("tid")))return CommonMethod.getReturnMessageError("该工号已被注册！");
                String companyId=signUpRequest.getString("company");
                if(!checker(signUpRequest.getString("check"),companyId))return CommonMethod.getReturnMessageError("校验密码错误！");
                AdminCompany adminCompany=new AdminCompany();
                adminCompany.setId(signUpRequest.getString("tid"));
                adminCompany.setCompany(companyRepository.getById(companyId));
                userRepository.save(user);
                adminCompany.setUser(user);
                adminCompanyRepository.save(adminCompany);
            case ROLE_ADMIN_STATION:
                if(adminStationRepository.existsById(signUpRequest.getString("tid")))return CommonMethod.getReturnMessageError("该工号已被注册！");
                String stationId=signUpRequest.getString("station");
                if(!checker(signUpRequest.getString("check"),stationId))return CommonMethod.getReturnMessageError("校验密码错误！");
                AdminStation adminStation=new AdminStation();
                adminStation.setId(signUpRequest.getString("tid"));
                adminStation.setStation(stationRepository.getById(stationId));
                userRepository.save(user);
                adminStation.setUser(user);
                adminStationRepository.save(adminStation);
        }
        return CommonMethod.getReturnMessageOK();
    }

    private boolean checker(String st1,String st2){
        if(st1.length()!=st2.length())return false;
        for(int i=0;i<st1.length();i++){
            char c1=st1.charAt(i),c2=st2.charAt(i);
            if(c1==c2)return false;
            if(c1%233!=c2%233)return false;
        }
        return true;
    }


}
