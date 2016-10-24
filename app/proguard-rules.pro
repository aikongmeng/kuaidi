    #-applymapping {filename}    重用映射增加混淆
    #指定代码的压缩级别
    -optimizationpasses 5

    #包明不混合大小写
    -dontusemixedcaseclassnames

    #不去忽略非公共的库类
    -dontskipnonpubliclibraryclasses
    -dontskipnonpubliclibraryclassmembers
     #优化  不优化输入的类文件
    -dontoptimize
    -allowaccessmodification
     #预校验
    -dontpreverify

     #混淆时是否记录日志
    -verbose
    #-dontshrink

     # 混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

    #保护注解
    -keepattributes *Annotation*

    -keepattributes SourceFile,LineNumberTable

    # 保持哪些类不被混淆
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class com.android.vending.licensing.ILicensingService
    #如果有引用v4包可以添加下面这行
    -keep public class * extends android.support.v4.app.Fragment

-keep public class * extends android.os.Parcel
-keep public class * extends android.support.v4.view.ActionProvider

    #忽略警告
    -ignorewarning

    ##记录生成的日志数据,gradle build时在本项目根目录输出##

    #apk 包内所有 class 的内部结构
    -dump class_files.txt
    #未混淆的类和成员
    -printseeds seeds.txt
    #列出从 apk 中删除的代码
    -printusage unused.txt
    #混淆前后的映射
    -printmapping mapping.txt

    ########记录生成的日志数据，gradle build时 在本项目根目录输出-end######


    #####混淆保护自己项目的部分代码以及引用的第三方jar包library#######

    #-libraryjars libs/umeng-analytics-v5.2.4.jar

    #三星应用市场需要添加:sdk-v1.0.0.jar,look-v1.0.1.jar
    #-libraryjars libs/sdk-v1.0.0.jar
    #-libraryjars libs/look-v1.0.1.jar

    #如果不想混淆 keep 掉
    #-keep class com.lippi.recorder.iirfilterdesigner.** {*; }
    #友盟
    #-keep class com.umeng.**{*;}
    #项目特殊处理代码

    #忽略警告
    #-dontwarn com.lippi.recorder.utils**
    #保留一个完整的包
    #-keep class com.lippi.recorder.utils.** {
    #    *;
    # }

    #-keep class  com.lippi.recorder.utils.AudioRecorder{*;}


    #如果引用了v4或者v7包
    -dontwarn android.support.**
    -keep class android.support.**{*;}
    ####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####

    -keep public class * extends android.view.View {
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
        public void set*(...);
    }

    #保持 native 方法不被混淆
    -keepclasseswithmembernames class * {
        native <methods>;
    }

    #保持自定义控件类不被混淆
    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }

    #保持自定义控件类不被混淆
    -keepclassmembers class * extends android.app.Activity {
       public void *(android.view.View);
    }

-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
    #保持 Parcelable 不被混淆
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }

    #保持 Serializable 不被混淆
    -keepnames class * implements java.io.Serializable

    #保持 Serializable 不被混淆并且enum 类也不被混淆
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }

    #保持枚举 enum 类不被混淆 如果混淆报错，建议直接使用上面的 -keepclassmembers class * implements java.io.Serializable即可
    #-keepclassmembers enum * {
    #  public static **[] values();
    #  public static ** valueOf(java.lang.String);
    #}

    -keepclassmembers class * {
        public void *ButtonClicked(android.view.View);
    }

    #不混淆资源类
    -keepclassmembers class **.R$* {
        public static <fields>;
    }

    #避免混淆泛型 如果混淆报错建议关掉
    #–keepattributes Signature

    #移除log 测试了下没有用还是建议自己定义一个开关控制是否输出日志
#    -assumenosideeffects class android.util.Log {
#             public static boolean isLoggable(java.lang.String, int);
#             public static int v(...);
#             public static int i(...);
#             public static int w(...);
#             public static int d(...);
#             public static int e(...);
#         }

    #如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
    #gson
    #-libraryjars libs/gson-2.2.4.jar
    -keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
    # Gson specific classes
    -keep class sun.misc.Unsafe { *; }
    # Application classes that will be serialized/deserialized over Gson

    -dontwarn com.net.tsz.afinal.**
    -keep class com.net.tsz.afinal.**{*;}

    -dontwarn com.alipay.**
    -keep class com.alipay.**{*;}

    -dontwarn com.alipay.mobilesecurtitysdk.**
    -keep class com.alipay.mobilesecurtitysdk.**{*;}

    -dontwarn HttpUtils.**
    -keep class HttpUtils.**{*;}

    -dontwarn com.ta.utdid2.**
    -keep class com.ta.utdid2.**{*;}

    -dontwarn com.ut.device.**
    -keep class com.ut.device.**{*;}

    -dontwarn com.amap.api.services.**
    -keep class com.amap.api.services.**{*;}

    -dontwarn com.amap.api.**
    -keep class com.amap.api.**{*;}

    -dontwarn com.amap.api.location.**
    -keep class com.amap.api.location.**{*;}

    -dontwarn com.aps.**
    -keep class com.aps.**{*;}

    -dontwarn com.baidu.**
    -keep class com.baidu.**{*;}

    -dontwarn pvi.com.gdi.bgl.android.**
    -keep class pvi.com.gdi.bgl.android.**{*;}

    -dontwarn vi.com.gdi.bgl.android.**
    -keep class vi.com.gdi.bgl.android.**{*;}

    -dontwarn com.android.barcodecontroll.**
    -keep class com.android.barcodecontroll.**{*;}

    -keep class butterknife.** { *; }
    -dontwarn butterknife.internal.**
    -keep class **$$ViewBinder { *; }

    -keepclasseswithmembernames class * {
        @butterknife.* <fields>;
    }

    -keepclasseswithmembernames class * {
        @butterknife.* <methods>;
    }


    -dontwarn org.apache.commons.lang.**
    -keep class org.apache.commons.lang.**{*;}

    -dontwarn org.apache.commons.io.**
    -keep class org.apache.commons.io.**{*;}
    -dontwarn org.apache.commons.cli.**
    -keep class org.apache.commons.cli.**{*;}

    -dontwarn com.google.zxing.**
    -keep class com.google.zxing.**{*;}

    -dontwarn org.greenrobot.eventbus.**
    -keep class org.greenrobot.eventbus.**{*;}

    -dontwarn com.pocketdigi.utils.**
    -keep class com.pocketdigi.utils.**{*;}

    -dontwarn com.google.gson.**
    -keep class com.google.gson.**{*;}

    -dontwarn org.apache.http.entity.mime.**
    -keep class org.apache.http.entity.mime.**{*;}

    -dontwarn org.kobjects.**
    -keep class org.kobjects.**{*;}

    -dontwarn org.ksoap2.**
    -keep class org.ksoap2.**{*;}

    -dontwarn org.kxml2.**
    -keep class org.kxml2.**{*;}

    #-dontwarn org.xmlpull.v1.**
    #-keep class org.xmlpull.v1.**{*;}

    -dontwarn com.iData.ledcontroll.**
    -keep class com.iData.ledcontroll.**{*;}

    -dontwarn iData.**
    -keep class iData.**{*;}

    -dontwarn com.iflytek.**
    -keep class com.iflytek.**{*;}

    -dontwarn uk.co.senab.photoview.**
    -keep class uk.co.senab.photoview.**{*;}

    -dontwarn com.hp.hpl.sparta.**
    -keep class com.hp.hpl.sparta.**{*;}

    -dontwarn demo.**
    -keep class demo.**{*;}

    -dontwarn net.sourceforge.pinyin4j.**
    -keep class net.sourceforge.pinyin4j.**{*;}

    -dontwarn pinyindb.**
    -keep class pinyindb.**{*;}

    -dontwarn pinyindb.**
    -keep class pinyindb.**{*;}

    -dontwarn com.tencent.**
    -keep class com.tencent.**{*;}

    -dontwarn com.umeng.**
    -keep class com.umeng.**{*;}

    -dontwarn com.sina.sso.**
    -keep class com.sina.sso.**{*;}

    -dontwarn Decoder.**
    -keep class Decoder.**{*;}

    -dontwarn com.iflytek.sunflower.**
    -keep class com.iflytek.sunflower.**{*;}

    -dontwarn com.nostra13.universalimageloader.**
    -keep class com.nostra13.universalimageloader.** {*; }

    -dontwarn com.yuntongxun.ecsdk.**
    -keep class com.yuntongxun.ecsdk.** {*; }

    #不要混淆MyPushMessageReceiver的所有属性与方法 里面有反射!
#    -keepclasseswithmembers class com.kuaibao.skuaidi.service.MyPushMessageReceiver {
#        <fields>;
#        <methods>;
#    }

     -keepclassmembers class com.kuaibao.skuaidi.service.MyPushMessageReceiver {
            public void showNotification(**);
     }


    -keepclassmembers class ** {
    public void onEvent*(**);
    void onEvent*(**);
    }

     -dontwarn com.socks.klog.**
     -keep class com.socks.klog.** {*; }

     -dontwarn com.socks.library.**
     -keep class com.socks.library.** {*; }

     -dontwarn retrofit2.adapter.rxjava.**
     -keep class retrofit2.adapter.rxjava.** {*; }

     -dontwarn retrofit2.converter.gson.**
     -keep class retrofit2.converter.gson.** {*; }

     -dontwarn retrofit2.converter.jackson.**
     -keep class retrofit2.converter.jackson.** {*; }

     -dontwarn com.bumptech.glide.**
     -keep class com.bumptech.glide.** {*; }

     -dontwarn com.squareup.haha.**
     -keep class com.squareup.haha.** {*; }

     -dontwarn org.hamcrest.**
     -keep class org.hamcrest.** {*; }



     -dontwarn com.squareup.**
     -keep class com.squareup.** {*; }

     -dontwarn com.squareup.leakcanary.**
     -keep class com.squareup.leakcanary.** {*; }

     -dontwarn okhttp3.**
     -keep class okhttp3.** {*; }

     -dontwarn okio.**
     -keep class okio.** {*; }

     -dontwarn retrofit2.**
     -keep class retrofit2.** {*; }

     -dontwarn rx.android.**
     -keep class rx.android.** {*; }

     -dontwarn rx.**
     -keep class rx.** {*; }

-keep class com.alibaba.sdk.android.feedback.** {*;}

-keep class com.alibaba.wireless.security.** {*;}
-keep class com.alibaba.sdk.android.ut.impl.** {*;}

-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-keep class com.ut.** {*;}
-dontwarn com.ut.**
-keep class com.ta.** {*;}
-dontwarn com.ta.**
-keep class ta.** {*;}
-keep class ut.** {*;}
-keep class sdk.** {*;}
-keep class android.app.** {*;}
-keep class org.apache.http.** {*;}
-dontwarn org.apache.http.**

-keep class org.apache.http.entity.mime.** {*;}
-dontwarn org.apache.http.entity.mime.**

-keep class com.hannesdorfmann.mosby.mvp.** {*;}
-dontwarn com.hannesdorfmann.mosby.mvp.**

-keep class de.greenrobot.dao.** {*;}
-dontwarn de.greenrobot.dao.**

 -keepclassmembers class com.kuaibao.skuaidi.activity.nettelephone.NetTelePhoneActivity {
            public void disableShowSoftInput(**);
     }
-keep class com.yqritc.recyclerviewflexibledivider.** {*;}
-dontwarn  com.yqritc.recyclerviewflexibledivider.**
-keep class com.wangjie.shadowviewhelper.** {*;}
-dontwarn  com.wangjie.shadowviewhelper.**

#    3D 地图
#    -keep   class com.amap.api.mapcore.**{*;}
#
#    -keep   class com.amap.api.maps.**{*;}
#
#    -keep   class com.autonavi.amap.mapcore.*{*;}

#    定位

   -keep class com.amap.api.location.**{*;}

   -keep class com.amap.api.fence.**{*;}

   -keep class com.autonavi.aps.amapapi.model.**{*;}

   -keep class com.loc.**{*;}


#    搜索

    -keep   class com.amap.api.services.**{*;}


#    2D地图

    -keep class com.amap.api.maps2d.**{*;}

    -keep class com.amap.api.mapcore2d.**{*;}


#    导航

    -keep class com.amap.api.navi.**{*;}

    -keep class com.autonavi.**{*;}

    -keep class printpp.printpp_yt.**{*;}

    # AndFix
    -keep class * extends java.lang.annotation.Annotation
    -keepclasseswithmembernames class * { native <methods>; }
    -keep class com.alipay.euler.andfix.** { *; }

    -keep class com.thin.downloadmanager.** { *; }

-keep class com.alibaba.sdk.android.feedback.** {*;}
-keep public class com.alibaba.mobileim.gingko.model.provider.WXProvider.DatabaseHelper { public void onDowngrade(android.database.sqlite.SQLiteDatabase,int,int);}
-keep public class * implements android.os.Parcelable { public static final android.os.Parcelable$Creator *;}



-keep class com.alibaba.mobileim.YWUIAPI{
    public <methods>;
}

-keep class com.alibaba.mobileim.YWSDK {*;}
-keep class com.alibaba.mobileim.YWIMKit {*;}
-keep class com.alibaba.mobileim.WXAPI {*;}
-keep class com.alibaba.mobileim.ui.chat.widget.ChattingRecordView {*;}
-keep interface com.alibaba.mobileim.IYWUIPushListener {*;}
-keep interface com.alibaba.mobileim.ui.chat.widget.IRecordCallback {*;}

-keep interface com.alibaba.mobileim.IYWConversationItemClickListener {*;}
-keep class com.alibaba.mobileim.aop.** {*;}
-keep class com.alibaba.mobileim.ui.chat.ChattingHandlerManager {*;}
-keep class com.alibaba.mobileim.ui.chat.AbstractCustomHandler {*;}


-keep class * implements java.io.Serializable {*;}
-keep interface com.alibaba.tcms.IPushManager {*;}
-keep interface com.alibaba.tcms.PushListener {*;}
-keep interface com.alibaba.vconn.ChannelConnectionListener {*;}
-keep class * implements com.alibaba.tcms.IPushManager {*;}
-keep class com.alibaba.tcms.service.TCMPush {*;}
-keep class com.alibaba.tcms.service.ITCMPushListener {*;}
-keep class com.alibaba.tcms.service.TCMSService {*;}
-keep class com.alibaba.tcms.TCMResult {*;}
-keep class com.alibaba.tcms.TCMSBroadcastReceiver {*;}
-keep class com.alibaba.tcms.DefaultClientManager {*;}
-keep class com.alibaba.tcms.NtfCommandVO {*;}
-keep class com.alibaba.tcms.client.ResultMessage {*;}
-keep class com.alibaba.tcms.client.ClientRegInfo {*;}
-keep class com.alibaba.tcms.action.param.** {*;}
-keep class com.alibaba.tcms.track.** {*;}
-keep class com.alibaba.tcms.utils.** {*;}
-keep class com.alibaba.vconn.** {*;}
-keep class com.alibaba.util.** {*;}
-keep class com.alibaba.tcms.notice.** {*;}
-keep class com.alibaba.tcms.env.** {*;}
-keep class com.alibaba.tcms.PushConstant {*;}
-keep class com.alibaba.tcms.PersistManager {*;}
-keep class com.alibaba.mobileim.channel.IMChannel {
    android.app.Application sApp;
}

-keep class * implements com.alibaba.mobileim.channel.itf.ItfPacker {*;}

-keep class com.alibaba.mobileim.channel.itf.mimsc.** {*;}

-keep class com.alibaba.mobileim.channel.itf.mpcsc.** {*;}
-keep class com.alibaba.mobileim.channel.service.InetIO {*;}
-keep interface com.alibaba.mobileim.aop.Advice {*;}
-keep class * implements com.alibaba.mobileim.aop.Advice {*;}

#okhttputils
-dontwarn com.zhy.http.**
-keep class com.zhy.http.**{*;}

#eventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keepclassmembers class * {
    void *(**On*Event);
}

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keep class org.greenrobot.greendao.** {*;}
-keep class org.greenrobot.greendao.generator.** {*;}
#保持greenDao的方法不被混淆
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
#用来保持生成的表名不被混淆
-keep class **$Properties

-keep class com.kuaibao.skuaidi.activity.model.** {*;}
-keep class com.kuaibao.skuaidi.business.entity.** {*;}
-keep class com.kuaibao.skuaidi.business.nettelephone.entity.** {*;}
-keep class com.kuaibao.skuaidi.activity.recordcount.sms_yunhu.entity.** {*;}
-keep class com.kuaibao.skuaidi.activity.template.sms_yunhu.entry.** {*;}
-keep class com.kuaibao.skuaidi.activity.template.sms_yunhu.entry.** {*;}
-keep class com.kuaibao.skuaidi.application.bugfix.model.PatchBean {*;}
-keep class com.kuaibao.skuaidi.dispatch.bean.** {*;}
-keep class com.kuaibao.skuaidi.entry.** {*;}
-keep class com.kuaibao.skuaidi.json.entry.** {*;}
-keep class com.kuaibao.skuaidi.retrofit.api.entity.** {*;}
-keep class com.kuaibao.skuaidi.sto.etrhee.bean.** {*;}
-keep class com.kuaibao.skuaidi.activity.wallet.entity.**{*;}
-keep class com.kuaibao.skuaidi.circle.entity.**{*;}
-keep class com.kuaibao.skuaidi.customer.entity.**{*;}
-keep class com.kuaibao.skuaidi.dispatch.DispatchlEvent
-keep class com.kuaibao.skuaidi.payali.**{*;}
-keep class com.kuaibao.skuaidi.personal.personinfo.entity.ResponseBranch
-keep class com.kuaibao.skuaidi.wheelview.widget.ItemsRange
-keep class gen.greendao.bean.**{*;}

-keep class com.facebook.** {*;}
-keep class javax.annotation.** {*;}
-keep class com.fasterxml.jackson.** {*;}
-keep class freemarker.** {*;}

-keep class com.alipay.euler.andfix.AndFix {*;}
-keep class com.alipay.euler.andfix.annotation.MethodReplace {*;}
-keep class * extends java.lang.annotation.Annotation
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.kuaibao.skuaidi.greendao.helper.** {*;}
-keep class gen.greendao.** {*;}
-keep class kaicom.android.app.** {*;}
-keep class com.motorolasolutions.adc.decoder.** {*;}
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }
-keep class com.kuaibao.skuaidi.qrcode.CaptureActivity {*;}
-keep class com.kuaibao.skuaidi.litepal.entry.** {*;}
# litepal 数据库
#-libraryjars libs/litepal-1.1.1.jar
-dontwarn org.litepal.*
-keep class org.litepal.** { *; }
-keep enum org.litepal.**
-keep interface org.litepal.** { *; }
-keep public class * extends org.litepal.**
-keepattributes *Annotation*
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * extends org.litepal.crud.DataSupport{*;}
#手机号码识别
-keep class com.kuaibao.skuaidi.activity.scan_mobile.tesseract.ui.TesseractMobileActivity{*;}
-keep class com.googlecode.leptonica.android.** { *; }
-keep class com.googlecode.tesseract.android.** { *; }
#fastjson
-dontwarn android.support.**
-dontwarn com.alibaba.fastjson.**
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
-keep class com.alibaba.fastjson.** { *; }
-keepclassmembers class * {
public <methods>;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
    public <fields>;
}
-keepattributes Signture

 #okgo
    -dontwarn com.lzy.okgo.**
    -keep class com.lzy.okgo.**{*;}

    #okrx
    -dontwarn com.lzy.okrx.**
    -keep class com.lzy.okrx.**{*;}

    #okserver
    -dontwarn com.lzy.okserver.**
    -keep class com.lzy.okserver.**{*;}

    #okhttp
    -dontwarn okhttp3.**
    -keep class okhttp3.**{*;}

    #okio
    -dontwarn okio.**
    -keep class okio.**{*;}

    -keepclassmembers class com.kuaibao.skuaidi.util.Utility {
        public static int getStatusBarHeight(**);
    }

    -keepclassmembers class com.kuaibao.skuaidi.activity.scan_mobile.execute.PlatformSupportManager {
        public T build();
    }
    -keepclassmembers class com.kuaibao.skuaidi.business.nettelephone.util.NetTeleUtils {
        public static void clearTextLineCache();
    }
    -keepclassmembers class com.kuaibao.skuaidi.qrcode.camera.FlashlightManager {
        private static Class<?> maybeForName(**);
    }
    -keepclassmembers class com.alipay.euler.andfix.AndFix {
        public static Class<?> initTargetClass(**);
    }
    -keepclassmembers class com.alipay.euler.andfix.AndFixManager {
        public synchronized void fix(**);
    }
    -keepclassmembers class com.alipay.euler.andfix.Compat {
        private static boolean isYunOS();
    }
    -keepclassmembers class com.blog.www.guideview.Guide {
        private MaskView onCreateView(**);
    }
    -keepclassmembers class cn.bingoogolapple.photopicker.imageloader.BGAImage {
        private static final boolean isClassExists(**);
    }

    -keep class com.kuaibao.skuaidi.service.BuildProperties{*;}
    -keep class com.kuaibao.skuaidi.service.RomUtils{*;}

    -keep class com.kuaibao.skuaidi.greendao.helper.MigrationHelper{*;}
    -keep class com.kuaibao.skuaidi.greendao.helper.MySQLiteOpenHelper{*;}