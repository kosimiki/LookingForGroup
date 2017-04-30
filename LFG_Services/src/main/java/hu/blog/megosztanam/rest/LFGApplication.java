package hu.blog.megosztanam.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

/**
 * Created by kosimiki on 2016. 11. 26..
 */
@Configuration
@SpringBootApplication(scanBasePackages = "hu.blog.megosztanam")
public class LFGApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(LFGApplication.class, args);

        System.out.println("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }
}



