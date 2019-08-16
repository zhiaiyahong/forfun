package fun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySources;

/**
 * @author pengweichao
 * @date 2019/8/8
 */
@SpringBootApplication
@ComponentScan(basePackages = "fun")
@ImportResource("classpath*:/applicationContext.xml")
//@PropertySources("加载 *.properties 配置文件")
public class Bootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
}
