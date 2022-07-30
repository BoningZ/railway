package DAO.line;

import DAO.JDBCUtils;
import DAO.station.StationDAO;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class StoppingDAO {
    public void inject(int arrival,double constant,int orderInLine,int stay,String lineId,String stationId){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "insert into stopping (arrival,constant,order_in_line,stay,line_id,station_id)values(?,?,?,?,?,?)";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setInt(1,arrival);
            preparedStatement.setDouble(2,constant);
            preparedStatement.setInt(3,orderInLine);
            preparedStatement.setInt(4,stay);
            preparedStatement.setString(5,lineId);
            preparedStatement.setString(6,stationId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
    }



    public boolean existByLineAndStation(String line_id,String station_id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from stopping where line_id=? and station_id=?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,line_id);
            preparedStatement.setString(2,station_id);
            resultSet=preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
        return false;
    }
    public void reInject(int arrival,double constant,int orderInLine,int stay,String lineId,String stationId){
        if(existByLineAndStation(lineId,stationId)){
            System.out.println(lineId+" & "+stationId+" already injected!");
            return;
        }
        System.out.println("injecting: "+lineId+" & "+stationId);
        inject(arrival,constant,orderInLine,stay,lineId,stationId);
    }

    public void addChinaStopping(){
        StationDAO stationDAO=new StationDAO();
        DepartureDAO departureDAO=new DepartureDAO();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;
        Map<String,Map<String,List<Map<String,String>>>> json=null;
        List<String> lines=new ArrayList<>();
        try{
            connection = JDBCUtils.getconn();
            String sql = "select * from line where id like ?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,"CN%");
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next())lines.add(resultSet.getString("id").substring(3));
            Collections.reverse(lines);
        }catch (SQLException e){e.printStackTrace();}
        for(String line:lines){
            try{
                String res=getByString("https://kyfw.12306.cn/otn/queryTrainInfo/query?leftTicketDTO.train_no="+line+"&leftTicketDTO.train_date=2022-07-31&rand_code=0");
                json= (Map<String,Map<String,List<Map<String,String>>>>)JSON.parse(res);
                List<Map<String,String>> stops=json.get("data").get("data");
                int flag=0;
                for(Map<String,String> stop:stops){
                    int orderInLine=Integer.parseInt(stop.get("station_no"));
                    if(orderInLine==1)if(departureDAO.existById("CN-"+line+stop.get("start_time").replace(":",""))){
                        departureDAO.reInject("CN-"+line+stop.get("start_time").replace(":",""),3,parseHHMM(stop.get("start_time")),"CN-"+line,"CN-001");
                        flag=1;
                        break;
                    }
                }
                if(flag>0)continue;
                for(Map<String,String> stop:stops){
                    int orderInLine=Integer.parseInt(stop.get("station_no"));
                    int arrival=parseHHMM(stop.get("running_time"));
                    boolean isGCD=false;
                    for(int i=5;i<=8;i++)isGCD|="GCD".contains(line.substring(i,i+1));
                    double constant=isGCD?arrival*2.2:arrival*0.12;
                    int stay=parseHHMM(stop.get("start_time"))-parseHHMM(stop.get("arrive_time"));
                    String lineId="CN-"+line;
                    String stationName=stop.get("station_name");
                    String stationId=stationDAO.findIdByNameCN(stationName);
                    if(stationId==null){
                        stationDAO.reInject("CN-new"+stationName,stationName,0,0,8,null,null);
                        stationId=stationDAO.findIdByNameCN(stationName);
                    }
                    try{
                        reInject(arrival,constant,orderInLine,stay,lineId,stationId);
                    }catch (Exception e){e.printStackTrace();}
                    if(orderInLine==1){
                        try {
                            departureDAO.reInject("CN-"+line+stop.get("start_time").replace(":",""),3,parseHHMM(stop.get("start_time")),"CN-"+line,"CN-001");
                        }catch (Exception e){e.printStackTrace();}
                    }
                }
            }catch (Exception e){e.printStackTrace();}
        }
    }

    public final static String getByString(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        try {
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("Accept", "text/html");
            httpget.addHeader("Accept-Charset", "utf-8");
            httpget.addHeader("Accept-Encoding", "gzip");
            httpget.addHeader("Accept-Language", "en-US,en");
            httpget.addHeader("User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.160 Safari/537.22");
            ResponseHandler responseHandler = new ResponseHandler() {

                public String handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {
                        HttpEntity entity = response.getEntity();
                        System.out.println(status);
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        System.out.println(status);
                        java.util.Date date=new Date();
                        System.out.println(date);
                        System.exit(0);
                        throw new ClientProtocolException("Unexpected response status: " + status);
                    }
                }
            };
            String responseBody = (String) httpclient.execute(httpget, responseHandler);


            Thread.currentThread().sleep(200);
            return responseBody;
        } finally {
            httpclient.close();
        }
    }
    public int parseHHMM(String time){
        int res=0;
        try{
            String hours = time.substring(0,2);
            String minutes = time.substring(3);
            res+=Integer.parseInt(minutes);
            res+=Integer.parseInt(hours)*60;
            return res;
        }
        catch(Exception e){return -1;}

    }
}
