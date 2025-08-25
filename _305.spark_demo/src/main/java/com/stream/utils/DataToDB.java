package com.stream.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.Iterator;

@Log
public class DataToDB implements VoidFunction<Iterator<Tuple2<String, Iterable<JSONObject>>>> {

    JavaSparkContext javaSparkContext;

    public DataToDB(JavaSparkContext jc) {
        javaSparkContext = jc;
    }

    @Override
    public void call(Iterator<Tuple2<String, Iterable<JSONObject>>> tuple2Iterator) throws Exception {
        while (tuple2Iterator.hasNext()) {
            Tuple2<String, Iterable<JSONObject>> data = tuple2Iterator.next();
            processData(data);
        }
    }

    public void processData(Tuple2<String, Iterable<JSONObject>> data) {
        String tableName = data._1;
        data._2.forEach(row -> {
            JSONObject after = row.getJSONObject("after");
            JSONObject before = row.getJSONObject("before");
            StringBuilder insertSql = new StringBuilder();
            insertSql.append("insert into ").append(tableName);
            StringBuilder deleteSql;
            insertSql.append("delete from ").append(tableName);
            switch (row.getString("op")) {
                case "c":
                    // 插入数据
                    log.info(StringUtils.join("插入数据：", after));
                    break;
                case "d":
                    // 删除数据
                    log.info(StringUtils.join("删除数据：", before));
                    break;
                case "u":
                    // upsert 或 先删后插
                    log.info(StringUtils.join("先删数据：", before));
                    log.info(StringUtils.join("后插数据：", after));
                    break;
                default: break;
            }
        });
    }
 }
