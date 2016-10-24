package com.kuaibao.skuaidi.util;

import android.content.Context;
import android.os.Environment;

public class Constants {
    public Context context;

    public Constants(Context context) {
        this.context = context;
    }

    public static String SPACE = "";
    // URL和路径
    public static String ROOT = Environment.getExternalStorageDirectory()
            .getAbsolutePath();
    // 缓存根目录
    public static final String RECORDER_PATH = ROOT + "/skuaidi/voice/";
    public static final String PICTURE_PATH = ROOT + "/skuaidi/pic/";
    public static final String HEADER_PATH = ROOT + "/skuaidi/header/";
    public static final String LOCAL_RECORD_PATH = ROOT + "/skuaidi/audiorecord/";
    //public static final String TEMP_HEADER_PATH = HEADER_PATH + "temp/";
    public static final String SPLASH_PATH = ROOT + "/skuaidi/splash/";
    public static final String CALL_RECORDING_PATH = ROOT
            + "/skuaidi/call_recording/";
    // 根路径URL
    public static String URL_IMG_ROOT = "http://upload.kuaidihelp.com/liuyan/";// 图片
    public static final String URL_RECORDER_ROOT = "http://upload.kuaidihelp.com/liuyan/new/";// 路径(留言图片)
    public static final String URL_HEADER_ROOT = "http://upload.kuaidihelp.com/touxiang/";
    public static final String URL_RECORDER_ORDER_ROOT = "http://upload.kuaidihelp.com/order_im/";
    public static final String URL_TUCAO_ROOT = "http://upload.kuaidihelp.com/kuaidiyuan_tucao/";// 快递员吐槽图片(图片名字前面加上"thumb."则是缩略图的地址)
    public static final String URL_MY_SHOP_IMG_ROOT = "http://upload.kuaidihelp.com/weishop/";// 我的店铺中所有关于图片的地址（图片名字前面加上"thumb."则是缩略图的地址）
    public static final String URL_MY_PRICE_IMG_ROOT = "http://upload.kuaidihelp.com/cm_price/";// 我的价格单图片下载地址
    public static final String URL_OUTSIDE_IMG = "http://upload.kuaidihelp.com/kuaidiyuan_weirenwu/";//外块中需求图片地址

    // 功能介绍
    public static final String URL_FUNCTION_DEFAULT = "http://m.kuaidihelp.com/help/android";// 默认
    public static final String URL_FUNCTION_DEFAULT_STO = "http://m.kuaidihelp.com/help/android_sto";// 申通
    // 短信和云呼通知详情
    public static final String URL_NOTIFY_SEND_MSG = "http://m.kuaidihelp.com/help/broadcast?type=sms&mobile=";
    public static final String URL_NOTIFY_YUNHU = "http://m.kuaidihelp.com/help/broadcast?mobile=";
    // 关于严禁发送违法短信内容的公告
    public static final String URL_SEND_WEIFA_DUANXIN_TONGZHI = "http://m.kuaidihelp.com/help/app_notice.html";
    // 审核帮助
    public static final String CHECK_HELP = "http://m.kuaidihelp.com/help/yewuyuan";
    public static final String CHECK_KUAIDI = "http://wdadmin.kuaidihelp.com/";
    // 云呼相关
    public static final String CLOUD_VOICE_HELP = "http://m.kuaidihelp.com/help/yh_voice";// 云呼语音帮助
    // 免责声明
    public static final String DISCLAIMER = "http://m.kuaidihelp.com/help/userPro";
    // 更多-设置-账户设置-注销
    public static final String CANCELLATION_APP = "http://m.kuaidihelp.com/wduser/courierFeedback?mobile=";
    // 短信VIP特权协议
    public static final String VIP_PRIVILEGE_FOR_SMS = "http://m.kuaidihelp.com/help/vipPro";

    // 支付相关
    public static final String MAN_MADE_REGISTER = "http://m.kuaidihelp.com/help/manual_reg";//人工注册说明
    public static final String USE_MODEL_EXPLAIN = "http://m.kuaidihelp.com/explain/url4";//使用模板说明
    public static final String ACTIVITY_EXPLAIN = "http://m.kuaidihelp.com/explain/url2";// 支付金额及补贴充活动说明
    public static final String BIND_BAIDU_ACCOUNT = "http://m.kuaidihelp.com/binding/?phone=";// 绑定百度账号
    public static final String ZHIFU_QRCODE = "http://m.kuaidihelp.com/order/show/?order_no=";// 支付二维码url（需要加上订单号）
    public static final String BUSINESS_ZHIFU_QRCODE = "http://m.kuaidihelp.com/order/show/?cm_id=";// 我的名片二维码url（需要加上用户user_id）
    public static final String MY_ACCOUNT_CHONGZHI = "http://m.kuaidihelp.com/order/pay?cm_id=";// 充值url（需要加上用户user_id）
    public static final String SPRING_FESTIVAL_MESG_MARKETING = "http://m.kuaidihelp.com/cm_mkt/mktsms?sess_id=";// 群发春节营销短信
    public static final String GUN_SCAN_DESC = "http://m.kuaidihelp.com/help/bqscan.html";// 短信界面巴枪扫描说明
    public static final String POST_PAY_URL = "http://m.kuaidihelp.com/order/payurl";// 请求支付的url-百度扫码和支付宝扫码充值
    public static final String RECEIVE_MONEY_FOR_BUSINESS = "http://m.kuaidihelp.com/pay/show?cm_id=";// 业务员向客户收款链接-业务界面中的二维码收款
    public static final String RECEIVE_MONEY_FOR_ORDERDETAIL = "http://m.kuaidihelp.com/pay/show?order_no=";// 业务员向客户收款连接-订单详情中二维码收款

    // 其它说明
    public static final String DESC_BALANCE_CASH_COMSUMPATION = "http://m.kuaidihelp.com/help/money_exp";// 常见问题：余额、提现金额、消费金额说明
    public static final String MY_ACCOUNT_CHONGZHI_DESC = "http://m.kuaidihelp.com/explain/url1";// 常见问题：充值问题
    public static final String FAQ_PROBLEM_COMSUMPATION = "http://m.kuaidihelp.com/help/consume_exp";// 常见问题：消费问题
    public static final String FAQ_PROBLEM_CASH = "http://m.kuaidihelp.com/explain/url3";// 常见问题：提现问题
    public static final String ADD_MODEL_DESC = "http://m.kuaidihelp.com/help/fun_sms";// 添加模板【如何添加编号？】

    public static final String WEIGHING_EXP = "http://m.kuaidihelp.com/help/weight_in_exp";

    // WXPay
    // appid 请同时修改 androidmanifest.xml里面，.PayActivityd里的属性<data android:scheme="wx389287be6cfdb2fa"/>为新设置的appid
    public static final String APP_ID = "wx389287be6cfdb2fa";
    // 商户号
//	public static final String MCH_ID = "1243016202";
    public static final String MCH_ID = "1267889101";
    // API密钥，在商户平台设置
//	public static final String API_KEY = "cba8613105da1d7b0ecc0e21f3144233";
    public static final String API_KEY = "25f9e794323b453885f5181f1b624d0b";

    // 使用帮助--在线客服
    public static final String USE_HELP = "http://m.kuaidihelp.com/help/weixinbangzhu";
    public static final String NEW_USE_HELP = "http://m.kuaidihelp.com/help/s_help";

    public static final int PAGE_SIZE = 30;

    // Message.what MaxNum

    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final int PHOTORESOULT = 3;// 结果

    //拍照-添加店铺商品图片
    public static final int REQUEST_ADD_PHOTO = 101;


    //onActivityResult处理请求和返回码
    public static final int REQUEST_CODE = 776;
    public static final int RESULT_CODE = 777;
    public static final int RESULT_CODE_2 = 778;

    // Dialog处理不同事件
    public static final int USER_EXIT = 1;
    public static final int REGIST_EXIT = 2;
    public static final int FORGET_EXIT = 3;
    public static final int SHOP_MIGRATE = 4;
    public static final int MODIFY_USER_INFO = 5;

    public static final int ADD_DELIVE_NO = 511;
    public static final int DELETE_ORDER = 600;
    public static final int DELETE_LIUYAN = 600;
    public static final int DELETE_COMPLAIN = 600;
    public static final int DELETE_NOTICE = 600;

    // 接口相关消息
    public static final int ACCEPT_SUCCESS = 401;
    public static final int ACCEPT_FAILED = 403;

    // Http请求相关
    public static final int HTTP_TIME_OUT = 408;
    public static final int HTTP_EXCEPTION = 400;
    public static final int HTTP_OK = 200;

    // 注册相关
    public static final int REGIST_GET_OK = 401;
    public static final int REGIST_GET_FAILD = 402;
    public static final int REGIST_SUCCESS = 403;
    public static final int REGIST_FAILD = 404;
    public static final int REGIST_PARSE_FAILD = 405;

    public static final int CHECKCODE_GET_OK = 406;
    public static final int CHECKCODE_GET_FAILD = 407;
    public static final int CHECKCODE_PARSE_OK = 408;
    public static final int CHECKCODE_PARSE_FAILD = 409;

    public static final int CHECKCODE_VERIFY_SUCCESS = 411;
    public static final int CHECKCODE_VERIFY_FAILD = 412;

    public static final int TIME_COUNTING = 410;

    public static final int USER_INFO_GET_OK = 406;
    public static final int USER_INFO_GET_FAILED = 407;
    public static final int USER_INFO_PARSE_OK = 408;
    public static final int USER_INFO_PARSE_FAILED = 409;
    public static final int USER_GETROLETYPE_OK = 420;// 获取个人身份信息成功
    public static final int USER_GETROLETYPE_FAIL = 421;// 获取个人身份信息失败
    public static final int USER_GETBUSINESSTYPE_OK = 422;// 获取业务类型成功
    public static final int USER_GETBUSINESSTYPE_FAIL = 423;// 获取业务类型失败
    public static final int USER_XIUGAI_PERSON_INFO_OK = 424;
    public static final int USER_MODIFY_ROLETYPE_SUCCESS = 425;// 修改个人身份成功
    public static final int USER_MODIFY_BUSYNESSTYPE_SUCCESS = 426;// 修改业务类型成功


    public static final int CLICK_AREA = 401;
    // 获取网点相关
    public static final int BRANCH_GET_OK = 401;
    public static final int BRANCH_GET_FAILD = 402;
    public static final int BRANCH_PARSE_OK = 403;
    public static final int BRANCH_PARSE_FAILD = 404;

    // 消息相关
    public static final int DELETE_TO_FINISH = 401;
    public static final int SUCCESS = 402;
    public static final int FAILED = 403;
    public static final int GET_FAID = 404;
    public static final int SUCCESSS = 415;
    public static final int FAILEDS = 416;
    public static final int PRINT_ORDER = 360;//打印电子面单
    public static final int ZT_SEND_ORDER = 361;//中通录单

    // 消息列表相关
    public static final int PHONE_CALL = 425;// 点击电话图标

    public static final int ORDER_INFO = 406;// 点击消息列表项
    public static int MESSAGE_HOT = 407;

    // 获取消息相关
    public static final int ORDER_GET_OK = 407;
    public static final int ORDER_GET_FAILD = 408;
    public static final int ORDER_PARSE_OK = 409;
    public static final int ORDER_PARSE_FAILD = 410;

    public static final int MSG_UPDATE_RECORDER = 401;

    public static final int ORDER_IM_DETAIL_GET_OK = 413;
    public static final int ORDER_IM_DETAIL_GET_FAILD = 406;
    public static final int ORDER_IM_DETAIL_PARSE_OK = 407;
    public static final int ORDER_IM_DETAIL_PARSE_FAILD = 408;

    public static final int ORDER_IM_ADD_GET_OK = 409;
    public static final int ORDER_IM_ADD_GET_FAILD = 410;
    public static final int ORDER_IM_ADD_PARSE_OK = 411;
    public static final int ORDER_IM_ADD_PARSE_FAILD = 412;

    // 留言相关
    public static final int LIUYANLIST_GET_OK = 405;
    public static final int LIUYANLIST_GET_FAID = 406;
    public static final int LIUYANLIST_PAISE_OK = 409;
    public static final int LIUYANLIST_PAISE_FAID = 408;
    public static final int LIUYANLIST_GET_SCREENING_SUCCESS = 410;

    public static final int LIUYANCONTENT_GET_OK = 402;
    public static final int LIUYANCONTENT_GET_FAID = 403;
    public static final int LIUYANCONTENT_PAISE_OK = 404;
    public static final int LIUYANCONTENT_PAISE_FAID = 405;

    public static final int SEND_LIUYAN_GET_OK = 406;
    public static final int SEND_LIUYAN_GET_FAID = 407;
    public static final int SEND_LIUYAN_PAISE_OK = 408;
    public static final int SEND_LIUYAN_PAISE_FAID = 409;

    // 资讯相关
    public static final int INFO_GET_FAID = 401;
    public static final int INFO_GET_SUCCESS = 402;
    public static final int INFO_NOT_DATA = 403;
    public static final int INFO_OK_DATA = 404;
    public static final int INFO_EXCEPTION = 405;
    public static final int HOT_DOT = 406;
    public static final int INFO_NOT_DETA = 407;

    // 投诉相关
    public static final int COMPLAIN_GET_SUCCESS = 405;
    public static final int COMPLAIN_GET_FAILD = 406;
    public static final int COMPLAIN_PARSE_SUCCESS = 407;
    public static final int COMPLAIN_PARSE_FAILD = 408;

    public static final int COMPLAIN_CONTENT_GET_SUCCESS = 401;
    public static final int COMPLAIN_CONTENT_GET_FAILD = 402;
    public static final int COMPLAIN_CONTENT_PARSE_SUCCESS = 403;
    public static final int COMPLAIN_CONTENT_PARSE_FAILD = 404;

    public static final int DOWNLOAD_OK = 401;

    // 超派查询相关
    public static final int OVERAREA_GET_OK = 401;
    public static final int OVERAREA_GET_FAILD = 402;
    public static final int OVERAREA_PARSE_OK = 403;
    public static final int OVERAREA_PARSE_FAILD = 404;

    public static final int SEND_RANGE_GET_OK = 401;
    public static final int SEND_RANGE_GET_FAILD = 402;
    public static final int SEND_RANGE_PARSE_OK = 403;
    public static final int SEND_RANGE_PARSE_FAILD = 404;

    public static final int FIND_EXPRESS_GET_OK = 409;
    public static final int FIND_EXPRESS_GET_FAID = 410;
    public static final int FIND_EXPRESS_PAISE_OK = 411;
    public static final int FIND_EXPRESS_PAISE_FAID = 412;
    public static final int FIND_EXPRESS_PAISE_FAID_TWO = 413;

    public static final int GET_BRNCH_INFO = 405;
    public static final int GET_SENDER_INFO = 4005;
    public static final int NOT_PHONE = 406;
    public static final int DEAL_EXCEPTION = 407;
    public static final int PROBLEM_CAUSE = 408;

    //	public static final int RESPONSE_CLIENT_PHONENUMBER = 417;
    public static final int RESPONSE_SEND_POSITION = 418;
    public static final int GET_MESSAGE_COUNT_SUCCESS = 420;// 获取短信发送数量成功

    public static final int GET_SIGN_IN_STATUS_SUCCESS = 421;// 获取短信发送签收状态更新成功

    // 登录相关
    public static final int LOGIN_GET_OK = 401;
    public static final int LOGIN_GET_FAILD = 402;
    public static final int LOGIN_SUCCESS = 403;
    public static final int LOGIN_FAILD = 404;
    public static final int LOGIN_PARSE_FAILD = 405;

    public static final int UNREAD_GET_OK = 402;
    public static final int UNREAD_PARSE_OK = 403;

    public static final int MODEL_UPDATE = 401;

    public static final int GET_VERIFI_STATUS = 413;

    // 派件相关
    public static final int NOTIFY_PHONE_GET_OK = 410;
    public static final int NOTIFY_PHONE_GET_FAILD = 406;
    public static final int NOTIFY_PHONE_PARSE_OK = 407;
    public static final int NOTIFY_PHONE_PARSE_FAILD = 408;
    public static final int NOTIFY_AUDITE3 = 411;

    public static final int DELIVERY_LIST_GET_SUCCESS = 407;
    public static final int DELIVERY_LIST_GET_FAILED = 408;

    public static final int DELIVERY_SCAN_GET_SUCCESS = 510;
    public static final int DELIVERY_SCAN_GET_FAILD = 511;
    public static final int DELIVERY_SCAN_PARSER_OK = 512;
    public static final int DELIVERY_SCAN_PARSER_FAILD = 513;
    public static final int DELIVERY_SCAN_PHONE = 514;
    public static final int DELIVERY_SCAN_PARSER_OKS = 515;
    public static final int DELIVERY_SCAN_PARSER_FAILDS = 516;
    public static final int DELIVERY_SCAN_PARSER_OKSS = 517;
    public static final int DELIVERY_SCAN_PARSER_FAILDSS = 518;
    public static final int NOTIFY_ORDER_SEND_FAILD = 519;
    public static final int NOTIFY_YUNDAN_GENZONG = 520;
    public static final int NOTIFY_LIUYAN_DETAIL = 521;
    public static final int NOTIFY_RESPONSE_PHONE = 522;
    public static final int NOTIFY_INPUT_PHONE = 523;
    public static final int SELECT_MODEL_SUCCESS = 503;
    public static final int REQUEST_NOTIFYDETAIL_CAPTURE = 524;
    public static final int RESULT_NOTIFYDETAIL_CAPTURE = 525;
    public static final int SELECT_SEND_MORE = 526;

    // 模板相关
    public static final int MODEL_ADD_SUCCESS = 101;//模板发送成功

    // 快递圈相关
    public static final int CIRCLE_EXPRESS_GET_TUCAOINFO_SUCCESS = 601;
    public static final int CIRCLE_EXPRESS_GET_TUCAOINFO_ABNORMAL = 602;
    public static final int CIRCLE_EXPRESS_GET_TUCAODETAIL_SUCCESS = 603;
    public static final int CIRCLE_EXPRESS_FAIL = 604;
    public static final int GETDATA_FAIL = 605;
//    public static final int CIRCLE_EXPRESS_GET_TUCAOINFO_DEATIL_ABNORMAL = 606;
    public static final int CIRCLE_EXPRESS_GET_TUCAOINFO_DETAIL_FAIL = 612;
    public static final int CIRCLE_EXPRESS_ADD_PINGLUN_SUCCESS = 607;
    public static final int CIRCLE_EXPRESS_ADD_PINGLUN_FAIL = 608;
    public static final int CIRCLE_EXPRESS_GOOD_SUCCESS = 609;
    public static final int CIRCLE_EXPRESS_GOOD_FAIL = 610;
    public static final int CIRCLE_EXPRESS_GET_TUCAOINFO_ERROR = 611;
    public static final int CIRCLE_DETAIL_TO_EXPRESS = 612;
//    public static final int CIRCLE_GETDATA_ISNULL = 613;
    public static final int CIRCLE_GETDATA_FAIL = 614;
    public static final int CIRCLE_SEND_MESSAGE_SUCCESS = 615;// 发送消息成功
    public static final int CIRCLE_GET_HEADINFO_OK = 616;// 推送进来获取指定吐槽详情成功
    public static final int CIRCLE_GET_HEADINFO_FAIL = 617;
    public static final int CIRCLE_ISGOOD_REQUEST = 618;// 点赞请求
//    public static final int CIRCLE_GETDATA_ISNULL_DEFAULT = 619;// 非上拉刷新或加载

    // 外块相关
    public static final int OUTSIDE_GET_SUCCESS = 101;//获取外单数据成功
    public static final int OUTSIDE_GET_DATAISNULL = 102;//未获取到获取外单
    public static final int ROB_OUTSIDE_SUCCESS = 103;//抢单成功
    public static final int ROB_OUTSIDE_FAIL = 104;//抢单失败
    public static final int RELEASE_OUTSIDE_SUCCESS = 105;//释放外单成功
    public static final int RELEASE_OUTSIDE_FAIL = 106;//释放外单失败
    public static final int OUTSIDE_CODE_ISNOT_ZERO = 107;//code 不为0

    // 支付相关
    public static final int ZHIFU_GETINFO_SUCCESS = 700;// 获取账户余额成功
    public static final int ZHIFU_GETINFO_DETAIL_SUCCESS = 701;// 获取账户流水成功

    // 百度推送有关
    public static final int BAIDU_PUSH_GET_OK = 461;
    public static final int BAIDU_PUSH_GET_FAID = 462;
    public static final int BAIDU_PUSH_PAISE_OK = 463;
    public static final int BAIDU_PUSH_PAISE_FAID = 464;

    // ResultCode和RequestCode
    // 选择快递公司
    public static final int RESULT_CHOOSE_EXPRESSFIRM = 469;
    public static final int REQUEST_CHOOSE_EXPRESSFIRM = 470;
    // 选择区域
    public static final int RESULT_CHOOSE_AREA = 471;
    public static final int REQUEST_CHOOSE_AREA = 472;
    // 选择网点
    public static final int RESULT_CHOOSE_BRANCH = 473;
    public static final int REQUEST_CHOOSE_BRANCH = 474;
    // 条形码扫描
    public static final int RESULT_QRCODE = 475;
    public static final int REQUEST_QRCODE = 476;
    public static final int SCAN_DEL = 477;

    // 设置头像相关
    public static final int REQUEST_SET_HEADER = 495;
    public static final int RESULT_UPLOAD_FROM_PHOTO = 490;
    public static final int RESULT_UPLOAD_FROM_CAMERA = 491;
    public static final int PHOTO_REQUEST_TAKEPHOTO = 492;
    public static final int PHOTO_REQUEST_GALLERY = 493;
    public static final int PHOTO_REQUEST_CUT = 494;

    // 设置模板
    public static final int RESULT_SET_MODEL = 100;
    public static final int REQUEST_SET_MODEL = 101;

    public static final int RESULT_ADD_MODEL = 103;
    public static final int REQUEST_ADD_MODEL = 104;

    public static final int RESULT_EDIT_MODEL = 105;
    public static final int REQUEST_OPERATE_MODEL = 106;
    public static final int RESULT_DELETE_MODEL = 107;
    // 其他描述
    public static final int RESULT_OTHER = 105;
    public static final int REQUEST_OTHER = 106;
    public static final int NOTIFY_ITEM_SHOW = 502;
    public static final int RESULT_GET_PHONE = 503;

    // 取派范围相关
    public static final int RANGE_GET_OK = 101;
    public static final int RANGE_GET_FAILD = 102;
    public static final int RANGE_PARSE_OK = 103;
    public static final int RANGE_PARSE_FAILD = 104;
    public static final int RESULT_BACK_TO_MAIN = 700;

    // 没有网络
    public static final int NETWORK_FAILED = 500;
    public static final int SHOW = 501;

    // 录音相关
    public static final int MSG_PLAY_RECORD = 478;

    // 分享
    public static final int SHARE_GET_OK = 501;
    public static final int SHARE_GET_FAILED = 502;

    // 二维码名片访问收藏
    public static final int PARSE_VISIT_CARD_OK = 430;
    public static final int PARSE_VISIT_CARD_FAILD = 431;

    // 消息相关
    /*
	 * public static final int RESULT_CUSTOME_NEW=423; public static final int
	 * REQUEST_CUSTOME_NEW=424; public static final int
	 * RESULT_CUSTOME_NEW_REFUSED=425; public static final int
	 * RESULT_CUSTOME_DEALING=426; public static final int
	 * REQUEST_CUSTOME_DEALING=427; public static final int
	 * RESULT_CUSTOME_DEALING_REFUSED=428;
	 */
    public static final int RESULT_UPDATE_ORDER = 479;
    public static final int REQUEST_UPDATE_ORDER = 480;

    public static final int RESULT_CHOOSE_FAST_REPLY = 509;
    public static final int REQUEST_CHOOSE_FAST_REPLY = 510;
    public static final int DELIVERNOHISTORY_SHOW_MENG = 511;

    // 服务类型
    public static final int TYPE_CUSTOM = 481;
    public static final int TYPE_CONMMEN = 482;
    // 消息类型
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_EXCEPTION = 2;
    public static final int TYPE_COMPLAIN = 466;
    public static final int TYPE_NOTICE = 467;
    public static final int TYPE_TXT = 1;
    public static final int TYPE_IMG = 2;
    public static final int TYPE_YUYIN = 3;
    public static final int TYPE_TXTIMG = 4;
    public static final int TYPE_CLOUD_VOICE = 6;// 云呼语音类型

    // 回复类型
    public static final int TYPE_REPLY_CUSTOMER = 1;
    public static final int TYPE_REPLY_SERVER = 2;

    public static final int TYPE_REPLY_MODEL_SIGN = 1;
    public static final int TYPE_QUICK_MODEL_SIGN = 2;
    public static final int TYPE_QUICK_SERVER_MODEL_SIGN = 3;

    // 扫描类型
    public static final int TYPE_PAIJIAN = 2;
    public static final int TYPE_COLLECT = 3;
    public static final int TYPE_DELIVER = 4;
    public static final int TYPE_FIND_EXPRESS = 5;
    public static final int TYPE_PAIJIAN_ONE = 6;
    public static final int TYPE_ORDER_ONE = 7;
    public static final int TYPE_ZJ_REAL_NAME_SCAN_ORDER = 0Xa001;// 浙江地区实名登记扫描单号
    public static final int RESULT_GETREALNAME_SUCCESS = 0xa1006;// 实名采集成功
    //收款扫单号
    public static final int TYPE_COLLECTION = 8;
    // 记账扫单号
    public static final int TYPE_KEEP_ACCOUNTS = 10;
    //发起留言扫单号
    public static final int TYPE_CREATE_LIUYAN = 9;
    //派件搜索扫单号
    public static final int TYPE_DISPATCH = 11;
    // 审核结果
    public static final int TYPE_AUDIT_SUCCESS = 11;
    public static final int TYPE_AUDIT_FAIL = 12;
    // 扫单号
    public static final int TYPE_SCAN_ORDER_REQUEST = 0XA002;
    public static final int TYPE_SCAN_ORDER_RESULT = 0XA003;
    public static final int TYPE_SCAN_ORDER_COLLECTION_OFFLINE_REQUEST = 0XA004;
    public static final int TYPE_SCAN_ORDER_COLLECTION_OFFLINE_RESULT= 0XA005;

    // 短链接
    public static final int SHORT_URL_SUCCESS = 21;
    public static final int SHORT_URL_FAIL = 22;
    public static final int SHORT_PARSER_OK = 23;
    public static final int SHORT_PARSER_FAIL = 24;
    // 广播
    public static final String ACTION_REFRESH_MESSAGE_ALARM = "com.kuaibao.skuaidi.ACTION_REFRESH_MESSAGE_ALARM";
    public static final String ACTION_MESSAGE_UPDATE = "com.kuaibao.skuaidi.ACTION_MESSAGE_UPDATE";
    public static final String ACTION_DOWNLOAD = "com.kuaibao.skuaidi.ACTION_DOWNLOAD";
    public static final String ACTION_DOWNLOAD_OK = "com.kuaibao.skuaidi.ACTION_DOWNLOAD_OK";

    //添加微店相关
    public static final int GET_SHOP_INFO_SUCCESS = 103;//获取店铺列表成功
    public static final int GET_SHOP_INFO_FAIL = 102;//获取店铺列表失败

    public static final int ADD_SHOP_SUCCESS = 101;//添加店铺成功
    public static final int UPDATE_SHOP_SUCCESS = 104;//更新店铺成功
    public static final int ADD_SHOP_IMAGE_ARR_SUCCESS = 105;//添加店铺商品图片成功
    public static final int ADD_SHOP_INAGE_ARR_FAIL = 106;//添加店铺商品图片失败
    public static final int GET_SHOP_IMAGE_SUCCESS = 107;//获取店铺商品图片成功
    public static final int GET_SHOP_IMAGE_FAIL = 108;//获取店铺商品图片失败
    public static final int GET_SHOP_IMAGE_NULL = 109;//没有获取到图片
    //打印电子面单获取运单号
    public static final int GET_TRANS_NUMBER = 166;


    //session_id 失效
    public static final int SESSION_ID_FAIL = 101;

    //time_out
    public static final int TIME_OUT_FAIL = 1991;
    //上传图片
    public static final int PIC_UP_REQUESTCODE = 900;
    public static final int PIC_UP_RESULTCODE = 901;

    //相机识别身份证
    public static final int CAMREA_IDENTIFY_IDCARD = 0x101;

    //EventBus
    public static final int EVENT_BUS_TYPE_10005 = 0XA1005;
    public static final int EVENT_BUS_TYPE_1007 = 0XA1007;

    /**
     * 下载有关文件
     *
     * @author Administrator
     */
    public enum DOWN_TYPE {
        ARM, JPG
    }
}
