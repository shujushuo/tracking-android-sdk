package com.shujushuo.app;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.content.SharedPreferences;

import com.github.gzuliyujiang.oaid.DeviceIdentifier;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class AnalysisAAABBB {

    private final static String TAG = "AnalysisAAABBB";

    private static Context pContext;

    private static final String SDK_VERSION = "1.0.0";
    public static String UPLOADURL = "https://tracking.wdyxgames.com/up";
    public static final int CONNECT_TIMEOUT = 5000;
    public static final int READ_TIMEOUT = 5000;

    public interface EVENT {
        int INSTALL = 1;
        int START = 2;
        int LOGIN = 3;
        int REGISTER = 4;
        int PAY = 5;
    }

    private static TimerTask pTimerTask = null;

    private static Timer pHBTimer = new Timer(true);

    private final static int HB_TIMER_INTERVAL = 15000;

    private static String pChannel = null;
    private static String pAppid = null;

    public static synchronized void init(Context application) {
        try {
            if (application == null) {
                Log.e(TAG, "preInit param Context can not be null");
                return;
            }

            pContext = application;
            DispatchHandler.getInstance();
            LogUtil.debug(TAG, "--------------- module preInit success ---------------");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static synchronized void start(String appid, String channel) {
        try {
            pChannel = channel;
            pAppid = appid;
            DatabaseUtil.getInstance(pContext);
            startHeartBeat(pContext);
            trackEvent(EVENT.INSTALL, null);
            trackEvent(EVENT.START, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArrayList<String> sendingList = new ArrayList<>();

    private static void trackEvent(int event, JSONObject obj) {
        try {
            DispatchHandler.getInstance().submit(
                    new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Context tempContext = pContext;
                                if (null == tempContext) {
                                    return;
                                }
                                String whatString = null;
                                switch (event) {
                                    case EVENT.INSTALL: {
                                        SharedPreferences sharedPreferences = tempContext.getSharedPreferences("ANALYSISAAABBB", MODE_PRIVATE);
                                        int suc = sharedPreferences.getInt("install_success", 0);
                                        if (suc == 1) {
                                            return;
                                        }
                                        whatString = "install";
                                    }
                                    break;
                                    case EVENT.START: {
                                        whatString = "startup";
                                    }
                                    break;
                                    case EVENT.LOGIN: {
                                        whatString = "login";
                                        if (obj != null) {
                                            String userName = obj.getString("who");
                                            if (userName != null && userName.length() > 0) {
                                                SharedPreferences sharedPreferences = tempContext.getSharedPreferences("ANALYSISAAABBB", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("username", userName);
                                                editor.apply();
                                            }
                                        }
                                    }
                                    break;
                                    case EVENT.REGISTER: {
                                        whatString = "register";
                                    }
                                    break;
                                    case EVENT.PAY: {
                                        whatString = "pay";
                                    }
                                    break;

                                }
                                String tempAppid = pAppid;
                                String tempChannel = pChannel;
                                if (tempAppid == null || tempAppid.length() == 0) {
                                    return;
                                }
                                if (whatString == null || whatString.length() == 0) {
                                    return;
                                }
                                JSONObject root = new JSONObject();
                                root.put("appid", tempAppid);
                                root.put("xwhen", System.currentTimeMillis());

                                root.put("xwhat", whatString);
                                SharedPreferences sharedPreferences = tempContext.getSharedPreferences("ANALYSISAAABBB", MODE_PRIVATE);
                                String whoString = "unknown";//sharedPreferences.getString("install_success", "unknown");
                                root.put("xwho", whoString);

                                if (tempChannel != null && tempChannel.length() == 0) {
                                    root.put("channel", tempChannel);
                                }


                                JSONObject context = new JSONObject();
                                context.put("gaid", getGAID(pContext));
                                context.put("androidid", getAndroidId(pContext));
                                context.put("oaid", getOAID(pContext));
                                context.put("os", "android");
                                context.put("osversion", Build.VERSION.RELEASE);
                                context.put("model", Build.MODEL);
                                context.put("pkgname", getPkgName(pContext));
                                context.put("tz", getTimeZone());
                                context.put("sdkver", SDK_VERSION);
                                context.put("installid", UUID.randomUUID().toString());

                                root.put("xcontext", context);
                                long rid = addDataToDB(event + "", root);
                                if (rid >= 0) {
                                    sendRequest(pContext, root, rid, event);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(Context context, JSONObject root, long rid, int event) {
        synchronized (AnalysisAAABBB.class) {
            sendingList.add(rid + "");
        }

        LogUtil.debug(TAG, "event id:" + rid +
                "   sending  event:" + root.toString());
        final IHttpListener jsonhandler = new IHttpListener() {
            @Override
            public void onFailure(Throwable exception, String responseBody) {
                try {
                    Log.e(TAG, "event id:" + rid +
                            " https error with exception:" + exception.getMessage() + " response:" + responseBody);
                    synchronized (AnalysisAAABBB.class) {
                        sendingList.remove(rid + "");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int respCode, Object responseBody) {
                try {
                    Log.i(TAG, "event id:" + rid +
                            "  send  success");
                    synchronized (AnalysisAAABBB.class) {
                        sendingList.remove(rid + "");
                    }
                    deleteDBData(rid);
                    Context tempContext = pContext;
                    if (null == tempContext) {
                        return;
                    }
                    switch (event) {
                        case EVENT.INSTALL:
                            SharedPreferences sharedPreferences = tempContext.getSharedPreferences("ANALYSISAAABBB", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("install_success", 1);
                            editor.apply();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        HttpAAABBB.postJson(pContext, AnalysisAAABBB.UPLOADURL, root, 3,
                jsonhandler);
    }


    private static long addDataToDB(final String what, JSONObject record) {
        try {
            final byte[] byteDataArr = jsonObj2ByteArray(record);
            ContentValues values = new ContentValues();
            values.put("what", what);
            values.put("value", byteDataArr);

            DatabaseUtil dbUtil = DatabaseUtil.getInstance(pContext);
            long result = dbUtil.insert(values);
            return result;
        } catch (Exception e) {
            LogUtil.debug(TAG, "Exception in addRecordToDbase:" + e.getMessage());
            return -1;
        }
    }

    private static ArrayList getDBData(int count) {
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance(pContext);
            ArrayList data = dbUtil.queryWithLimit(count);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void deleteDBData(long rid) {
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance(pContext);
            dbUtil.deleteFromById(String.valueOf(rid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void stopHeartBeat() {
        LogUtil.debug(TAG, "stop heart beat function called");
        if (pHBTimer != null) {
            pHBTimer.cancel();
            pHBTimer = null;
        }
        if (pTimerTask != null) {
            pTimerTask.cancel();
            pTimerTask = null;
        }
    }

    private static void startHeartBeat(Context context) {
        pContext = context;
        if (pContext == null) {
            return;
        }
        stopHeartBeat();
        pHBTimer = new Timer(true);
        pTimerTask = new TimerTask() {
            @Override
            public void run() {
                sendFaildDataFromHB(10);
            }
        };
        try {
            pHBTimer.schedule(pTimerTask, 1000, HB_TIMER_INTERVAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendFaildDataFromHB(final int record_count) {
        //LogUtil.debug(TAG, "heart beat...");
        try {
            ArrayList data = getDBData(record_count);
            if (data == null || data.size() == 0) {
                LogUtil.debug(TAG, "heart beat data size == 0");
            } else {
                mDBHandler.sendMessage(mDBHandler.obtainMessage(1, record_count, 0, data));
            }
        } catch (Exception e) {
            LogUtil.debug(TAG, "send faild data exp:" + e.getMessage());
        }
    }

    private static Handler mDBHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            final ArrayList<DatabaseUtil.DBDataItem> data = (ArrayList<DatabaseUtil.DBDataItem>) msg.obj;
            for (DatabaseUtil.DBDataItem item : data) {
                sendRequest(pContext, item.json, item.id, -1);
            }
        }
    };

    private static byte[] jsonObj2ByteArray(JSONObject obj) {
        byte[] bytes = null;
        if (obj != null) {
            try {
                bytes = obj.toString().getBytes("utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    public static String getGAID(Context context) {
        try {
            AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            if (adInfo != null) {
                return adInfo.getId();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public static String getAndroidId(Context context) {
        String androidid = "unknown";
        try {
            androidid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (androidid == null || androidid.length() == 0) {
            androidid = "unknown";
        }
        return androidid;
    }


    public static String getOAID(Context context) {
        String oaid = "unknown";
        try {

            oaid = DeviceIdentifier.getOAID(context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (oaid == null || oaid.length() == 0) {
            oaid = "unknown";
        }
        return oaid;
    }

    public static String getTimeZone() {
        String result = "";
        try {
            int time_zone = TimeZone.getDefault().getRawOffset() / 1000 / 3600;
            if (time_zone > 0) {
                result = "+" + time_zone;
            } else {
                result = "" + time_zone;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getPkgName(Context context) {
        if (context == null) {
            return "error";
        }
        return context.getPackageName();
    }
}
