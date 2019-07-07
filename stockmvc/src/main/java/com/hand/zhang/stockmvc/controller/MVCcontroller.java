package com.hand.zhang.stockmvc.controller;

import com.hand.zhang.stockmvc.crawler.Tencent_multi_getprices;
import com.hand.zhang.stockmvc.crawler.Tencent_update_today;
import com.hand.zhang.stockmvc.job.MyJob;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MVCcontroller {

    @RequestMapping("/")
    public String Hello(){
        return "Home.html";
    }
    @RequestMapping("/history")
    public String history(){
        return "history.html";
    }
    @RequestMapping("/history30")
    public String history30(){
        return "history30.html";
    }
    @RequestMapping("/admin")
    public String admin(){ return "admin.html"; }
    @RequestMapping("/historyINF")
    public String historyINF(){
        return "historyINF.html";
    }
}
