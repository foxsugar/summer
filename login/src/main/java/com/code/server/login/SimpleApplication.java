package com.code.server.login;

import com.code.server.db.dao.IUserDao;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by win7 on 2017/3/10.
 */
@SpringBootApplication(scanBasePackages={"com.code.server.*"})
public class SimpleApplication implements ApplicationRunner {
    @Autowired
    private IUserDao userdao;



    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("-------------"+args);
    }
}
