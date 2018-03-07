package com.cool.util;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

/**
 * Created by codelover on 17/4/23.
 * 用于获取Jsonson提供类型转换的JsonPath
 */
public class JsonPathWithJacksonMappingProvider {
    public static final MappingProvider mappingProvider;
    public static final Configuration configuration;
    public static final ParseContext parseContext;
    static {
        mappingProvider = new JacksonMappingProvider(StringUtil.getJsonObjectMapper());
        configuration = Configuration.builder().mappingProvider(mappingProvider).build();
        parseContext = JsonPath.using(configuration);
    }
    private JsonPathWithJacksonMappingProvider(){}

    public static ParseContext getParseContext(){
        return JsonPath.using(configuration);
    }
}
