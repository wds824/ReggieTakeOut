package com.wds.reggie.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WDs , wds8.24@outlook.com
 * @version 1.0
 * @since 2022-08-13 7:39
 */
@RestController
public class BaseController {
    @RequestMapping("/")
    public String index() {
        return "<script> document.location.href = '" +
                "/static/index.html" +
                "' </script>";
    }

    @RequestMapping("/front")
    @ResponseBody
    public String frontHome() {
        return "<script> document.location.href = '" +
                "/front/page/login.html" +
                "' </script>";
    }

    @RequestMapping("/backend")
    @ResponseBody
    public String backedHome() {
        return "<script> document.location.href = '" +
                "/backend/page/login/login.html" +
                "' </script>";
    }
//
//    @GetMapping("/shoppingCart/list")
//    public R<List<Object>> listTemp(){
//        return R.success(new ArrayList<>());
//    }
}
