package com.cool.util;

import com.cool.exception.ProjectIORunTimeException;
import com.cool.exception.ProjectRunTimeException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by codelover on 17/3/23.
 * 对字符串做一个操作的综合工具类
 */
@Component
public class StringUtil {

    private static final String DATE_PATTERN = "(?<left>\\d+-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2})\\s*-\\s*(?<right>\\d+-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2})";

    /**
     * 向指定的mapping key路径添加路径参数
     *
     * @param environment
     * @param key         mapping key
     * @param vars        要添加到这个路径上的参数
     * @return 添加好的路径，eg. /admin/admin/edit/{id}
     */
    public static String appendPathVariable(Environment environment,
                                            String key, Object... vars) {
        String mapping = environment.getRequiredProperty(key);
        if (vars == null || vars.length == 0) {
            return mapping;
        }
        if (!mapping.endsWith("/"))
            mapping = mapping.concat("/");
        StringBuilder stringBuilder = new StringBuilder(mapping);
        for (Object str :
                vars) {
            stringBuilder.append(str).append("/");
        }
        return stringBuilder.toString();
    }

    /**
     * toSearch 是否包含search中的任何一项
     * @param toSearch
     * @param search
     * @return
     */
    public static boolean contains(String toSearch, String[] search) {
        if (search == null || search.length == 0) {
            return false;
        }
        for (String t :
                search) {
            if (toSearch.contains(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * arr中是否有任何一项与search相同
     * @param arr
     * @param search
     * @param <T>
     * @return
     */
    public static <T> boolean contains(T[] arr, T search) {
        if (arr == null || arr.length == 0) {
            return false;
        }
        for (T t :
                arr) {
            if (t.equals(search)) {
                return true;
            }
        }
        return false;
    }

    public static <T> Set<T> convertToSetIgnoreDuplicate(Iterable<T> it) {
        Set<T> set = new HashSet<T>();
        for (T o :
                it) {
            set.add(o);
        }
        return set;
    }

    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    /**
     * 只保留数字
     *
     * @param str
     * @return
     */
    public static String leftNumber(String str) {
        return isEmpty(str) ? str : str.replaceAll("[^0-9]", "");
    }

    /**
     * 四舍五入,保留两位小数
     *
     * @param d 要操作的数字
     */
    public static String roundHalfUpString(double d) {
        return roundHalfUpString(d, 2);
    }

    /**
     * 四舍五入
     *
     * @param d      要操作的数字
     * @param digits 要保留几位小数
     * @return
     */
    public static String roundHalfUpString(double d, int digits) {
        return String.format("%." + digits + "f", roundHalfUp(d, digits));
    }

    /**
     * 四舍五入
     *
     * @param d      要操作的数字
     * @param digits 要保留几位小数
     * @return
     */
    public static double roundHalfUp(double d, int digits) {
        BigDecimal bigDecimal = new BigDecimal(d);
        return bigDecimal.setScale(digits, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 去除保留小数后的数值
     *
     * @param d      要操作的数字
     * @param digits 要保留几位小数
     * @return
     */
    public static double roundDown(double d, int digits) {
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(d));
        return bigDecimal.setScale(digits, RoundingMode.DOWN).doubleValue();
    }


    /**
     * 去除保留小数后的数值
     *
     * @param d      要操作的数字
     * @param digits 要保留几位小数
     * @return
     */
    public double _roundDown(double d, int digits) {
        return roundDown(d, digits);
    }

    /**
     * 去除保留小数后的数值
     *
     * @param d      要操作的数字
     * @param digits 要保留几位小数
     * @return
     */
    public static String roundDownString(double d, int digits) {
        return String.format("%." + digits + "f", roundDown(d, digits));
    }

    /**
     * 去除保留小数后的数值,保留两位小数
     *
     * @param d 要操作的数字
     * @return
     */
    public static String roundDownString(double d) {
        return roundDownString(d, 2);
    }

    /**
     * 去除保留小数后的数值
     *
     * @param d      要操作的数字
     * @param digits 要保留几位小数
     * @return
     */
    public String _roundDownString(double d, int digits) {
        return roundDownString(d, digits);
    }

    /**
     * 去除保留小数后的数值,保留两位小数
     *
     * @param d 要操作的数字
     * @return
     */
    public String _roundDownString(double d) {
        return roundDownString(d);
    }

    /**
     * 获取所有的参数
     *
     * @param request
     * @return
     */
    public static String getParameters(HttpServletRequest request) {
        Map<String, String[]> ps = request.getParameterMap();
        return paramMapToString(ps);
    }

    public static <K, V> String mapToString(Map<K, V> map) {
        int max = map.size() - 1;
        if (max == -1)
            return "{}";

        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();

        sb.append('{');
        for (int i = 0; ; i++) {
            Map.Entry<K, V> e = it.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == map ? "(this Map)" : key.toString());
            sb.append('=');
            sb.append(value == map ? "(this Map)" : value.toString());

            if (i == max)
                return sb.append('}').toString();
            sb.append(", ");
        }
    }

    public static String paramMapToString(Map<String, String[]> map) {
        int max = map.size() - 1;
        if (max == -1)
            return "{}";

        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String[]>> it = map.entrySet().iterator();

        sb.append('{');
        for (int i = 0; ; i++) {
            Map.Entry<String, String[]> e = it.next();
            String key = e.getKey();
            if (key.equals("password")) {
                continue;
            }
            String[] value = e.getValue();
            sb.append(key);
            sb.append('=');
            sb.append(arrayToString(value));

            if (i == max)
                return sb.append('}').toString();
            sb.append(", ");
        }
    }

    public static String arrayToString(Object[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(String.valueOf(a[i]));
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    /**
     * 获取引用页
     *
     * @param request
     * @return
     */
    public static String getReferer(HttpServletRequest request) {
        return request.getHeader("referer");
    }

    /**
     * 获取用户代理信息
     *
     * @param request
     * @return
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 获取用户真实IP地址，不使用request.getRemoteAddr();的原因是有可能用户使用了代理软件方式避免真实IP地址,
     * <p>
     * 可是，如果通过了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP值，究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * <p>
     * 如：X-Forwarded-For：192.168.1.110, 192.168.1.120, 192.168.1.130,
     * 192.168.1.100
     * <p>
     * 用户真实IP为： 192.168.1.110
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getString(Object str) {
        return isNotEmpty(str) ? str.toString() : "";
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }


    public static String passwordSHA(String password) {
        return SHA.SHA256(password);
    }


    /**
     * 获取4位数年份
     *
     * @param year 年份字符串
     * @return 4位数年份
     */
    public static int getFullYear(String year) {
        int l = 4 - year.length();
        if (l == 0) {
            return Integer.parseInt(year);
        } else {
            String fullYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            String pre = fullYear.substring(0, l);
            return Integer.parseInt(pre + year);
        }
    }

    /**
     * 转换为数字
     *
     * @param str
     * @return
     */
    public static Number parseNumber(String str) {
        try {
            return NumberFormat.getNumberInstance().parse(str);
        } catch (Throwable e) {
            return null;
        }
    }


    public static boolean isEmpty(Object str) {
        return str == null || str.equals("") || str.toString().trim().equals("");
    }

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    /**
     * 拼接两个目录URI,指定是否需要保持/结尾
     *
     * @param base
     * @param toContact
     * @param needEnd   如果需要保持/结尾，则为true,不管是什么结尾就传入false
     * @return
     */
    public static String contactURIDir(final String base, final String toContact, final boolean needEnd) {
        StringBuilder sb = new StringBuilder(base);
        if (!base.endsWith("/")) {
            sb.append("/");
        }
        if (toContact.startsWith("/")) {
            sb.append(toContact, 1, toContact.length());
        } else {
            sb.append(toContact);
        }
        if (needEnd && sb.charAt(sb.length() - 1) != '/') {
            sb.append('/');
        }
        return sb.toString();
    }

    public boolean contains(String source, String find) {
        return source.contains(find);
    }

    public boolean isNull(Object o) {
        return o == null;
    }

    public boolean isNotNull(Object o) {
        return o != null;
    }

    public static ObjectMapper getJsonObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        //设置将对象转换成JSON字符串时候:包含的属性不能为空或"";
        //Include.Include.ALWAYS 默认
        //Include.NON_DEFAULT 属性为默认值不序列化
        //Include.NON_EMPTY 属性为 空（""）  或者为 NULL 都不序列化
        //Include.NON_NULL 属性为NULL 不序列化
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        //设置有属性不能映射成PO时不报错
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }

    public static String getJsonString(Object o) {
        try {
            return getJsonObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new ProjectRunTimeException("对象转换到JSON字符串失败!", e);
        }
    }

    /**
     * 将json 转换为java Bean
     *
     * @param json  json 字符串
     * @param clazz 转换后的类型信息
     * @param <T>   转换后的类型
     * @return 成功返回对象的实例
     */
    public static <T> T getObjectFromJson(String json, Class<T> clazz) {
        try {
            return getJsonObjectMapper().readValue(json, clazz);
        } catch (IOException e) {
            throw new ProjectIORunTimeException("json转换时IO失败", e);
        }
    }

    public static String generalGetMethod(String fieldName) {
        String f = fieldName.substring(0, 1);
        String l = fieldName.substring(1, fieldName.length());
        String method = "get" + f.toUpperCase() + l;
        return method;
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * 获取与地区相关的Date格式化对象
     *
     * @param locale
     * @param messageSource
     * @return
     */
    public static SimpleDateFormat createDateFormat(final Locale locale, final MessageSource messageSource) {
        final String format = messageSource.getMessage("date.format", null, locale);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        return dateFormat;
    }

    /**
     * 格式化时间范围字符串
     * @param dateRange 时间范围
     * @return 返回这样格式的字符串 {@code 2017-09-23 00:00:00 - 2017-09-23 23:59:59}
     */
    public static <T extends Date> String getFormattedTimeRangeString(Range<T, T> dateRange) {
        return getFormattedTimeRangeString(dateRange.getLeft(), dateRange.getRight());
    }

    /**
     * 格式化时间范围字符串
     * @param start 开始时间
     * @param end 结束时间
     * @return 返回这样格式的字符串 {@code 2017-09-23 00:00:00 - 2017-09-23 23:59:59}
     */
    public static <T extends Date> String getFormattedTimeRangeString(T start, T end) {
        return formatDate(start) + " - " + formatDate(end);
    }

    /**
     * 转换成Timestamp类型。转换失败的值将会是null
     *
     * @param timeRange 时间范围字符串, eg. {@code 2017-09-23 00:00:00 - 2017-09-23 23:59:59}
     * @return 转换失败返null , 否则返回时间范围类型
     */
    public static Range<Timestamp, Timestamp> matcherTimeRange(String timeRange) {
        Range<Timestamp, Timestamp> keyValuePair = Range.getInstance();
        try {
            Pattern pattern = Pattern.compile(DATE_PATTERN);
            Matcher matcher = pattern.matcher(timeRange);
            if (matcher.matches()) {
                String minTime = matcher.group("left");
                String maxTime = matcher.group("right");
                Timestamp left = Timestamp.valueOf(minTime);
                keyValuePair.setLeft(left);
                Timestamp right = Timestamp.valueOf(maxTime);
                keyValuePair.setRight(right);
            }
        } catch (Exception ignored) {
        }
        return keyValuePair;
    }

    public static boolean isMobile(String account) {
        return account.matches("^(13[0-9]{9})|(18[0-9]{9})|(14[0-9]{9})|(17[0-9]{9})|(15[0-9]{9})$");
    }

    /**
     * 获取月份的数字表示，eg. 201700 表示2017年1月
     *
     * @return
     */
    public static int getMonthNumber(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR) * 100 + calendar.get(Calendar.MONTH);
    }

    /**
     * 获取月份的字符串表示, eg. 201700 表示2017年1月
     *
     * @return
     */
    public static String getMonthString(Date date) {
        return String.valueOf(getMonthNumber(date));
    }
}
