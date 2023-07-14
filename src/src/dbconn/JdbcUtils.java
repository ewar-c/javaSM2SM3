package src.dbconn;


import java.io.IOException;
import java.sql.*;
import java.util.Properties;


public class JdbcUtils {

    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String url ="jdbc:mysql://localhost:3306/encrypted_transmission";
    private static final String user = "root";
    private static final String password = "123456";

    static {
        try {
            //加载数据库驱动
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //get链接方法
    public static Connection getConnection(){
        Connection conn = null;
        try {
            //获取数据库链接
            conn = DriverManager.getConnection(url,user,password);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return conn;
    }

    //关流方法
    public static void close(ResultSet res,PreparedStatement pst,Connection conn) {
        if(null !=res) {
            try {
                res.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(null !=pst) {
            try {
                pst.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(null !=conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws SQLException {
        String sql = "select count(*) from encrypted_transmission.login_table where user_hashcode = '166c7f0f462eeedd9d1f2d46bdc10e4e24167c4875cf2f7a2297da02b8f4ba8e0'";
        Connection conn = JdbcUtils.getConnection();
        Statement stmt=conn.createStatement();
        ResultSet result = stmt.executeQuery(sql);
        if (result.next()){
            System.out.println(result.getInt(1));

        }
    }
}