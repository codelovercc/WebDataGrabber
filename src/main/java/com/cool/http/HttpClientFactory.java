package com.cool.http;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by codelover on 17/5/22.
 * 带有链接池的httpClient,这个spring bean 是原型的，每交要求注入的时候都会创建一个新的，
 * 也可以使用同一个实例，相当于两个类使用了同一个连接池
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HttpClientFactory {
    private final static Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

    private PoolingHttpClientConnectionManager poolingConnectManager;

    public static HttpClientFactory createNew(){
        return new HttpClientFactory();
    }

    /**
     * 新建一个链接池
     */
    public HttpClientFactory() {
        initCM();
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new Thread(this::releaseCM));
    }

    /**
     * 创建链接池的HttpClient
     *
     * @param cookieStore 用于存储Cookie的容器
     * @return
     */
    public HttpClient createHttpClient(CookieStore cookieStore) {
        return HttpClients.custom().setConnectionManager(poolingConnectManager)
                .setDefaultCookieStore(cookieStore)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                .build();
    }

    /**
     * 创建基础的Cookie容器
     *
     * @return
     */
    public CookieStore createCookieStore() {
        return new BasicCookieStore();
    }

    /**
     * 创建http上下文
     * @return
     */
    public HttpContext createHttpContext(){
        return HttpClientContext.create();
    }
    /**
     * 创建上下文
     * @param requestTimeOut 请求超时，毫秒
     * @param connectTimeOut 连接超时，毫秒
     * @param sockTimeOut 套接字超时，毫秒
     * @return
     */
    public HttpContext createHttpContext(int requestTimeOut, int connectTimeOut, int sockTimeOut){
        RequestConfig requestConfig = RequestConfig.custom().setCircularRedirectsAllowed(false)
                .setConnectionRequestTimeout(requestTimeOut)
                .setConnectTimeout(connectTimeOut)
                .setSocketTimeout(sockTimeOut).build();
        HttpClientContext context = HttpClientContext.create();
        context.setRequestConfig(requestConfig);
        return context;
    }
    public Header createHeader(final String name,final String value){
        return new BasicHeader(name, value);
    }
    /**
     * 获取链接池
     *
     * @return
     */
    public final PoolingHttpClientConnectionManager getPoolingConnectManager() {
        return poolingConnectManager;
    }

    /**
     * 初始化链接池
     */
    private void initCM() {
        poolingConnectManager = new PoolingHttpClientConnectionManager();
        poolingConnectManager.setMaxTotal(50);
        poolingConnectManager.setDefaultMaxPerRoute(20);
        logger.info("链接池已经初始化");
    }

    private void releaseCM() {
        if (poolingConnectManager != null) {
            poolingConnectManager.close();
        }
        logger.info("链接池已经释放");
    }

    @Override
    protected void finalize() throws Throwable {
        if (poolingConnectManager != null) {
            releaseCM();
        }
        super.finalize();
    }
}
