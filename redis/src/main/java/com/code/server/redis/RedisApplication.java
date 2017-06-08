package com.code.server.redis;

import com.code.server.constant.game.UserBean;
import com.code.server.redis.service.RedisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication(scanBasePackages={"com.code.server.*"})
public class RedisApplication {



	@Autowired
	private RedisTemplate redisTemplate;

	public void test(){
		UserBean userBean = new UserBean();
		userBean.setGold(1);
		HashOperations<String,String,UserBean> user_money = redisTemplate.opsForHash();
		user_money.put("testString","1",userBean);
		Map<String,Object> params = new HashMap<>();

	}

	public static void main(String[] args) {
		SpringApplication.run(RedisApplication.class, args);

		System.out.println(RedisManager.getUserRedisService().getUserMoney(2L));


	}
}
