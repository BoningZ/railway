package inject.admin;

import DAO.CityDAO;
import DAO.DistrictDAO;
import DAO.ProvinceDAO;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ChinaAdminInject {
    private static ProvinceDAO provinceDAO=new ProvinceDAO();
    private static CityDAO cityDAO=new CityDAO();
    private static DistrictDAO districtDAO=new DistrictDAO();
    private static HashMap<String,Integer> provinces=new HashMap<>();
    private static HashMap<String,Integer> cities=new HashMap<>();
    public static void main(String[] args) throws IOException {
        Path path= Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\china_admin.txt");
        Scanner sc=new Scanner(path);
        while(sc.hasNext()){
            String id=sc.next();
            String name=sc.next();
            if(id.substring(2).equals("0000")){
                provinces.put(id,1);
                if(provinceDAO.existById("CN-"+id)) {
                    System.out.println(id + ":" + name + " injected!");
                    continue;
                }
                System.out.println("injecting prov: "+id+":"+name);
                provinceDAO.inject("CN-"+id,name,"CN");
            }else if(id.substring(4).equals("00")){
                cities.put(id,1);
                if(cityDAO.existById("CN-"+id)) {
                    System.out.println(id + ":" + name + " injected!");
                    continue;
                }
                System.out.println("injecting city: "+id+":"+name);
                cityDAO.inject("CN-"+id,name,"CN-"+id.substring(0,2)+"0000");
            }else{
                if(cities.get(id.substring(0,4)+"00")!=null) {
                    if(districtDAO.existById("CN-"+id)) {
                        System.out.println(id + ":" + name + " injected!");
                        continue;
                    }
                    System.out.println("injecting dis: "+id+":"+name);
                    districtDAO.inject("CN-" + id, name, "CN-" + id.substring(0, 4) + "00");
                }
                else {
                    if(cityDAO.existById("CN-"+id)) {
                        System.out.println(id + ":" + name + " injected!");
                        continue;
                    }
                    System.out.println("injecting city: "+id+":"+name);
                    cityDAO.inject("CN-"+id,name,"CN-"+id.substring(0,2)+"0000");
                }
            }
        }
    }
}
