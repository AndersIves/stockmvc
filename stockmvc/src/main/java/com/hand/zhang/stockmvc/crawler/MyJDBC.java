package com.hand.zhang.stockmvc.crawler;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class MyJDBC {
    private Connection connection = null;
    private String url = "jdbc:mysql://localhost:3306/samp_db";
    private String username = "root";
    private String password = "";
    public static void main(String[] args) {
        MyJDBC myJDBC = new MyJDBC();
        //do...
        List l = myJDBC.getStocks();
        System.out.println(l);
        System.out.println(l.size());
        myJDBC.closeConnection();
    }

    public MyJDBC(){
        this.readPortUserPassword();
        this.init();
        this.initTables();
    }

    private void readPortUserPassword(){
        try {
            File file = new File("configuration.ini");
            FileInputStream fileInputStream = new FileInputStream(file);
            Scanner fileReader = new Scanner(fileInputStream);
            this.url = fileReader.nextLine();
            this.username = fileReader.nextLine();
            this.password = fileReader.nextLine();
            fileReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void init(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(this.url+"?serverTimezone=GMT", this.username, this.password);
            System.out.println("[Connected:" + this.url + "]");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int count30daysRate(String id){
        try {
            ResultSet resultSet = connection.prepareStatement(
                    "select finalprice from prices " +
                            "where stocksid=\""+id+"\""+
                            "and dealdata >= date_format(date_add(now(), interval -30 day),\"%y%m%d\") " +
                            "ORDER BY dealdata desc; ").executeQuery();
            List<Double> list = new ArrayList<>();
            while (resultSet.next()){
                list.add(resultSet.getDouble(1));
            }
            if(list.size()<2){
                return 0;
            }
            int count=0;
            for(int i=0;i<list.size()-1;i++){
                if((list.get(i)-list.get(i+1))/list.get(i+1)>=0.05){
                    count++;
                }
            }
            return count;
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public void closeConnection() {
        try {
            this.connection.close();
            System.out.println("[close connection]");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getNameByID(String id){
        try {
            ResultSet resultSet = connection.prepareStatement(
                    "select stockname from stocks where stocksid=\""+id+"\""
            ).executeQuery();
            resultSet.next();
            return (String)resultSet.getObject(1);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Map<String,String> getPrices(String id){
        try {
            ResultSet resultSet = connection.prepareStatement(
                    "select dealdata,finalprice from prices " +
                            "where stocksid=\""+id+"\""
            ).executeQuery();
            Map<String,String> map = new LinkedHashMap<>();
            while (resultSet.next()){
                map.put(String.valueOf(resultSet.getInt(1)),String.valueOf(resultSet.getDouble(2)));
            }
            System.out.println(map);
            return map;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Map<String,String> getStocksMap(){
        try {
            ResultSet resultSet = connection.prepareStatement(
                    "select stocksid,stockname from stocks"
            ).executeQuery();
            Map<String,String> map = new LinkedHashMap<>();
            while (resultSet.next()){
                map.put((String)resultSet.getObject(1),(String)resultSet.getObject(2));
            }
            return map;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getStocks(){
        try {
            ResultSet resultSet = connection.prepareStatement(
                    "select * from stocks"
            ).executeQuery();
            List<String> list = new ArrayList<>();
            while (resultSet.next()){
                list.add((String)resultSet.getObject(1));
            }
            return list;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void initTables(){
        try {
            connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS stocks(" +
                            "stocksid VARCHAR(10) primary key," +
                            "stockname VARCHAR(20) NOT NULL" +
                            ");").executeUpdate();
            connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS prices(" +
                            "stocksid VARCHAR(10) NOT NULL," +
                            "dealdata INTEGER (6) NOT NULL," +
                            "primary key(stocksid,dealdata)," +
                            "finalprice DECIMAL (10,2) NOT NULL" +
                            ");").executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void addStocks(String stocksid,String stockname){
        try {
            PreparedStatement pstmt = this.connection.prepareStatement(
                    "insert IGNORE into stocks (stocksid,stockname) " +
                            "values(?,?)");
            //IGNORE是MySQL相对于标准SQL的扩展。如果在新表中有重复关键字，此种方法效率比较高,判断是否存在，存在会丢弃掉这行数据，不做任何插入，否则插入。当插入数据时，如出现错误时，如重复数据，将不返回错误，只以警告形式返回。所以使用ignore请确保语句本身没有问题，否则也会被忽略掉。这样不用校验是否存在了，有则忽略，无则添加。
            pstmt.setString(1, stocksid);
            pstmt.setString(2, stockname);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<List<String>> addPricesList = new LinkedList<>();

    public void addPricesAddmit(){
        try {
            connection.setAutoCommit(false);
            PreparedStatement pstmt = this.connection.prepareStatement(
                    "insert IGNORE into prices (stocksid,dealdata,finalprice) " +
                            "values(?,?,?)");
            for(List<String> s:addPricesList){
                pstmt.setString(1, s.get(0));
                pstmt.setInt(2, Integer.parseInt(s.get(1)));
                pstmt.setDouble(3, Double.parseDouble(s.get(2)));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            connection.commit();
            pstmt.close();
            connection.setAutoCommit(true);
            addPricesList.clear();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addPrices(String stocksid, Map<String, String> map){
        for(String s:map.keySet()){
            List<String> list = new ArrayList<>();
            list.add(stocksid);
            list.add(s);
            list.add(map.get(s));
            addPricesList.add(list);
        }
    }
}
