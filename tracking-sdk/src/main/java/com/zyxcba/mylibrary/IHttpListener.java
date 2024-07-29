package com.zyxcba.mylibrary;

public interface IHttpListener {

    void onSuccess(int code, Object result);

    void onFailure(Throwable exception, String responseBody);
}
