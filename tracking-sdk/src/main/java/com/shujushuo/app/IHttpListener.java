package com.shujushuo.app;

public interface IHttpListener {

    void onSuccess(int code, Object result);

    void onFailure(Throwable exception, String responseBody);
}
