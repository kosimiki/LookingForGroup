package hu.blog.megosztanam.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by kosimiki on 2016. 11. 26..
 */
@EnableTransactionManagement
@Configuration
@EnableCaching
@EnableFeignClients(basePackages = "hu.blog.megosztanam")
@SpringBootApplication(scanBasePackages = "hu.blog.megosztanam")
public class LFGApplication {
    public static void main(String[] args) {
        SpringApplication.run(LFGApplication.class, args);
    }

}



