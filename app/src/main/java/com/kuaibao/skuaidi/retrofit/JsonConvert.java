package com.kuaibao.skuaidi.retrofit;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.convert.Converter;
import com.socks.library.KLog;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class JsonConvert<T> implements Converter<T> {

//    private Type type;
//
//    public void setType(Type type) {
//        this.type = type;
//    }

    @Override
    public T convertSuccess(Response response) throws Exception {
        String responseStr=new String(response.body().bytes(),"UTF-8");
        KLog.i("okGo","--------------------------------------------开始打印返回数据----------------------------------------------------");
        KLog.json("okGo",responseStr);
//        JsonReader jsonReader = new JsonReader(response.body().charStream());
//        if (type == null) {
//            //以下代码是通过泛型解析实际参数,泛型必须传
//            Type genType = getClass().getGenericSuperclass();
//            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
//            type = params[0];
//        }
//        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");
//        Type rawType = ((ParameterizedType) type).getRawType();
//
//        //无数据类型
//        if (rawType == Void.class) {
//            SimpleResponse baseWbgResponse = Convert.fromJson(jsonReader, SimpleResponse.class);
//            //noinspection unchecked
//            return (T) baseWbgResponse.toLzyResponse();
//        }
//
//        //有数据类型
//        if (rawType == com.kuaibao.skuaidi.retrofit.api.entity.Response.class) {
//            com.kuaibao.skuaidi.retrofit.api.entity.Response lzyResponse = Convert.fromJson(jsonReader, type);
//            int code = lzyResponse.code;
//            if (code == 0) {
//                //noinspection unchecked
//                return (T) lzyResponse;
//            } else if (code == 1011 || code==1103 || code==5 || code==6 || code==401) {
//                throw new OkGoApiException("登录状态失效,请重新登录");
//            } else {
//                throw new OkGoApiException("错误代码:" + code + ",错误信息:" + lzyResponse.msg,lzyResponse);
//            }
//        }
//        throw new OkGoApiException("基类错误无法解析!");

        com.kuaibao.skuaidi.retrofit.api.entity.Response responseEntity= JSON.parseObject(responseStr, com.kuaibao.skuaidi.retrofit.api.entity.Response.class);
        if(responseEntity!=null){
            if (responseEntity.code == 0) {
                //noinspection unchecked
                return (T) responseEntity;
            } else if (responseEntity.code == 1011 || responseEntity.code==1103 || responseEntity.code==5 || responseEntity.code==6 || responseEntity.code==401 ) {
                //比如：用户授权信息无效，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new OkGoApiException("登录状态失效,请重新登录");
            }  else {
                throw new OkGoApiException("错误代码:" + responseEntity.code + ",错误信息:" + responseEntity.msg,responseEntity);
            }
        }else{
            throw new OkGoApiException("基类错误无法解析!");
        }
    }
}