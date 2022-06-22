package com.ucas.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author qingjiusanliangsan
 * create 2022-06-04-21:37
 */

@Controller
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping("/hello")
    public String hello(Model model){
        model.addAttribute("name","ucas");
        return "hello";
    }

}
