package com.hand.zhang.stockmvc.crawler;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Tencent_getprices extends Thread{

    private MyJDBC myJDBC = new MyJDBC();
    private String tempDataName = null;
    private List<String> stocksList = null;
    private InputStream data = null;
    public static void main(String[] args) {
        MyJDBC myJDBC = new MyJDBC();
        List<String> list = myJDBC.getStocks();

        Tencent_getprices T1 = new Tencent_getprices("1",list);
        T1.start();
    }
    Tencent_getprices(){

    }
    Tencent_getprices(String tempDataName, List<String> list){
        this.tempDataName = tempDataName;
        this.stocksList = list;
    }

    @Override
    public void run() {
        super.run();

        int size = this.stocksList.size();
        int i = 0;

        for(String s:this.stocksList){
            this.saveData(s);
            if(i%10==0){
                System.out.println("[Worker: "+this.tempDataName+"]: "+((double)i*100/(double)size)+"%");
                this.myJDBC.addPricesAddmit();
            }
            i++;
        }
        this.myJDBC.addPricesAddmit();
        this.myJDBC.closeConnection();
    }

    private void saveStoreINF(String url){
        try {
            File file = new File("TencentData\\StoreINF.txt");
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(url+"\n");
            fileWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private Set<String> loadStoreINF(){
        try {
            File file = new File("TencentData\\StoreINF.txt");
            if(!file.exists()){
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("");
                fileWriter.close();
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            Scanner Reader = new Scanner(fileInputStream);
            Set<String> list = new LinkedHashSet<>();
            while (Reader.hasNextLine()){
                list.add(Reader.nextLine());
            }
            Reader.close();
            return list;
        }catch (Exception e){
            return loadStoreINF();
        }
    }

    private boolean isReaded = false;
    private Set<String> listt = null;
    private boolean isDownload(String url){
        if(!isReaded){
            listt = this.loadStoreINF();
            isReaded = true;
        }

        return listt.contains(url);
    }

    private void saveData(String ID){
        List<String> urls = getURL(ID);
        for(String url:urls){
            if(this.isDownload(url)){
                continue;
            }
            Map<String,String> map = readJS(url);
            if(map!=null){
                myJDBC.addPrices(ID,map);
                this.saveStoreINF(url);
            }
        }
    }

    public Map<String,String> readJS(String url){
        try {
            if(getResponseCode(url)!=404){
                downLoadFromUrl(url);
                Scanner Reader = new Scanner(this.data);
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

    private void  downLoadFromUrl(String urlStr){
        try {
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(3*1000);
            //403
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            this.data = conn.getInputStream();
        }catch (Exception e){
            System.out.println("[redo]: "+urlStr);
            downLoadFromUrl(urlStr);
        }
    }

    private List<String> getURL(String ID){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy");
        Calendar c = Calendar.getInstance();
        String date = sdf.format(c.getTime());
        date = date.substring(2);
        int year = Integer.parseInt(date);
        List<String> list = new ArrayList<>();
        for(int i = year;i>=year-10;i--){
            String s = "http://data.gtimg.cn/flashdata/hushen/daily/" +
                    i +
                    "/" +
                    ID +
                    ".js?visitDstTime=1";
            list.add(s);
        }
        return list;
    }
    private int getResponseCode(String urlString){
        try {
            URL u = new URL(urlString);
            HttpURLConnection huc =  (HttpURLConnection)  u.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            return huc.getResponseCode();
        }catch (Exception e){
            return getResponseCode(urlString);
        }
    }
}
