package com.yhwch.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author pengweichao
 * @date 2019/8/8
 */
@SpringBootApplication
@ImportResource("classpath*:/applicationContext.xml")
//@PropertySources("加载 *.properties 配置文件")
public class Bootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
}
