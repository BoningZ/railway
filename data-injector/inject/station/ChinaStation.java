package inject.station;

import DAO.admin.CityDAO;
import DAO.station.StationDAO;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

public class ChinaStation {
    private static StationDAO stationDAO=new StationDAO();
    public static CityDAO cityDAO=new CityDAO();

    public static void main(String[] args) throws IOException {
        /*PrintStream ps = new PrintStream("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\station\\exp.txt");
        System.setOut(ps);*/
        Path path= Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\station\\cnstation.csv");
        Scanner sc1=new Scanner(path);
        HashMap<String,String[]>infos=new HashMap<>();
        sc1.nextLine();
        while(sc1.hasNextLine()){
            String tmp= sc1.nextLine();
            String[] tmps=tmp.split(",");
            infos.put(tmps[0],tmp.split(","));
        }
        Path path1=Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\station\\cnstationid.txt");
        Scanner sc2=new Scanner(path1);
        sc2.useDelimiter("\\|");
        String cur=null,pre=null;
        while(sc2.hasNext()){
            if(cur!=null)pre=new String(cur);
            cur=sc2.next();
            cur=cur.replaceAll(" ","");
            if(cur.charAt(0)>='A'&&cur.charAt(0)<='Z'){
                int cityf=0,statf=0;
                String cityId=null;
                String[] curInfos=infos.get(pre);
                if(curInfos==null)curInfos=infos.get(pre+"站");
                if(curInfos==null) {System.out.println("no station like "+pre);statf=cityf=1;}
                else {
                    cityId=cityDAO.getIdByName(curInfos[6]);
                    if(cityId==null){ System.out.println("not found city like "+curInfos[6]);cityf=1;}
                }
                if(cityf+statf==0)stationDAO.reInject("CN-"+cur,pre,Double.parseDouble(curInfos[9]),Double.parseDouble(curInfos[10]),8,cityId);
                else if(statf==0)stationDAO.reInject("CN-"+cur,pre,Double.parseDouble(curInfos[9]),Double.parseDouble(curInfos[10]),8,"nocity","JP-083000");
                else stationDAO.reInject("CN-"+cur,pre,0,0,8,"noall","JP-083000");
            }
        }
    }
}
