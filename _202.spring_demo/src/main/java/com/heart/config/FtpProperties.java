package com.heart.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = FtpProperties.PREFIX, ignoreUnknownFields = false)
public class FtpProperties {
    public static final String PREFIX = "ftp";

    /**
     * Ip
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 登录账号
     */
    private String name;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 访问前缀
     */
    private String urlPrefix;
    /**
     * 是否被动模式
     */
    private boolean passiveMode = false;
    /**
     * 编码格式
     */
    private String encoding = "UTF-8";
    /**
     * 连接超时时间
     */
    private int connectTimeout = 30000;
    /**
     * 缓存
     */
    private int bufferSize = 8096;
}