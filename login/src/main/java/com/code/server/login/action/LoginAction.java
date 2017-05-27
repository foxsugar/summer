package com.code.server.login.action;


import com.code.server.db.Service.ServerService;
import com.code.server.db.Service.UserService;
import com.code.server.login.kafka.MsgProducer;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by win7 on 2017/3/8.
 */

@Controller
@EnableAutoConfiguration
public class LoginAction {

    @Autowired
    private StringRedisTemplate template;

//    @Autowired
//    private RedisTemplate<String,Person> redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private MsgProducer producer;

    // inject the actual template



    @Autowired
    private RedisTemplate redisTemplate;

    public void testObj() throws Exception {
        Person user=new Person();
        user.id = 1;
        ValueOperations<String, Person> operations=redisTemplate.opsForValue();
        ValueOperations<String, Integer> operations1=redisTemplate.opsForValue();
        HashOperations<String, String,String> operations2=redisTemplate.opsForHash();
//        operations1.set("111",1);
        operations1.increment("111",10.65);
        System.out.println(operations2.get("789","890"));
//
//        operations.set("com.neox", user);
//        operations.set("com.neo.f", user,1, TimeUnit.SECONDS);
//        Thread.sleep(1000);
//        //redisTemplate.delete("com.neo.f");
//        boolean exists=redisTemplate.hasKey("com.neo.f");
//        if(exists){
//            System.out.println("exists is true");
//        }else{
//            System.out.println("exists is false");
//        }
        // Assert.assertEquals("aa", operations.get("com.neo.f").getUserName());
    }

    @RequestMapping("/")
    @ResponseBody
    String home() {


        String a;


//        Test test = new Test();
//        test.test();
//        String a;
//        System.out.println("hhhhh");
//        Test test = new Test();
//        test.test();


        for(int i = 0;i<100;i++){

            producer.send();
        }

        return "Hello World!";
    }

    @RequestMapping("/login")
    @ResponseBody
    String login(){
        template.opsForValue().set("hello", "99999");
        template.hasKey("hello");
        ValueOperations<String, String> valueOper = template.opsForValue();

        HashOperations<String,String,String> mapOper = template.opsForHash();
        mapOper.put("testMap","hello","1");
        mapOper.put("testMap","hello1","2");

        try {
            testObj();
        } catch (Exception e) {
            e.printStackTrace();
        }

//
//        List<Integer> list = new ArrayList<>();
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        redisTemplate.opsForHash().put("map1","key1",list);

        return "hello";
    }


    /**
     *  支付demo
     * @return
     */
    private void httpUrlConnection() {

    }

    public static void main(String[] args) {
        Map<Integer,Integer> map = new HashMap<>();
        List<Test> list = new ArrayList<>();
        list.add(new Test());
        list.add(new Test());
        list.add(new Test());
        list.add(new Test());
        list.add(new Test());

        map.put(1, 2);
        map.put(2, 2);
        map.put(3, 2);
        map.put(4, 2);
        map.put(5, 2);


    }


    public static class Test{
        int a = 1;
        String b = "";
    }
}
