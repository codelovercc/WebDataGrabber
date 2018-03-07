package com.cool.http;

import com.cool.exception.ProjectException;
import com.cool.exception.ProjectIOException;
import com.cool.exception.ProjectUnsupportedEncodingException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by codelover on 17/5/22.
 * httpClient工具类
 */
public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    /**
     * 获取响应字符串
     * @param url
     * @return
     * @throws IOException 请求发生错误时
     * @throws ProjectException 解析响应错误时
     */
    public static String getResponseString(String url) throws ProjectException {
        return getResponseString(url, null);
    }

    /**
     *
     * @param url
     * @param charset 传入null，响应有 charset则使用响应的charset,如果没有则使用系统默认的
     * @return
     * @throws ProjectException
     */
    public static String getResponseString(String url, Charset charset) throws ProjectException{
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = HttpClients.createDefault();
        return executeString(httpGet,httpClient,charset);
    }

    /**
     * 获取响应实体，可以使用流方式读取响应
     * @param url
     * @return
     * @throws IOException
     * @throws ProjectException
     */
    public static HttpEntity getResponseEntity(String url) throws IOException, ProjectException {
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse = httpClient.execute(httpGet);
        return getDecodeEntity(httpResponse.getEntity());
    }

    /**
     * 获取httpEntity的文本
     *
     * @param httpEntity 要获取的实体
     * @return 返回实体中的文本
     * @throws ProjectException 如果内容压缩方式不受支持，将抛出。如果读取时发生IO错误会抛出。
     */
    public static String getEntityString(HttpEntity httpEntity) throws ProjectException {
        return getEntityString(httpEntity,ContentType.getOrDefault(httpEntity).getCharset());
    }

    public static String getEntityString(HttpEntity httpEntity, Charset charset) throws ProjectException{
        httpEntity = getDecodeEntity(httpEntity);
        try {
            return EntityUtils.toString(httpEntity, charset);
        } catch (IOException e) {
            throw new ProjectIOException("读取字符串时发生错误!", e);
        }
    }

    public static String executeString(HttpUriRequest request,
                                       HttpContext context, HttpClient httpClient) throws ProjectException {
        return executeString(request,context,httpClient,null);
    }

    /**
     *
     * @param request
     * @param context
     * @param httpClient
     * @param charset 传入null，响应有 charset则使用响应的charset,如果没有则使用系统默认的
     * @return
     * @throws ProjectException
     */
    public static String executeString(HttpUriRequest request, HttpContext context,
                                       HttpClient httpClient, Charset charset) throws ProjectException{
        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(request, context)) {
            HttpEntity httpEntity = response.getEntity();
            if(charset == null){
                return HttpClientUtil.getEntityString(httpEntity);
            }
            return HttpClientUtil.getEntityString(httpEntity, charset);
        } catch (IOException e) {
            throw new ProjectIOException("读取数据时发生错误", e);
        }
    }

    public static String executeString(HttpUriRequest request, HttpClient httpClient) throws ProjectException {
       return executeString(request,httpClient, null);
    }

    /**
     *
     * @param request
     * @param httpClient
     * @param charset 传入null，响应有 charset则使用响应的charset,如果没有则使用系统默认的
     * @return
     * @throws ProjectException
     */
    public static String executeString(HttpUriRequest request, HttpClient httpClient,Charset charset) throws ProjectException{
        try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(request)) {
            HttpEntity httpEntity = response.getEntity();
            if(charset == null){
                return HttpClientUtil.getEntityString(httpEntity);
            }
            return HttpClientUtil.getEntityString(httpEntity,charset);
        } catch (IOException e) {
            throw new ProjectIOException("读取数据时发生错误", e);
        }
    }
    /**
     * 获取对应的解码的entity
     * @param httpEntity
     * @return
     * @throws ProjectException
     */
    public static HttpEntity getDecodeEntity(HttpEntity httpEntity) throws ProjectException {
        if (httpEntity.getContentEncoding() != null) {
            if ("gzip".equalsIgnoreCase(httpEntity.getContentEncoding().getValue())) {
                httpEntity = new GzipDecompressingEntity(httpEntity);
            } else if ("deflate".equalsIgnoreCase(httpEntity.getContentEncoding().getValue())) {
                httpEntity = new DeflateDecompressingEntity(httpEntity);
            } else if ("sdch".equalsIgnoreCase(httpEntity.getContentEncoding().getValue())) {
                logger.error("不支持的压缩方式:sdch");
                throw new ProjectUnsupportedEncodingException("不支持的压缩方式:sdch");
            } else {
                logger.error("未知的压缩方式:{0}", httpEntity.getContentEncoding().getValue());
                throw new ProjectUnsupportedEncodingException("未知的压缩方式:" + httpEntity.getContentEncoding().getValue());
            }
        }
        return httpEntity;
    }

    /**
     * 这个方法将安静的关闭httpClient，不会有任务异常。<br>
     * 这个方法调用了  HttpClientUtils.closeQuietly(httpClient) <br>
     * 这个工具类中提供了静默关闭其它对象的方法
     *
     * @param httpClient
     */
    public static void closeHttpClient(HttpClient httpClient) {
        HttpClientUtils.closeQuietly(httpClient);
    }

    /**
     * 为请求添加头<br>
     * 会往头集合中添加头，而不管原来有没有这个头，如果有的话，会出现多个同样的头。<br>
     * 这个方法不适合添加唯一头。要添加唯一头请用{@link HttpClientUtil#addHeadersOnly(HttpMessage, Header...)}
     *
     * @param httpMessage 请求
     * @param headers 头们
     * @return 原样返回请求
     */
    public static HttpMessage addHeaders(HttpMessage httpMessage, Header... headers) {
        for (Header h :
                headers) {
            httpMessage.addHeader(h);
        }
        return httpMessage;
    }

    /**
     * 向请求中添加头，如果头存在，将覆盖原来的头,如果存在多个同样的头，都会被移除，只会存在一个<br>
     * 如果想要某个头保存唯一，请用这个方法,否则使用{@link HttpClientUtil#addHeaders(HttpMessage, Header...)}
     *
     * @param httpMessage
     * @param headers
     * @return
     */
    public static HttpMessage addHeadersOnly(HttpMessage httpMessage, Header... headers) {
        for (Header h :
                headers) {
            httpMessage.removeHeaders(h.getName());
            httpMessage.addHeader(h);
        }
        return httpMessage;
    }
}
