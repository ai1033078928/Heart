package cn;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@Slf4j
@SpringBootApplication
//@ComponentScan(basePackages = "cn")
public class ApplicationJob implements CommandLineRunner {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ApplicationJob.class, args);
        log.info(StringUtils.join("程序【原神】", "启动！！！"));
        // 退出应用
        SpringApplication.exit(context, () -> 0);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Start Job...");

        log.info(environment.toString());
        Thread.sleep(10 * 1000);

        log.info("End Job...");
    }
}
