package com.kuaibao.skuaidi.api;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.util.MD5;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import //KLog.LogUtils;

public class HttpHelper {

    private OnResultListener mListener;
    public final static String FAILD_INFO = "连接失败";// 连接失败
    public final static String TIME_OUT = "连接超时";// 连接超时
    private Handler handler;
    public static final int SERVICE_V1 = 3;
    private static final String APP_KEY_V1 = "4accd1296e8f514627a411e4e2fbfc5f";
    private static final String APP_ID_V1 = "10002";
    private static final String URL_V1 = "http://api.kuaidihelp.com";
    private static final String V1 = "v1";

    public HttpHelper(Context context) {
    }

    public HttpHelper(OnResultListener listener, Handler handler) {
        mListener = listener;
        this.handler = handler;

    }

    public interface OnResultListener {
        void onSuccess(String result, String sname);

        void onFail(String result, JSONObject data_fail, String code);
    }

    public void getPart(final JSONObject data) {
        //Log.i("iii", data.toString());

        if (!Utility.isNetworkConnected()) {
            if (mListener != null) {
                SKuaidiApplication.getInstance().sendMessage(mListener, 99, "无网络连接,请检查网络设置!", "");
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String sname = data.getString("sname");
                        final String result = getHttp(data);
                        if (result.equals("error")) {
                            //Log.i("iii", "error");
                            if (mListener != null) {
                                SKuaidiApplication.getInstance().sendMessage(mListener, 99, FAILD_INFO, "");
                            }
                        } else {
                            //Log.i("iii", sname + "result:" + result);
                            if (mListener != null) {
                                SKuaidiApplication.getInstance().sendMessage(mListener, 98, result, sname);
                            }
                        }
                    } catch (Exception e) {
                        //Log.i("iii", "timeout");
                        if (mListener != null) {
                            SKuaidiApplication.getInstance().sendMessage(mListener, 99, TIME_OUT, "");
                        }

                    }
                }
            }).start();
        }
    }

    private String getHttp(JSONObject data) throws Exception {
        String result = "";
        String url = "http://dts.kuaidihelp.com/api.php";
        String key = "bac500a42230c8d7d1820f1f1fa9b578";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("content", data.toString()));
        params.add(new BasicNameValuePair("token", new MD5().toMD5(data.toString() + key)));
        HttpPost httpPost = new HttpPost(url);
        HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        httpPost.setEntity(entity);
        httpPost.addHeader("version", SKuaidiApplication.VERSION_NAME);
        KLog.d("kb", "请求地址:--->url=" + url);
        KLog.json("kb", data.toString());
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            result = EntityUtils.toString(response.getEntity(), "UTF-8");
        } else {
            result = String.valueOf(response.getStatusLine().getStatusCode());
        }
        KLog.i("kb", "接口返回#############################################################");
        KLog.json("kb", result);
        return result;
    }

    @SuppressWarnings("rawtypes")
    private String getHttp(JSONObject data, Map<String, String> head) throws Exception {
        String result = "";
        long ts = System.currentTimeMillis();
        String url = "http://dts.kuaidihelp.com/api.php";
        String key = "bac500a42230c8d7d1820f1f1fa9b578";
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("content", data.toString()));
        params.add(new BasicNameValuePair("token", new MD5().toMD5(data.toString() + key)));
        HttpPost httpPost = new HttpPost(url);
        HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        httpPost.setEntity(entity);
        httpPost.addHeader("version", SKuaidiApplication.VERSION_NAME);
        Iterator iter = head.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            httpPost.addHeader("Cookie", entry.getKey().toString() + "=" + entry.getValue().toString());
            //Log.i("session_id", entry.getValue().toString());
        }
        KLog.d("kb", "请求地址:--->url=" + url);
        KLog.json("kb", data.toString());
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        HttpResponse response = httpClient.execute(httpPost);
        Header[] hs = response.getAllHeaders();
        SKuaidiApplication.getInstance().postMsg("login", "login_res_header", hs);
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (java.lang.OutOfMemoryError e) {
                e.printStackTrace();
                //System.out.println("可能因为溢出");
            }

        } else {
            result = String.valueOf(response.getStatusLine().getStatusCode());
        }
        KLog.i("kb", "接口返回#############################################################");
        KLog.json("kb", result);
        return result;
    }

    @SuppressWarnings("rawtypes")
    private String getHttpV3(JSONObject data) throws Exception {
        String result = "";
        long ts = System.currentTimeMillis();
        StringBuffer buffer = new StringBuffer();
        buffer.append(ts).append(APP_KEY_V1).append("/" + V1 + "/" + data.optString("sname").replace(".", "/"))
                .append(APP_ID_V1);
        String sign = new MD5().toMD5(buffer.toString());
        String url = URL_V1 + "/" + V1 + "/" + data.optString("sname").replace(".", "/");
        //Log.d("url", url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("app_id", APP_ID_V1));
        params.add(new BasicNameValuePair("ts", ts + ""));
        params.add(new BasicNameValuePair("sign", sign));
        params.add(new BasicNameValuePair("data", Uri.encode(data.toString())));

        KLog.d("kb", "请求地址:--->url=" + url);
        KLog.json("kb",  data.toString());
        KLog.json(data.toString());

        HttpPost httpPost = new HttpPost(url);
        HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
        httpPost.setEntity(entity);
        httpPost.addHeader("version", SKuaidiApplication.VERSION_NAME);
        Iterator iter = Utility.getSession_id(SKuaidiApplication.getInstance()).entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            httpPost.addHeader("Cookie", entry.getKey().toString() + "=" + entry.getValue().toString());
            //Log.i("session_id", entry.getValue().toString());
        }
//		if(data.optString("sname","").equals("launcher")){
//			httpPost.addHeader("E-Tag", SkuaidiSpf.getSplashVersion(SKuaidiApplication.getInstance()));
//			KLog.d("add launcher header:--->E-Tag="+SkuaidiSpf.getSplashVersion(SKuaidiApplication.getInstance()));
//		}
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
        HttpResponse response = httpClient.execute(httpPost);
        Header[] hs = response.getAllHeaders();

//		if(data.optString("sname","").equals("launcher")){
//			KLog.d("launcher response Header:--->");
//			KLog.d(hs);
//			SKuaidiApplication.getInstance().postMsg("launcher", "launcher_splash_header", hs);
//		}else{
        SKuaidiApplication.getInstance().postMsg("login", "login_res_header", hs);
        //}
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                result = EntityUtils.toString(response.getEntity(), "UTF-8");
            } catch (java.lang.OutOfMemoryError e) {
                e.printStackTrace();
                //System.out.println("可能因为溢出");
            }

        } else {
            result = String.valueOf(response.getStatusLine().getStatusCode());
        }
        KLog.json("kb", result);
        return result;
    }


    public void getPartV3(final JSONObject data) {
        //Log.i("iii", data.toString());
        if (!Utility.isNetworkConnected()) {
            if (mListener != null) {
                SKuaidiApplication.getInstance().sendMessage(mListener, 99, "无网络连接,请检查网络设置!", "");
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String sname = data.getString("sname");
                        // final String result = getHttpV3(data);
                        final String result = getHttpV3(data);

                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (result.equals("error")) {
                                    //Log.i("iii", "error");
                                    if (mListener != null) {
                                        SKuaidiApplication.getInstance().sendMessage(mListener, 99, FAILD_INFO, "");
                                    }
                                } else {
                                    //Log.i("iii", sname + " result:" + result);
                                    if (mListener != null) {
                                        SKuaidiApplication.getInstance().sendMessage(mListener, 98, result, sname);
                                    }
                                }
                            }
                        });

                    } catch (Exception e) {
                        //Log.i("iii", "timeout");
                        if (mListener != null) {
                            SKuaidiApplication.getInstance().sendMessage(mListener, 99, TIME_OUT, "");
                        }
                    }
                }
            }).start();
        }
    }

    public void getPart(final JSONObject data, final Map<String, String> head) {
        //Log.i("iii", data.toString());
        if (!Utility.isNetworkConnected()) {
            if (mListener != null) {
                SKuaidiApplication.getInstance().sendMessage(mListener, 99, "无网络连接,请检查网络设置!", "");
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String sname = data.getString("sname");
                        final String result = getHttp(data, head);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                if (result.equals("error")) {
                                    //Log.i("iii", "error");
                                    if (mListener != null) {
                                        SKuaidiApplication.getInstance().sendMessage(mListener, 99, FAILD_INFO, "");
                                    }
                                } else {
                                    //Log.i("iii", sname + " result:" + result);
                                    if (mListener != null) {
                                        SKuaidiApplication.getInstance().sendMessage(mListener, 98, result, sname);
                                    }
                                }
                            }
                        });

                    } catch (Exception e) {
                        //Log.i("iii", "timeout");
                        if (mListener != null) {
                            SKuaidiApplication.getInstance().sendMessage(mListener, 99, TIME_OUT, "");
                        }
                    }
                }
            }).start();
        }
    }
}
