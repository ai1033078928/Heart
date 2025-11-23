package com.heart.config.job;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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

    @Value("${job.timeInterval}")
    private String timeInterval;

    public Map<String, String> getMapProperties() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("filePath", this.filePath);
        hashMap.put("jobName", this.jobName);
        hashMap.put("timeInterval", this.timeInterval);
        return hashMap;
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(getMapProperties());
        return properties;
    }
}
