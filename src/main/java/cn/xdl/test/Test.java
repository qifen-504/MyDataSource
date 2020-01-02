package cn.xdl.test;

import cn.xdl.dataSource.MyDataSource;
import cn.xdl.util.DBCPUtil;


public class Test {
    public static void main(String[] args) {
        MyDataSource dataSource = DBCPUtil.createDataSource();
        ThreadOne one =new ThreadOne(dataSource);
        ThreadOne two =new ThreadOne(dataSource);
        ThreadOne three =new ThreadOne(dataSource);
        Thread t1 =new Thread(one);
        Thread t2 =new Thread(two);
        Thread t3 =new Thread(three);
        t1.start();
        t2.start();
        t3.start();
    }
}
