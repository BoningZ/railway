package DAO.station;

import DAO.JDBCUtils;
import DAO.admin.CityDAO;
import DAO.admin.DistrictDAO;
import com.alibaba.fastjson.JSON;
import inject.util.Requester;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StationDAO {

    public double getDistance(String id1,String id2) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        double lat1 = 0, lat2 = 0, lon1 = 0, lon2 = 0;

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from station where id=?";
            preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
            preparedStatement.setString(1, id1);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                lat1 = resultSet.getDouble("lat"); lon1=resultSet.getDouble("lon");
            }
            String sql2 = "select * from station where id=?";
            preparedStatement = (PreparedStatement) connection.prepareStatement(sql2);
            preparedStatement.setString(1, id2);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                lat2 = resultSet.getDouble("lat"); lon2=resultSet.getDouble("lon");
            }
            if(lon1*lat1*lon2*lat2==0||100*Math.sqrt((lon1-lon2)*(lon1-lon2)+(lat1-lat2)*(lat1-lat2))>1000)return 2.5;
            return 100*Math.sqrt((lon1-lon2)*(lon1-lon2)+(lat1-lat2)*(lat1-lat2));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(preparedStatement, connection);
        }
        return 0;

    }

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
    public List<String> getAllKorea(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;
        List<String> res=new ArrayList<>();

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from station where id like ?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,"KR%");
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next())res.add(resultSet.getString("name"));
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
        return res;
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

    public String findIdByNameCN(String name){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from station where name=? and id like ?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(2,"CN%");
            preparedStatement.setString(1,name);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next())return resultSet.getString("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
        return null;
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

    public void addJapanCity() throws IOException {
        CityDAO cityDAO=new CityDAO();
        DistrictDAO districtDAO=new DistrictDAO();

        Path path= Paths.get("D:\\Java_demo\\railway\\railway\\data-injector\\inject\\station\\jpstation.csv");
        Scanner sc1=new Scanner(path);
        HashMap<String,String[]> infos=new HashMap<>();
        sc1.nextLine();
        while(sc1.hasNextLine()){
            String tmp= sc1.nextLine();
            String[] tmps=tmp.split(",");
            infos.put(tmps[0],tmp.split(","));
        }


        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from station where city_id is null and id like ?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,"JP%");
            resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                String id=resultSet.getString("id");
                String[] info=infos.get(id.substring(3));
                String addr=info[8],city=null,cityId=null;
                String name=info[2]; double lon=Double.parseDouble(info[9]),lat=Double.parseDouble(info[10]);String transferId=info[1];
                if(addr.indexOf('市')!=-1) {
                    if(addr.indexOf("県")+1<addr.indexOf("市")+1&& addr.contains("県"))
                        city = addr.substring(addr.indexOf("県")+1, addr.indexOf("市")+1);
                    else  if(addr.indexOf("都")+1<addr.indexOf("市")+1&&addr.contains("都"))
                        city = addr.substring(addr.indexOf("都")+1, addr.indexOf("市")+1);
                    else  if(addr.indexOf("府")+1<addr.indexOf("市")+1&&addr.contains("府"))
                        city = addr.substring(addr.indexOf("府")+1, addr.indexOf("市")+1);
                    else  if(addr.indexOf("道")+1<addr.indexOf("市")+1&&addr.contains("道"))
                        city = addr.substring(addr.indexOf("道")+1, addr.indexOf("市")+1);
                    else city = addr.substring(0, addr.indexOf("市")+1);
                    cityId = cityDAO.findIdByName(city);
                }
                else if(addr.indexOf('区')!=-1) {
                    city = addr.substring(addr.indexOf("都")+1, addr.indexOf('区') + 1);
                    cityId = cityDAO.findIdByName(city);
                    if (cityId == null) cityId = districtDAO.findCityIddByName(city);
                }
                else if(addr.indexOf('町')!=-1) {
                    city = addr.substring(addr.indexOf("郡")+1, addr.indexOf('町') + 1);
                    cityId = districtDAO.findCityIddByName(city);
                }
                else if(addr.indexOf('村')!=-1) {
                    city = addr.substring(addr.indexOf("郡")+1, addr.indexOf('村') + 1);
                    cityId = districtDAO.findCityIddByName(city);
                }
                if(addr.contains("岩舟町"))city="栃木市";


                cityId = cityDAO.findIdByName(city);
                if (cityId == null) cityId = districtDAO.findCityIddByName(city);

                System.out.println("addr:"+addr+" id:"+id+" city:"+city+" cityId:"+cityId);

                String update="update station set city_id=?,name=?,lon=?,lat=?,transfer_id=? where id=?";
                preparedStatement=connection.prepareStatement(update);
                preparedStatement.setString(1,cityId);
                preparedStatement.setString(2,name);
                preparedStatement.setDouble(3,lon);
                preparedStatement.setDouble(4,lat);
                preparedStatement.setString(5,transferId);
                preparedStatement.setString(6,id);
                preparedStatement.executeUpdate();
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
