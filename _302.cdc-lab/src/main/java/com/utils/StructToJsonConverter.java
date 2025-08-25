package com.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StructToJsonConverter {
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 将Struct对象转换为JSON字符串
     * @param struct Kafka Connect Struct对象
     * @return JSON字符串
     */
    public static String toJson(Struct struct) throws Exception {
        Map<String, Object> resultMap = convertStructToJsonMap(struct);
        return mapper.writeValueAsString(resultMap);
    }

    /**
     * 递归转换Struct到Map（支持嵌套结构）
     */
    private static Map<String, Object> convertStructToJsonMap(Struct struct) {
        Map<String, Object> result = new HashMap<>();
        for (Field field : struct.schema().fields()) {
            String fieldName = field.name();
            Object fieldValue = struct.get(field);
            result.put(fieldName, convertFieldToJsonValue(fieldValue));
        }
        return result;
    }

    /**
     * 递归处理字段值（支持嵌套Struct和数组）
     */
    private static Object convertFieldToJsonValue(Object value) {
        if (value instanceof Struct) {
            // 递归处理嵌套Struct
            return convertStructToJsonMap((Struct) value);
        } else if (value instanceof Iterable) {
            // 处理数组/集合类型
            return convertIterableToJsonArray((Iterable<?>) value);
        }
        // 基本类型直接返回
        return value;
    }

    /**
     * 处理数组/集合类型
     */
    private static Object convertIterableToJsonArray(Iterable<?> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(item -> {
                    if (item instanceof Struct) {
                        return convertStructToJsonMap((Struct) item);
                    }
                    return item;
                })
                .collect(Collectors.toList());
    }
}
