package com.hrbu.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import org.junit.Test;

public class 唯一ID工具 {
    /**
     * UUID
     */
    @Test
    public void Test() {
        System.out.println(IdUtil.fastUUID());          // 带-的UUID
        System.out.println(IdUtil.fastSimpleUUID());    // 不带-的UUID
    }

    /**
     * ObjectId
     * ObjectId是MongoDB数据库的一种唯一ID生成策略
     */
    @Test
    public void Test2() {
        System.out.println(IdUtil.objectId());
    }

    /**
     * 雪花算法
     * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。Twitter的Snowflake 算法就是这种生成器。
     * 注意: IdUtil.createSnowflake每次调用会创建一个新的Snowflake对象，不同的Snowflake对象创建的ID可能会有重复，因此请自行维护此对象为单例，或者使用IdUtil.getSnowflake使用全局单例对象。
     */
    @Test
    public void Test3() {
        // 参数1为终端ID
        // 参数2为数据中心ID
        Snowflake snowflake = IdUtil.getSnowflake(1, 2);
        for (int i = 0; i < 200; i++) {
            System.out.println(snowflake.nextId());
        }
    }

}
