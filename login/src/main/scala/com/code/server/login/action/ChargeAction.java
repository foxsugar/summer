package com.code.server.login.action;

import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Convert;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by sunxianping on 2017/4/13.
 */
@Controller
@EnableAutoConfiguration
public class ChargeAction {


    @RequestMapping("/charge")
    @ResponseBody
    String charge(HttpServletRequest request, HttpServletResponse response){
        System.out.println("======");
        return "";
    }

}
