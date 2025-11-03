package com.heart.config.swagger;

// import com.kfang.web.price.manager.interceptor.AuthorizationInterceptor;
// import com.kfang.web.price.manager.interceptor.ContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Resource
    private Environment env;

    // @Resource
    // private AuthorizationInterceptor authorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {


        String[] swaggerExcludePathPatterns = {"/doc.html","/swagger**/**","/swagger-resources/**","/webjars/**","/v3/**"};
        // registry.addInterceptor(new ContextInterceptor()).addPathPatterns("/**");
        if(!"dev".equals(env.getProperty("spring.profiles.active"))){
            swaggerExcludePathPatterns = new String[0];
        }
        registry
                // .addInterceptor(authorizationInterceptor)
                .addInterceptor(new HandlerInterceptor() {})
                .excludePathPatterns("/user/logout")
                .excludePathPatterns("/user/login")
                .excludePathPatterns(swaggerExcludePathPatterns)
                .addPathPatterns("/**")
                .order(-1);
    }
}