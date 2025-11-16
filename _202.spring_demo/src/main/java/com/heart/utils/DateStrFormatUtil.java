package com.heart.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Pattern;

/**
 * 日期字符串工具类，用于日期字符串的转换及拼接
 * 1. 日期与字符串的相互转换
 * 2. 日期字符串的拼接：根据传入delay字符串，截取日期并拼接
 */
public class DateStrFormatUtil {

    /**
     * 将日期字符串转换为 LocalDate 对象
     * @param dateStr 日期字符串，格式为 yyyy-MM-dd
     * @return LocalDate 对象
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 将 LocalDateTime 字符串转换为 LocalDateTime 对象
     * @param dateTimeStr 日期时间字符串，格式为 yyyy-MM-dd HH:mm:ss
     * @return LocalDateTime 对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 将 LocalDate 对象格式化为字符串
     * @param date LocalDate 对象
     * @return 格式化后的日期字符串 yyyy-MM-dd
     */
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 将 LocalDateTime 对象格式化为字符串
     * @param dateTime LocalDateTime 对象
     * @return 格式化后的日期时间字符串 yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 获取当前日期字符串
     * @return 当前日期字符串，格式为 yyyy-MM-dd
     */
    public static String getCurrentDate() {
        return formatDate(LocalDate.now());
    }

    /**
     * 获取当前日期时间字符串
     * @return 当前日期时间字符串，格式为 yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentDateTime() {
        return formatDateTime(LocalDateTime.now());
    }

    /**
     * 根据延迟字符串计算目标日期
     * @param baseDate 基准日期字符串
     * @param delay 延迟字符串，如 "+1M" 表示加1个月，"-2D" 表示减2天
     * @return 计算后的日期字符串
     */
    public static String calculateDateByDelay(String baseDate, String delay) {
        LocalDate date = parseDate(baseDate);

        if (delay != null && !delay.isEmpty()) {
            char operator = delay.charAt(0);
            char unit = delay.charAt(delay.length() - 1);
            int amount = Integer.parseInt(delay.substring(1, delay.length() - 1));

            if (operator == '+') {
                switch (unit) {
                    case 'D': // 天
                        date = date.plusDays(amount);
                        break;
                    case 'M': // 月
                        date = date.plusMonths(amount);
                        break;
                    case 'Y': // 年
                        date = date.plusYears(amount);
                        break;
                }
            } else if (operator == '-') {
                switch (unit) {
                    case 'D': // 天
                        date = date.minusDays(amount);
                        break;
                    case 'M': // 月
                        date = date.minusMonths(amount);
                        break;
                    case 'Y': // 年
                        date = date.minusYears(amount);
                        break;
                }
            }
        }

        return formatDate(date);
    }

    /**
     * 获取指定日期所在月份的第一天
     * @param dateStr 日期字符串
     * @return 该月第一天的日期字符串
     */
    public static String getFirstDayOfMonth(String dateStr) {
        LocalDate date = parseDate(dateStr);
        return formatDate(date.with(TemporalAdjusters.firstDayOfMonth()));
    }

    /**
     * 获取指定日期所在月份的最后一天
     * @param dateStr 日期字符串
     * @return 该月最后一天的日期字符串
     */
    public static String getLastDayOfMonth(String dateStr) {
        LocalDate date = parseDate(dateStr);
        return formatDate(date.with(TemporalAdjusters.lastDayOfMonth()));
    }

    /**
     * 字符串右补0
     * @param str 原始字符串
     * @param length 目标长度
     * @return 右补0后的字符串
     */
    public static String rightPadWithZeros(String str, int length) {
        if (str == null) {
            str = "";
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append('0');
        }
        return sb.toString();
    }



    /**************************************** 项目使用 *****************************************************/
    /**
     * 字符串右补字符到26长度
     * @param str 原始字符串
     * @return 补齐后的字符串
     */
    public static String rightPadTo26(String str) {
        if (str.length() == 19) {
            str = str + ".000000";
        } else if (str.length() > 19 && str.length() < 26) {
            str = str + "000000";
        }
        return str.substring(0, 26);
    }

    /**
     * 获取不含分隔符的日期字符串 yyyyMMddHHmmss
     * @param str 原始字符串
     * @return 目标字符串
     */
    public static String getDateStrNoSeparator(String str) {
        return str.split("\\.")[0]
                .replace("-", "")
                .replace(":", "")
                .replace(" ", "");
    }

    /**
     * 基于当前时间，获取指定间隔的整点时间字符串：计算指定间隔的调度时间点
     * @param timeInterval 间隔
     * @param dalay 拼接延迟串 HH:mm:ss
     * @param type 枚举值 min hour day month
     * @return
     */
    public static String getPointTime(Integer timeInterval, String dalay, String type) {
        // 转换为格式化时间字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Pattern minAndSec = Pattern.compile("[0-2][0-9]:[0-5][0-9]:[0-5][0-9]");
        boolean matches = minAndSec.matcher(dalay).matches();

        LocalDateTime now = LocalDateTime.now();

        if (type.equalsIgnoreCase("min")) {
            String format = now.atZone(ZoneId.systemDefault()).plusMinutes(timeInterval).format(formatter);
            return format.substring(0, 15) + "0:00.000000";
        } else if (type.equalsIgnoreCase("hour")) {
            String format = now.atZone(ZoneId.systemDefault()).plusHours(timeInterval).format(formatter);
            return format.substring(0, 13) + (matches ? ":" + dalay.split(":")[1] + ":00.000000" : ":00:00.000000");
        } else {
            String format = now.atZone(ZoneId.systemDefault()).plusDays(timeInterval).format(formatter);
            return format.substring(0, 11) + (matches ? dalay + ".000000" : "00:00:00.000000");
        }
    }
}
