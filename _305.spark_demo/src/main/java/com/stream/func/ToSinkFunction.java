package com.stream.func;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ToSinkFunction implements VoidFunction<Iterator<ConsumerRecord<String, String>>> {

    JavaSparkContext jsc;

    public ToSinkFunction(JavaSparkContext javaSparkContext) {
        jsc = javaSparkContext;  // spark 上下文对象
    }

    @Override
    public void call(Iterator<ConsumerRecord<String, String>> records) throws Exception {
        // 处理每条记录
        while (records.hasNext()) {
            ConsumerRecord<String, String> record = records.next();
            List<JSONObject> list;
            // 处理业务逻辑
            JSONObject row = JSONObject.parseObject(record.value());
            if (null != row.getString("op")) {
                System.out.printf("数据 %s：%s%n", record.offset(), row);
                list = reFormatData(row);
            } else {
                // TODO ddl消息，处理ddl信息
                System.out.printf("ddl %s：%s%n", record.offset(), row);
                list = reFormatDDL(row);
            }
            // 发送消息
            for (JSONObject jsonObject : list) {
                sendData(jsonObject);
            }
        }
    }


    public void sendData(JSONObject data) {
        // TODO 发送消息到kafka

    }

    public List<JSONObject> reFormatDDL(JSONObject ddl) {
        ArrayList<JSONObject> resList = new ArrayList<>();

        JSONObject row = new JSONObject();

        List<String> headCols = Arrays.asList("pos", "op_ts", "db", "table");
        JSONObject source = ddl.getJSONObject("source");
        for (String headCol : headCols) {
            Object value = source.get(headCol);
            if (!headCol.equals("op_ts")) {
                value = formatTimestamp(value);
            }
            row.put(headCol, value);
        }


        String historyRecordStr = ddl.getJSONObject("historyRecord").toString();
        JSONObject historyRecord = JSONObject.parseObject(historyRecordStr);
        String ddlSql = historyRecord.getString("ddl");
        List<String> pkList = historyRecord.getJSONArray("tableChanges").getJSONObject(0).getJSONObject("table").getObject("primaryKeyColumnNames", List.class);
        List<Map> colsList = historyRecord.getJSONArray("tableChanges").getJSONObject(0).getJSONObject("table").getObject("columns", List.class);

        // System.out.println(ddlSql);
        // System.out.println(pkList);
        // System.out.println(colsList);

        for (int i = 0; i < colsList.size(); i++) {
            System.out.printf("字段 %s %s%n", i+1, colsList.get(i));
        }
        return resList;
    }


    public List<JSONObject> reFormatData(JSONObject data) {
        ArrayList<JSONObject> resList = new ArrayList<>();

        JSONObject before = (null == data.getJSONObject("before") ? new JSONObject() : data.getJSONObject("before"));
        JSONObject after = (null == data.getJSONObject("after") ? new JSONObject() : data.getJSONObject("after"));

        List<String> headCols = Arrays.asList("pos", "op_ts", "db", "table");
        JSONObject source = data.getJSONObject("source");
        for (String headCol : headCols) {
            Object value = source.get(headCol);
            if (!headCol.equals("op_ts")) {
                value = formatTimestamp(value);
            }
            before.put(headCol, value);
            after.put(headCol, value);
        }

        switch (data.getString("op")) {
            case "c":
                after.put("op_type", "I");
                resList.add(after);
                break;
            case "d":
                before.put("op_type", "D");
                resList.add(before);
                break;
            case "u":
                before.put("op_type", "D");
                resList.add(before);
                after.put("op_type", "I");
                resList.add(after);
                break;
        }
        return resList;
    }

    private String formatTimestamp(Object timestampObj) {
        if (timestampObj == null) {
            return "";
        }

        try {
            long timestamp;
            if (timestampObj instanceof Number) {
                timestamp = ((Number) timestampObj).longValue();
            } else if (timestampObj instanceof String) {
                timestamp = Long.parseLong((String) timestampObj);
            } else {
                return timestampObj.toString();
            }

            // 使用LocalDateTime处理（Java 8+）
            LocalDateTime dateTime = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(timestamp),
                    ZoneId.systemDefault()
            );

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return dateTime.format(formatter);

        } catch (Exception e) {
            // 如果转换失败，返回原始值
            return timestampObj.toString();
        }
    }
}
