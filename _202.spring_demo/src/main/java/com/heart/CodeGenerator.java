package com.heart;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CodeGenerator {

    public static void main(String[] args) {
        getQuickBuilder();
        // getInteractionBuilder();
    }

    private static void getQuickBuilder() {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/swap?e&characterEncoding=utf8&useSSL=false",
                        "root",
                        "root")
                .globalConfig(builder -> {
                    builder.author("ahb") // 设置作者
                            // .enableSwagger() // 开启 swagger 模式
                            .outputDir("D:\\Program Files\\JetBrains\\project\\heart\\_202.spring_demo\\src\\main\\java") // 指定输出目录
                            ;
                })
                .dataSourceConfig(builder ->
                    builder.schema("swap")
                            .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                        int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                        if (typeCode == Types.SMALLINT) {
                            // 自定义类型转换
                            return DbColumnType.INTEGER;
                        }
                        return typeRegistry.getColumnType(metaInfo);
                    })
                )
                .packageConfig(builder ->
                        builder.parent("com.heart") // 设置父包名
                                //.moduleName("spring_demo") // 设置父包模块名
                                .entity("entity") // 设置实体类包名
                                .mapper("mapper") // 设置 Mapper 接口包名
                                .service("service") // 设置 Service 接口包名
                                .serviceImpl("service.impl") // 设置 Service 实现类包名
                                .pathInfo(Collections.singletonMap(OutputFile.xml, "D:\\Program Files\\JetBrains\\project\\heart\\_202.spring_demo\\src\\main\\resources\\mappers")) // 设置mapperXml生成路径
                                .xml("mappers") // 设置 Mapper XML 文件包名
                )
                .strategyConfig(builder ->
                        builder.addInclude("ods_loading_job_test", "ods_send_info_test") // 设置需要生成的表名
                                // .addTablePrefix("t_", "c_") // 设置过滤表前缀
                                .entityBuilder()
                                .enableLombok() // 启用 Lombok
                                .enableTableFieldAnnotation() // 启用字段注解
                                .controllerBuilder()
                                .enableRestStyle() // 启用 REST 风格
                                .mapperBuilder()
                                .serviceBuilder()
                )
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .injectionConfig(builder ->
                        builder.beforeOutputFile(new BiConsumer<TableInfo, Map<String, Object>>() {
                            @Override
                            public void accept(TableInfo tableInfo, Map<String, Object> stringObjectMap) {
                                System.out.println("开始执行代码生成");
                            }
                        })
                )
                .execute();
    }


    private static void getInteractionBuilder() {
        FastAutoGenerator.create("url", "username", "password")
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(scanner.apply("请输入作者名称？")))
                // 包配置
                .packageConfig((scanner, builder) -> builder.parent(scanner.apply("请输入包名？")))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .entityBuilder()
                        .enableLombok()
                        .addTableFills(
                                new Column("create_time", FieldFill.INSERT)
                        )
                        .build())
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

}
