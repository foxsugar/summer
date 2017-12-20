package com.code.server.login.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

/**
 * Created by sunxianping on 2017/12/20.
 */
@Controller
public class TestAction {


    @RequestMapping(path = "/testjsp")
    public String  index() {
//        view.setViewName("testjsp");
//        return view;
        return "testjsp";
    }


    @RequestMapping("/tt")
    public String welcome(Map<String, Object> model) {
        model.put("time", new Date());
        model.put("message", "hh");
        return "welcome";
    }
}
