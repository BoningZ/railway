package DAO.admin;

import DAO.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CityDAO {
    public void inject(String id,String name,String province){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "insert into city values(?,?,?)";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            preparedStatement.setString(2,name);
            preparedStatement.setString(3,province);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
    }

    public String getIdByName(String name){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from city where name like ?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,name+"%");
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

    public boolean existById(String id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from city where id=?";
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

    public void reInject(String id,String name,String country){
        if(existById(id)){
            System.out.println(id+":"+name+" already injected!");
            return;
        }
        System.out.println("injecting: "+id+":"+name);
        inject(id,name,country);
    }
}
