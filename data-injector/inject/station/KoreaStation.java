package inject.station;

import DAO.admin.CityDAO;
import DAO.admin.DistrictDAO;
import DAO.station.StationDAO;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class KoreaStation {
    private static StationDAO stationDAO=new StationDAO();
    public static CityDAO cityDAO=new CityDAO();
    public static DistrictDAO districtDAO=new DistrictDAO();

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\station\\krstation.json");
        Scanner sc1 = new Scanner(path);
        sc1.useDelimiter("反正不包含这一串捏");
        Map<String,Map<String,List<Map<String,String>>>> json;
        json= (Map<String,Map<String,List<Map<String,String>>>>)JSON.parse(sc1.next());
        List<Map<String,String>> statList=json.get("stns").get("stn");
        for(Map<String,String> stat:statList){
            String name=stat.get("stn_nm");
            String cityId=districtDAO.findCityIddByName(name);
            if(cityId==null)cityId=cityDAO.getIdByName(name);
            stationDAO.reInject("KR-"+stat.get("stn_cd"),
                    name,
                    Double.parseDouble(stat.get("longitude")),Double.parseDouble(stat.get("latitude")),
                    9,
                    cityId);
        }
    }
}
