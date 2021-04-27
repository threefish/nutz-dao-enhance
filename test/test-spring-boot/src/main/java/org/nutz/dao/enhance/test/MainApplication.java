package org.nutz.dao.enhance.test;

import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.spring.boot.annotation.DaoScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
/**
 * @author 黄川 huchuc@vip.qq.com
 */
@EnableConfigurationProperties
@SpringBootApplication
@Slf4j
@DaoScan("org.nutz.dao.enhance.test")
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
