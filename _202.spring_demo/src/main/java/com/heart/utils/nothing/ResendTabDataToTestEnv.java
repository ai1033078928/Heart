package com.heart.utils.nothing;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import com.jcraft.jsch.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.util.Watchdog;


public class ResendTabDataToTestEnv {

    String url;
    String user;
    String password;

    String impalaUrl;

    String linuxHost;
    String linuxUserName;
    String linuxPassWD;

    Connection mysql_conn;
    Connection implala_conn;
    Session session;

    // 清理 ANSI 转义序列（包括颜色、光标移动、OSC 等）
    private static final Pattern ANSI_PATTERN = Pattern.compile(
            "\u001b\\[[;\\d]*m|" +           // 颜色代码: \033[32m
                    "\u001b\\[?[;\\d]*[ABCDEFGHJKST]" + // 光标控制
                    "|\\e\\]1337;.*?(\u0007|\\x1b\\\\)"   // iTerm2 OSC 序列
            //"|[\b\r\n\t]"                     // 可选：多余的控制字符
    );

    public ResendTabDataToTestEnv() {
        Properties config = getConfigFromFile("init.properties");
        initVar(config);
    }

    private Properties getConfigFromFile(String file) {
        Properties prop = new Properties();
        try {
            prop.load(ResendTabDataToTestEnv.class.getClassLoader().getResourceAsStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
        return prop;
    }

    private void initVar(Properties conf) {
        // TODO: chk column
        this.url = conf.getProperty("url");
        this.user = conf.getProperty("user");
        this.password = conf.getProperty("password");
        this.impalaUrl = conf.getProperty("impalaUrl");
        this.linuxHost = conf.getProperty("linuxHost");
        this.linuxUserName = conf.getProperty("linuxUserName");
        this.linuxPassWD = conf.getProperty("linuxPassWD");
    }

    @Override
    public String toString() {
        return "ResendTabDataToTestEnv{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + StringUtils.isEmpty(password) + '\'' +
                ", impalaUrl='" + StringUtils.isEmpty(impalaUrl) + '\'' +
                ", linuxHost='" + linuxHost + '\'' +
                ", linuxUserName='" + linuxUserName + '\'' +
                ", linuxPassWD='" + StringUtils.isEmpty(linuxPassWD) + '\'' +
                '}';
    }

    public static void main(String[] args) {
        ResendTabDataToTestEnv test = new ResendTabDataToTestEnv();
        // atest2.insert_test();
        test.resendDataByJob("xxx.xxxx",
                "xx",
                "/xx/xx",
                "/xx/xx/xx/xx",
                null  // 删除对应时间的文件，为空时取当前时间
        );
    }

    /**
     * 删除上一次生成文件并重新生成
     * @param odsTable 推送表名
     * @param send_system 推送系统名
     * @param filePath   推送目录（基础目录）
     * @param filePathTemp 推送临时目录（基础目录）
     * @param datetime yyyyMMddHHmmss
     */
    public void resendDataByJob(String odsTable, String send_system, String filePath, String filePathTemp, String datetime) {
        try {
            // Mysql操作
            initMysqlConn();
            String queryJobInfo = String.format("select *\n" +
                    "  from swap.ods_loading_job_test\n" +
                    " where ods_table = '%s'\n" +
                    "   and instr(send_systems, '%s') > 0", odsTable, send_system);
            List<Map<String, Object>> data = queryInfo(mysql_conn, queryJobInfo);

            if (data.size() != 1) return;  // 不唯一，直接退出

            Map<String, Object> line = data.get(0);
            if (StringUtils.isEmpty(datetime)) {
                // 时间基于当前时间计算（假设当前时间有推送），可能与实际不符
                String datetimeAndSeparator = getPointTime((Integer) line.get("time_interval"), (String) line.get("delay_time"), (String) line.get("time_unit"), 0);
                datetime = datetimeAndSeparator.replace("-", "")
                        .replace(":", "")
                        .replace(" ", "")
                        .substring(0, 14);
            }

            // 1. 删除上次推送的文件
            if (!StringUtils.isEmpty(filePath) && !StringUtils.isEmpty(filePathTemp)) {
                // Linux 操作：基础路径不为空，删除文件（或删除当天）
                initLinuxSession();
                StringBuilder path1 = new StringBuilder();
                path1.append(filePath).append("/").append(line.get("send_systems")).append("/").append(line.get("from_system")).append("/")
                        .append(datetime, 0, 8).append("/")
                        .append("*").append(line.get("ods_table")).append(".").append(datetime).append("*");
                StringBuilder path2 = new StringBuilder();
                path2.append(filePathTemp).append("/").append(datetime, 0, 8).append("/")
                        .append(Integer.parseInt(line.get("load_type").toString()) == 0 ? "increase" : "full").append("/")
                        .append(line.get("time_unit")).append("/")
                        .append(line.get("time_interval")).append("/")
                        .append("*").append(line.get("ods_table")).append(".").append(datetime).append("*")
                ;
                List<String> resStrs = linuxExecCommands(this.session, Arrays.asList("ls -l " + path1, "ls -l " + path2));
                // List<String> resStrs = linuxExecCommands(this.session, Arrays.asList("rm -f " + path1, "rm -f " + path2));
                resStrs.forEach(System.out::println);

                // linuxExecCommand(this.session, "ls " + path1);
                // return;
            }

            // 2. impala操作，刷kuduloadtime
            String isViewSql = String.format("select db.NAME as db_name, tbl.TBL_NAME, tbl.TBL_TYPE" +
                            "           , tbl.VIEW_EXPANDED_TEXT /*解析、语义分析并重写后的视图定义*/" +
                            "           , tbl.VIEW_ORIGINAL_TEXT /*原始语句*/\n" +
                            "      from hive.dbs db\n" +
                            " left join hive.tbls tbl\n" +
                            "        on tbl.DB_ID = db.DB_ID\n" +
                            "     where lower(db.NAME) = lower('%s')\n" +
                            "       and lower(tbl.TBL_NAME) = lower('%s')"
                    , line.get("ods_table").toString().split("\\.")[0]
                    , line.get("ods_table").toString().split("\\.")[1]
            );
            List<Map<String, Object>> list = queryInfo(this.mysql_conn, isViewSql);
            String updateLoadTime;
            if (list.size() == 1 && list.get(0).get("TAL_TYPE").toString().contains("VIEW")) {
                String viewSql = list.get(0).get("VIEW_EXPANDED_TEXT").toString();
                String firstTabFromSql = getFirstTabFromSql(viewSql);  // 解析sql获取第一个来源表名
                if (StringUtils.isEmpty(firstTabFromSql)) throw new JSQLParserException("未从sql中解析到第一个表名");
                updateLoadTime = String.format("update %s\n" +
                                "   set kuduloadtime = from_timestamp(date_sub(now()/*kuduloadtime*/, INTERVAL 1 DAY), 'yyyy-MM-dd HH:mm:ss.SSSSSS')"
                        , firstTabFromSql
                );
            } else {
                updateLoadTime = String.format("update %s\n" +
                                "   set kuduloadtime = from_timestamp(date_sub(now()/*kuduloadtime*/, INTERVAL 1 DAY), 'yyyy-MM-dd HH:mm:ss.SSSSSS')"
                        , line.get("ods_table")
                );
            }
            initImpalaConn();
            // 执行更新
            execUpdate(this.implala_conn, Collections.singletonList(updateLoadTime));

            // 3. 刷任务配置
            String sql;
            if (line.get("send_systems").toString().contains(",")) {
                // 多个下游
                sql = String.format("insert into swap.ods_resend_job (\n" +
                                "     ods_table,             /*推送表名*/\n" +
                                "     time_interval,         /*推送频率*/\n" +
                                "     time_unit,             /*推送频率*/\n" +
                                "     delay_time,            /*推送延迟时间*/\n" +
                                "     send_system,           /*推送系统*/\n" +
                                "     reload_type ,          /*推送类型*/\n" +
                                "     start_load_time,       /*推送时间暂时无用*/\n" +
                                "     last_load_time,        /*推送数据开始时间*/\n" +
                                "     retransmission_flag,   /*推送文件名类型*/\n" +
                                "     reload_status,         /*推送状态*/\n" +
                                "     reload_Table)          /*推送数据来源表名*/\n" +
                                "select ods_table\n" +
                                "     , time_interval\n" +
                                "     , time_unit\n" +
                                "     , delay_time\n" +
                                "     , '%s' as send_systems\n" +
                                "     , '%s' as reload_type\n" +
                                "     , '1970-12-22 01:00:00.000000' as start_load_time\n" +
                                "     , '1970-12-22 01:00:00.000000' as last_load_time\n" +
                                "     , '1' as retransmission_flag\n" +
                                "     , 'Active' as reload_status\n" +
                                "     , ods_table\n" +
                                "from swap.ods_loading_job_test\n" +
                                "where ods_table = '%s'\n" +
                                "  and send_systems = '%s'"
                        , send_system
                        , Integer.parseInt(line.get("load_type").toString()) == 1 ? "I" : "F"
                        , line.get("ods_table").toString()
                        , line.get("send_systems").toString()
                );
            } else {
                sql = String.format("update swap.ods_loading_job_test\n" +
                                "set last_load_time = '%s' /*date_sub(last_load_time, interval 1 day)*/,\n" +
                                "    start_load_time = '1970-12-22 01:00:00.000000',\n" +
                                "    load_enable = '1'\n" +
                                "where ods_table = '%s'\n" +
                                "  and send_systems = '%s'"
                        // 基于当前时间，刷新时间到两个间隔前，保证能执行
                        , getPointTime(Integer.parseInt(line.get("ods_table").toString()), line.get("delay_time").toString(), line.get("time_unit").toString(), -2)
                        , line.get("ods_table")
                        , line.get("send_systems")
                );
            }
            // 执行修改
            execUpdate(this.mysql_conn, Collections.singletonList(sql));

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    public List<Map<String, Object>> queryInfo(Connection conn, String sql) {
        List resList = new ArrayList<Map<String, Object>>();

        try (Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map line = new HashMap<String, Object>(columnCount + 1);
                for (int i = 0; i < columnCount; i++) {
                    line.put(metaData.getColumnName(i+1), resultSet.getObject(i+1));
                }
                resList.add(line);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resList;
    }


    public void execUpdate(Connection connOpenTP, List<String> sqls) throws SQLException {
        int dataCount = 0;
        try (Statement statement = connOpenTP.createStatement()) {
            for (String sql : sqls) {
                int i = statement.executeUpdate(sql);
                dataCount += i;
                System.out.println(String.format("执行sql：%s", sql));
                System.out.println(String.format("更新数据量：%s", dataCount));
            }
            connOpenTP.commit();
            System.out.println(String.format("共更新数据量：%s", dataCount));
        } catch (SQLException e) {
            try {
                connOpenTP.rollback();
            } catch (SQLException ex) {
                System.out.println("SQL回滚失败");
                ex.printStackTrace();
                // throw new RuntimeException(ex);
            }
            System.out.println("SQL执行失败");
            e.printStackTrace();
            throw new SQLException(e);

        }
    }

    public void linuxExecCommand(Session session, String command) throws IOException, JSchException {
        if (command.trim().startsWith("rm") && command.trim().endsWith("/")) {
            return;
        }
        // 执行单条命令
        ChannelExec channel;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();

            // 读取命令输出
            InputStream in = channel.getInputStream();
            byte[] buffer = new byte[1024];
            while (in.read(buffer) > 0) {
                System.out.println(new String(buffer));
            }

            channel.disconnect();
        } catch (JSchException e) {
            throw new JSchException("", e);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public List<String> linuxExecCommands(Session session, List<String> commands) throws JSchException {
        List<String> result = new ArrayList<>();
        for (String command : commands) {
            if (command.trim().startsWith("rm") &&  command.trim().endsWith("/")) {
                result.add("包含危险删除操作，退出");
                return result;
            }
        }

        // 执行多条命令
        ChannelShell channel = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
        OutputStream outputStream = null;
        PrintWriter printWriter = null;
        Watchdog watchdog = new Watchdog(30 * 1000); // 执行超时时长

        try {

            channel = (ChannelShell) session.openChannel("shell");
            channel.setPty(true);
            channel.setEnv("LANG", "zh_CN.UTF-8");
            inputStream = channel.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            outputStream = channel.getOutputStream();
            printWriter = new PrintWriter(outputStream);

            Thread thread = Thread.currentThread();
            watchdog.addTimeoutObserver(w -> thread.interrupt());

            channel.connect();
            watchdog.start();

            for (String cmd : commands) {
                printWriter.println(cmd + " 2>&1");
            }
            // 发送 exit 命令，通知远程 shell 结束
            printWriter.println("exit");
            printWriter.flush();

            while (true) {
                if (inputStream.available() > 0) {
                    String buffer;
                    while ((buffer = reader.readLine()) != null) {
                        result.add(ANSI_PATTERN.matcher(buffer).replaceAll(""));
                    }
                }
                if (channel.isClosed()) break;
                Thread.sleep(500);
            }
        } catch (Exception e) {
            throw new JSchException("执行Shell命令失败", e);
        } finally {
            watchdog.stop();
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignores) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignores) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignores) {
                }
            }
            if (printWriter != null) {
                try {
                    printWriter.close();
                } catch (Exception ignores) {
                }
            }
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
        return result;
    }


    /**
     * @param session 会话
     * @param commands 命令列表
     * @param delayMs 执行间隔时间
     * @param num 重复次数
     * @throws JSchException
     */
    public void linuxExecCommandsOnlyPrint(Session session, List<String> commands, Long delayMs, int num) throws JSchException {
        List<String> result = new ArrayList<>();
        for (String command : commands) {
            if (command.trim().startsWith("rm") && (command.trim().endsWith(" / ") || command.trim().endsWith("/"))) {
                System.out.println(("包含危险删除操作，退出"));
            }
        }

        // 执行多条命令
        ChannelShell channel = null;
        OutputStream outputStream = null;
        PrintWriter printWriter = null;
        Watchdog watchdog = new Watchdog(600 * 1000); // 执行超时时长

        try {

            channel = (ChannelShell) session.openChannel("shell");
            channel.setPty(true);
            channel.setEnv("LANG", "zh_CN.UTF-8");
            InputStream inputStream = channel.getInputStream();
            outputStream = channel.getOutputStream();
            // 使用 PrintWriter 来方便地发送命令，autoFlush=true 确保 println 后立即发送
            printWriter = new PrintWriter(outputStream, true);

            Thread thread = Thread.currentThread();
            watchdog.addTimeoutObserver(w -> thread.interrupt());

            channel.connect();
            watchdog.start();

            Thread printThread = new Thread(() -> {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    // 通道关闭时，readLine会抛出异常
                    System.out.println("\nShell channel closed by server.");
                } finally {
                    try {
                        if (null == reader) reader.close();
                    } catch (IOException e) {
                    }
                }

            });
            printThread.start();

            for (int i = 0; i < num; i++) {
                System.out.printf("---------------------------------- 第 %s 次执行 --------------------------\n", i+1);
                for (String cmd : commands) {
                    printWriter.println(cmd + " 2>&1");
                }
                if (i + 1 != num) Thread.sleep(delayMs);
            }

            if (commands.get(commands.size() - 1).contains("tail -f")) {
                Thread.sleep(600 * 1000);
            } else {
                Thread.sleep(3 * 100);  // 等待另一进程打印完成
            }

            // 发送 exit 命令，通知远程 shell 结束
            printWriter.println("exit");
            // printWriter.flush();

        } catch (Exception e) {
            throw new JSchException("执行Shell命令失败", e);
        } finally {
            watchdog.stop();
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignores) {
                }
            }
            if (printWriter != null) {
                try {
                    printWriter.close();
                } catch (Exception ignores) {
                }
            }
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
        }
    }


    public void initMysqlConn() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.mysql_conn = DriverManager.getConnection(url, user, password);
            this.mysql_conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("", e);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    public void initImpalaConn() throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.cloudera.impala.jdbc41.Driver");
            this.implala_conn = DriverManager.getConnection(impalaUrl);
            this.implala_conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("", e);
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    private void initLinuxSession() throws JSchException {
        JSch jSch = new JSch();
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        try {
            this.session = jSch.getSession(linuxUserName, linuxHost, 22);
            this.session.setConfig(sshConfig);
            this.session.setPassword(linuxPassWD);
            this.session.connect(30000);
        } catch (JSchException e) {
            throw new JSchException("创建ssh连接失败", e);
        }
    }

    public void close() {
        try {
            if (null != implala_conn) implala_conn.close();
            if (null != mysql_conn) mysql_conn.close();
            if (null != session && session.isConnected()) session.disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 基于当前时间，获取指定间隔的整点时间字符串：计算指定间隔的调度时间点
     * @param timeInterval 间隔
     * @param dalay 拼接延迟串 HH:mm:ss
     * @param time_unit 枚举值 min hour day month
     * @param point 计算偏移时间点
     * @return
     */
    public static String getPointTime(Integer timeInterval, String dalay, String time_unit, Integer point) {
        // 转换为格式化时间字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Pattern minAndSec = Pattern.compile("[0-2][0-9]:[0-5][0-9]:[0-5][0-9]");
        boolean matches = minAndSec.matcher(dalay).matches();

        LocalDateTime now = LocalDateTime.now();

        if (time_unit.equalsIgnoreCase("min")) {
            String format = now.atZone(ZoneId.systemDefault()).plusMinutes((long) timeInterval * point).format(formatter);
            return format.substring(0, 15) + "0:00.000000";
        } else if (time_unit.equalsIgnoreCase("hour")) {
            String format = now.atZone(ZoneId.systemDefault()).plusHours((long) timeInterval * point).format(formatter);
            return format.substring(0, 13) + (matches ? ":" + dalay.split(":")[1] + ":00.000000" : ":00:00.000000");
        } else {
            String format = now.atZone(ZoneId.systemDefault()).plusDays((long) timeInterval * point).format(formatter);
            return format.substring(0, 11) + (matches ? dalay + ".000000" : "00:00:00.000000");
        }
    }

    public String getFirstTabFromSql(String sql) throws JSQLParserException {
        try {
            net.sf.jsqlparser.statement.Statement statement = CCJSqlParserUtil.parse(sql);
            if (statement instanceof Select) {
                Select selectStatement = (Select) statement;
                PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
                Table firstTable = (Table) plainSelect.getFromItem(); // 获取第一个表
                System.out.println("第一个表名：" + firstTable.getFullyQualifiedName());
                return firstTable.getFullyQualifiedName();
            } else {
                System.out.println("不是Select语句");
                return "";
            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
            throw new JSQLParserException(e);
        }
    }


}
