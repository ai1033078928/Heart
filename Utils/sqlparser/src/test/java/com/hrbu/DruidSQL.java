package com.hrbu;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class DruidSQL {
    public static void main(String[] args) {
        String sql = "select id,name from user";

        // 新建 MySQL Parser
        SQLStatementParser parser = new OracleStatementParser(sql); // MySqlStatementParser(sql);


    }

    public void printInfo() {
        String sql = "select id,name from user";

        // 新建 MySQL Parser
        SQLStatementParser parser = new OracleStatementParser(sql); // MySqlStatementParser(sql);

        // 使用Parser解析生成AST，这里SQLStatement就是AST
        SQLStatement statement = parser.parseStatement();

        // 使用visitor来访问AST
        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        // MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        statement.accept(visitor);

        // 从visitor中拿出你所关注的信息
        System.out.println(visitor.getColumns());
        System.out.println(visitor.getTables());
        System.out.println(visitor.getDbType());
        System.out.println(visitor.getConditions());
    }
}
