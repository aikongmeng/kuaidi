package com.kuaibao.skuaidi.retrofit;

import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.util.MD5;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.socks.library.KLog;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：默认将返回的数据解析成需要的Bean,可以是 BaseBean，String，List，Map
 * 修订历史：
 * -
 * -
 * -
 * -
 * -我的注释都已经写的不能再多了,不要再来问我怎么获取数据对象,怎么解析集合数据了,你只要会 gson ,就会解析
 * -
 * -
 * -
 * ================================================
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //主要用于在所有请求之前添加公共的请求头或请求参数，例如登录授权的 token,使用的设备信息等,可以随意添加,也可以什么都不传
        String url=request.getUrl();
        String appendStr=url.indexOf("/"+Constant.VERSION+"/")>0? url.substring(url.indexOf("/"+Constant.VERSION+"/")):"";
        long ts = System.currentTimeMillis();
        StringBuffer buffer = new StringBuffer();
        buffer.append(ts).append(Constant.APP_KEY_V3).append(appendStr).append(Constant.APP_ID_V3);
        String signData=buffer.toString();
        String sign = new MD5().toMD5(signData);
        request.headers("Cookie", "session_id="+ SkuaidiSpf.getSessionId())
                .headers("version", SKuaidiApplication.VERSION_NAME)
                .params("app_id", Constant.APP_ID_V3)
                .params("ts", ts+"")
                .params("sign", sign);
        KLog.i("okGo","--------------------------------------------开始打印请求参数----------------------------------------------------");
        KLog.json("okGo",request.getUrlParam("data"));
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertSuccess(Response response) throws Exception {

        //以下代码是通过泛型解析实际参数,泛型必须传
//        Type genType = getClass().getGenericSuperclass();
//        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        JsonConvert<T> convert = new JsonConvert<>();
//        convert.setType(params[0]);
        T t = convert.convertSuccess(response);
        response.close();
        return t;
    }
}