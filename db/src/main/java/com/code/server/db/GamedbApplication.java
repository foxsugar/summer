package com.code.server.db;

import com.code.server.db.Service.ServerService;
import com.code.server.db.model.Model;
import com.code.server.db.model.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.code.server.*"})
public class GamedbApplication {



	public static void main(String[] args) {
//		SpringApplication.run(GamedbApplication.class, args);
//
//
//		Test test = new Test();
//		Model m = new Model();
//		test.setLocation(m);
//		ServerService serverService = SpringUtil.getBean(ServerService.class);
//		serverService.testDao.save(test);
//
//
//		for(Test tt : serverService.testDao.findAll()){
//			System.out.println(tt.getLocation().getId());
//		}
	}
}
