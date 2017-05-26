package com.code.server.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.HashOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class RedisApplication {

	@Resource(name="redisTemplate")
	private HashOperations<String,String, List<Integer>> mapOps;


	public void test(){
		List list = new ArrayList();
		list.add(1);
		list.add(2);
		list.add(3);
		mapOps.put("testMap2","hello",list);
	}

	public static void main(String[] args) {
		SpringApplication.run(RedisApplication.class, args);

		RedisApplication r = new RedisApplication();
		r.test();


	}
}
