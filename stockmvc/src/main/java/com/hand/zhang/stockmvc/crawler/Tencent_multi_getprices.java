package com.hand.zhang.stockmvc.crawler;

import java.util.List;

public class Tencent_multi_getprices extends Thread{

    private MyJDBC myJDBC = new MyJDBC();
    private int workers;

    public static void main(String[] args) {
        Tencent_multi_getprices tencent_multi_getprices = new Tencent_multi_getprices(10);
        tencent_multi_getprices.start();
    }

    public Tencent_multi_getprices(int workers){
        this.workers = workers;
    }

    @Override
    public void run() {
        super.run();
        List<String> list = myJDBC.getStocks();
        List<List> lists = Spliter.splitList(list,workers);
        for(int i=0;i<workers;i++){
            Tencent_getprices tencent_getprices = new Tencent_getprices(i+"",lists.get(i));
            tencent_getprices.start();
        }
        this.myJDBC.closeConnection();
    }

    public void updateToday(){
        List<String> list = myJDBC.getStocks();
        List<List> lists = Spliter.splitList(list,workers);
        for(int i=0;i<workers;i++){
            Tencent_update_today tencent_update_today = new Tencent_update_today(i+"",lists.get(i));
            tencent_update_today.start();
        }
        this.myJDBC.closeConnection();
    }
}
