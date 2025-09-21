package com.heart.config.job;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Data
@Component
public class CustomProperties {
    // private Environment environment;

    @Value("${job.filePath}")
    private String filePath;

    @Value("${job.jobName}")
    private String jobName;

    public Map<String, String> getMapProperties() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("filePath", this.filePath);
        hashMap.put("jobName", this.jobName);
        return hashMap;
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(getMapProperties());
        return properties;
    }
}
