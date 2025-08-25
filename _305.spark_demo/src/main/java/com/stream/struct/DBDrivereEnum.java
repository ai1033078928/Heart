package com.stream.struct;

public enum DBDrivereEnum {
    MYSQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://"),
    SQLITE("org.sqlite.JDBC", "jdbc:sqlite:"),
    ORACLE("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@"),
    DM("dm.jdbc.driver.Driver", "jdbc:dm:"),
    H2("org.h2.Driver", "jdbc:h2:");

    private final String driverClass;
    private final String urlPrefix;

    DBDrivereEnum(String driverClass, String urlPrefix) {
        this.driverClass = driverClass;
        this.urlPrefix = urlPrefix;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }
}