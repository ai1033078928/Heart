package com.hrbu;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://www.cnblogs.com/zhihuifan10/articles/11124953.html
public class SqlParser {
    public static void main(String[] args) throws JSQLParserException {
        String sql = "select * from dual;";

        // 表名
        for (String s : test_select_table(sql)) {
            System.out.println(s);
        }

        // 字段名
        for (String test_select_item : test_select_items(sql)) {
            System.out.println(test_select_item);
        }

        for (String s : test_select_join(sql)) {
            System.out.println(s);
        }
    }

    /**
     * 查询表名 table
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    private static List<String> test_select_table(String sql) throws JSQLParserException {

        Statement parse = CCJSqlParserUtil.parse(sql);

        Select select = (Select) parse;

        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        return tablesNamesFinder
                .getTableList(select);
    }

    /**
     * 查询字段
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    private static List<String> test_select_items(String sql)
            throws JSQLParserException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(sql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        List<SelectItem> selectitems = plain.getSelectItems();
        List<String> str_items = new ArrayList<String>();
        if (selectitems != null) {
            for (SelectItem selectitem : selectitems) {
                str_items.add(selectitem.toString());
            }
        }
        return str_items;
    }

    /**
     * 查询表名 table
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    /*private static List<String> test_select_table(String sql)
            throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        //  System.out.println(statement.toString());
        Select selectStatement = (Select) statement;
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        return tablesNamesFinder
                .getTableList(selectStatement);
    }*/

    /**
     * 查询 join
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    private static List<String> test_select_join(String sql)
            throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Select selectStatement = (Select) statement;

        PlainSelect plain = (PlainSelect) selectStatement.getSelectBody();
        //System.out.println(plain.toString());
        List<Join> joinList = plain.getJoins();
        List<String> tablewithjoin = new ArrayList<String>();
        if (joinList != null) {
            for (Join join : joinList) {
                join.setLeft(false);
                tablewithjoin.add(join.toString());
                //注意 ， leftjoin rightjoin 等等的to string()区别
            }
        }
        return tablewithjoin;
    }
    /**
     * 查询where
     */
    private static String test_select_where(String sql)
            throws JSQLParserException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(sql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        Expression where_expression = plain.getWhere();
        return where_expression.toString();
    }
    /**
     * 对where的结果进行解析
     */
    private static void testParseWhere1(String sql){
        try {
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            SelectBody selectBody = select.getSelectBody();
            PlainSelect plainSelect = (PlainSelect)selectBody;

            Expression where = plainSelect.getWhere();
            ExpressionDeParser expressionDeParser = new ExpressionDeParser();
            plainSelect.getWhere().accept(expressionDeParser);

            // 此处根据where实际情况强转 最外层
            EqualsTo equalsTo = (EqualsTo)where;
            System.out.println("Table:"+((Column)equalsTo.getLeftExpression()).getTable());
            System.out.println("Field:"+((Column)equalsTo.getLeftExpression()).getColumnName());
            System.out.println("equal:"+equalsTo.getRightExpression());

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }

    /**
     * where两个条件or连接
     * 代码中有两个条件or连接，可回忆转成OrExpression,里面还是两个EqualsTo。
     */
    private static void testParseWhere2(String sql){
        try {
            Select select = (Select)CCJSqlParserUtil.parse(sql);
            SelectBody selectBody = select.getSelectBody();
            PlainSelect plainSelect = (PlainSelect)selectBody;

            Expression where = plainSelect.getWhere();
            ExpressionDeParser expressionDeParser = new ExpressionDeParser();
            plainSelect.getWhere().accept(expressionDeParser);

            // 此处根据where实际情况强转 最外层
            OrExpression orExpression = (OrExpression)where;
            EqualsTo equalsTo = (EqualsTo)orExpression.getLeftExpression();

            System.out.println("Table:"+((Column)equalsTo.getLeftExpression()).getTable());
            System.out.println("Field:"+((Column)equalsTo.getLeftExpression()).getColumnName());
            System.out.println("equal:"+equalsTo.getRightExpression());
            System.out.println("--------");
            equalsTo = (EqualsTo)orExpression.getRightExpression();

            System.out.println("Table:"+((Column)equalsTo.getLeftExpression()).getTable());
            System.out.println("Field:"+((Column)equalsTo.getLeftExpression()).getColumnName());
            System.out.println("equal:"+equalsTo.getRightExpression());

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }
    /**
     * where三个条件or连接
     *得到的第一层的leftExpression还是一个orExpression,rightExpression是一个EqualsTo
     */
    private static void testParseWhere3(String sql){
        try {
            Select select = (Select)CCJSqlParserUtil.parse(sql);
            SelectBody selectBody = select.getSelectBody();
            PlainSelect plainSelect = (PlainSelect)selectBody;

            Expression where = plainSelect.getWhere();
            ExpressionDeParser expressionDeParser = new ExpressionDeParser();
            plainSelect.getWhere().accept(expressionDeParser);

            // 此处根据where实际情况强转 最外层
            OrExpression orExpression = (OrExpression)where;

            OrExpression leftOrExpression = (OrExpression)orExpression.getLeftExpression();

            EqualsTo equalsTo = (EqualsTo)leftOrExpression.getLeftExpression();

            System.out.println("Table:"+((Column)equalsTo.getLeftExpression()).getTable());
            System.out.println("Field:"+((Column)equalsTo.getLeftExpression()).getColumnName());
            System.out.println("equal:"+equalsTo.getRightExpression());
            System.out.println("--------");
            equalsTo = (EqualsTo)leftOrExpression.getRightExpression();

            System.out.println("Table:"+((Column)equalsTo.getLeftExpression()).getTable());
            System.out.println("Field:"+((Column)equalsTo.getLeftExpression()).getColumnName());
            System.out.println("equal:"+equalsTo.getRightExpression());
            System.out.println("--------");
            equalsTo = (EqualsTo)orExpression.getRightExpression();

            System.out.println("Table:"+((Column)equalsTo.getLeftExpression()).getTable());
            System.out.println("Field:"+((Column)equalsTo.getLeftExpression()).getColumnName());
            System.out.println("equal:"+equalsTo.getRightExpression());

        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询 group by
     * @param sql
     * @return
     * @throws JSQLParserException
     */
    /*private static List<String> test_select_groupby(String sql)
            throws JSQLParserException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(sql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        List<Expression> GroupByColumnReferences = plain
                .getGroupByColumnReferences();
        List<String> str_groupby = new ArrayList<String>();
        if (GroupByColumnReferences != null) {
            for (Expression groupByColumnReference : GroupByColumnReferences) {
                str_groupby.add(groupByColumnReference.toString());
            }
        }
        return str_groupby;
    }*/
    /**
     * 查询order by
     */
    private static List<String> test_select_orderby(String sql)
            throws JSQLParserException {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(sql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        List<OrderByElement> OrderByElements = plain.getOrderByElements();
        List<String> str_orderby = new ArrayList<String>();
        if (OrderByElements != null) {
            for (OrderByElement orderByElement : OrderByElements) {
                str_orderby.add(orderByElement.toString());
            }
        }
        return str_orderby;
    }
    /**
     * 子查询
     */
    /*private static Map test_select_subselect(SelectBody selectBody) throws JSQLParserException {
        Map<String, String> map = new HashMap<String, String>();

        if (selectBody instanceof PlainSelect) {
            List<SelectItem> selectItems = ((PlainSelect) selectBody).getSelectItems();
            for (SelectItem selectItem : selectItems) {
                if (selectItem.toString().contains("(") && selectItem.toString().contains(")")) {
                    map.put("selectItemsSubselect", selectItem.toString());
                }
            }
            Expression where = ((PlainSelect) selectBody).getWhere();
            String whereStr = where.toString();
            if (whereStr.contains("(") && whereStr.contains(")")) {
                int firstIndex = whereStr.indexOf("(");
                int lastIndex = whereStr.lastIndexOf(")");
                CharSequence charSequence = whereStr.subSequence(firstIndex, lastIndex + 1);
                map.put("whereSubselect", charSequence.toString());
            }

            FromItem fromItem = ((PlainSelect) selectBody).getFromItem();
//            System.out.println("111----"+((PlainSelect) selectBody).getFromItem());
//            System.out.println(fromItem);
            if (fromItem instanceof SubSelect) {
                map.put("fromItemSubselect", fromItem.toString());
            }

        } else if (selectBody instanceof WithItem) {
            SqlParser.test_select_subselect(((WithItem) selectBody).getSelectBody());
        }
        return map;
    }*/
}
