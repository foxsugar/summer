package com.code.server.game.mahjong;

import com.code.server.game.mahjong.config.ServerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackages = {"com.code.server.*"})
@EnableConfigurationProperties({ServerConfig.class})
public class MahjongApplication {

	public static void main(String[] args) {
		SpringApplication.run(MahjongApplication.class, args);
	}
}
