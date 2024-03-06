package com.example.editanywhere.utils;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.example.editanywhere.entity.view.SimpleResponse;

import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OKHttpUtil {

    public static final MediaType MIME_JSON = MediaType.get("application/json; charset=utf-8");
    public static final String server_address = "http://192.168.31.79:8080";
    private static final OkHttpClient client = new OkHttpClient();
    private static final String TAG = "OKHttpUtil";
    private static Application application;

    public static void init(Activity activity) {
        if (activity != null) {
            application = activity.getApplication();
        }
    }

    public static String getUrl() {
        String res = server_address;
        if (application != null) {
            res = SPUtil.getString(application, SPUtil.TAG_SERVER_ADDRESS, server_address);
        }
        return res;
    }

    public static void resetUrl() {
        if (application != null) {
            SPUtil.putString(application, SPUtil.TAG_SERVER_ADDRESS, server_address);
        }
    }

    /**
     * todo 使用thrift 进行统一的接口规定
     *
     * @param body
     * @param
     * @param
     * @return
     * @throws
     */
    public static void post(String api, Map<String, Object> body, OkHttpCallBack callBack) {

        new Thread(() -> {
            try {
                RequestBody requestBody = RequestBody.create(JSON.toJSONString(body), MIME_JSON);
                String url = getUrl() + api;
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                if (response.body() != null) {
                    String res = response.body().string();
                    SimpleResponse simpleResponse = JSON.parseObject(res, SimpleResponse.class);
                    if (simpleResponse != null) {
                        if (simpleResponse.getCode() == 0) {
                            callBack.onSuccess(simpleResponse.getData().toString());
                        } else {
                            callBack.onError(simpleResponse.getMsg());
                        }
                    }
                } else {
                    callBack.onError(response.message());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "post: error " + e.getMessage());
                callBack.onError(e.getMessage());
            }
        }).start();

    }

    public static boolean isValidIpv4(String ip) {
        if ((ip != null) && (!ip.isEmpty())) {
            return Pattern.matches("^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$", ip);
        }
        return false;
    }

    public static boolean isValidPort(String port) {
        //端口号验证 1 ~ 65535
        if (port != null) {
            String regex = "^([1-9]|[1-9]\\d{1,3}|[1-6][0-5][0-5][0-3][0-5])$";
            return Pattern.matches(regex, port);
        }
        return false;
    }


}
