package cn.xdl.test;

import cn.xdl.dataSource.MyDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ThreadOne implements Runnable{
    private MyDataSource dataSource;
    public ThreadOne(MyDataSource dataSource)
    {
        this.dataSource=dataSource;

    }

    @Override
    public void run() {
        for (int i =0;i<10;i++){
            Connection conn = dataSource.getConnection();
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
