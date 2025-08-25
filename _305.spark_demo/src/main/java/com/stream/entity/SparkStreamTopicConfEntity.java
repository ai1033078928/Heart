package com.stream.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SparkStreamTopicConfEntity implements Serializable {
    String topic;
    String tableName;
}
