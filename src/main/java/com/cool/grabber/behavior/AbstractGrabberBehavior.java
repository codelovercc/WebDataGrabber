package com.cool.grabber.behavior;

import com.cool.exception.ProjectException;
import com.cool.exception.ProjectFormatException;
import com.cool.grabber.exception.*;
import com.cool.http.AbstractSingleGetHttpClient;
import com.cool.http.HttpClientUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by codelover on 18/3/7.
 * 一次只抓取一个链接
 */
public abstract class AbstractGrabberBehavior<T> extends AbstractSingleGetHttpClient implements GrabberBehavior<T> {
    private final static Logger logger = LoggerFactory.getLogger(AbstractGrabberBehavior.class);

    @Override
    protected String getReferer() {
        //返回空字符串，使初始化通过
        return "";
    }

    @Override
    protected HttpGet createHttpGet() {
        return new HttpGet();
    }

    @Override
    protected HttpContext createHttpContext() {
        RequestConfig requestConfig = RequestConfig.custom().setCircularRedirectsAllowed(false)
                .setConnectionRequestTimeout(DEFAULT_REQUEST_TIMEOUT)
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                .setSocketTimeout(DEFAULT_SOCK_TIMEOUT).build();
        HttpClientContext context = HttpClientContext.create();
        context.setRequestConfig(requestConfig);
        return context;
    }

    @Override
    public List<T> doGrab(String grabLink, String referer) throws GrabberException {
        //创建新的httpGet使多线程不会对一份httpGet同时做操作
        HttpGet httpGet = new HttpGet();
        try {
            httpGet.setURI(new URI(grabLink));
        } catch (URISyntaxException e) {
            throw new GrabberException("链接格式错误", e);
        }
        httpGet.setHeaders(defaultHeaders);
        httpGet.setHeader("Referer", referer);
        try (CloseableHttpResponse response = getHttpClient().execute(httpGet, httpContext)) {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                throw new GrabberEmptyDataException("未能获取数据,响应码: " + response.getStatusLine().getStatusCode());
            }
            String content = HttpClientUtil.getEntityString(httpEntity);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new GrabberStatusInvalidException("未能获取数据,服务器没有正常响应，响应码: " + response.getStatusLine().getStatusCode());
            }
            EntityUtils.consumeQuietly(httpEntity);
            return getModels(content, grabLink);
        } catch (IOException | ProjectException e) {
            httpGet.abort();
            getCm().closeExpiredConnections();
            throw new GrabberException(e);
        }finally {
        }
    }

    /**
     * 将抓取到的字符串数据转换成模型
     *
     * @param data     字符串数据
     * @param fromLink 数据是从哪个链接来的
     * @return
     */
    protected abstract List<T> getModels(String data, String fromLink) throws GrabberInvalidDataException;
}
