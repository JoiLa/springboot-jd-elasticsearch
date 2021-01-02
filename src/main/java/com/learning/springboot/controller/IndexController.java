package com.learning.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by lfs
 * Email: 1144828910@qq.com
 * User: 11448
 * Date: 2021/1/2
 * Time: 16:36
 */
@Controller
public class IndexController {

    @GetMapping(value = "/index")
    public String index() {
        return "index";
    }
}
