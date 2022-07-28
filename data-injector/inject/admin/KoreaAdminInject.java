package inject.admin;

import DAO.admin.CityDAO;
import DAO.admin.DistrictDAO;
import DAO.admin.ProvinceDAO;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class KoreaAdminInject {
    private static ProvinceDAO provinceDAO=new ProvinceDAO();
    private static CityDAO cityDAO=new CityDAO();
    private static DistrictDAO districtDAO=new DistrictDAO();
    private static HashMap<String,Integer> provinces=new HashMap<>();
    private static HashMap<String,Integer> cities=new HashMap<>();
    private static HashMap<String,Integer> districts=new HashMap<>();
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\kr_admin2.txt");
        Scanner sc = new Scanner(path);
        int cityCount=0,districtCount=0;
        while (sc.hasNextLine()){
            String fullLocate=sc.nextLine();
            String[] data=fullLocate.split(" ");
            if(provinces.get(data[0])==null){
                cities.clear();
                cityCount=districtCount=0;
                provinces.put(data[0],provinces.size()+1);
                provinceDAO.reInject("KR-"+String.format("%02d",provinces.get(data[0]))+"0000",data[0],"KR");
            }
            if(cities.get(data[1])==null){
                districts.clear();
                districtCount=0;
                cities.put(data[1],++cityCount);
                cityDAO.reInject("KR-"+String.format("%02d",provinces.get(data[0]))+String.format("%02d",cities.get(data[1]))+"00",
                        data[1],"KR-"+String.format("%02d",provinces.get(data[0]))+"0000");
            }
            if(districts.get(data[2])==null){
                districts.put(data[2],++districtCount);
                districtDAO.reInject("KR-"+String.format("%02d",provinces.get(data[0]))+String.format("%02d",cities.get(data[1]))+String.format("%02d",districts.get(data[2])),
                        data[2],"KR-"+String.format("%02d",provinces.get(data[0]))+String.format("%02d",cities.get(data[1]))+"00");
            }
        }
    }
}
