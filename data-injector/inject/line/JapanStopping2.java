package inject.line;

import DAO.JDBCUtils;
import DAO.station.StationDAO;
import com.alibaba.fastjson.support.odps.udf.CodecCheck;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class JapanStopping2 {//啊对对对，逼你爹上dfs
    public static StationDAO stationDAO = new StationDAO();

    public static void main(String[] args) throws IOException {
        Map<String, MyLine> map = new HashMap<>();
        List<String> lines = new ArrayList<>();
        List<String> stations = new ArrayList<>();
        List<String> rest=Arrays.asList("99423","99424","99425","99426","99427","99206");
        /*Path path0 = Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\station\\jpstation.csv");
        Scanner sc0 = new Scanner(path0);
        sc0.nextLine();
        while (sc0.hasNextLine()) stations.add(sc0.nextLine().split(",")[0]);
        System.out.println(stations);*/
        Path path = Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\line\\jp-stopping.csv");
        Scanner sc1 = new Scanner(path);
        sc1.nextLine();
        while (sc1.hasNextLine()) {
            String[] info = sc1.nextLine().split(",");
            if(!rest.contains(info[0]))continue;
            if (map.get(info[0]) == null) {
                map.put(info[0], new MyLine(info[0]));
                lines.add(info[0]);
            }
            for (int i = 1; i <= 2; i++)
                if (!stations.contains(info[i])) {
                    if(info[i].length()==6)break;
                    stations.add(info[i]);
                    stationDAO.reInject("JP-" + info[i], info[0] + ":" + info[i], 0, 0, 9, null, null);
                }
            map.get(info[0]).addEdge("JP-" + info[1], "JP-" + info[2]);
        }
        for (String l : lines) map.get(l).execute();
    }
}

class MyLine{
    String lineId;
    double speed,price;
    List<String> stations=new ArrayList<>();
    Map<String,List<String>> via=new HashMap<>();
    Map<String,Integer> deg=new HashMap<>();
    List<String> visit=new ArrayList<>();
    MyLine(String lineId){this.lineId=lineId;speed=(lineId.length()==4)?250:80;price=(lineId.length()==4)?35:75;}
    public void addEdge(String sta1,String sta2){
        if(!stations.contains(sta1)){stations.add(sta1);via.put(sta1,new ArrayList<>());deg.put(sta1,0);}
        if(!stations.contains(sta2)){stations.add(sta2);via.put(sta2,new ArrayList<>());deg.put(sta2,0);}
        via.get(sta1).add(sta2); via.get(sta2).add(sta1);
        deg.replace(sta1,deg.get(sta1)+1); deg.replace(sta2,deg.get(sta2)+1);
    }

    public List<String> dfs(String cur){
        List<String> res=new ArrayList<>();
        res.add(cur);
        for(String nxt:via.get(cur))
            if(!visit.contains(nxt)){
                visit.add(nxt);
                res.addAll(dfs(nxt));
            }
        return res;
    }

    public List<String> getDir(){
        String start=null;
        for (String s : stations)
            if (deg.get(s) == 1) {
                start = s;
                break;
            }
        if (start == null) start = stations.get(0);
        return dfs(start);
    }

    public void execute() {
        StationDAO stationDAO=new StationDAO();
        try {
            Connection connection = null;
            try{connection=JDBCUtils.getconn();}catch (SQLException e){try{connection=JDBCUtils.getconn();}catch (SQLException ee){connection=JDBCUtils.getconn();}}
            PreparedStatement preparedStatement = null;
            String sql = "insert into stopping (arrival,constant,order_in_line,stay,line_id,station_id)values(?,?,?,?,?,?)";
            preparedStatement=connection.prepareStatement(sql);

            List<String>[] dirs=new List[2];
            dirs[0]=getDir(); dirs[1]=new ArrayList<>(dirs[0]); Collections.reverse(dirs[1]);
            int flag=0;
            for(List<String> dir:dirs) {
                String pre=null; int order=1,arrival=0;double constant=0;
                for(String cur:dir){
                    if(pre==null)pre=cur;
                    preparedStatement.setInt(3, order);
                    preparedStatement.setInt(4, 5);
                    preparedStatement.setString(5, (flag==0?"JP-":"JP-r") + lineId);
                    preparedStatement.setString(6, cur);
                    double dis = stationDAO.getDistance(pre, cur);
                    pre = cur;
                    arrival += 60 * dis / speed + 5;
                    constant += dis * price;
                    order++;
                    preparedStatement.setInt(1, arrival);
                    preparedStatement.setDouble(2, constant);
                    preparedStatement.addBatch();
                }
                System.out.println("injecting: "+(flag==0?"JP-":"JP-r") + lineId);
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
                flag++;
            }
        }catch (SQLException e){e.printStackTrace();}

    }
}
