package com.baling.controllers.company;

import com.baling.models.train.Train;
import com.baling.models.train.TrainCoach;
import com.baling.payload.request.DataRequest;
import com.baling.payload.response.DataResponse;
import com.baling.repository.train.CoachRepository;
import com.baling.repository.train.TrainCoachRepository;
import com.baling.repository.train.TrainRepository;
import com.baling.repository.train.TrainTypeRepository;
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
@RequestMapping("/api/train")
public class TrainController {
    @Autowired
    TrainRepository trainRepository;
    @Autowired
    TrainCoachRepository trainCoachRepository;
    @Autowired
    CoachRepository coachRepository;
    @Autowired
    TrainTypeRepository trainTypeRepository;

    @PostMapping("/getTrainTable")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse getTrainTable(@Valid @RequestBody DataRequest dataRequest){
        List<Train> trainList=trainRepository.findByIdLike("%"+dataRequest.getString("trainId")+"%");
        List dataList=new ArrayList();
        for(Train train:trainList){
            Map m= new HashMap();
            m.put("id",train.getId());
            m.put("speed",train.getSpeed());
            m.put("name",train.getName());
            m.put("trainTypeId",train.getTrainType().getId());
            m.put("trainType",train.getTrainType().getName());
            List coachList=new ArrayList();
            for(TrainCoach tc:trainCoachRepository.findByTrain(train))
                coachList.add(tc.getCoach().getName());
            m.put("coaches",coachList);
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    @PostMapping("/getTrainInfo")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse getTrainInfo(@Valid @RequestBody DataRequest dataRequest){
        Train train=trainRepository.findById(dataRequest.getString("trainId")).get();
        Map res=new HashMap();
        res.put("id",train.getId());
        res.put("name",train.getName());
        res.put("speed",train.getSpeed());
        res.put("trainTypeId",train.getTrainType().getId());
        List coaches=new ArrayList();
        for(TrainCoach tc:trainCoachRepository.findByTrain(train)){
            Map m=new HashMap();
            m.put("coachId",tc.getCoach().getId());
            m.put("position",CommonMethod.getNum(tc.getPosition()));
            coaches.add(m);
        }
        res.put("coaches",coaches);
        return CommonMethod.getReturnData(res);
    }


    @PostMapping("/submitTrain")
    @PreAuthorize("hasRole('ADMIN_COMPANY')")
    public DataResponse submitTrain(@Valid @RequestBody DataRequest dataRequest){
        Map form=dataRequest.getMap("form");
        Train train;
        if(trainRepository.existsById((String) form.get("id"))){
            if((Boolean)form.get("isNew"))return CommonMethod.getReturnMessageError("该编号已存在");
            train=trainRepository.findById((String) form.get("id")).get();
        }else train=new Train();
        train.setId((String) form.get("id"));
        train.setName((String) form.get("name"));
        train.setSpeed((Integer) form.get("speed"));
        train.setTrainType(trainTypeRepository.findById((String) form.get("trainTypeId")).get());
        trainRepository.save(train);
        trainCoachRepository.deleteAll(trainCoachRepository.findByTrain(train));
        for(Object o:(List)form.get("coaches")){
            Map coachMap=(Map) o;
            TrainCoach trainCoach=new TrainCoach();
            trainCoach.setTrain(train); trainCoach.setCoach(coachRepository.getById((String) coachMap.get("coachId")));
            trainCoach.setPosition(CommonMethod.getStatus((List<Integer>) coachMap.get("position")));
            trainCoachRepository.save(trainCoach);
        }
        return CommonMethod.getReturnMessageOK();
    }

}
