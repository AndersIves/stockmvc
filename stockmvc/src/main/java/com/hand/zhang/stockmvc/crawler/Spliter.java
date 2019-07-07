package com.hand.zhang.stockmvc.crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Spliter {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        for(int i=-1;i<15;i++){
            System.out.println(Spliter.splitList(list,i));
        }
    }

    public static List splitList(List list, int part){
        if(list==null || list.size()<part || part<1){
            return null;
        }
        else {
            int splitSize = list.size() / part;
            int offSet = list.size() % part;
            List<List> lists = new ArrayList<>();
            for(int i = 0;i<part;i++){
                List l = new ArrayList();
                for(int j = splitSize*i;j<splitSize*(i+1);j++){
                    l.add(list.get(j));
                    if(i==part-1 && j==splitSize*(i+1)-1 && offSet!=0){
                        for(int k = 1;k<=offSet;k++){
                            l.add(list.get(j+k));
                        }
                    }
                }
                lists.add(l);
            }
            return lists;
        }
    }
}
