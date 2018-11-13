package com.schedule.util;

/**
 * http协议返回请求体
 */
public class HttpResult {

    private int statusCode;

    private Object body;

    public HttpResult() {
        super();
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "statusCode=" + statusCode +
                ", body=" + body +
                '}';
    }
}
