package inject.line;

import DAO.line.DepartureDAO;
import DAO.line.StoppingDAO;
import DAO.station.StationDAO;
import com.alibaba.fastjson.support.odps.udf.CodecCheck;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JapanStopping {
    private static StoppingDAO stoppingDAO=new StoppingDAO();
    private static StationDAO stationDAO=new StationDAO();
    private static DepartureDAO departureDAO=new DepartureDAO();

    public static void main(String[] args) throws IOException {
        Path path= Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\line\\jp-stopping.csv");
        Scanner sc1=new Scanner(path);
        Map<String, Map<String,String>> lines=new HashMap<>();
        List<String> eLines=new ArrayList<>();
        Map<String,List<String>> eStation=new HashMap<>();
        sc1.nextLine();
        while(sc1.hasNextLine()){
            String[] info=sc1.nextLine().split(",");
            if(lines.get("JP-"+info[0])==null){
                lines.put("JP-"+info[0],new HashMap<>());
                lines.put("JP-r"+info[0],new HashMap<>());
                eStation.put(info[0],new ArrayList<>());
                eLines.add(info[0]);
            }
            lines.get("JP-"+info[0]).put(info[1],info[2]);
            lines.get("JP-r"+info[0]).put(info[2],info[1]);
            /*if(info[2].length()==6)stationDAO.reInject("JP-"+info[2],"新干线tmp",0,0,9,null,null);
            if(info[1].length()==6)stationDAO.reInject("JP-"+info[1],"新干线tmp",0,0,9,null,null);*/

            List<String>eSta=eStation.get(info[0]);
            if(eSta.indexOf(info[1])<0)eSta.add(info[1]);else if(eSta.size()>1)eSta.remove(info[1]);
            if(eSta.indexOf(info[2])<0)eSta.add(info[2]);else if(eSta.size()>1)eSta.remove(info[2]);
        }
        int flag=0;
        for(String l:eLines){
            if(!l.equals("99301")&&flag==0)continue;
            flag=1;
            Map<String,String> map=lines.get("JP-"+l), rmap=lines.get("JP-r"+l),cur=null;
            double speed=(l.length()==4)?250:80;
            double price=(l.length()==4)?35:75;
            if(eStation.get(l).size()==1){
                String s=eStation.get(l).get(0);
                cur=map;
                while(cur!=null){
                    String pre=(cur==map)?"JP-":"JP-r";
                    String tmp=s;
                    stoppingDAO.reInject(0,0,1,5,pre+l,"JP-"+s);
                    int hour=5; Random random=new Random();
                    while(hour<24){
                        departureDAO.reInject(pre+l+String.format("%02d",hour)+"00",7,hour*60,pre+l,"JP-001");
                        hour+=random.nextInt(4);
                    }
                    int order=2,constant=0,arrival=0;
                    while(order<=cur.size()+1){
                        String preId="JP-"+tmp;
                        tmp=cur.get(tmp);
                        double dis=stationDAO.getDistance(preId,"JP-"+tmp);
                        System.out.println(dis);
                        constant+=dis*price;
                        arrival+=60*(dis/speed)+5;
                        stoppingDAO.reInject(arrival,constant,order,5,pre+l,"JP-"+tmp);
                        order++;
                    }

                    if(cur==rmap)cur=null;
                    if(cur==map)cur=rmap;
                }

                continue;
            }
            for(String s:eStation.get(l)){
                cur=(rmap.get(s)==null)?map:rmap;
                String pre=(rmap.get(s)==null)?"JP-":"JP-r";
                String tmp=s;
                stoppingDAO.reInject(0,0,1,5,pre+l,"JP-"+s);
                int hour=5; Random random=new Random();
                while(hour<24){
                    departureDAO.reInject(pre+l+String.format("%02d",hour)+"00",7,hour*60,pre+l,"JP-001");
                    hour+=random.nextInt(4);
                }
                int order=2,constant=0,arrival=0;
                while(cur.get(tmp)!=null&&order<=cur.size()+1){
                    String preId="JP-"+tmp;
                    tmp=cur.get(tmp);
                    double dis=stationDAO.getDistance(preId,"JP-"+tmp);
                    System.out.println(dis);
                    constant+=dis*price;
                    arrival+=60*(dis/speed)+5;
                    stoppingDAO.reInject(arrival,constant,order,5,pre+l,"JP-"+tmp);
                    order++;
                }

            }
        }
    }
}
