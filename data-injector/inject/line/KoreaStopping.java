package inject.line;

import DAO.JDBCUtils;
import DAO.line.DepartureDAO;
import DAO.line.LineDAO;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class KoreaStopping {
    public static DepartureDAO departureDAO=new DepartureDAO();
    public static LineDAO lineDAO=new LineDAO();
    public static StationDAO stationDAO=new StationDAO();

    public static void main(String[] args) {
        List<String> allLine=new LineDAO().getKRLine();
        for(String line:allLine){
            try {
                Connection connection = null;
                try{connection= JDBCUtils.getconn();}catch (SQLException e){try{connection=JDBCUtils.getconn();}catch (SQLException ee){connection=JDBCUtils.getconn();}}
                PreparedStatement preparedStatement = null;
                String sql = "insert into stopping (arrival,constant,order_in_line,stay,line_id,station_id)values(?,?,?,?,?,?)";
                preparedStatement=connection.prepareStatement(sql);

                String result=getByString("https://smart.letskorail.com/classes/com.korail.mobile.trainsInfo.TrainSchedule?Device=IP&srtCheckYn=Y&txtRunDt=20220805&txtTrnNo="+line);
                Map<String,Map<String,List<Map<String,String>>>> json= (Map<String,Map<String,List<Map<String,String>>>>)JSON.parse(result);
                List<Map<String,String>> times=json.get("time_infos").get("time_info");
                Integer departure=null; double price=lineDAO.getNameById("KR-"+line).contains("KTX")?150:70; double curDis=0; String preSta=null; int order=0;
                for(Map<String,String> t:times){
                    String depstr=t.get("h_dpt_tm");
                    String arrstr=t.get("h_arv_tm");
                    int dep=0,arr=0;
                    if(departure==null){
                        preSta="KR-"+t.get("h_stop_rs_stn_cd");
                        departure=parseMin(depstr);
                        departureDAO.reInject("KR-"+line+depstr.substring(0,4),7,departure,"KR-"+line,"KR-001");
                        arr=departure;
                    }else arr=parseMin(arrstr);
                    if(depstr.equals("999999"))dep=arr;
                        else dep=parseMin(depstr);
                    curDis+=stationDAO.getDistance(preSta,"KR-"+t.get("h_stop_rs_stn_cd"));
                    order++;
                    preparedStatement.setInt(1,arr-departure);
                    preparedStatement.setDouble(2,curDis*price);
                    preparedStatement.setInt(3,order);
                    preparedStatement.setInt(4,dep-arr);
                    preparedStatement.setString(5,"KR-"+line);
                    preparedStatement.setString(6,"KR-"+t.get("h_stop_rs_stn_cd"));
                    preparedStatement.addBatch();
                    preSta="KR-"+t.get("h_stop_rs_stn_cd");
                }
                System.out.println("injecting: "+line);
                preparedStatement.executeBatch();
                preparedStatement.clearBatch();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public final static int parseMin(String str){
        return Integer.parseInt(str.substring(0,2))*60+Integer.parseInt(str.substring(2,4));
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
                        Date date=new Date();
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
}
