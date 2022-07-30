package DAO.line;

import DAO.JDBCUtils;
import com.alibaba.fastjson.JSON;
import inject.util.Requester;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.conn.ssl.SSLContexts;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LineDAO {
    public void inject(String id,String name,String train_id,String company_id,boolean is_regular){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "insert into line (id,name,train_id,company_id,is_regular)values(?,?,?,?,?)";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            preparedStatement.setString(2,name);
            preparedStatement.setString(3,train_id);
            preparedStatement.setString(4,company_id);
            preparedStatement.setBoolean(5,is_regular);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
    }

    public boolean existById(String id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from line where id=?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
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

    public void reInject(String id,String name,String train_id,String company_id,boolean is_regular){
        if(existById(id)){
            System.out.println(id+":"+name+" already injected!");
            return;
        }
        System.out.println("injecting: "+id+":"+name);
        inject(id,name,train_id,company_id,is_regular);
    }

    public void chinaLine(){
        List<String> head=List.of("G","D","C","Z","T","K","N","L","Y","A","");
        Requester requester=new Requester();
        Map<String, List<Map<String,String>>> json=null;
        for(String h:head){
            for(int i=0;i<=99;i++){
                String pre=h+i;
                try {
                    String result1=getByString("https://search.12306.cn/search/v1/train/search?keyword="+pre+"&date=20220731");
                    json = ( Map<String, List<Map<String,String>>>) JSON.parse(result1);
                    List<Map<String,String>> lineList=json.get("data");
                    for(Map<String,String> line:lineList){
                        String id=line.get("train_no");
                        String name=line.get("station_train_code");
                        String train_id=("GCD".contains(name.substring(0,1))?"CN-CRH380A":"CN-HXD1D");
                        try{
                            reInject("CN-"+id,name,train_id,"CN-110000",true);
                        }catch (Exception ee){
                            ee.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
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
