package com.hand.zhang.stockmvc.crawler;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tencent_update_today extends Thread{
    public static void main(String[] args) {
        MyJDBC myJDBC = new MyJDBC();
        List<String> list = myJDBC.getStocks();
        Tencent_update_today t = new Tencent_update_today(1+"",list);
        t.start();
    }
    String workerName = null;
    List<String> stockslist = null;
    Tencent_update_today(String workerName,List<String> list ){
        this.workerName = workerName;
        this.stockslist = list;
    }

    @Override
    public void run() {
        super.run();
        MyJDBC myJDBC = new MyJDBC();
        int size = stockslist.size();
        int i =0;
        for(String id:stockslist){
            String url = getTodayURL(id);
            Map<String,String> map = readJS(url);
            if(map!=null){
                i++;
                System.out.println("[update "+ workerName +"]: " + ((double)i/(double)size*100) + "%");
                myJDBC.addPrices(id,map);
            }
        }
        myJDBC.addPricesAddmit();
        myJDBC.closeConnection();
    }

    private InputStream data = null;
    private void  downLoadFromUrl(String urlStr){
        try {
            URL url = null;
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(3*1000);
            //403
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            this.data = conn.getInputStream();
        }catch (Exception e){
            downLoadFromUrl(urlStr);
            System.out.println("[redo]: "+urlStr);
        }
    }
    private int getResponseCode(String urlString){
        try {
            URL u = null;
            u =new URL(urlString);
            HttpURLConnection huc =  (HttpURLConnection)  u.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            return huc.getResponseCode();
        }catch (Exception e){
            return getResponseCode(urlString);
        }
    }

    public Map<String,String> readJS(String url){
        try {
            if(getResponseCode(url)!=404){
                downLoadFromUrl(url);
                Scanner Reader = null;
                Reader = new Scanner(this.data);
                Reader.nextLine();
                Map<String,String> map = new LinkedHashMap<>();
                while (Reader.hasNextLine()){
                    String [] line = Reader.nextLine().split(" ");
                    if(line.length==6){
                        map.put(line[0],line[2]);
                    }
                }
                Reader.close();
                return map;
            }
            else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String getTodayURL(String ID){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        date = date.substring(2);
        int year = Integer.parseInt(date);
        return  "http://data.gtimg.cn/flashdata/hushen/daily/" +
                    year +
                    "/" +
                    ID +
                    ".js?visitDstTime=1";
    }
}
