package DAO;

import java.sql.*;

public class JDBCUtils {
    private static String url = "jdbc:mysql://bj-cdb-koq9r5sw.sql.tencentcdb.com:60263/railway";
    private static String user = "root";
    private static String password = "zbn123ZBN456";
    private static String dv = "com.mysql.cj.jdbc.Driver";

    static{
        try {
            Class.forName(dv);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Connection getconn() throws SQLException {
        Connection connection = DriverManager.getConnection(url,user,password);
        return connection;

    }

    public static void close(Statement statement, Connection connection){
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(PreparedStatement preparedStatement, Connection connection, ResultSet result) {
        if(preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(result != null){
            try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
