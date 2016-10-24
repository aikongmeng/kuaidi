package com.kuaibao.skuaidi.retrofit;

import android.graphics.Bitmap;
import android.util.SparseArray;

import com.kuaibao.skuaidi.retrofit.api.APIService;
import com.kuaibao.skuaidi.retrofit.api.entity.Response;
import com.kuaibao.skuaidi.retrofit.util.ClippingPicture;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ligg on 2016/4/18 15:30.
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
public class RetrofitUtil {

    private APIService service;
    public static final String TAG = "okHttp";

    // 管理不同HostType的单例
    private static SparseArray<RetrofitUtil> sInstanceManager = new SparseArray<>(HostType.TYPE_COUNT);

    public static APIService getService(int hostType) {
        return getInstance(hostType).service;
    }

    public RetrofitUtil() {

    }

    public RetrofitUtil(@HostType.HostTypeChecker int hostType) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(HostType.getHost(hostType))
                .client(OkHttpFactory.getOkHttpClient()).addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();

        service = retrofit.create(APIService.class);
    }

    /**
     * 获取单例
     *
     * @param hostType host类型
     * @return 实例
     */
    public static RetrofitUtil getInstance(int hostType) {
        RetrofitUtil instance = sInstanceManager.get(hostType);
        if (instance == null) {
            instance = new RetrofitUtil(hostType);
            sInstanceManager.put(hostType, instance);
            return instance;
        } else {
            return instance;
        }
    }


    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response
     */
    public static <T> Observable<T> flatResponse(final Response<T> response) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (response.isSuccess()) {
                    //KLog.i("rx","response.isSuccess()");
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(response.data);
                        //KLog.i("rx","RetrofitUtil onNext");
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new APIException(response.code, response.msg, response.sname));
                    }
                    return;
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }

            }
        });
    }


    /**
     * 自定义异常，当接口返回的{@link Response#code}不为{@link Constant#OK}时，需要跑出此异常
     * eg：登陆时验证码错误；参数为传递等
     */
    public static class APIException extends Exception {
        public int code;
        public String msg;
        public String sname;

        public APIException(int code, String msg, String sname) {
            this.code = code;
            this.msg = msg;
            this.sname = sname;
        }

        @Override
        public String getMessage() {
            return msg;
        }

        @Override
        public String toString() {
            return "APIException{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", sname='" + sname + '\'' +
                    '}';
        }
    }

    /**
     * Transformer实际上就是一个Func1<Observable<T>, Observable<R>>，
     * 换言之就是：可以通过它将一种类型的Observable转换成另一种类型的Observable，
     * 和调用一系列的内联操作符是一模一样的。
     */
    protected <T> Observable.Transformer<Response<T>, T> applySchedulersNoFlatMap() {
        return (Observable.Transformer<Response<T>, T>) schedulersTransformer;
    }

    final Observable.Transformer schedulersTransformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    protected <T> Observable.Transformer<Response<T>, T> applySchedulers() {
        return (Observable.Transformer<Response<T>, T>) transformer;
    }

    final Observable.Transformer transformer = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1() {
                        @Override
                        public Object call(Object response) {
                            return flatResponse((Response<Object>) response);
                        }
                    })
                    ;
        }
    };

    protected <T> Observable.Transformer<Response<T>, T> applySchedulersIO() {
        return (Observable.Transformer<Response<T>, T>) transformerIO;
    }

    final Observable.Transformer transformerIO = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1() {
                        @Override
                        public Object call(Object response) {
                            return flatResponse((Response<Object>) response);
                        }
                    })
                    ;
        }
    };

    /**
     * 当{@link APIService}中接口的注解为{@link retrofit2.http.Multipart}时，参数为{@link RequestBody}
     * 生成对应的RequestBody
     */
    protected RequestBody createRequestBody(int param) {
        return RequestBody.create(MediaType.parse("text/plain"), String.valueOf(param));
    }

    protected RequestBody createRequestBody(long param) {
        return RequestBody.create(MediaType.parse("text/plain"), String.valueOf(param));
    }

    protected RequestBody createRequestBody(String param) {
        return RequestBody.create(MediaType.parse("text/plain"), param);
    }

    protected RequestBody createRequestBody(File param) {
        return RequestBody.create(MediaType.parse("image/*"), param);
    }

    /**
     * 已二进制传递图片文件，对图片文件进行了压缩
     */
    protected RequestBody createPictureRequestBody(String path) {
        Bitmap bitmap = ClippingPicture.decodeResizeBitmapSd(path, 400, 800);
        return RequestBody.create(MediaType.parse("image/*"), ClippingPicture.bitmapToBytes(bitmap));
    }
}
