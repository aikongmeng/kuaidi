package com.kuaibao.skuaidi.retrofit;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.kuaibao.skuaidi.BuildConfig;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.retrofit.api.entity.ResponseExtra;
import com.kuaibao.skuaidi.util.MD5;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;
import com.lzy.okgo.OkGo;
import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by ligg on 2016/6/7 14:48.
 * Copyright (c) 2016, gangyu79@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */
public class OkHttpFactory {
    //private static volatile OkHttpClient sOkHttpClient;
    public  static final String TAG="okHttp";
    // 配置OkHttpClient
    public static OkHttpClient getOkHttpClient() {
//        if (sOkHttpClient == null) {
//            synchronized (OkHttpFactory.class) {
//                if (sOkHttpClient == null) {
//                    // OkHttpClient配置是一样的,静态创建一次即可
//                    //OkHttpClient.Builder builder=new OkHttpClient.Builder();
//                    OkGo.getInstance().getOkHttpClientBuilder().addInterceptor(new Interceptor() {// 在header中添加session
//                        @Override
//                        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
//                            Request original = chain.request();
//                            Request request=null;
//                            String url=original.url().toString();
//                            Request.Builder requestBuilder = original.newBuilder();
//                            String cookie = "session_id="+ SkuaidiSpf.getSessionId();
//                            requestBuilder.header("Cookie", cookie).header("version", SKuaidiApplication.VERSION_NAME);
//                            if(url.contains(HostType.V3_BASE_URL)){
//                                String appendStr=url.indexOf("/"+Constant.VERSION+"/")>0? url.substring(url.indexOf("/"+Constant.VERSION+"/")):"";
//                                if (original.body() instanceof FormBody) {
//                                    FormBody.Builder newFormBody = new FormBody.Builder();
//                                    FormBody oidFormBody = (FormBody) original.body();
//                                    JSONObject jsonData=new JSONObject();
//                                    ResponseExtra extra=new ResponseExtra();
//                                    extra.setsName(appendStr);
//                                    for (int i = 0; i < oidFormBody.size(); i++) {
//                                        if("jsonType".equals(oidFormBody.name(i))){
//                                            extra.setJsonType(oidFormBody.value(i));
//                                            continue;
//                                        }
//                                        if(JSON_TYPE.JSON_TYPE_ERROR!=getJSONType(oidFormBody.value(i))){
//                                            jsonData.put(oidFormBody.name(i), JSON.parse(oidFormBody.value(i)));
//                                        }else{
//                                            jsonData.put(oidFormBody.name(i),oidFormBody.value(i));
//                                        }
//                                    }
//                                    requestBuilder.tag(extra);
//                                    long ts = System.currentTimeMillis();
//                                    StringBuffer buffer = new StringBuffer();
//                                    buffer.append(ts).append(Constant.APP_KEY_V3).append(appendStr).append(Constant.APP_ID_V3);
//                                    String signData=buffer.toString();
//                                    String sign = new MD5().toMD5(signData);
//                                    newFormBody.add("app_id", Constant.APP_ID_V3);
//                                    newFormBody.add("ts", ts + "");
//                                    newFormBody.add("sign", sign);
//                                    newFormBody.add("data",jsonData.toJSONString());
//                                    FormBody newBody=newFormBody.build();
//                                    requestBuilder.method(original.method(), newBody);
//                                    request = requestBuilder.build();
//                                }else{
//                                    KLog.i(TAG,"original.body() not instanceof FormBody");
//                                    requestBuilder.method(original.method(), original.body());
//                                    request = requestBuilder.build();
//                                }
//                            }else if(url.contains(HostType.DTS_LOGIN_URL)){//老版接口
//                                if (original.body() instanceof FormBody) {
//                                    FormBody.Builder newFormBody = new FormBody.Builder();
//                                    FormBody oidFormBody = (FormBody) original.body();
//                                    JSONObject jsonData=new JSONObject();
//                                    ResponseExtra extra=new ResponseExtra();
//                                    for (int i = 0; i < oidFormBody.size(); i++) {
//                                        if("jsonType".equals(oidFormBody.name(i))){
//                                            extra.setJsonType(oidFormBody.value(i));
//                                            continue;
//                                        }
//                                        if("sname".equals(oidFormBody.name(i))){
//                                            extra.setsName(oidFormBody.value(i));
//                                            jsonData.put(oidFormBody.name(i),oidFormBody.value(i));
//                                            continue;
//                                        }
//                                        if(JSON_TYPE.JSON_TYPE_ERROR!=getJSONType(oidFormBody.value(i))){
//                                            jsonData.put(oidFormBody.name(i),JSON.parse(oidFormBody.value(i)));
//                                        }else{
//                                            jsonData.put(oidFormBody.name(i),oidFormBody.value(i));
//                                        }
//                                    }
//                                    jsonData.put("pname","androids");
//                                    requestBuilder.tag(extra);
//                                    newFormBody.add("content", jsonData.toJSONString());
//                                    newFormBody.add("token",new MD5().toMD5(jsonData.toJSONString() + "bac500a42230c8d7d1820f1f1fa9b578"));
//                                    FormBody newBody=newFormBody.build();
//                                    requestBuilder.method(original.method(), newBody);
//                                    request = requestBuilder.build();
//                                }else{
//                                    KLog.i(TAG,"original.body() not instanceof FormBody");
//                                    requestBuilder.method(original.method(), original.body());
//                                    request = requestBuilder.build();
//                                }
//                            }
//                            if(BuildConfig.DEBUG && request!=null){
//                                KLog.i(TAG,"请求地址Url:--->"+request.url().toString());
//                                KLog.i(TAG,"headers:--->"+request.headers().toString());
//                                if(request.body() instanceof  FormBody){
//                                    FormBody logBody=(FormBody) request.body();
//                                    JSONObject jsonObject=new JSONObject();
//                                    for (int i = 0; i < logBody.size(); i++) {
//                                        if(JSON_TYPE.JSON_TYPE_ERROR!=getJSONType(logBody.value(i))){
//                                            jsonObject.put(logBody.name(i),JSON.parse(logBody.value(i)));
//                                        }else{
//                                            jsonObject.put(logBody.name(i),logBody.value(i));
//                                        }
//                                    }
//                                    KLog.v(TAG,"--------------------------------------------开始打印请求参数----------------------------------------------------");
//                                    KLog.json(TAG,jsonObject.toJSONString());
//                                    //KLog.v(TAG,"--------------------------------------------结束打印请求参数----------------------------------------------------");
//                                }
//                            }
//                            return chain.proceed(request==null? original:request);
//                        }
//                    });
//                    OkGo.getInstance().getOkHttpClientBuilder().addInterceptor(mResponseInterceptor);
//                    if(BuildConfig.DEBUG) {
//                        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
//                            @Override
//                            public void log(String message) {
//                                //KLog.i("HttpLog",message);
//                            }
//                        });
//                        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                        OkGo.getInstance().getOkHttpClientBuilder().addInterceptor(interceptor);
//                        OkGo.getInstance().getOkHttpClientBuilder().addNetworkInterceptor(new StethoInterceptor());
//                    }
//                    //网络缓存路径文件
//                    File cacheFile = new File(SKuaidiApplication.getContext().getCacheDir(), "httpCache");
//                    Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
//                    OkGo.getInstance().getOkHttpClientBuilder().addNetworkInterceptor(new HttpCacheInterceptor());
//                    OkGo.getInstance().getOkHttpClientBuilder().cache(cache);
//                    sOkHttpClient=builder.readTimeout(30000, TimeUnit.MILLISECONDS)
//                            .connectTimeout(30000, TimeUnit.MILLISECONDS)
//                            .addNetworkInterceptor(new HttpCacheInterceptor())
//                            .cache(cache)
//                            .build();
//                    sOkHttpClient=OkGo.getInstance().getOkHttpClient();
//                }
//            }
//        }
        return OkGo.getInstance().getOkHttpClient();
    }

    public static void initOkGoClient(){
        OkGo.getInstance().getOkHttpClientBuilder().addInterceptor(new Interceptor() {// 在header中添加session
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                Request request=null;
                String url=original.url().toString();
                Request.Builder requestBuilder = original.newBuilder();
                String cookie = "session_id="+ SkuaidiSpf.getSessionId();
                requestBuilder.header("Cookie", cookie).header("version", SKuaidiApplication.VERSION_NAME);
                if(url.contains(HostType.V3_BASE_URL)){
                    String appendStr=url.indexOf("/"+Constant.VERSION+"/")>0? url.substring(url.indexOf("/"+Constant.VERSION+"/")):"";
                    if (original.body() instanceof FormBody) {
                        FormBody.Builder newFormBody = new FormBody.Builder();
                        FormBody oidFormBody = (FormBody) original.body();
                        JSONObject jsonData=new JSONObject();
                        ResponseExtra extra=new ResponseExtra();
                        extra.setsName(appendStr);
                        for (int i = 0; i < oidFormBody.size(); i++) {
                            if("jsonType".equals(oidFormBody.name(i))){
                                extra.setJsonType(oidFormBody.value(i));
                                continue;
                            }
                            if(JSON_TYPE.JSON_TYPE_ERROR!=getJSONType(oidFormBody.value(i))){
                                jsonData.put(oidFormBody.name(i), JSON.parse(oidFormBody.value(i)));
                            }else{
                                jsonData.put(oidFormBody.name(i),oidFormBody.value(i));
                            }
                        }
                        requestBuilder.tag(extra);
                        long ts = System.currentTimeMillis();
                        StringBuffer buffer = new StringBuffer();
                        buffer.append(ts).append(Constant.APP_KEY_V3).append(appendStr).append(Constant.APP_ID_V3);
                        String signData=buffer.toString();
                        String sign = new MD5().toMD5(signData);
                        newFormBody.add("app_id", Constant.APP_ID_V3);
                        newFormBody.add("ts", ts + "");
                        newFormBody.add("sign", sign);
                        newFormBody.add("data",jsonData.toJSONString());
                        FormBody newBody=newFormBody.build();
                        requestBuilder.method(original.method(), newBody);
                        request = requestBuilder.build();
                    }else{
                        KLog.i(TAG,"original.body() not instanceof FormBody");
                        requestBuilder.method(original.method(), original.body());
                        request = requestBuilder.build();
                    }
                }else if(url.contains(HostType.DTS_LOGIN_URL)){//老版接口
                    if (original.body() instanceof FormBody) {
                        FormBody.Builder newFormBody = new FormBody.Builder();
                        FormBody oidFormBody = (FormBody) original.body();
                        JSONObject jsonData=new JSONObject();
                        ResponseExtra extra=new ResponseExtra();
                        for (int i = 0; i < oidFormBody.size(); i++) {
                            if("jsonType".equals(oidFormBody.name(i))){
                                extra.setJsonType(oidFormBody.value(i));
                                continue;
                            }
                            if("sname".equals(oidFormBody.name(i))){
                                extra.setsName(oidFormBody.value(i));
                                jsonData.put(oidFormBody.name(i),oidFormBody.value(i));
                                continue;
                            }
                            if(JSON_TYPE.JSON_TYPE_ERROR!=getJSONType(oidFormBody.value(i))){
                                jsonData.put(oidFormBody.name(i),JSON.parse(oidFormBody.value(i)));
                            }else{
                                jsonData.put(oidFormBody.name(i),oidFormBody.value(i));
                            }
                        }
                        jsonData.put("pname","androids");
                        requestBuilder.tag(extra);
                        newFormBody.add("content", jsonData.toJSONString());
                        newFormBody.add("token",new MD5().toMD5(jsonData.toJSONString() + "bac500a42230c8d7d1820f1f1fa9b578"));
                        FormBody newBody=newFormBody.build();
                        requestBuilder.method(original.method(), newBody);
                        request = requestBuilder.build();
                    }else{
                        KLog.i(TAG,"original.body() not instanceof FormBody");
                        requestBuilder.method(original.method(), original.body());
                        request = requestBuilder.build();
                    }
                }
                if(BuildConfig.DEBUG && request!=null){
                    KLog.i(TAG,"请求地址Url:--->"+request.url().toString());
                    KLog.i(TAG,"headers:--->"+request.headers().toString());
                    if(request.body() instanceof  FormBody){
                        FormBody logBody=(FormBody) request.body();
                        JSONObject jsonObject=new JSONObject();
                        for (int i = 0; i < logBody.size(); i++) {
                            if(JSON_TYPE.JSON_TYPE_ERROR!=getJSONType(logBody.value(i))){
                                jsonObject.put(logBody.name(i),JSON.parse(logBody.value(i)));
                            }else{
                                jsonObject.put(logBody.name(i),logBody.value(i));
                            }
                        }
                        KLog.v(TAG,"--------------------------------------------开始打印请求参数----------------------------------------------------");
                        KLog.json(TAG,jsonObject.toJSONString());
                        //KLog.v(TAG,"--------------------------------------------结束打印请求参数----------------------------------------------------");
                    }
                }
                return chain.proceed(request==null? original:request);
            }
        });
        OkGo.getInstance().getOkHttpClientBuilder().addInterceptor(mResponseInterceptor);
        if(BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    //KLog.i("HttpLog",message);
                }
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkGo.getInstance().getOkHttpClientBuilder().addInterceptor(interceptor);
            OkGo.getInstance().getOkHttpClientBuilder().addNetworkInterceptor(new StethoInterceptor());
        }
        //网络缓存路径文件
        File cacheFile = new File(SKuaidiApplication.getContext().getCacheDir(), "httpCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 100); //100Mb
        OkGo.getInstance().getOkHttpClientBuilder().addNetworkInterceptor(new HttpCacheInterceptor());
        OkGo.getInstance().getOkHttpClientBuilder().cache(cache);
    }

    static final class HttpCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {

            Request request = chain.request();
            if (!Utility.isNetworkAvailable(SKuaidiApplication.getContext())) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
                KLog.d(TAG, "no network");
            }
            okhttp3.Response originalResponse = chain.proceed(request);
            if (Utility.isNetworkAvailable(SKuaidiApplication.getContext())) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                        .removeHeader("Pragma")
                        .build();
            }
        }
    }

    // 打印返回的json数据拦截器
    private static Interceptor mResponseInterceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            final Request request = chain.request();
            final okhttp3.Response response = chain.proceed(request);
            final ResponseBody responseBody = response.body();
            final long contentLength = responseBody.contentLength();

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(charset);
                } catch (UnsupportedCharsetException e) {
                    KLog.e(TAG,"Couldn't decode the response body; charset is likely malformed.");
                    return response;
                }
            }
            ResponseExtra extra=null;
            if(request.tag() instanceof ResponseExtra){
                 extra= (ResponseExtra) (request.tag());
                if(extra!=null && !TextUtils.isEmpty(extra.getsName()) && extra.getsName().contains("Wduser/login")){
                    //登录接口,保存sessionId
                    Headers headers=response.headers();
                    String session=headers.get("Set-Cookie");
                    if(!TextUtils.isEmpty(session) && session.length()>=46){
                        String sessionId=session.substring(11, 46);
                        SkuaidiSpf.setSessionId(TextUtils.isEmpty(sessionId)?"":sessionId);
                        KLog.i("kb","sessionId:---->"+sessionId);
                    }
                }
            }
            if (contentLength != 0) {
                String jsonStr=buffer.clone().readString(charset);
                //KLog.i(TAG,"request Tag：--->"+extra.toString()+";indexOf data []:---->"+jsonStr.indexOf("\"data\":[]"));
                if(extra!=null && "JsonObject".equals(extra.getJsonType())){
                    if(jsonStr.indexOf("\"data\":[]")>0){
                        jsonStr=jsonStr.replace("\"data\":[]","\"data\":{}");//解决PHP返回的JSONObject不统一的问题
                        KLog.v(TAG,"--------------------------------------------开始打印返回数据(已转换[])----------------------------------------------------");
                        KLog.json(TAG,jsonStr);
                        //KLog.v(TAG,"--------------------------------------------结束打印返回数据(已转换[])----------------------------------------------------");
                    }else{
                        KLog.v(TAG,"--------------------------------------------开始打印返回数据----------------------------------------------------");
                        KLog.json(TAG,jsonStr);
                        //KLog.v(TAG,"--------------------------------------------结束打印返回数据----------------------------------------------------");
                    }
                }else{
                    KLog.v(TAG,"--------------------------------------------开始打印返回数据----------------------------------------------------");
                    KLog.json(TAG,jsonStr);
                    //KLog.v(TAG,"--------------------------------------------结束打印返回数据----------------------------------------------------");
                }
                JSONObject jsonResponse=JSON.parseObject(jsonStr);
                if(extra!=null){
                    jsonResponse.put("sname",TextUtils.isEmpty(extra.getsName())?"":extra.getsName());
                }
                return response.newBuilder().body(ResponseBody.create(responseBody.contentType(),jsonResponse.toJSONString())).build();
            }else{
                return response;
            }
        }
    };

    /***
     * 获取JSON类型
     * 判断第一个字母是否为{或[ 如果都不是则不是一个JSON格式的文本
     */
    public enum JSON_TYPE{
        /**JSONObject*/
        JSON_TYPE_OBJECT,
        /**JSONArray*/
        JSON_TYPE_ARRAY,
        /**不是JSON格式的字符串*/
        JSON_TYPE_ERROR
    }
    public static JSON_TYPE getJSONType(String str){
        if(TextUtils.isEmpty(str)){
            return JSON_TYPE.JSON_TYPE_ERROR;
        }
        final char[] strChar = str.substring(0, 1).toCharArray();
        final char firstChar = strChar[0];
        if(firstChar == '{'){
            return JSON_TYPE.JSON_TYPE_OBJECT;
        }else if(firstChar == '['){
            return JSON_TYPE.JSON_TYPE_ARRAY;
        }else{
            return JSON_TYPE.JSON_TYPE_ERROR;
        }
    }
}
