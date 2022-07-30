package DAO.line;

import DAO.JDBCUtils;

import java.sql.*;

public class DepartureDAO {
    public void inject(String id, int schedule, int start,String lineId,String driverId){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "insert into departure (id,schedule,start,line_id,driver_id)values(?,?,?,?,?)";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            preparedStatement.setInt(2,schedule);
            preparedStatement.setInt(3,start);
            preparedStatement.setString(4,lineId);
            preparedStatement.setString(5,driverId);
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
            String sql = "select * from departure where id=?";
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

    public Integer getScheduleById(String id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from departure where id=?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
                return resultSet.getInt("schedule");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
        return null;
    }

    public void addScheduleById(String id,int newSchedule){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet=null;

        try {
            connection = JDBCUtils.getconn();
            String sql = "select * from departure where id=?";
            preparedStatement = (PreparedStatement)connection.prepareStatement(sql);
            preparedStatement.setString(1,id);
            resultSet=preparedStatement.executeQuery();
            resultSet.next();
            int schedule=resultSet.getInt("schedule");
            String newsql="update departure set schedule=? where id=?";
            preparedStatement=connection.prepareStatement(newsql);
            preparedStatement.setInt(1,schedule|newSchedule);
            preparedStatement.setString(2,id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally{
            JDBCUtils.close(preparedStatement,connection);
        }
    }





    public void reInject(String id, int schedule, int start,String lineId,String driverId){
        Integer oldSchedule=getScheduleById(id);
        if(oldSchedule!=null){
            System.out.println(id+":"+lineId+" already injected, now modify schedule!");
            addScheduleById(id,schedule);
            return;
        }
        System.out.println("injecting: "+id+":"+lineId);
        inject(id,schedule,start,lineId,driverId);
    }
}
