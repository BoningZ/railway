package com.baling.util;

import com.baling.models.administration.Station;

import java.util.ArrayList;
import java.util.List;

public class DataProcessor {
    public static List<Station> filterStation(List<Station> stations){
        List<String> existed=new ArrayList<>();
        List<Station> good=new ArrayList<>();
        List data=new ArrayList();
        for(Station s:stations){
            if(existed.contains(s.getName()+"###"+s.getTransferId()))continue;
            good.add(s);
            if(s.getTransferId()!=null)
                existed.add(s.getName()+"###"+s.getTransferId());
        }
        return good;
    }

}
