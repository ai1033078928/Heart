package com.data.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = "job")
public class CustomProperties {

    @Value("${job.filePath}")
    private String filePath;

    @Value("${job.jobName}")
    private String jobName;

    @Value("${job.ftp.host}")
    private String ftpHost;

    @Value("${job.ftp.port}")
    private String ftpPort;

    @Value("${job.ftp.username}")
    private String ftpUsername;

    @Value("${job.ftp.passwd}")
    private String ftpPasswd;

    // p2.使用 @ConfigurationProperties 绑定到 Map：
    private Map<String, String> config = new HashMap<>();

    @PostConstruct
    public void init() {
        config = new HashMap<>();
        config.put("filePath", filePath);
        config.put("jobName", jobName);

        config.put("ftp.host", ftpHost);
        config.put("ftp.port", ftpPort);
        config.put("ftp.username", ftpUsername);
        config.put("ftp.passwd", ftpPasswd);
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(this.config);
        return properties;
    }
}
