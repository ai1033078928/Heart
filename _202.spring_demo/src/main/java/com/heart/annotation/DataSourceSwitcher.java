package com.heart.annotation;

import com.heart.datasource.DataSourceType;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DataSourceSwitcher {
    DataSourceType value() default DataSourceType.PRIMARY;
}
