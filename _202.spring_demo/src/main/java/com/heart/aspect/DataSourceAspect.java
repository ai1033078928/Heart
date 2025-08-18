package com.heart.aspect;

import com.heart.annotation.DataSourceSwitcher;
import com.heart.datasource.DataSourceContextHolder;
import com.heart.datasource.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Slf4j
@Aspect
@Component
@Order(1)  // 设置AOP执行顺序(需要在事务之前，否则事务只发生在默认库中)
public class DataSourceAspect {

    @PersistenceContext
    private EntityManager entityManager;

    /*
     * @Before("@annotation(ds)")
     * 的意思是：
     * @Before：在方法执行之前进行执行：@annotation(targetDataSource)：
     * 会拦截注解targetDataSource的方法，否则不拦截;
     */
    @Before("@annotation(ds)")
    public void changeDataSource(JoinPoint point, DataSourceSwitcher ds) throws Throwable {
        //获取当前的指定的数据源;
        DataSourceType dataSourceType = ds.value();
        /**
         * 通过aop拦截，获取注解上面的value的值key，然后取判断我们注册的keys集合中是否有这个key,如果没有，则使用默认数据源，如果有，则设置上下文中当前数据源的key为注解的value。
         */
        if (DataSourceContextHolder.containsDataSource(dataSourceType)) {
            log.debug("切入:{} >", dataSourceType, point.getSignature());
            DataSourceContextHolder.setDataSourceType(dataSourceType);
        } else {
            log.info("数据源[{}]不存在，使用默认数据源 >{}", dataSourceType, point.getSignature());
            DataSourceContextHolder.setDataSourceType(DataSourceType.PRIMARY);
        }
    }

    @After("@annotation(ds)")
    public void restoreDataSource(JoinPoint point, DataSourceSwitcher ds) {
        log.debug("Revert DataSource : {} > {}", ds.value(), point.getSignature());
        //logger.info("切完");
        //方法执行完毕之后，销毁当前数据源信息，进行垃圾回收。
        DataSourceContextHolder.clearDataSourceType();
        /*SessionImplementor session = entityManager.unwrap(SessionImplementor.class);
        //最关键的一句代码， 手动断开连接，不用重新设置 ，会自动重新设置连接。
        session.disconnect();*/

        // 检查 entityManager 是否可用且处于事务中
        try {
            if (entityManager != null) {
                // 检查 EntityManager 是否处于事务中
                if (entityManager.isOpen() && entityManager.getTransaction().isActive()) {
                    SessionImplementor session = entityManager.unwrap(SessionImplementor.class);
                    if (session != null && session.isConnected()) {
                        // 手动断开连接，会自动重新设置连接
                        session.disconnect();
                    }
                }
            }
        } catch (Exception e) {
            // 如果无法获取 session 或出现异常，仅记录日志，不中断流程
            log.debug("Unable to disconnect session in restoreDataSource: {}", e.getMessage());
        }
    }
}
