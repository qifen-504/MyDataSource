package cn.xdl.util;

import cn.xdl.dataSource.MyDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBCPUtil {
    static String url;
    static String driver;
    static String username;
    static String password;
    static  int initialSize;
    static int maxActive;
    static long maxWait;
    //加载配置文件并得到驱动
    static {
        Properties ppt = new Properties();
        InputStream is = DBCPUtil.class.getClassLoader().getResourceAsStream("dbcp.properties");
        try {
            ppt.load(is);
            driver=ppt.getProperty("driverClassName");
            url=ppt.getProperty("url");
            username=ppt.getProperty("username");
            password=ppt.getProperty("password");
            initialSize= Integer.parseInt(ppt.getProperty("initialSize"));
            maxActive= Integer.parseInt(ppt.getProperty("maxActive"));
            maxWait= Long.parseLong(ppt.getProperty("maxWait"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建连接池
     * @return 连接池对象
     */
    public static MyDataSource createDataSource(){
        MyDataSource dataSource = new MyDataSource();
        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.createPool(initialSize);
        return dataSource;

    }
    public static Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName(driver);
            conn= DriverManager.getConnection(url,username,password);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放连接
     * @param r
     * @param s
     * @param conn
     */
    public static void realese(ResultSet r, Statement s,Connection conn){
        if (r!=null){
            try {
                r.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (s!=null){
            try {
                s.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
