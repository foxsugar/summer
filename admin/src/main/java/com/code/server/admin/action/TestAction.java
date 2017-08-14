package com.code.server.admin.action;

import com.code.server.util.JsonUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunxianping on 2017/8/11.
 */
@RestController
@EnableAutoConfiguration
public class TestAction {




    @RequestMapping(value = "/test")
    public String callback(HttpServletRequest request, HttpServletResponse response) {


        Map map = new HashMap<>();
        map.put("a", "a");
        map.put("b", "b");
        map.put("c", "c");

        return JsonUtil.toJson(map);
    }


}
