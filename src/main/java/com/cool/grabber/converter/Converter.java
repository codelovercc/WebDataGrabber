package com.cool.grabber.converter;

/**
 * Created by codelover on 18/3/7.
 * 接受一定格式的字符串，然后将其转换为相应的类型 {@link T}
 * @param <T> 转换后的模型
 */
public interface Converter<T> {
    /**
     * 接受一定格式的字符串，然后将其转换为相应的类型
     * @param data 一定格式的字符串
     * @return 转换后的模型
     */
    T convert(String data);
}
