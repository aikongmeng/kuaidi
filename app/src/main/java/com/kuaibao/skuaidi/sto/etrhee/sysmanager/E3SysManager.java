package com.kuaibao.skuaidi.sto.etrhee.sysmanager;

import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.kuaibao.skuaidi.activity.model.CourierReviewInfo;
import com.kuaibao.skuaidi.activity.model.E3Type;
import com.kuaibao.skuaidi.application.SKuaidiApplication;
import com.kuaibao.skuaidi.db.SkuaidiNewDB;
import com.kuaibao.skuaidi.entry.E3_order;
import com.kuaibao.skuaidi.entry.NotifyInfo;
import com.kuaibao.skuaidi.entry.UserInfo;
import com.kuaibao.skuaidi.retrofit.api.entity.CurrentE3VerifyInfo;
import com.kuaibao.skuaidi.sto.ethree2.UpdateReviewInfoUtil;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;
import com.socks.library.KLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * E3系统管理类
 *
 * @author xy
 */
public class E3SysManager {
    private static final Map<String, Integer> badWaiBillTypes_sto = new HashMap<String, Integer>();
    private static final Map<String, HashMap<String, String>> badWaiBillSubjectGroup_qf = new HashMap<String, HashMap<String, String>>();
    private static final HashMap<String, String> badWaiBillTypesGroup1_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup2_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup3_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup4_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup5_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup6_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup7_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup8_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup9_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup10_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup11_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup12_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup13_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup14_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup15_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup16_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup17_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup18_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup19_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup20_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup21_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup22_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup23_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup24_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup25_qf = new HashMap<String, String>();
    private static final HashMap<String, String> badWaiBillTypesGroup26_qf = new HashMap<String, String>();

    // 品牌缩写
    public static final String BRAND_STO = "sto";
    public static final String BRAND_ZT = "zt";
    public static final String BRAND_QF = "qf";

    public static final String SCAN_TO_E3 = "scan.to.e3";// 申通数据上传接口名
    public static final String SCAN_TO_ZT = "scan.to.zt";// 中通数据上传接口名
    public static final String SCAN_TO_QF = "scan.to.qf";// 全峰数据上传接口名

    public static final String SCAN_TO_E3_V2 = "scan.to.e3.v2";// 申通数据上传接口名
    public static final String SCAN_TO_ZT_V2 = "scan.to.zt.v2";// 中通数据上传接口名
    public static final String SCAN_TO_QF_V2 = "scan.to.qf.v2";// 全峰数据上传接口名

    public static final String SCAN_E3_LIST = "scan.e3.list";// 申通获取扫描记录接口名
    public static final String SCAN_ZT_LIST = "scan.zt.list";// 中通获取扫描记录接口名
    public static final String SCAN_QF_LIST = "scan.qf.list";// 全峰获取扫描记录接口名

    private static final String STO_EXPRESS_NO_REGEX = "^4[012356789][0-9]{10}$";
    private static final String STO_EXPRESS_NO_REGEX2 = "^(sto|STO|[2-9]68)\\d{9}$|^(588|688|888|900|((11|22)\\d)|((33|44|55|77|99)\\d{2})|((66|88)([0-7]|9)\\d))\\d{9}$";

    // 查询快件最新状态接口
    public static final String EXPRESS_END_STATUS = "express.end.status";
    /**
     * 有派无签
     */
    public static final String SCAN_E3_UNSIGNED = "scan_e3_unsigned";

    /**
     * 验证
     */
    public static final String SCAN_QF_VERIFY = "scan.qf.verify";
    public static final String SCAN_COUNTERMAN_VERIFY = "scan.counterman.verify";
    public static final String SCAN_ZT_VERIFY = "scan.zt.verify";

    private static final Map<String, String> badWaiBillTypes_zt = new LinkedHashMap<>();

    public static final String[] scanTypes = {"收件", "发件", "到件", "派件", "签收件", "问题件"};
    public static final HashMap<String, String> scanToTypeMap = new HashMap<>();

    static {
        scanToTypeMap.put("扫收件", "收件");
        scanToTypeMap.put("扫发件", "发件");
        scanToTypeMap.put("扫到件", "到件");
        scanToTypeMap.put("扫派件", "派件");
        scanToTypeMap.put("扫签收", "签收件");
        scanToTypeMap.put("问题件", "问题件");
        scanToTypeMap.put("第三方签收", "第三方签收");
    }

    public static final HashMap<String, Integer> typeToIDMap = new HashMap<>();

    static {
        typeToIDMap.put("扫收件", 1);
        typeToIDMap.put("扫发件", 6);
        typeToIDMap.put("扫到件", 5);
        typeToIDMap.put("扫派件", 2);
        typeToIDMap.put("扫签收", 3);
        typeToIDMap.put("问题件", 4);
        typeToIDMap.put("退件", 7);
        typeToIDMap.put("第三方签收", 8);
    }

    public static HashMap<String, Integer> typeMap = new HashMap<String, Integer>() {
        {
            put("收件", 1);
            put("派件", 2);
            put("签收件", 3);
            put("问题件", 4);
            put("到件", 5);
            put("发件", 6);
            put("第三方签收", 8);

        }
    };
    public static final HashMap<Integer, String> IDToTypeMap = new HashMap<>();

    static {
        IDToTypeMap.put(1, "扫收件");
        IDToTypeMap.put(2, "扫派件");
        IDToTypeMap.put(3, "扫签收");
        IDToTypeMap.put(4, "问题件");
        IDToTypeMap.put(5, "扫到件");
        IDToTypeMap.put(6, "扫发件");
        IDToTypeMap.put(8, "第三方签收");
    }

    // 中通同问题件描述最长字符限制
    public static final int BAD_DESC_MAX_LENGTH_ZT = 400;
    public static final int SIGNE_MAX_LENGTH_QF = 20;
    public static final String SCAN_TYPE_LANPICE = "扫收件";
    public static final String SCAN_TYPE_FAPICE = "扫发件";
    public static final String SCAN_TYPE_DAOPICE = "扫到件";
    public static final String SCAN_TYPE_PIEPICE = "扫派件";
    public static final String SCAN_TYPE_BADPICE = "问题件";
    public static final String SCAN_TYPE_SIGNEDPICE = "扫签收";
    public static final String SCAN_TYPE_SIGNED_THIRD_PARTY = "第三方签收";
    public static final String[] invalidChars = {"自己", "支付宝", "英文", "艺术签名", "已收", "已签", "西溪园区", "无法", "妥投", "图片扫描",
            "投递", "淘宝城", "淘宝", "送货成功", "送到", "收下", "收了", "收件人", "收到", "实收", "什么", "傻", "亲收", "亲签", "签收", "其他", "模糊",
            "潦字", "潦签", "看不", "代收", "代签", "成功派送", "草签", "草", "不认识", "不认得", "不清", "不明", "不懂", "笨", "本收", "本人签收", "本人",
            "本签", "本·人", "阿里云", "阿里巴巴", "z", "y", "x", "w", "v", "u", "t", "s", "r", "q", "p", "o", "n", "m", "l", "k",
            "j", "i", "h", "g", "f", "e", "d", "c", "b",};
    public static final ArrayList<String> invalidSignedType_qf = new ArrayList<String>(Arrays.asList(invalidChars));

    public static final ArrayList<String> badWaiBillSubjects_qf = new ArrayList<String>(Arrays.asList(new String[]{
            "客户原因", "发件原因", "亚马逊—发件原因", "亚马逊—其他", "中转原因", "中转原因 操作结束", "中转原因 班车晚点", "留仓件", "派件原因", "投诉", "其他原因", "其他",
            "网络班车", "站点原因", "破损件", "问题件", "亚马逊—直送退货", "超区/无点", "国美拒收", "留仓", "亚马逊—全部拒收", "亚马逊—配送延迟", "亚马逊—无法投递",
            "亚马逊—上门退换(信封)", "亚马逊—总站操作", "亚马逊—包裹丢失"}));

    static {
        badWaiBillTypes_sto.put("外包装破损,内件破损,短少,丢失", 14);
        badWaiBillTypes_sto.put("外包装完好,内件不符,破损,短少,丢失", 15);
        badWaiBillTypes_sto.put("外包装污染", 16);
        badWaiBillTypes_sto.put("面单详情与实际内件不符", 17);
        badWaiBillTypes_sto.put("地址错误", 18);
        badWaiBillTypes_sto.put("电话错误,空号,停机", 19);
        badWaiBillTypes_sto.put("面单脱落", 20);
        badWaiBillTypes_sto.put("双面单", 21);
        badWaiBillTypes_sto.put("有单无货", 22);
        badWaiBillTypes_sto.put("收货信息不详", 23);
        badWaiBillTypes_sto.put("超派件,客户要求更改地址", 24);
        badWaiBillTypes_sto.put("超派件,客户自取", 25);
        badWaiBillTypes_sto.put("超派件,转发EMS", 26);
        badWaiBillTypes_sto.put("超派件,退回", 27);
        badWaiBillTypes_sto.put("天气,自然灾害等原因导致延误", 28);
        badWaiBillTypes_sto.put("交通事故导致延误", 29);
        badWaiBillTypes_sto.put("突发事件导致延误", 30);
        badWaiBillTypes_sto.put("违禁品,机关查扣", 31);
        badWaiBillTypes_sto.put("大字错写导致错发", 32);
        badWaiBillTypes_sto.put("建包错发件", 33);
        badWaiBillTypes_sto.put("送无人,电话联系明日送", 34);
        badWaiBillTypes_sto.put("送无人,电话联系不上明日送", 35);
        badWaiBillTypes_sto.put("搬家等特殊事件", 36);
        badWaiBillTypes_sto.put("节假日延迟派送", 37);
        badWaiBillTypes_sto.put("客户要求更改地址,时间送", 38);
        badWaiBillTypes_sto.put("客户拒收", 40);
        badWaiBillTypes_sto.put("学校自取件", 41);
        badWaiBillTypes_sto.put("面单注明自取件", 42);
        badWaiBillTypes_sto.put("面单注明自取,客户拒提", 43);
        badWaiBillTypes_sto.put("自取件", 44);
        badWaiBillTypes_sto.put("客户要求自取", 45);
        badWaiBillTypes_sto.put("发件公司要求改地址", 46);
        badWaiBillTypes_sto.put("发件公司要求退回", 47);
        badWaiBillTypes_sto.put("超派,电话联系不上", 48);
        badWaiBillTypes_sto.put("违禁品", 49);
        badWaiBillTypes_sto.put("超规格", 50);
        badWaiBillTypes_sto.put("无面单已认领", 51);
        badWaiBillTypes_sto.put("联系好下午送", 52);
        badWaiBillTypes_sto.put("网络错分件", 53);
        badWaiBillTypes_sto.put("到付费用问题", 54);
        badWaiBillTypes_sto.put("发票问题", 55);

        badWaiBillSubjectGroup_qf.put("客户原因", badWaiBillTypesGroup1_qf);
        badWaiBillSubjectGroup_qf.put("发件原因", badWaiBillTypesGroup2_qf);
        badWaiBillSubjectGroup_qf.put("亚马逊—发件原因", badWaiBillTypesGroup3_qf);
        badWaiBillSubjectGroup_qf.put("亚马逊—其他", badWaiBillTypesGroup4_qf);
        badWaiBillSubjectGroup_qf.put("中转原因", badWaiBillTypesGroup5_qf);
        badWaiBillSubjectGroup_qf.put("中转原因 操作结束", badWaiBillTypesGroup6_qf);
        badWaiBillSubjectGroup_qf.put("中转原因 班车晚点", badWaiBillTypesGroup7_qf);
        badWaiBillSubjectGroup_qf.put("留仓件", badWaiBillTypesGroup8_qf);
        badWaiBillSubjectGroup_qf.put("派件原因", badWaiBillTypesGroup9_qf);
        badWaiBillSubjectGroup_qf.put("投诉", badWaiBillTypesGroup10_qf);
        badWaiBillSubjectGroup_qf.put("其他原因", badWaiBillTypesGroup11_qf);
        badWaiBillSubjectGroup_qf.put("其他", badWaiBillTypesGroup12_qf);
        badWaiBillSubjectGroup_qf.put("网络班车", badWaiBillTypesGroup13_qf);
        badWaiBillSubjectGroup_qf.put("站点原因", badWaiBillTypesGroup14_qf);
        badWaiBillSubjectGroup_qf.put("破损件", badWaiBillTypesGroup15_qf);
        badWaiBillSubjectGroup_qf.put("问题件", badWaiBillTypesGroup16_qf);
        badWaiBillSubjectGroup_qf.put("亚马逊—直送退货", badWaiBillTypesGroup17_qf);
        badWaiBillSubjectGroup_qf.put("超区/无点", badWaiBillTypesGroup18_qf);
        badWaiBillSubjectGroup_qf.put("国美拒收", badWaiBillTypesGroup19_qf);
        badWaiBillSubjectGroup_qf.put("留仓", badWaiBillTypesGroup20_qf);
        badWaiBillSubjectGroup_qf.put("亚马逊—全部拒收", badWaiBillTypesGroup21_qf);
        badWaiBillSubjectGroup_qf.put("亚马逊—配送延迟", badWaiBillTypesGroup22_qf);
        badWaiBillSubjectGroup_qf.put("亚马逊—无法投递", badWaiBillTypesGroup23_qf);
        badWaiBillSubjectGroup_qf.put("亚马逊—上门退换(信封)", badWaiBillTypesGroup24_qf);
        badWaiBillSubjectGroup_qf.put("亚马逊—总站操作", badWaiBillTypesGroup25_qf);
        badWaiBillSubjectGroup_qf.put("亚马逊—包裹丢失", badWaiBillTypesGroup26_qf);

        badWaiBillTypesGroup1_qf.put("地址不详", "客户原因");
        badWaiBillTypesGroup1_qf.put("拒付代收货款", "客户原因");
        badWaiBillTypesGroup1_qf.put("电话无人接听", "客户原因");
        badWaiBillTypesGroup1_qf.put("客户要求重新派送", "客户原因");
        badWaiBillTypesGroup1_qf.put("查无此人", "客户原因");
        badWaiBillTypesGroup1_qf.put("恶意订单", "客户原因");
        badWaiBillTypesGroup1_qf.put("取消订单", "客户原因");
        badWaiBillTypesGroup1_qf.put("联系不上无人", "客户原因");
        badWaiBillTypesGroup1_qf.put("更改地址", "客户原因");
        badWaiBillTypesGroup1_qf.put("要求工作日派送", "客户原因");
        badWaiBillTypesGroup1_qf.put("要求周末派送", "客户原因");
        badWaiBillTypesGroup1_qf.put("要求推迟派送", "客户原因");
        badWaiBillTypesGroup1_qf.put("客户拒收", "客户原因");
        badWaiBillTypesGroup1_qf.put("拒付到付", "客户原因");
        badWaiBillTypesGroup1_qf.put("客户拒付货款", "客户原因");
        badWaiBillTypesGroup1_qf.put("拒付到付款", "客户原因");
        badWaiBillTypesGroup1_qf.put("客户拒收", "客户原因");

        badWaiBillTypesGroup2_qf.put("超区/无点", "发件原因");
        badWaiBillTypesGroup2_qf.put("面单不规范", "发件原因");
        badWaiBillTypesGroup2_qf.put("大头笔错写", "发件原因");
        badWaiBillTypesGroup2_qf.put("违禁品", "发件原因");
        badWaiBillTypesGroup2_qf.put("包装不规范", "发件原因");
        badWaiBillTypesGroup2_qf.put("地址不详", "发件原因");
        badWaiBillTypesGroup2_qf.put("电话错误", "发件原因");
        badWaiBillTypesGroup2_qf.put("未/少带派费", "发件原因");

        badWaiBillTypesGroup3_qf.put("卖家取消提货", "亚马逊—发件原因");
        badWaiBillTypesGroup3_qf.put("卖家推迟提货", "亚马逊—发件原因");
        badWaiBillTypesGroup3_qf.put("包裹未包装好", "亚马逊—发件原因");
        badWaiBillTypesGroup3_qf.put("联系不上提无人", "亚马逊—发件原因");

        badWaiBillTypesGroup4_qf.put("其他", "亚马逊—其他");

        badWaiBillTypesGroup5_qf.put("应到未到件", "中转原因");
        badWaiBillTypesGroup5_qf.put("未赶上中转", "中转原因");
        badWaiBillTypesGroup5_qf.put("错发件", "中转原因");
        badWaiBillTypesGroup5_qf.put("面单脱落", "中转原因");
        badWaiBillTypesGroup5_qf.put("延误件", "中转原因");
        badWaiBillTypesGroup5_qf.put("遗失件", "中转原因");
        badWaiBillTypesGroup5_qf.put("破损件", "中转原因");
        badWaiBillTypesGroup5_qf.put("提货包内短少", "中转原因");
        badWaiBillTypesGroup5_qf.put("留仓件", "中转原因");
        badWaiBillTypesGroup5_qf.put("主干线爆仓", "中转原因");
        badWaiBillTypesGroup5_qf.put("包装破损重装", "中转原因");
        badWaiBillTypesGroup5_qf.put("站点异常", "中转原因");
        badWaiBillTypesGroup5_qf.put("破损件", "中转原因");

        badWaiBillTypesGroup6_qf.put("班车到达卸货", "中转原因 操作结束");

        badWaiBillTypesGroup7_qf.put("未赶上中转", "中转原因 班车晚点");

        badWaiBillTypesGroup8_qf.put("干线爆仓", "留仓件");
        badWaiBillTypesGroup8_qf.put("网点爆仓", "留仓件");
        badWaiBillTypesGroup8_qf.put("破损件", "留仓件");
        badWaiBillTypesGroup8_qf.put("违禁品", "留仓件");
        badWaiBillTypesGroup8_qf.put("地址不详", "留仓件");
        badWaiBillTypesGroup8_qf.put("错发件", "留仓件");
        badWaiBillTypesGroup8_qf.put("内件异常", "留仓件");
        badWaiBillTypesGroup8_qf.put("区域不明", "留仓件");
        badWaiBillTypesGroup8_qf.put("退件", "留仓件");
        badWaiBillTypesGroup8_qf.put("超区/无点", "留仓件");
        badWaiBillTypesGroup8_qf.put("双面单", "留仓件");
        badWaiBillTypesGroup8_qf.put("未赶上中转", "留仓件");
        badWaiBillTypesGroup8_qf.put("站点交货晚点", "留仓件");
        badWaiBillTypesGroup8_qf.put("站点提前离场", "留仓件");
        badWaiBillTypesGroup8_qf.put("站点未来取件", "留仓件");
        badWaiBillTypesGroup8_qf.put("站点整顿", "留仓件");
        badWaiBillTypesGroup8_qf.put("无中班", "留仓件");
        badWaiBillTypesGroup8_qf.put("大头笔错写", "留仓件");
        badWaiBillTypesGroup8_qf.put("资产盘点", "留仓件");
        badWaiBillTypesGroup8_qf.put("其他", "留仓件");

        badWaiBillTypesGroup9_qf.put("超出配送能力", "派件原因");
        badWaiBillTypesGroup9_qf.put("派件延误", "派件原因");
        badWaiBillTypesGroup9_qf.put("快件丢失", "派件原因");
        badWaiBillTypesGroup9_qf.put("快件破损", "派件原因");
        badWaiBillTypesGroup9_qf.put("班车晚点", "派件原因");

        badWaiBillTypesGroup10_qf.put("其他", "投诉");
        badWaiBillTypesGroup10_qf.put("未及时接单", "投诉");
        badWaiBillTypesGroup10_qf.put("未及时取件", "投诉");
        badWaiBillTypesGroup10_qf.put("延误", "投诉");
        badWaiBillTypesGroup10_qf.put("遗失", "投诉");
        badWaiBillTypesGroup10_qf.put("破损/短少", "投诉");
        badWaiBillTypesGroup10_qf.put("多收费用", "投诉");
        badWaiBillTypesGroup10_qf.put("未带POS机", "投诉");
        badWaiBillTypesGroup10_qf.put("服务态度", "投诉");

        badWaiBillTypesGroup11_qf.put("自提件", "其他原因");
        badWaiBillTypesGroup11_qf.put("异常天气", "其他原因");
        badWaiBillTypesGroup11_qf.put("商品质量", "其他原因");
        badWaiBillTypesGroup11_qf.put("货款延迟申请", "其他原因");
        badWaiBillTypesGroup11_qf.put("乡镇件", "其他原因");
        badWaiBillTypesGroup11_qf.put("转单件", "其他原因");
        badWaiBillTypesGroup11_qf.put("其他", "其他原因");

        badWaiBillTypesGroup12_qf.put("其他", "其他");

        badWaiBillTypesGroup13_qf.put("班车晚点", "网络班车");
        badWaiBillTypesGroup13_qf.put("操作延迟", "网络班车");
        badWaiBillTypesGroup13_qf.put("主干线爆仓", "网络班车");

        badWaiBillTypesGroup14_qf.put("车辆偏小", "站点原因");
        badWaiBillTypesGroup14_qf.put("大货增加", "站点原因");
        badWaiBillTypesGroup14_qf.put("站点迟到", "站点原因");
        badWaiBillTypesGroup14_qf.put("无中班", "站点原因");
        badWaiBillTypesGroup14_qf.put("站点提前发车", "站点原因");
        badWaiBillTypesGroup14_qf.put("站点未来拉货", "站点原因");

        badWaiBillTypesGroup15_qf.put("内件异常", "破损件");
        badWaiBillTypesGroup15_qf.put("外包装破损", "破损件");

        badWaiBillTypesGroup16_qf.put("收件地址不详", "问题件");
        badWaiBillTypesGroup16_qf.put("区域不明确", "问题件");
        badWaiBillTypesGroup16_qf.put("面单脱落", "问题件");
        badWaiBillTypesGroup16_qf.put("双面单", "问题件");
        badWaiBillTypesGroup16_qf.put("大头笔错写", "问题件");
        badWaiBillTypesGroup16_qf.put("违禁品", "问题件");
        badWaiBillTypesGroup16_qf.put("破损件", "问题件");
        badWaiBillTypesGroup16_qf.put("错发件", "问题件");
        badWaiBillTypesGroup16_qf.put("无点转件", "问题件");
        badWaiBillTypesGroup16_qf.put("站点车辆已装满", "问题件");
        badWaiBillTypesGroup16_qf.put("贝贝熊退货", "问题件");

        badWaiBillTypesGroup17_qf.put("寄件客户确认收货", "亚马逊—直送退货");

        badWaiBillTypesGroup18_qf.put("超区", "超区/无点");
        badWaiBillTypesGroup18_qf.put("无点转件", "超区/无点");

        badWaiBillTypesGroup19_qf.put("网站降价", "国美拒收");
        badWaiBillTypesGroup19_qf.put("配送超时", "国美拒收");
        badWaiBillTypesGroup19_qf.put("货品错误", "国美拒收");
        badWaiBillTypesGroup19_qf.put("商品存在质量问题", "国美拒收");
        badWaiBillTypesGroup19_qf.put("超区无法配送", "国美拒收");
        badWaiBillTypesGroup19_qf.put("网站信息与实物不符", "国美拒收");
        badWaiBillTypesGroup19_qf.put("恶意订单", "国美拒收");

        badWaiBillTypesGroup20_qf.put("错分", "留仓");

        badWaiBillTypesGroup21_qf.put("客户取消订单", "亚马逊—全部拒收");
        badWaiBillTypesGroup21_qf.put("与客户期望不符", "亚马逊—全部拒收");
        badWaiBillTypesGroup21_qf.put("配送超时", "亚马逊—全部拒收");
        badWaiBillTypesGroup21_qf.put("商品外包装破损", "亚马逊—全部拒收");
        badWaiBillTypesGroup21_qf.put("商品破损(内件)", "亚马逊—全部拒收");
        badWaiBillTypesGroup21_qf.put("配错货", "亚马逊—全部拒收");
        badWaiBillTypesGroup21_qf.put("商品少配件", "亚马逊—全部拒收");

        badWaiBillTypesGroup22_qf.put("客户要求推迟(自提、延期)", "亚马逊—配送延迟");
        badWaiBillTypesGroup22_qf.put("联系不上送无人", "亚马逊—配送延迟");
        badWaiBillTypesGroup22_qf.put("客户要求更改地址", "亚马逊—配送延迟");
        badWaiBillTypesGroup22_qf.put("仅面单备注只工作日送货", "亚马逊—配送延迟");
        badWaiBillTypesGroup22_qf.put("仅面单备注双休日送货", "亚马逊—配送延迟");
        badWaiBillTypesGroup22_qf.put("分拣错误", "亚马逊—配送延迟");
        badWaiBillTypesGroup22_qf.put("无联系无派送", "亚马逊—配送延迟");
        badWaiBillTypesGroup22_qf.put("异常天气", "亚马逊—配送延迟");
        badWaiBillTypesGroup22_qf.put("非本站点派送范围", "亚马逊—配送延迟");

        badWaiBillTypesGroup23_qf.put("恶意订单", "亚马逊—无法投递");
        badWaiBillTypesGroup23_qf.put("联系多天无果", "亚马逊—无法投递");
        badWaiBillTypesGroup23_qf.put("超区", "亚马逊—无法投递");
        badWaiBillTypesGroup23_qf.put("丢失包裹", "亚马逊—无法投递");

        badWaiBillTypesGroup24_qf.put("弃件(无货可办、重复退单)", "亚马逊—上门退换(信封)");
        badWaiBillTypesGroup24_qf.put("取货完成", "亚马逊—上门退换(信封)");

        badWaiBillTypesGroup25_qf.put("包裹未到", "亚马逊—总站操作");
        badWaiBillTypesGroup25_qf.put("包裹回站", "亚马逊—总站操作");
        badWaiBillTypesGroup25_qf.put("去库房途中", "亚马逊—总站操作");
        badWaiBillTypesGroup25_qf.put("确认差异订单", "亚马逊—总站操作");
        badWaiBillTypesGroup25_qf.put("亚马逊投诉", "亚马逊—总站操作");

        badWaiBillTypesGroup26_qf.put("丢失包裹", "亚马逊—包裹丢失");
        badWaiBillTypesGroup26_qf.put("确认差异订单", "亚马逊—包裹丢失");

        badWaiBillTypes_zt.put("面单重复使用", "B15");
        badWaiBillTypes_zt.put("报关资料不详", "B10");
        badWaiBillTypes_zt.put("错分/错发", "B3");
        badWaiBillTypes_zt.put("地址不详，电话联系不上", "A2");
        badWaiBillTypes_zt.put("地址错误，电话错误停机", "A3");
        badWaiBillTypes_zt.put("禁运品", "B6");
        badWaiBillTypes_zt.put("拒付费用", "A6");
        badWaiBillTypes_zt.put("客户地址禁止快递出入", "A14");
        badWaiBillTypes_zt.put("客户拒收", "A5");
        badWaiBillTypes_zt.put("客户强制验货", "A7");
        badWaiBillTypes_zt.put("客户要求改地址", "A12");
        badWaiBillTypes_zt.put("两次免费派送，第三次有偿派送", "A8");
        badWaiBillTypes_zt.put("面单脱落或破损", "B5");
        badWaiBillTypes_zt.put("破损件、短少", "B4");
        badWaiBillTypes_zt.put("少带、未带派送费", "B8");
        badWaiBillTypes_zt.put("送无人，电话联系不上", "A1");
        badWaiBillTypes_zt.put("淘宝“大促”", "C3");
        badWaiBillTypes_zt.put("突发情况下不能及时送达", "B7");
        badWaiBillTypes_zt.put("退件/改寄件", "A13");
        badWaiBillTypes_zt.put("无电话、无收件人、无此人", "A4");
        badWaiBillTypes_zt.put("已电联，按预约时间派送", "A9");
        badWaiBillTypes_zt.put("有单无货", "B9");
        badWaiBillTypes_zt.put("政府干涉", "C2");
        badWaiBillTypes_zt.put("自取未取", "A11");
        badWaiBillTypes_zt.put("自然灾害", "C1");
        badWaiBillTypes_zt.put("自提件", "A10");
        badWaiBillTypes_zt.put("签收未收到", "B17");
        badWaiBillTypes_zt.put("催查件", "B16");
        badWaiBillTypes_zt.put("进仓件", "B13");
        badWaiBillTypes_zt.put("双面单", "B11");
        badWaiBillTypes_zt.put("非中通包装", "B14");
        badWaiBillTypes_zt.put("污染件", "B12");
        badWaiBillTypes_zt.put("节假日快件,假后派送", "A16");
        badWaiBillTypes_zt.put("空包件", "A15");

    }

    public static android.support.v4.util.ArrayMap<String, String> brandMap = new android.support.v4.util.ArrayMap<>();

    static {
        brandMap.put(E3SysManager.BRAND_STO, "申通");
        brandMap.put(E3SysManager.BRAND_ZT, "中通");
        brandMap.put(E3SysManager.BRAND_QF, "全峰");
    }

    private E3SysManager() {
    }

    /**
     * 获取问题类型对应编号
     *
     * @param badWaiBillType
     * @return
     */
    public static int getBadWaiBillTypeId(String badWaiBillType) {
        try {
            return badWaiBillTypes_sto.get(badWaiBillType);
        } catch (java.lang.Exception e) {
            return 0;
        }

    }

    /**
     * 根据问题类型的subject获取全峰问题类型type
     *
     * @return
     */
    public synchronized static ArrayList<E3Type> getQFBadWaiBillType(String value) {
        if (TextUtils.isEmpty(value))
            return null;
        HashMap<String, String> map = badWaiBillSubjectGroup_qf.get(value);
        if (map == null)
            return null;
        ArrayList<E3Type> typeList = new ArrayList<E3Type>();
        for (String key : map.keySet()) {
            E3Type type = new E3Type();
            type.setCompany("qf");
            type.setType(key);
            typeList.add(type);
        }
        return typeList;
    }

    /**
     * 获取中通问题类型对应编号
     *
     * @param badWaiBillType
     * @return
     */
    public static String getZTBadWaiBillTypeId(String badWaiBillType) {
        try {
            return badWaiBillTypes_zt.get(badWaiBillType);
        } catch (java.lang.Exception e) {
            return "";
        }

    }

    /**
     * 获取申通所有问题件类型
     *
     * @return
     */
    public static List<E3Type> getSTOBadWaiBillTypes() {
        List<E3Type> types = new ArrayList<>();
        for (String key : badWaiBillTypes_sto.keySet()) {
            E3Type e3Type = new E3Type();
            e3Type.setCompany(BRAND_STO);
            e3Type.setType(key);
            types.add(e3Type);
        }
        return types;
    }

    /**
     * 获取中通所有问题件类型
     *
     * @return
     */
    public static List<E3Type> getZTBadWaiBillTypes() {
        List<E3Type> types = new ArrayList<>();
        for (String key : badWaiBillTypes_zt.keySet()) {
            E3Type e3Type = new E3Type();
            e3Type.setCompany(BRAND_ZT);
            e3Type.setType(key);
            types.add(e3Type);
        }
        return types;
    }

    /**
     * 获取所有签收类型
     *
     * @return
     */
    public static synchronized List<E3Type> getSignedTypes(String company) {
        List<E3Type> types = SkuaidiNewDB.getInstance().queryAllE3SignedType(company);
        return types;
    }

    /**
     * 批量删除图片
     */
    public static void deletePicsByNotifyInfo(List<NotifyInfo> infos) {
        for (NotifyInfo info : infos) {
            deletePic(info.getPicPath());
        }

    }

    /**
     * 根据图片位置删除图片
     *
     * @param picPath
     */
    public static void deletePic(String picPath) {
        if (!TextUtils.isEmpty(picPath)) {
            File delFile = new File(picPath);
            // 删除图片
            if (delFile != null && delFile.exists()) {
                delFile.delete();
            }
        }
    }


    /**
     * 批量删除图片
     *
     * @param orders
     */
    public static void deletePics(List<E3_order> orders) {
        for (E3_order order : orders) {
            deletePic(order.getPicPath());
        }

    }

    /**
     * 根据品牌获取相应的数据上传接口名
     *
     * @return
     */
    public static String getScanName() {
        String name = null;
        UserInfo userInfo = SkuaidiSpf.getLoginUser();
        if (BRAND_QF.equals(userInfo.getExpressNo())) {
            name = SCAN_TO_QF;
        } else if (BRAND_STO.equals(userInfo.getExpressNo())) {
            name = SCAN_TO_E3;
        } else if (BRAND_ZT.equals(userInfo.getExpressNo())) {
            name = SCAN_TO_ZT;
        }
        return name;
    }

    /**
     * 根据品牌获取相应的数据上传接口名(新版)
     *
     * @return
     */
    public static String getScanNameV2() {
        String name = null;
        UserInfo userInfo = SkuaidiSpf.getLoginUser();
        if (BRAND_QF.equals(userInfo.getExpressNo())) {
            name = SCAN_TO_QF;//全峰先不改了
        } else if (BRAND_STO.equals(userInfo.getExpressNo())) {
            name = SCAN_TO_E3_V2;
        } else if (BRAND_ZT.equals(userInfo.getExpressNo())) {
            name = SCAN_TO_ZT_V2;
        }
        return name;
    }

    /**
     * 获取工号
     *
     * @return
     */
    public static String getCourierNO() {
        String courierNO = "";
        CurrentE3VerifyInfo currentE3VerifyInfo = getReviewInfoNew();
        if (currentE3VerifyInfo != null) {
            courierNO = currentE3VerifyInfo.getCounterman_code();
        }
        return courierNO;
    }

    /**
     * 获取登录用户
     *
     * @return
     */
    public static CourierReviewInfo getReviewInfo() {
        CourierReviewInfo reviewInfo = new CourierReviewInfo();
        String localReviewInfo = UpdateReviewInfoUtil.getCurrentReviewStatus();
        CurrentE3VerifyInfo currentE3VerifyInfo = JSON.parseObject(localReviewInfo, CurrentE3VerifyInfo.class);
        if (currentE3VerifyInfo == null) {
            return reviewInfo;
        }
        reviewInfo.setCourierJobNo(currentE3VerifyInfo.getCounterman_code());
        reviewInfo.setCourierName(currentE3VerifyInfo.getCounterman_name());
        reviewInfo.setCourierLatticePoint(currentE3VerifyInfo.getShop_name());
        reviewInfo.setCourierPhone(SkuaidiSpf.getLoginUser().getPhoneNumber());
        return reviewInfo;
    }

    public static CurrentE3VerifyInfo getReviewInfoNew() {
        String localReviewInfo = UpdateReviewInfoUtil.getCurrentReviewStatus();
        CurrentE3VerifyInfo currentE3VerifyInfo = JSON.parseObject(localReviewInfo, CurrentE3VerifyInfo.class);
        if (currentE3VerifyInfo == null) {
            return new CurrentE3VerifyInfo();
        }
        return currentE3VerifyInfo;
    }

    /**
     * 获取用户所在网点名称
     *
     * @return
     */
    public static String getCourierLatticePoint() {
        String courierLatticePointName = "";
        CurrentE3VerifyInfo currentE3VerifyInfo = getReviewInfoNew();
        if (currentE3VerifyInfo != null) {
            courierLatticePointName = currentE3VerifyInfo.getShop_name();
        }
        return courierLatticePointName;
    }

    /**
     * 获取用户所在网点名称
     *
     * @return
     */
    public static String getCourierName() {
        String name = "";
        CurrentE3VerifyInfo currentE3VerifyInfo = getReviewInfoNew();
        if (currentE3VerifyInfo != null) {
            name = currentE3VerifyInfo.getCounterman_name();
        }
        return name;
    }

    /**
     * 根据站点编号获取相对应的站点名称
     *
     * @param key_str
     * @return
     */
    public static String getSiteCode(String key_str, String type) {
        Map<String, String> nextMap;
        String site_code = "";
        if (type.equals("到件")) {
            nextMap = SkuaidiSpf.getUpstation(SKuaidiApplication.getInstance());
        } else {
            nextMap = SkuaidiSpf.getNextstation(SKuaidiApplication.getInstance());
        }
        Set<String> keySet = nextMap.keySet();
        for (String key : keySet) {
            if (key.equals(key_str)) {
                site_code = nextMap.get(key);
            }
        }
        return site_code;
    }

    /**
     * 到件，获取上一站
     * 根据站点编号获取相对应的站点名称
     *
     * @return
     */
    public static String getPreSiteCode(String key_str) {
        Map<String, String> preMap;
        String site_code = "";
        preMap = SkuaidiSpf.getUpstation(SKuaidiApplication.getInstance());
        Set<String> keySet = preMap.keySet();
        for (String key : keySet) {
            if (key.equals(key_str)) {
                site_code = preMap.get(key);
            }
        }
        return site_code;

    }

    /**
     * 发件，获取下一站
     * 根据站点编号获取相对应的站点名称
     *
     * @return
     */
    public static String getNextSiteCode(String key_str) {
        Map<String, String> nextMap;
        String site_code = "";
        nextMap = SkuaidiSpf.getNextstation(SKuaidiApplication.getInstance());
        Set<String> keySet = nextMap.keySet();
        for (String key : keySet) {
            if (key.equals(key_str)) {
                site_code = nextMap.get(key);
            }
        }
        return site_code;

    }


    /**
     * @param orders     isupload 是否已经上传过
     * @param dataSource
     * @return
     */
    public static List<NotifyInfo> orderToInfo(List<E3_order> orders, int isUpload, int isCache, String scanType, String dataSource) {
        List<NotifyInfo> infos = new ArrayList<>();
        for (E3_order order : orders) {
            NotifyInfo info = new NotifyInfo();
            info.setExpress_number(order.getOrder_number());
            if (!"record".equals(dataSource)) {
                info.setWayBillTypeForE3(order.getType_extra());
            }
            info.setCourierJobNO(order.getOperatorCode());
            CourierReviewInfo currentE3VerifyInfo = getReviewInfo();
            if (scanType.equals(E3SysManager.SCAN_TYPE_LANPICE) && TextUtils.isEmpty(order.getOperatorCode())) {// 收件
                info.setCourierJobNO(currentE3VerifyInfo.getCourierJobNo());
                info.setWayBillTypeForE3(currentE3VerifyInfo.getCourierName());
            } else if (scanType.equals(E3SysManager.SCAN_TYPE_FAPICE) && TextUtils.isEmpty(order.getOperatorCode())) {// 发件
                info.setCourierJobNO(currentE3VerifyInfo.getCourierJobNo());
                info.setWayBillTypeForE3(currentE3VerifyInfo.getCourierName());
            } else if (scanType.equals(E3SysManager.SCAN_TYPE_DAOPICE)) {// 到件

            } else if (scanType.equals(E3SysManager.SCAN_TYPE_PIEPICE) && TextUtils.isEmpty(order.getOperatorCode())) {// 派件
                info.setCourierJobNO(currentE3VerifyInfo.getCourierJobNo());
                info.setWayBillTypeForE3(currentE3VerifyInfo.getCourierName());
            } else if (scanType.equals(E3SysManager.SCAN_TYPE_BADPICE)) {// 问题件

            } else if (scanType.equals(E3SysManager.SCAN_TYPE_SIGNEDPICE)) {// 签收件

            }
            info.setStation_name(order.getSta_name());
            info.setPicPath(order.getPicPath());
            info.setProblem_desc(order.getProblem_desc());

            info.setScanTime(order.getScan_time());
            info.setLatitude(order.getLatitude());
            info.setLongitude(order.getLongitude());
            order.setIsUpload(isUpload);
            order.setIsCache(isCache);
            infos.add(info);
        }
        return infos;
    }

    /**
     * @param notifyInfos 运单数据
     * @param isUpload    是否已经上传过
     * @param isCache     是否是缓存
     * @return ArrayList<E3_order> 可保存的数据集
     */
    public static ArrayList<E3_order> infoToOrder(String scanType, List<NotifyInfo> notifyInfos, int isUpload, int isCache) {
        ArrayList<E3_order> orders = new ArrayList<>();
        UserInfo mUserInfo = SkuaidiSpf.getLoginUser();
        for (int i = 0; i < notifyInfos.size(); i++) {

            E3_order order = new E3_order();
            NotifyInfo info = notifyInfos.get(i);
            order.setOrder_number(info.getExpress_number());
            order.setType_extra(info.getWayBillTypeForE3());
            order.setFirmname(mUserInfo.getExpressNo());
            order.setOrder_weight(info.getWeight());
            order.setResType(info.getResType());
            if ("扫收件".equals(scanType)) {
                order.setType("收件");
                order.setOperatorCode(TextUtils.isEmpty(info.getCourierJobNO()) ? getCourierNO() : info.getCourierJobNO());
            } else if ("扫派件".equals(scanType)) {
                order.setType("派件");
                order.setOperatorCode(TextUtils.isEmpty(info.getCourierJobNO()) ? getCourierNO() : info.getCourierJobNO());
            } else if ("扫到件".equals(scanType)) {
                order.setType("到件");
                order.setSta_name(info.getStation_name());
            } else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                order.setType("签收件");
                if (!TextUtils.isEmpty(info.getPicPath())) {
                    order.setPicPath(info.getPicPath());
                    order.setWayBillType_E3("图片签收");
                    order.setType_extra("图片签收");// 两个字段表示同一意思
                } else {
                    order.setWayBillType_E3(info.getWayBillTypeForE3());
                    order.setType_extra(info.getWayBillTypeForE3());
                }
            } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
                order.setType("第三方签收");
                //签收营业厅
                order.setThirdBranch(info.getThirdBranch());
                order.setThirdBranchId(info.getThirdBranchId());
                //签收营业厅
            } else if ("问题件".equals(scanType)) {
                order.setType("问题件");
                order.setWayBillType_E3(info.getWayBillTypeForE3());
                order.setProblem_desc(info.getProblem_desc());
                order.setPhone_number(info.getPhone_number());
            } else if ("扫发件".equals(scanType)) {// 发件不指定操作员
                order.setType("发件");
                order.setSta_name(info.getStation_name());
            } else {
                try {
                    throw new Exception("scanType 错误");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            order.setScan_time(info.getScanTime());
            order.setCompany(mUserInfo.getExpressNo());
            order.setCourier_job_no(getCourierNO());
            order.setIsUpload(isUpload);
            order.setIsCache(isCache);
            order.setLatitude(info.getLatitude());
            order.setLongitude(info.getLongitude());
            orders.add(order);

        }
        return orders;
    }


    /**
     * 验证是否为申通单号
     *
     * @param waybillNo
     * @return
     */
    public static boolean isSTOWaybillNo(String waybillNo) {
//        $rule_conf = [
//        'verify' => [
//        '/^(sto|STO|[2-9]68)\d{9}$/',
//                '/^(588|688|888|900|((11|22)\d{1})|((33|44|55|77|99)\d{2})|((66|88)([0-7]|9)\d{1}))\d{9}$/',
//        ],
//        'free' => [
//        '/^4[012356789][0-9]{10}$/'
//        ],
//
//        ];

        Pattern mPattern = Pattern.compile(STO_EXPRESS_NO_REGEX);
        Matcher mMatcher = mPattern.matcher(waybillNo);
        if (mMatcher.matches()) {
            return true;
        } else {
            Pattern sPattern = Pattern.compile(STO_EXPRESS_NO_REGEX2);
            Matcher sMatcher = sPattern.matcher(waybillNo);
            if (sMatcher.matches()) {
                //        if(strlen($num) != 13){
//            return TRUE;
//        }
//        $val = substr($num, 3, 9);
//        $v = $val[0]*7;
//        $v += $val[1]*9;
//        $v += $val[2]*5;
//        $v += $val[3]*3;
//        $v += $val[4]*2;
//        $v += $val[5]*4;
//        $v += $val[6]*6;
//        $v += $val[7]*8;
//        $v += $val[8]*9;
//        $remainder = $v%11;
//        switch($remainder){
//            case 0:
//                $validCode = 5;
//                break;
//            case 1:
//                $validCode = 0;
//                break;
//            default:
//                $validCode = 11-$remainder;
//        }
//        return $validCode == substr($num, 12, 1) ? TRUE : FALSE;

                if (waybillNo.length() != 13) {
                    return true;
                }
                String str = waybillNo.substring(3, 12);
                int a = (str.charAt(0) - '0') * 7;
                a += (str.charAt(1) - '0') * 9;
                a += (str.charAt(2) - '0') * 5;
                a += (str.charAt(3) - '0') * 3;
                a += (str.charAt(4) - '0') * 2;
                a += (str.charAt(5) - '0') * 4;
                a += (str.charAt(6) - '0') * 6;
                a += (str.charAt(7) - '0') * 8;
                a += (str.charAt(8) - '0') * 9;
                int remainder = a % 11;
                int validCode;
                switch (remainder) {
                    case 0:
                        validCode = 5;
                        break;
                    case 1:
                        validCode = 0;
                        break;
                    default:
                        validCode = 11 - remainder;

                }
                return validCode == Integer.parseInt(waybillNo.substring(12, 13));
            }
        }

        return false;
    }

    /**
     * 验证是否为申通单号
     *
     * @param waybillNo
     * @return
     */
    public static boolean isZTWaybillNo(String waybillNo) {
        String ZT_EXPRESS_NO_REGEX = SkuaidiSpf.getZTExpressNOregulation();
        KLog.d("re", ZT_EXPRESS_NO_REGEX);
        if (TextUtils.isEmpty(ZT_EXPRESS_NO_REGEX))
            return (waybillNo.length() > 7 && waybillNo.length() < 17);
        Pattern pattern = Pattern.compile(ZT_EXPRESS_NO_REGEX);
        Matcher matcher = pattern.matcher(waybillNo);
        return matcher.matches();
    }

    /**
     * 验证单号是否合法
     *
     * @param waybillNo
     * @return
     */
    public static boolean isValidWaybillNo(String waybillNo) {
        UserInfo userInfo = SkuaidiSpf.getLoginUser();
        if (E3SysManager.BRAND_STO.equals(userInfo.getExpressNo())) {
            return E3SysManager.isSTOWaybillNo(waybillNo);
        } else if (E3SysManager.BRAND_ZT.equals(userInfo.getExpressNo())) {
            return E3SysManager.isZTWaybillNo(waybillNo);
        } else if (E3SysManager.BRAND_QF.equals(userInfo.getExpressNo())) {
            return (waybillNo.length() > 7 && waybillNo.length() < 17);
        } else {
            return true;
        }
    }

    /**
     * 获取当前时间，用来设置scanTime (扫描时间)
     * 中通要求与中通服务器时间一致
     *
     * @return
     */
    public static String getTimeBrandIndentify() {
        return Utility.getSMSCurTime2();
    }

    /**
     * 构造可直接上传的数据，单条数据
     *
     * @param scanType 扫描类型
     * @param info
     * @return
     */
    public static Map<String, String> buildUploadableData(String scanType, NotifyInfo info) {

        JSONArray wayBills = new JSONArray();
        JSONObject wayBill = new JSONObject();
        String brand = SkuaidiSpf.getLoginUser().getExpressNo();
        Map<String, String> dataMap = new HashMap<>();
        try {
            if (BRAND_STO.equals(brand)) {
                // 申通
                if (E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)
                        || E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {// 发件不指定发件员
                    wayBill.put("operatorCode",
                            TextUtils.isEmpty(info.getCourierJobNO()) ? getCourierNO()
                                    : info.getCourierJobNO());
                } else if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                    if (!TextUtils.isEmpty(info.getPicPath())) {
                        wayBill.put("signPic", Utility.bitMapToString(Utility.getImage(info.getPicPath())));
                    } else {
                        wayBill.put("signType", info.getWayBillTypeForE3());
                    }
                    dataMap.put("sendSms", String.valueOf(SkuaidiSpf.getAutoSignNotify(getCourierNO()) ? 1 : 0));
                } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
                    wayBill.put("badWayBillCode", E3SysManager.getBadWaiBillTypeId(info.getWayBillTypeForE3()));
                    if (E3SysManager.getBadWaiBillTypeId(info.getWayBillTypeForE3()) == 0) {
                        wayBill.put("badWayBillType", info.getWayBillTypeForE3());
                    }
                    wayBill.put("mobile", info.getPhone_number());
                    wayBill.put("badWayBillDesc", info.getProblem_desc());
                    dataMap.put("sendSms", String.valueOf(SkuaidiSpf.getAutoProblemNotify(getCourierNO()) ? 1 : 0));
                } else if (E3SysManager.SCAN_TYPE_DAOPICE.equals(scanType)) {
                    // 站点编号
                    wayBill.put("forward_station", getPreSiteCode(info.getStation_name()));
                } else if (E3SysManager.SCAN_TYPE_FAPICE.equals(scanType)) {
                    // 站点编号
                    wayBill.put("next_station", getNextSiteCode(info.getStation_name()));
                }

            } else if (BRAND_QF.equals(brand)) {// 全峰
                if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                    if (!TextUtils.isEmpty(info.getPicPath())) {
                        wayBill.put("signPic", Utility.bitMapToString(Utility.getImage(info.getPicPath())));
                    } else {
                        wayBill.put("signType", info.getWayBillTypeForE3());
                    }
                    wayBill.put("delivery_status", "签收");// 运单状态详细说明
                    wayBill.put("sign_man", "unknow");// 签收人

                } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
                    String[] badDesc = info.getWayBillTypeForE3().split("\n");
                    String badSubject = badDesc[0];
                    String badType = badDesc[1];
                    wayBill.put("type", badSubject);// 问题件subject
                    wayBill.put("register_site", getCourierLatticePoint());// 录入网点
                    wayBill.put("send_site", "unknow");// 寄件网点
                    wayBill.put("scan_site", getCourierLatticePoint());// 扫描网点
                    wayBill.put("register_man", getCourierName());// 录入人
                    wayBill.put("problem_cause", badType);// 问题件具体类型
                    wayBill.put("register_man_department", "unknow");
                    wayBill.put("mobile", info.getPhone_number());

                } else if (E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)
                        || E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {// 发件不指定发件员
                    wayBill.put("operatorCode",
                            TextUtils.isEmpty(info.getCourierJobNO()) ? getCourierNO()
                                    : info.getCourierJobNO());
                }

            } else if (BRAND_ZT.equals(brand)) {// 中通

                if (E3SysManager.SCAN_TYPE_SIGNEDPICE.equals(scanType)) {
                    if (!TextUtils.isEmpty(info.getPicPath())) {
                        wayBill.put("signPic", Utility.bitMapToString(Utility.getImage(info.getPicPath())));
                    } else {
                        wayBill.put("signType", info.getWayBillTypeForE3());
                    }
                    dataMap.put("sendSms", String.valueOf(SkuaidiSpf.getAutoSignNotify(getCourierNO()) ? 1 : 0));

                } else if (E3SysManager.SCAN_TYPE_SIGNED_THIRD_PARTY.equals(scanType)) {
                    wayBill.put("thirdBranch", info.getThirdBranch());
                    wayBill.put("thirdBranchId", info.getThirdBranchId());
                } else if (E3SysManager.SCAN_TYPE_BADPICE.equals(scanType)) {
                    wayBill.put("question_desc", info.getProblem_desc());// 问题件内容
                    wayBill.put("badWayBillCode",
                            E3SysManager.getZTBadWaiBillTypeId(info.getWayBillTypeForE3()));
                    wayBill.put("mobile", info.getPhone_number());
                    dataMap.put("sendSms", String.valueOf(SkuaidiSpf.getAutoProblemNotify(getCourierNO()) ? 1 : 0));
                } else if (E3SysManager.SCAN_TYPE_LANPICE.equals(scanType)
                        || E3SysManager.SCAN_TYPE_PIEPICE.equals(scanType)) {// 发件不指定发件员
                    wayBill.put("operatorCode",
                            TextUtils.isEmpty(info.getCourierJobNO()) ? getCourierNO()
                                    : info.getCourierJobNO());
                }
            }
            JSONObject location = new JSONObject();
            location.put("latitude", info.getLatitude());
            location.put("longitude", info.getLongitude());
            wayBill.put("location", location);
            wayBill.put("waybillNo", info.getExpress_number());
            wayBill.put("scan_time", info.getScanTime());
            wayBill.put("weight", info.getWeight());
            wayBill.put("resType", info.getResType());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        wayBills.put(wayBill);


        dataMap.put("sname", E3SysManager.getScanNameV2());
        dataMap.put("appVersion", String.valueOf(Utility.getVersionCode()));
        dataMap.put("wayBillType", E3SysManager.typeToIDMap.get(scanType) + "");
        dataMap.put("dev_id", Utility.getOnlyCode());
        TelephonyManager tm = (TelephonyManager) SKuaidiApplication.getContext().getSystemService(TELEPHONY_SERVICE);
        dataMap.put("dev_imei", tm.getDeviceId());
        dataMap.put("wayBillDatas", wayBills.toString());
        return dataMap;

    }

}
