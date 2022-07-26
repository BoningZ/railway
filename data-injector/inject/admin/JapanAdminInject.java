package inject.admin;

import DAO.CityDAO;
import DAO.DistrictDAO;
import DAO.ProvinceDAO;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import com.alibaba.fastjson.*;

public class JapanAdminInject {
    private static ProvinceDAO provinceDAO=new ProvinceDAO();
    private static CityDAO cityDAO=new CityDAO();
    private static DistrictDAO districtDAO=new DistrictDAO();
    private static HashMap<String,Integer> provinces=new HashMap<>();
    private static HashMap<String,Integer> cities=new HashMap<>();
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\japan_admin.json");
        Scanner sc = new Scanner(path);
        sc.useDelimiter("反正没有这个字符串");
        Map<String,Map<String,String>> map=(Map<String, Map<String,String>>)JSON.parse(sc.next());
        for (Map.Entry<String, Map<String,String>> entry : map.entrySet()) {
            String key= entry.getKey();
            Map<String,String> value=entry.getValue();

            if(provinces.get("JP-"+key.substring(0,2)+"0000")==null){
                provinces.put("JP-"+key.substring(0,2)+"0000",1);
                provinceDAO.reInject("JP-"+key.substring(0,2)+"0000", value.get("prefecture"),"JP" );
            }
            if(key.charAt(2) == '1'){
                if(cities.get("JP-"+key.substring(0,3)+"000")==null){
                    cities.put("JP-"+key.substring(0,3)+"000",1);
                    cityDAO.reInject("JP-"+key.substring(0,3)+"000",value.get("city"),"JP-"+key.substring(0,2)+"0000");
                }
                districtDAO.reInject("JP-"+key,value.get("city"),"JP-"+key.substring(0,3)+"000");
            }else if(key.charAt(2)=='2'){
                cityDAO.reInject("JP-"+key,value.get("city"),"JP-"+key.substring(0,2)+"0000");
            }else{
                if(cities.get("JP-"+key.substring(0,2)+"3000")==null){
                    cities.put("JP-"+key.substring(0,2)+"3000",1);
                    cityDAO.reInject("JP-"+key.substring(0,2)+"3000",value.get("prefecture")+"で他の町村","JP-"+key.substring(0,2)+"0000");
                }
                districtDAO.reInject("JP-"+key,value.get("city"),"JP-"+key.substring(0,2)+"3000");
            }

        }
    }
}
