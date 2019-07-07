package com.hand.zhang.stockmvc.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Sina_getList extends Thread{
    private MyJDBC myJDBC = new MyJDBC();
    public static void main(String[] args) {
        new Sina_getList();
    }
    String url = "http://vip.stock.finance.sina.com.cn/q/go.php/vIR_CustomSearch/index.phtml?sr_p=-1&order=code%7C1&p=";
    private Sina_getList(){}
    public static Sina_getList sina_getList = new Sina_getList();
    public static Sina_getList  getInstance(){
        return sina_getList;
    }

    @Override
    public void run() {
        super.run();
        this.startDownload();
        this.myJDBC.closeConnection();
    }

    private void startDownload(){
        Map<String,String> listMap = Sina_getListsAndCombine();
        for(String s:listMap.keySet()){
            System.out.println(s + " " + listMap.get(s));
        }
        System.out.println("[total:" + listMap.size() + "]");
        this.saveList(listMap);
    }

    private void saveList(Map<String,String> nameMap){
        for(String s:nameMap.keySet()){
            myJDBC.addStocks(s,nameMap.get(s));
        }
    }

    private Map<String,String> Sina_getListsAndCombine(){
        Map<String,String> nameMap = new LinkedHashMap<>();
        int page = 0;
        while (true){
            page++;
            Map<String,String> singlePageMap = this.Sina_getListAtPage(page);
            if(singlePageMap==null){
                break;
            }
            else {
                this.removeExceptSZorSH(singlePageMap);
                nameMap.putAll(singlePageMap);
            }
        }
        return  nameMap;
    }

    private void removeExceptSZorSH(Map<String,String> input){
        Set<String> set = new LinkedHashSet<>(input.keySet());
        for(String s:set){
            if(!s.matches(".*[a-zA-z].*")){
                System.out.println("[remove:" + s + "]");
                input.remove(s);
            }
        }
    }

    private Map<String,String> Sina_getListAtPage(int page){
        Document doc = getConnection(url+page);
        System.out.println("[page:"+page+"]");
        Elements ee = doc.getElementsByClass("list_table").select("span");
        Map<String,String> nameMap = new LinkedHashMap<>();
        for(Element e:ee){
            if(e.hasText()){
                String num = e.toString().split("\"")[1].substring(5);
                System.out.println("[add:" + num + "]");
                nameMap.put(num,e.text());
            }
        }
        if(nameMap.size()==0){
            return null;
        }
        return nameMap;
    }
    private Document getConnection(String url){
        try {
            return Jsoup.connect(url).get();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
