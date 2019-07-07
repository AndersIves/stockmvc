package com.hand.zhang.stockmvc.job;


import com.hand.zhang.stockmvc.crawler.Tencent_multi_getprices;

import java.text.SimpleDateFormat;
import java.util.Date;

//job不会,自己写单例定时器
public class MyJob extends Thread{
    public static void main(String[] args) {
        MyJob.StartJob(15,03);
    }
    private MyJob(){

    }
    private static MyJob myJob = new MyJob();
    private static int updateHTime = 15;
    private static int updateMTime = 30;
    private static boolean isStart = false;
    public static void StartJob(int updateHour,int updateMin){
        if(!isStart){
            isStart = true;
            updateHTime = updateHour;
            updateMTime = updateMin;
            myJob.start();
        }
    }

    @Override
    public void run() {
        super.run();
        SimpleDateFormat hf = new SimpleDateFormat("HH");
        SimpleDateFormat mf = new SimpleDateFormat("mm");
        int Hnow = Integer.parseInt(hf.format(new Date()));
        int Mnow = Integer.parseInt(mf.format(new Date()));
        int HOffset = updateHTime - Hnow;
        int MOffset = updateMTime - Mnow;
        if(MOffset<0){
            HOffset--;
            MOffset=MOffset+60;
        }
        if(HOffset<0){
            HOffset=HOffset+24;
        }
        try {
            System.out.println("next updatetime:"+HOffset+"h "+MOffset+"m");
            Thread.sleep(1000*60*MOffset+1000*60*60*HOffset);
        }catch (Exception e){

        }
        while (true){
            try {
                Tencent_multi_getprices t = new Tencent_multi_getprices(10);
                t.updateToday();
                Thread.sleep(1000*60*60*24);
            }catch (Exception e){

            }
        }
    }
}
