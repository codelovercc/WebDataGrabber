package com.cool.http;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;

/**
 * Created by codelover on 17/8/24.
 * 通用的http client 基类，有独立的上下文和cookie容器,并且可观察这个对象
 */
public abstract class AbstractHttpContextClient extends Observable {

    private CookieStore cookieStore;
    private HttpContext httpContext;
    private HttpClient httpClient;
    private final HttpClientFactory httpClientFactory;
    private final List<Header> headers = new ArrayList<>(5);

    public AbstractHttpContextClient() {
        httpClientFactory = HttpClientFactory.createNew();
        initHeaders();
        initContext(httpClientFactory);
    }

    public AbstractHttpContextClient(HttpClientFactory httpClientFactory){
        this.httpClientFactory = httpClientFactory;
        initHeaders();
        initContext(httpClientFactory);
    }

    private void initContext(HttpClientFactory httpClientFactory) {
        if (getCookieStore() == null) {
            synchronized (this) {
                if (getCookieStore() == null) {
                    setCookieStore(httpClientFactory.createCookieStore());
                }
            }
        }
        if (getHttpContext() == null) {
            synchronized (this) {
                if (getHttpContext() == null) {
                    setHttpContext(httpClientFactory.createHttpContext());
                }
            }
        }
        if (getHttpClient() == null) {
            synchronized (this) {
                if (getHttpClient() == null) {
                    setHttpClient(httpClientFactory.createHttpClient(getCookieStore()));
                }
            }
        }
    }

    protected void addHeaders(Collection<Header> headers){
        getHeaders().addAll(headers);
    }

    protected void addHeader(Header header){
        getHeaders().add(header);
    }

    /**
     * 初始化固定的headers
     */
    protected abstract void initHeaders();

    @Override
    public void setChanged() {
        super.setChanged();
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    private void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public HttpContext getHttpContext() {
        return httpContext;
    }

    private void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    private void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    private List<Header> getHeaders() {
        return headers;
    }

    protected HttpClientFactory getHttpClientFactory(){
        return httpClientFactory;
    }
}
