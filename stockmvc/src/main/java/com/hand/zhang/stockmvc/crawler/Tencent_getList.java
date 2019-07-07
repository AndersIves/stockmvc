package com.hand.zhang.stockmvc.crawler;

import jxl.Sheet;
import jxl.Workbook;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tencent_getList extends Thread{
    public static void main(String[] args){
        Tencent_getList tencent_getList = new Tencent_getList();
        tencent_getList.run();
    }
    private MyJDBC myJDBC = new MyJDBC();
    private String Aurl = "http://stock.gtimg.cn/data/get_hs_xls.php?id=ranka&type=1&metric=name";
    private String Burl = "http://stock.gtimg.cn/data/get_hs_xls.php?id=rankb&type=1&metric=name";

    @Override
    public void run() {
        super.run();
        this.saveList(this.getMapFromXLS("A.xls"));
        this.saveList(this.getMapFromXLS("B.xls"));
        this.myJDBC.closeConnection();
    }

    private void saveList(Map<String,String> nameMap){
        for(String s:nameMap.keySet()){
            myJDBC.addStocks(s,nameMap.get(s));
        }
    }

    private Map<String,String> getMapFromXLS(String name){
        File file = new File("TencentData" + File.separator + name);
        List list = readExcel(file);
        list.remove(0);
        list.remove(0);
        Map<String,String> map = new LinkedHashMap<>();
        for(Object l:list){
            map.put(((List<String>)l).get(0),((List<String>)l).get(1));
        }
        return map;
    }
    private List readExcel(File file) {
        try {
            InputStream is = new FileInputStream(file.getAbsolutePath());
            Workbook wb = Workbook.getWorkbook(is);
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                List<List> outerList=new ArrayList<>();
                Sheet sheet = wb.getSheet(index);
                for (int i = 0; i < sheet.getRows(); i++) {
                    List innerList=new ArrayList();
                    for (int j = 0; j < sheet.getColumns(); j++) {
                        String cellinfo = sheet.getCell(j, i).getContents();
                        if(cellinfo.isEmpty()){
                            continue;
                        }
                        innerList.add(cellinfo);
                    }
                    outerList.add(i, innerList);
                }
                return outerList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Tencent_getList(){
        this.downloadListToXls(Aurl,"A.xls");
        this.downloadListToXls(Burl,"B.xls");
    }

    private void downloadListToXls(String downloadurl,String name){
        try {
            downLoadFromUrl(downloadurl,name,"TencentData");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void  downLoadFromUrl(String urlStr,String fileName,String savePath) throws Exception{
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(3*1000);
        //403
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+File.separator+fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }


        System.out.println("info:"+url+" download success");

    }
    private byte[] readInputStream(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
