package DAO.station;

import DAO.JDBCUtils;
import com.alibaba.fastjson.JSON;
import inject.util.Requester;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class StationDAO {

    public void inject(String id,String name,double lon,double lat,double timezone,String transfer_id,String city_id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "insert into station (id,lat,lon,name,time_zone,city_id,transfer_id)values(?,?,?,?,?,?,?)";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            preparedStatement.setDouble(2,lat);
            preparedStatement.setDouble(3,lon);
            preparedStatement.setString(4,name);
            preparedStatement.setDouble(5,timezone);
            preparedStatement.setString(6,city_id);
            preparedStatement.setString(7,transfer_id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
    }


    public void inject(String id,String name,double lon,double lat,double timezone,String city_id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "insert into station (id,lat,lon,name,time_zone,city_id)values(?,?,?,?,?,?)";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            preparedStatement.setDouble(2,lat);
            preparedStatement.setDouble(3,lon);
            preparedStatement.setString(4,name);
            preparedStatement.setDouble(5,timezone);
            preparedStatement.setString(6,city_id);
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
            String sql = "select * from station where id=?";
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

    public void editPosition(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;
        Requester requester=new Requester();


        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from station where transfer_id=?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,"noposition");
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                String name=resultSet.getString("name");
                Map<String,Map<String,Map<String, BigDecimal>>> json=null;
                try {
                    json = (Map<String, Map<String, Map<String, BigDecimal>>>) JSON.parse(requester.loadJson("http://api.map.baidu.com/geocoder?address=" + name + "站&output=json&key=7G3Qicpu3e75jkIOqqWRLIp4roSHrT7E"));
                    Map<String,BigDecimal> location=json.get("result").get("location");
                    //System.out.println(location.get("lng").toString());
                    double lon=Double.parseDouble(location.get("lng").toString()),
                            lat=Double.parseDouble(location.get("lat").toString());
                    String updater="update station set lon=?,lat=? where name=?";
                    preparedStatement=connection.prepareStatement(updater);
                    preparedStatement.setDouble(1,lon);
                    preparedStatement.setDouble(2,lat);
                    preparedStatement.setString(3,name);
                    preparedStatement.executeUpdate();
                }catch (Exception e){
                    System.out.println(name+" not found...");
                    e.printStackTrace();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
    }


    public void reInject(String id,String name,double lon,double lat,double timezone,String city_id){
        if(existById(id)){
            System.out.println(id+":"+name+" already injected!");
            return;
        }
        System.out.println("injecting: "+id+":"+name);
        inject(id,name,lon,lat,timezone,city_id);
    }

    public void reInject(String id,String name,double lon,double lat,double timezone,String transfer_id,String city_id){
        if(existById(id)){
            System.out.println(id+":"+name+" already injected!");
            return;
        }
        System.out.println("injecting: "+id+":"+name);
        inject(id,name,lon,lat,timezone,transfer_id,city_id);
    }
}