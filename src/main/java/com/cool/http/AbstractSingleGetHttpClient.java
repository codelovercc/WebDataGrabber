package com.cool.http;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by codelover on 17/5/19.
 * apache http client 的其类，内置了链接池
 */
public abstract class AbstractSingleGetHttpClient {
    private final static Logger logger = LoggerFactory.getLogger(AbstractSingleGetHttpClient.class);

    /**
     * 默认请求超时时间
     */
    protected static int DEFAULT_REQUEST_TIMEOUT;

    /**
     * 套接字超时时间
     */
    protected static int DEFAULT_SOCK_TIMEOUT;
    /**
     * 直到连接可用为止时的超时时间
     */
    protected static int DEFAULT_CONNECT_TIMEOUT;

    protected HttpContext httpContext;
    protected HttpGet httpGet;

    private static volatile PoolingHttpClientConnectionManager cm;
    private CloseableHttpClient httpClient;
    private CookieStore cookieStore;
    /**
     * 链接池引用计数
     */
    private static int cmRefCount = 0;
    private Map<String, Header> headers;
    private final Header[] defaultHeaders = {
            new BasicHeader("Accept", "application/json, text/javascript, */*; q=0.01"),
            new BasicHeader("Accept-Encoding", "gzip, deflate, sdch"),
            new BasicHeader("Accept-Language", "h-CN,zh;q=0.8,en;q=0.6"),
            new BasicHeader("Cache-Control", "no-cache"),
            new BasicHeader("Connection", "keep-alive"),
            new BasicHeader("Pragma", "no-cache"),
            new BasicHeader("Referer", ""),
            new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.110 Safari/537.36"),
            new BasicHeader("X-Requested-With", "XMLHttpRequest")
    };

    public AbstractSingleGetHttpClient(){
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(new File(Thread.currentThread().getContextClassLoader().getResource("/httpClient.properties").toURI())));
            DEFAULT_REQUEST_TIMEOUT = Integer.parseInt(properties.getProperty("httpClient.request.timeout"));
            DEFAULT_SOCK_TIMEOUT = Integer.parseInt(properties.getProperty("httpClient.sock.timeout"));
            DEFAULT_CONNECT_TIMEOUT = Integer.parseInt(properties.getProperty("httpClient.connect.timeout"));
        } catch (IOException | NumberFormatException | URISyntaxException e) {
            logger.warn("加载网络超时配置失败，将使用默认配置", e);
            DEFAULT_REQUEST_TIMEOUT = 800;
            DEFAULT_SOCK_TIMEOUT = 300;
            DEFAULT_CONNECT_TIMEOUT = 500;
        }
        if (cm == null) {//如果链接池没有初始化
            synchronized (AbstractSingleGetHttpClient.class){
                if(cm == null){
                    initCM();
                    Runtime runtime = Runtime.getRuntime();
                    runtime.addShutdownHook(new Thread(this::releaseCM));
                }
            }
        }
        cookieStore = new BasicCookieStore();
        httpClient = HttpClients.custom().setConnectionManager(cm).setDefaultCookieStore(cookieStore).build();
        cmRefCount++;//增加链接池引用计数
        headers = new HashMap<>();
        for (Header h :
                defaultHeaders) {
            headers.put(h.getName(), h);
        }
        setHeader("Referer", getReferer());
        httpContext = createHttpContext();
        httpGet = createHttpGet();
        httpGet.setHeaders(getHeaders());
    }


    protected static PoolingHttpClientConnectionManager getCm() {
        return cm;
    }

    protected CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    protected CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * 初始化链接池
     */
    private static void initCM() {
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(50);
        cm.setDefaultMaxPerRoute(20);
        logger.info("单一链接池已经初始化");
    }
    protected void releaseCM() {
        if(httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error("关闭HTTPClient对象出错");
            }
        }
        if(cm != null){
            cm.close();
        }
        logger.info("链接池已经释放");
    }

    /**
     * 调用此方法，使得子类能够改变要访问网址的对象
     * @param httpGet
     */
    protected void setHttpGet(HttpGet httpGet){
        this.httpGet = httpGet;
    }

    /**
     * 子类返回对象
     * @return
     */
    protected abstract HttpGet createHttpGet();

    /**
     * 子类返回对象
     * @return
     */
    protected abstract HttpContext createHttpContext();
    /**
     * 子类返回更新引用页
     */
    protected abstract String getReferer();

    public void setHeader(String name, String value) {
        if (headers.containsKey(name))
            headers.replace(name, new BasicHeader(name, value));
        else
            headers.put(name, new BasicHeader(name, value));
    }

    public void removeHeader(String name){
        headers.remove(name);
    }

    public Header[] getHeaders() {
        return headers.values().toArray(new Header[0]);
    }

    @Override
    protected void finalize() throws Throwable {
        if (--cmRefCount == 0 && cm != null) {
            releaseCM();
        }
        super.finalize();
    }
}
