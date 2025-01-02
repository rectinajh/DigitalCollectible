package com;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.alicp.jetcache.anno.config.EnableMethodCache;

@SpringBootApplication
@ComponentScan("com.nft")
@EnableMethodCache(basePackages = "com.nft")
public class MemberApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(MemberApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
	}

}
