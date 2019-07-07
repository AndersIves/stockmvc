package com.hand.zhang.stockmvc.controller;


import com.hand.zhang.stockmvc.crawler.MyJDBC;
import com.hand.zhang.stockmvc.crawler.Tencent_multi_getprices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {
    @RequestMapping("/getNameByID")
    public String getNameByID(String id){
        MyJDBC myJDBC = new MyJDBC();
        System.out.println(id);
        String m = myJDBC.getNameByID(id);
        myJDBC.closeConnection();
        System.out.println(m);
        return m;
    }
    @RequestMapping("/getPricesMap")
    public Map<String,String> getPricesMap(String id){
        MyJDBC myJDBC = new MyJDBC();
        Map m = myJDBC.getPrices(id);
        myJDBC.closeConnection();
        return m;
    }
    @RequestMapping("/get30StocksMap")
    public Map<String,String> get30StocksMap(){
        MyJDBC myJDBC = new MyJDBC();
        Map<String,String> m = myJDBC.getStocksMap();
        for(String s : m.keySet()){
            m.replace(s,m.get(s) +"?"+ myJDBC.count30daysRate(s));
        }
        myJDBC.closeConnection();
        return m;
    }
    @RequestMapping("/getStocksMap")
    public Map<String,String> getStocksMap(){
        MyJDBC myJDBC = new MyJDBC();
        Map m = myJDBC.getStocksMap();
        myJDBC.closeConnection();
        return m;
    }
    @RequestMapping("/getStarted")
    public Boolean getStarted(){
        return this.started;
    }
    private boolean started = false;
    @RequestMapping("/startcrawler")
    public String  startcrawler(){
        started = true;
        Tencent_multi_getprices t = new Tencent_multi_getprices(10);
        t.start();

        return "正在获取";
    }
}
