package com.kuaibao.skuaidi.circle.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kuaibao.skuaidi.R;
import com.kuaibao.skuaidi.activity.ImagePagerActivity;
import com.kuaibao.skuaidi.activity.adapter.CircleExpressItemImageAdapter;
import com.kuaibao.skuaidi.circle.CircleExpressDetailActivity;
import com.kuaibao.skuaidi.circle.listener.CircleItemCallBack;
import com.kuaibao.skuaidi.circle.widget.PeriscopeLayout;
import com.kuaibao.skuaidi.manager.UMShareManager;
import com.kuaibao.skuaidi.sto.etrhee.sysmanager.E3SysManager;
import com.kuaibao.skuaidi.util.SkuaidiSpf;
import com.kuaibao.skuaidi.util.Utility;

import java.util.ArrayList;
import java.util.List;

import gen.greendao.bean.SKuaidiCircle;

/**
 * Created by lgg on 2016/8/17 17:19.
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
 * #                     no bug forever                #
 * #                                                   #
 */
public class CircleListAdapter extends BaseQuickAdapter<SKuaidiCircle> {
    private Context mContext;
    private CircleItemCallBack mCircleItemCallBack;

    public void setCircleItemCallBack(CircleItemCallBack circleItemCallBack) {
        mCircleItemCallBack = circleItemCallBack;
    }

    public CircleListAdapter(Context context, List<SKuaidiCircle> list) {
        super(R.layout.adapter_list_circler, list);
        this.mContext=context;
    }
    @Override
    protected void convert(final BaseViewHolder helper, final SKuaidiCircle item) {
        helper.setText(R.id.tv_content,item.getContent());
        helper.setVisible(R.id.tv_content, !TextUtils.isEmpty(item.getContent()));
        GridView gridView=helper.getView(R.id.gridview);
        String imageUrlsString = item.getImageurls();//拿到的是小图（照片名)
        if(!TextUtils.isEmpty(imageUrlsString) && imageUrlsString.contains("#%#")){
            String imageUrlsBigString = item.getImageurlsbig();//拿到的是大图的（照片名）
            ArrayList<String> imageUrls = new ArrayList<>();
            final ArrayList<String> imageUrlBigs = new ArrayList<>();
            String[] arraysmall = imageUrlsString.split("#%#");
            String[] arraybig = imageUrlsBigString.split("#%#");
            for(int i = 1;i<arraysmall.length;i++){
                imageUrls.add(arraysmall[i]);
                imageUrlBigs.add(arraybig[i]);
            }
            gridView.setAdapter(new CircleExpressItemImageAdapter(mContext,imageUrls));// 将小图url的集合放到adapter里面（此处需要加入完整的url）
            // 点击回帖九宫格，查看大图
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(mContext, ImagePagerActivity.class);
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, imageUrlBigs);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    mContext.startActivity(intent);
                }
            });
            gridView.setVisibility(View.VISIBLE);
        }else{
            gridView.setVisibility(View.GONE);// 没有图片就隐藏gridview
        }

        helper.setOnClickListener(R.id.relativeLayout1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UMShareManager.onEvent(mContext.getApplicationContext(), "circleExpress_detail", "circleExpress", "快递圈:吐槽详情");
                Intent intent = new Intent(mContext, CircleExpressDetailActivity.class);
                intent.putExtra("position", helper.getAdapterPosition());
                intent.putExtra("cirTuCaoInfo",item);
                mContext.startActivity(intent);
            }
        });

        helper.setText(R.id.tv_comments_num,item.getHuifu());
        helper.setText(R.id.tv_admire_num,item.getGood());
        helper.setText(R.id.tv_express_outlets,item.getMessage());
        if(E3SysManager.BRAND_STO.equals(SkuaidiSpf.getLoginUser().getExpressNo())){
            helper.setTextColor(R.id.tv_express_outlets, ContextCompat.getColor(mContext,R.color.sto_main_color));
        }else{
            helper.setTextColor(R.id.tv_express_outlets, ContextCompat.getColor(mContext,R.color.status_green));
        }
        // 设置时间
        Utility.setTimeDate(item.getUpdate_time(), (TextView)helper.getView(R.id.tv_sendTime));
        helper.setImageResource(R.id.admire,item.getIs_good()?R.drawable.circle_express_dianzan_r:R.drawable.circle_express_dianzan_w);
        // 点赞
        helper.setOnClickListener(R.id.rl_admire_parent,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(item.getId()) && mCircleItemCallBack!=null){
                    UMShareManager.onEvent(mContext.getApplicationContext(), "circleExpress_praise", "circleExpress", "快递圈:点赞");
                    PeriscopeLayout periscopeLayout=helper.getView(R.id.relativeLayout1);
                    periscopeLayout.setId(R.id.admire);
                    periscopeLayout.addHeart(!item.getIs_good());
                    mCircleItemCallBack.onClickGoodEvent(helper.getAdapterPosition(),item.getId(),"c".equals(item.getChannel())? item.getChannel():"s");
                }
            }
        });

        helper.setVisible(R.id.iv_forwarding,!"c".equals(item.getChannel()));
        // 分享
        helper.setOnClickListener(R.id.rl_iv_forwarding_parent,new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCircleItemCallBack!=null){
                    String zhuanfaContent;
                    String zhuanfaId=item.getId();
                    if(!TextUtils.isEmpty(item.getContent()) && item.getContent().length() > 25){
                        zhuanfaContent = item.getContent().substring(0, 25)+"...";
                    }else {
                        zhuanfaContent = item.getContent();
                    }
                    String zhuanfaUrl = "http://m.kuaidihelp.com/tucao/detail?topic_id=" + zhuanfaId+"&cm_id="+SkuaidiSpf.getLoginUser().getUserId();
                    mCircleItemCallBack.onClickShareEvent("快递圈-快递员必看的行业圈",zhuanfaContent,zhuanfaUrl,R.drawable.share_tucao);
                }
            }
        });

        /**
         * 如果网点用户ID是官方的，就显示将本item显示为特定的样式（此处为字体设置为橙色）
         */
        if ("25040".equals(item.getWduser_id())) {// 官方发布 (测试手机号：18888880000)
            helper.setTextColor(R.id.tv_express_outlets,Color.rgb(255, 140, 0));//#ff8c00
            helper.setTextColor(R.id.tv_content,Color.rgb(0, 0, 0));
        } else if ("25041".equals(item.getWduser_id())) {// 新鲜资讯（测试手机号：18888881111）
            helper.setTextColor(R.id.tv_express_outlets,Color.rgb(255, 140, 0));
            helper.setTextColor(R.id.tv_content,Color.rgb(0, 0, 0));
        } else if ("29299".equals(item.getWduser_id())) {// 每日一乐（测试手机号：18888886666）
            helper.setTextColor(R.id.tv_express_outlets,Color.rgb(255, 140, 0));
            helper.setTextColor(R.id.tv_content,Color.rgb(0, 0, 0));
        } else if ("25658".equals(item.getWduser_id())) {// 软件小秘书（测试手机号：18888882222）
            helper.setTextColor(R.id.tv_express_outlets,Color.rgb(255, 140, 0));
            helper.setTextColor(R.id.tv_content,Color.rgb(0, 0, 0));
        } else if ("29298".equals(item.getWduser_id())) {// 社会百态（测试手机号：18888883333）
            helper.setTextColor(R.id.tv_express_outlets,Color.rgb(255, 140, 0));
            helper.setTextColor(R.id.tv_content,Color.rgb(0, 0, 0));
        } else if ("33986".equals(item.getWduser_id())) {// 快递人生（测试手机号：18888885555）
            helper.setTextColor(R.id.tv_express_outlets,Color.rgb(255, 140, 0));
            helper.setTextColor(R.id.tv_content,Color.rgb(0, 0, 0));
        } else if ("25817".equals(item.getWduser_id())) {// 网点留言（测试手机号：18888884444）
            helper.setTextColor(R.id.tv_express_outlets,Color.rgb(255, 140, 0));
            helper.setTextColor(R.id.tv_content,Color.rgb(0, 0, 0));
        }

    }
}
