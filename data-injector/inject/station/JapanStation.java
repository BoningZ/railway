package inject.station;

import DAO.admin.CityDAO;
import DAO.admin.DistrictDAO;
import DAO.station.StationDAO;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class JapanStation {
    private static StationDAO stationDAO=new StationDAO();
    public static CityDAO cityDAO=new CityDAO();
    public static DistrictDAO districtDAO=new DistrictDAO();

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\station\\jpstation.csv");
        Scanner sc1=new Scanner(path);
        sc1.nextLine();
        while(sc1.hasNextLine()){
            String[] infos=sc1.nextLine().split(",");
            String addr=infos[8],city=null,cityId=null;
            if(infos[0].compareTo("2100241")<=0)continue;
            if(addr.indexOf('市')!=-1) {
                if(addr.indexOf("県")+1<addr.indexOf("市")+1)
                    city = addr.substring(addr.indexOf("県")+1, addr.indexOf("市")+1);
                else city = addr.substring(0, addr.indexOf("市")+1);
                cityId = cityDAO.findIdByName(city);
            }
            else if(addr.indexOf('区')!=-1) {
                city = addr.substring(addr.indexOf("都")+1, addr.indexOf('区') + 1);
                cityId = cityDAO.findIdByName(city);
                if (cityId == null) cityId = districtDAO.findCityIddByName(city);
            }
            else if(addr.indexOf('町')!=-1) {
                city = addr.substring(addr.indexOf("郡")+1, addr.indexOf('町') + 1);
                cityId = districtDAO.findCityIddByName(city);
            }
            stationDAO.reInject("JP-"+infos[0],infos[2],Double.parseDouble(infos[9]),Double.parseDouble(infos[10]),9,infos[1],cityId);
        }
    }
}
