package com.schedule.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http基础公用类
 * <p>
 * Created by fengwei.cfw on 2017/10/9.
 */
@Component
public class HttpRequest {

    private static PoolingHttpClientConnectionManager cm = null;

    private static Logger LOGGER = LoggerFactory.getLogger(HttpRequest.class);

    /*---------------- 基础连接池管理（begin） ----------------*/
    private static CloseableHttpClient getHttpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5000)//从连接池获取连接的timeout
                .setConnectTimeout(5000)//客户端和服务器建立连接的timeout
                .setSocketTimeout(5000)//客户端从服务器读取数据的timeout
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .build();
        return httpClient;
    }

    @PostConstruct
    public void PostConstruct() {
        LoggerUtil.info(LOGGER, "程序启动，创建http连接池");
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("创建SSL连接失败");
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(20);
    }

    @PreDestroy
    public void PreDestory() throws IOException {
        LoggerUtil.info(LOGGER, "程序关闭，销毁http连接池");
        cm.close();
    }
    /*---------------- 基础连接池管理（end） ----------------*/

    /**
     * post 请求，默认json格式
     *
     * @param url  请求地址
     * @param body json消息体
     * @return
     */
    public static HttpResult post(String url, String body) {
        return post(url, new StringEntity(body, ContentType.APPLICATION_JSON));
    }

    /**
     * post请求，可自定义body格式
     *
     * @param url         请求地址
     * @param body        内容字符串
     * @param contentType 内容格式
     * @return
     */
    public static HttpResult post(String url, String body, ContentType contentType) {
        return post(url, new StringEntity(body, contentType));
    }

    /**
     * post请求，通过 kv 请求
     *
     * @param url
     * @param nameValueMaps
     * @return
     */
    public static HttpResult post(String url, Map<String, String> nameValueMaps) {
        List<BasicNameValuePair> formparams = new ArrayList<>();
        if (nameValueMaps != null && nameValueMaps.size() > 0) {
            for (String key : nameValueMaps.keySet()) {
                formparams.add(new BasicNameValuePair(key, nameValueMaps.get(key)));
            }
        }
        return post(url, new UrlEncodedFormEntity(formparams, Consts.UTF_8));
    }

    /**
     * post请求，上传文件
     *
     * @param url      上传目标地址
     * @param fileByte 文件byte数组
     * @param fileName 文件名
     * @return
     */
    public static HttpResult post(String url, byte[] fileByte, String fileName) {
        MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create().addPart("file", new ByteArrayBody(fileByte, fileName));
        return post(url, reqEntity.build());
    }

    /**
     * post请求基础方法
     *
     * @param url    请求地址
     * @param entity 请求体
     * @return
     */
    private static HttpResult post(String url, HttpEntity entity) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        return execute(httpPost);
    }

    /**
     * get请求
     *
     * @param url   请求地址
     * @param param url后面跟的参数
     * @return
     */
    public static HttpResult get(String url, String param) {
        HttpResult result = new HttpResult();
        try {
            String uri = StringUtils.isNotBlank(param) ? url + "?" + URLEncoder.encode(param, "UTF-8") : url;
            HttpGet httpGet = new HttpGet(uri);
            return execute(httpGet);
        } catch (IOException e) {
            return result;
        }
    }

    /**
     * put通用请求
     *
     * @param url   目标地址
     * @param param url后面跟的参数
     * @param body  请求body对象，json格式
     * @return
     */
    public static HttpResult put(String url, String param, String body) {
        HttpResult result = new HttpResult();
        try {
            String uri = StringUtils.isNotBlank(param) ? url + "?" + URLEncoder.encode(param, "UTF-8") : url;
            HttpPut httpPut = new HttpPut(uri);
            httpPut.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
            return execute(httpPut);
        } catch (IOException e) {
            return result;
        }
    }

    /**
     * delete请求
     *
     * @param url   请求地址
     * @param param url后面跟的参数
     * @return
     */
    public static HttpResult delete(String url, String param) {
        HttpResult result = new HttpResult();
        try {
            String uri = StringUtils.isNotBlank(param) ? url + "?" + URLEncoder.encode(param, "UTF-8") : url;
            HttpDelete httpDelete = new HttpDelete(uri);
            return execute(httpDelete);
        } catch (IOException e) {
            return result;
        }
    }

    /**
     * http执行基础方法
     *
     * @param requestBase
     * @return
     */
    private static HttpResult execute(HttpRequestBase requestBase) {
        HttpResult result = new HttpResult();
        CloseableHttpClient httpClient = HttpRequest.getHttpClient();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(requestBase);
            result.setStatusCode(httpResponse.getStatusLine().getStatusCode());
            if (null != httpResponse.getEntity() && result.getStatusCode() == HttpStatus.SC_OK) {
                result.setBody(EntityUtils.toString(httpResponse.getEntity(), Consts.UTF_8));
            }
        } catch (IOException e) {
            LOGGER.error("httpclient请求失败", e);
        } finally {
            if (httpResponse != null) {
                try {
                    EntityUtils.consume(httpResponse.getEntity());
                    httpResponse.close();
                } catch (IOException e) {
                    LOGGER.error("关闭response失败", e);
                }
            }
            return result;
        }
    }
}
