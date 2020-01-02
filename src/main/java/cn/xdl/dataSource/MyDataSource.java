package cn.xdl.dataSource;

import cn.xdl.util.DBCPUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MyDataSource {
    private int initialSize;
    private int maxActive;
    private static int count;
    private long  maxWait;

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    //创建连接池
    private static List<Connection> pool = Collections.synchronizedList(new LinkedList<Connection>());

    public void createPool (int initialSize){
        for (int i =0;i<initialSize;i++){
            addConnection();
        }
    }
    /**
     * 添加连接
     */
    private synchronized void dynamicLink(){
        //连接池为空
        if (pool.size()<=0){
            //达到最大连接数
            if (count==maxActive){
                System.out.println("达到最大连接数");
                //future 当时时间+最大等待时间
                long future = System.currentTimeMillis()+maxWait;
                //最大等待时间
                long current = maxWait;
                //当连接池为空 并且等待时间>0时
                while (pool.size()<=0 && current > 0){
                    try {
                        //等待
                        this.wait(current);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("开始等待...");
                    //当时时间+最大等待时间-当时时间 如果0则表示等待超时
                    current=future-System.currentTimeMillis();
                    if (current<0){

                        throw new RuntimeException("连接超时");
                    }else{
                        addConnection();
                    }
                }
            }
        }
    }
    /**
     * 获取单个连接
     * @return 返回连接代理对象
     */
    public synchronized Connection getConnection(){
        dynamicLink();
        System.out.println("连接总数:"+count+"-->剩余连接:"+pool.size());
        if (pool.size()==0){
            addConnection();
        }
        final Connection conn =pool.remove(0);
        Connection Connproxy = (Connection) Proxy.newProxyInstance(conn.getClass().getClassLoader(), new Class[]{Connection.class},
                new InvocationHandler() {
                    @Override
                    //                                  真实对象       正在调用的方法   方法参数
                    public synchronized Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object rtValue =null;
                        //如果调用的方法是close方法 则将连接归还连接池 而不是直接调用close被回收
                        if ("close".equals(method.getName())){
                            Thread.sleep(1000);
                            //归还给连接池
                            pool.add(conn);
                            System.out.println("归还完毕");
                            this.notifyAll();
                        }else{
                            rtValue =method.invoke(conn,args);
                        }
                        return  rtValue;
                    }
                });
        System.out.println(Thread.currentThread().getName()+"获取到的连接是:"+Connproxy);
                return Connproxy;
    }

    private void addConnection(){
        Connection conn = DBCPUtil.getConnection();
        pool.add(conn);
        count++;
    }
    /**
     * 获取连接池大小
     * @return
     */
    public static  int getPoolSize(){
        return pool.size();
    }
}
