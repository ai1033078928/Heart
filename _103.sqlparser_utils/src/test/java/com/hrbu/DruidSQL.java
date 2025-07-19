package com.hrbu;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.junit.Test;

public class DruidSQL {
    public static void main(String[] args) {
        String sql = "select id,name from user";

        // 新建 MySQL Parser
        SQLStatementParser parser = new OracleStatementParser(sql); // MySqlStatementParser(sql);


    }


    @Test
    public void printInfo() {
        String sql = "select t1.id, t1.name, max(t2.info) as info " +
                "from user t1 " +
                "left join user_info t2 " +
                "on t1.id = t2.user_id " +
                "and t1.x1 = t2.x1 " +
                "where t1.age < 100 " +
                "group by t1.id, t1.name;";

        // 新建 MySQL Parser
        SQLStatementParser parser = new OracleStatementParser(sql); // MySqlStatementParser(sql);
        // System.out.println(parser.parseSelect());

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
