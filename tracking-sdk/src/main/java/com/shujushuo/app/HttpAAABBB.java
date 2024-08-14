package com.shujushuo.app;


import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpAAABBB {
    private final static String TAG = "AnalysisAAABBB";
    private HttpAAABBB() {
    }

    public static void postJson(Context context, String url,
                                JSONObject jsonEntity, int repeat, IHttpListener responseHandler) {
        Runnable postRunnable = HttpAAABBB.postJson(url, jsonEntity.toString(), repeat, responseHandler);

        DispatchHandler.getInstance().submit(postRunnable);
    }

    public static Runnable postJson(String url, String data, int repeat,
                                    IHttpListener responseHandler) {
        PostRunnable runnable = new PostRunnable(url, null, data, repeat, responseHandler);
        return runnable;
    }

    public static class PostRunnable implements Runnable {
        private String mUrl;
        private String mData;
        private IHttpListener mResponseHandler;

        public PostRunnable(String url, String contentType, String data, int repeatCount,
                            IHttpListener responseHandler) {
            mUrl = url;
            mData = data;
            mResponseHandler = responseHandler;
        }

        public PostRunnable(String url, String contentType, String data,
                            IHttpListener responseHandler) {
            this(url, contentType, data, 1, responseHandler);
        }

        @Override
        public void run() {
            try {
                LogUtil.debug(TAG, "start post url "+mUrl);

                URL target = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) target.openConnection();
                conn.addRequestProperty("Content-Type", "application/json");

                conn.setConnectTimeout(AnalysisAAABBB.CONNECT_TIMEOUT);
                conn.setReadTimeout(AnalysisAAABBB.READ_TIMEOUT);
                conn.setRequestMethod("POST");
                if (mData != null) {
                    LogUtil.debug(TAG, "request data:\n" + mData);

                    conn.setDoOutput(true);
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                    dos.write(mData.getBytes("UTF-8"));
                    dos.flush();
                    dos.close();
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                int code = conn.getResponseCode();
                if (HttpURLConnection.HTTP_OK == code) {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String temp = br.readLine();
                        if (temp == null)
                            break;
                        sb.append(temp);
                    }
                    String sbString = sb.toString();

                    JSONObject json = new JSONObject();
                    json.put("status",sbString);

                    LogUtil.debug(TAG, "response data:" + json.toString());

                    int statusCode = json.optInt("status", -1);

                    mResponseHandler.onSuccess(statusCode, json);
                } else {
                    mResponseHandler.onFailure(new Exception("http request failed!"), "response Code=" + code);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mResponseHandler.onFailure(e, "response Code=" + -2);
            }
            LogUtil.debug(TAG, "---------------   request finish   ---------------");
        }
    }

}
