package com.kuaibao.skuaidi.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.kuaibao.skuaidi.util.KuaiBaoStringUtilToolkit;
import com.kuaibao.skuaidi.util.UtilToolkit;
import com.socks.library.KLog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMusic;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * 友盟分享
 * @author xy
 *
 */

public class UMShareManager{
	/**
	 * QQ
	 */
	public static final String SHARE_PLATFORM_QQ = "QQ";
	
	/**
	 * 微信
	 */
	public static final String SHARE_PLATFORM_WX = "WEIXIN";
	/**
	 * 朋友圈
	 */
	public static final String SHARE_PLATFORM_CIRCLE_WX = "WEIXIN_CIRCLE";
	/**
	 * 短信
	 */
	public static final String SHARE_PLATFORM_SMS = "SMS";
	/**
	 * 邮件
	 */
	public static final String SHARE_PLATFORM_EMAIL = "EMAIL";
	/**
	 * 新浪微博
	 */
	public static final String SHARE_PLATFORM_SINA = "SINA";
	/**
	 * 腾讯微博
	 */
	public static final String SHARE_PLATFORM_TENCENT = "TENCENT";
	/**
	 * QQ空间
	 */
	public static final String SHARE_PLATFORM_QQZONE = "QZONE";
	
	
	
//	private static final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
	
	 /**
     * 配置分享平台参数</br>
     */
/*    private static void configPlatforms(Activity context) {
         添加新浪SSO授权
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        // 添加腾讯微博SSO授权
        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        //邮件
        mController.getConfig().setSsoHandler(new EmailHandler());
      	//短信
      	mController.getConfig().setSsoHandler(new SmsHandler());
//        // 添加人人网SSO授权
//        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(getActivity(),
//                "201874", "28401c0964f04a72a14c812d6132fcef",
//                "3bf66e42db1e4fa9829b955cc300b737");
//        mController.getConfig().setSsoHandler(renrenSsoHandler);

        // 添加QQ、QZone平台
        addQQQZonePlatform(context);

        // 添加微信、微信朋友圈平台
        addWXPlatform(context);
		//微信    wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
		PlatformConfig.setWeixin("wx389287be6cfdb2fa", "ab5c0ba4e3821b85cfb1d2d4c45c43f9");
		//豆瓣RENREN平台目前只能在服务器端配置
		//新浪微博
		PlatformConfig.setSinaWeibo("4283839089", "872d74a2473da24ce0e796b018dde287");
		//qq空间
		PlatformConfig.setQQZone("1101129450", "elxPliBUHg3hEHtT");
    }*/


//
//    /**
//     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
//     *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
//     *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
//     *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
//     * @return
//     */
//    private static void addQQQZonePlatform(Activity context) {
//        //String appId = "101122701";
////    	String appId = "201181226";
////        String appKey = "0fb86e236ed8d1a090f0de092f534268";
//		String appId = "1101129450";
//		String appKey = "elxPliBUHg3hEHtT";
//        // 添加QQ支持, 并且设置QQ分享内容的target url
//        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(context,
//                appId, appKey);
//        if(qqSsoHandler!=null)
//        qqSsoHandler.addToSocialSDK();
//
//        // 添加QZone平台
//        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(context, appId, appKey);
//        qZoneSsoHandler.addToSocialSDK();
//    }
//
//    /**
//     * @功能描述 : 添加微信平台分享
//     * @return
//     */
//    private static void addWXPlatform(Activity context) {
//        // 注意：在微信授权的时候，必须传递appSecret
//        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
//        String appId = "wx389287be6cfdb2fa";
//        String appSecret = "ab5c0ba4e3821b85cfb1d2d4c45c43f9";
//        // 添加微信平台
//        UMWXHandler wxHandler = new UMWXHandler(context, appId, appSecret);
//        wxHandler.addToSocialSDK();
//
//        // 支持微信朋友圈
//        UMWXHandler wxCircleHandler = new UMWXHandler(context, appId, appSecret);
//        wxCircleHandler.setToCircle(true);
//        wxCircleHandler.addToSocialSDK();
//    }
//

    /**
     * 根据不同的平台设置不同的分享内容</br>
     */
    private static void setShareContent(final Activity context ,final String title,Map<String,String> shareTexts,String targetUrl,int drawableId) {

//    	QQShareContent qqShareContent = new QQShareContent;
//    	CircleShareContent circleShareContent = new CircleShareContent();
//    	 MailShareContent  mailShareContent = new MailShareContent();
//    	 QZoneShareContent qZoneShareContent = new QZoneShareContent();
//    	 SinaShareContent sinaShareContent = new SinaShareContent();
//    	 SmsShareContent smsShareContent = new SmsShareContent();
//    	 TencentWbShareContent tencentWbShareContent = new TencentWbShareContent();
//    	 WeiXinShareContent weixinContent = new WeiXinShareContent();

		//设置标题
//		if(!TextUtils.isEmpty(title)){
//			qZoneShareContent.setTitle(title);
//			qqShareContent.setTitle(title);
//			weixinContent.setTitle(title);
//			circleShareContent.setTitle(circleShareContent.getShareContent());//朋友圈显示的是标题
//		}
//		// 设置分享图片, 参数2为图片的url地址
//		/*if(!isBlank(imgUrl)&&(imgUrl.startsWith("http://")||imgUrl.startsWith("https://"))){
//			UMImage umImage = new UMImage(context, imgUrl);
//			qZoneShareContent.setShareImage(umImage);
//			qqShareContent.setShareImage(umImage);
//			weixinContent.setShareImage(umImage);
//			CircleShareContent.setShareImage(umImage);
//			mController.setShareImage(umImage);
//		}else{*/
//			UMImage umImage = new UMImage(SKuaidiApplication.getInstance(), drawableId);
//			qZoneShareContent.setShareImage(umImage);
//			qqShareContent.setShareImage(umImage);
//			weixinContent.setShareImage(umImage);
//			circleShareContent.setShareImage(umImage);
//			mController.setShareMedia(umImage);
//		/*}*/
//
//		//跳转地址
//		if(!TextUtils.isEmpty(targetUrl)&&
//				(targetUrl.startsWith("http://")||targetUrl.startsWith("https://"))){
//			qZoneShareContent.setTargetUrl(targetUrl);
//			qqShareContent.setTargetUrl(targetUrl);
//			weixinContent.setTargetUrl(targetUrl);
//			circleShareContent.setTargetUrl(targetUrl);
//		}else{
//			targetUrl = "http://ckd.so/2";
//			qZoneShareContent.setTargetUrl(targetUrl);
//			qqShareContent.setTargetUrl(targetUrl);
//			weixinContent.setTargetUrl(targetUrl);
//			circleShareContent.setTargetUrl(targetUrl);
//		}
//
////		qqSsoHandler.addToSocialSDK();
////		qZoneSsoHandler.addToSocialSDK();
////		wxHandler.addToSocialSDK();
////		wxCircleHandler.addToSocialSDK();
//
//		mController.setShareMedia(qqShareContent);
//		mController.setShareMedia(qZoneShareContent);
//		mController.setShareMedia(weixinContent);
//		mController.setShareMedia(circleShareContent);
//		mController.setShareMedia(sinaShareContent);
//		mController.setShareMedia(smsShareContent);
//		mController.setShareMedia(mailShareContent);
//		mController.setShareMedia(tencentWbShareContent);
//
//		mController.getConfig().registerListener(new SocializeListeners.SnsPostListener() {
//            @Override
//            public void onStart() {
//                //Toast.makeText(activity, "分享开始",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onComplete(SHARE_MEDIA share_media, int stCode, SocializeEntity socializeEntity) {
//            	if(title.equals("快递小哥送外卖")){
//            		return;
//            	}
//            	 if (stCode == 200) {
//            		 switch (share_media) {
//					case SINA://新浪
//						//Log.i("utilily","新浪微博");
////						Intent intent = new Intent(context, ShareCallbackActivity.class);
////						context.startActivity(intent);
//						break;
//					case TENCENT://腾讯微博
//						//Log.i("utilily","腾讯微博");
////						Intent intent1 = new Intent(context, ShareCallbackActivity.class);
////						context.startActivity(intent1);
//						break;
//					case QQ://QQ好友
//						//Log.i("utilily","QQ好友");
////						Intent intent2 = new Intent(context, ShareCallbackActivity.class);
////						context.startActivity(intent2);
//						break;
//					case QZONE://QQ空间
//						//Log.i("utilily","QQ空间");
////						Intent intent3 = new Intent(context, ShareCallbackActivity.class);
////						context.startActivity(intent3);
//						break;
//					case EMAIL://邮件
////						Intent intent4 = new Intent(context, ShareCallbackActivity.class);
////						context.startActivity(intent4);
//						break;
//					case SMS://短信
////						Intent intent5 = new Intent(context, ShareCallbackActivity.class);
////						context.startActivity(intent5);
//						break;
//					case WEIXIN://微信好友
////						Intent intent6 = new Intent(context, ShareCallbackActivity.class);
////						context.startActivity(intent6);
//						break;
//					case WEIXIN_CIRCLE://微信朋友圈
////						Intent intent7 = new Intent(context, ShareCallbackActivity.class);
////						context.startActivity(intent7);
//						break;
//
//					default:
//						break;
//					}
////        			Toast.makeText(context, "   分享成功", Toast.LENGTH_SHORT)
////			            .show();
//			      } else {
////			        Toast.makeText(context,
////			        		"分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
////			            .show();
//			      }
//            }
//        });
//
    }
	private static final UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			KLog.d("kb","platform"+platform);
//			if(platform.name().equals("WEIXIN_FAVORITE")){
//				UtilToolkit.showToast("收藏成功啦");
//			}else{
//				UtilToolkit.showToast("分享成功啦");
//			}
		}
		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			UtilToolkit.showToast("分享失败啦");
		}
		@Override
		public void onCancel(SHARE_MEDIA platform) {
			UtilToolkit.showToast("分享取消了");
		}
	};

	public static void openShareNetCall(final Activity context, String title, final Map<String,String> shareTexts, final String targetUrl, final int drawableId,String musicUrl){
		final String shareTitle;
		if(TextUtils.isEmpty(title)){
			shareTitle = "微快递业务员端";
		}else{
			shareTitle = title;
		}
		if(shareTexts == null){
			UtilToolkit.showToast("分享的文本不能为空！");
			return;
		}
		for (String key : shareTexts.keySet()) {
			if (KuaiBaoStringUtilToolkit.isEmpty(shareTexts.get(key))) {
				UtilToolkit.showToast("分享的文本不能为空！");
				return;
			}
		}
		final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
						SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA,
						SHARE_MEDIA.SMS,SHARE_MEDIA.EMAIL};
		final UMImage image = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), drawableId));
		final UMusic music = new UMusic(musicUrl);
		music.setTitle(shareTitle);
		music.setThumb(image);
		new ShareAction(context).setDisplayList(displaylist)
				.withTitle(shareTitle)
				.setShareboardclickCallback(new ShareBoardlistener() {
					@Override
					public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
						KLog.d("kb",share_media.name());
						switch(share_media.name()){
							case "WEIXIN":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.WEIXIN)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_WX))
										.withTargetUrl(targetUrl)
										.withMedia(image)
										.share();
								break;
							case "WEIXIN_CIRCLE":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_CIRCLE_WX))
										.withTargetUrl(targetUrl)
										.withMedia(image)
										.share();
								break;
							case "QQ":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.QQ)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_QQ))
										.withTargetUrl(targetUrl)
										.withMedia(image)
										.share();
								break;
							case "QZONE":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.QZONE)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_QQZONE))
										.withTargetUrl(targetUrl)
										.withMedia(image)
										.share();
								break;
							case "SINA":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.SINA)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_SINA))
										.withTargetUrl(targetUrl)
										.withMedia(music)
										.share();
								break;
							case "SMS":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.SMS)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_SMS))
										.withMedia(music)
										.share();
								break;
							case "EMAIL":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.EMAIL)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_EMAIL))
										.withMedia(music)
										.share();
								break;
						}
					}
				}).open();
	}


	public static void openShare(final Activity context, String title, final Map<String,String> shareTexts, final String targetUrl, final int drawableId){
		final String shareTitle;
    	if(TextUtils.isEmpty(title)){
			shareTitle = "微快递业务员端";
    	}else{
			shareTitle = title;
		}
    	if(shareTexts == null){
    		UtilToolkit.showToast("分享的文本不能为空！");
			return;
    	}
    	for (String key : shareTexts.keySet()) {
			if (KuaiBaoStringUtilToolkit.isEmpty(shareTexts.get(key))) {
				UtilToolkit.showToast("分享的文本不能为空！");
				return;
			}
		}
		final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]{SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
				SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA,
				SHARE_MEDIA.SMS,SHARE_MEDIA.EMAIL};
		final UMImage image = new UMImage(context, BitmapFactory.decodeResource(context.getResources(), drawableId));
		new ShareAction(context).setDisplayList(displaylist)
				.withTitle(shareTitle)
				.setShareboardclickCallback(new ShareBoardlistener() {
					@Override
					public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
						KLog.d("kb",share_media.name());
						switch(share_media.name()){
							case "WEIXIN":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.WEIXIN)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_WX))
										.withTargetUrl(targetUrl)
										.withMedia(image)
										.share();
								break;
							case "WEIXIN_CIRCLE":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_CIRCLE_WX))
										.withTargetUrl(targetUrl)
										.withMedia(image)
										.share();
								break;
							case "QQ":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.QQ)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_QQ))
										.withTargetUrl(targetUrl)
										.withMedia(image)
										.share();
								break;
							case "QZONE":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.QZONE)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_QQZONE))
										.withTargetUrl(targetUrl)
										.withMedia(image)
										.share();
								break;
							case "SINA":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.SINA)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_SINA))
										.withTargetUrl(targetUrl)
										.share();
								break;
							case "SMS":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.SMS)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_SMS))
										.share();
								break;
							case "EMAIL":
								new ShareAction(context)
										.setPlatform(SHARE_MEDIA.EMAIL)
										.setCallback(umShareListener)
										.withTitle(shareTitle)
										.withText(shareTexts.get(UMShareManager.SHARE_PLATFORM_EMAIL))
										.share();
								break;
						}
					}
				}).open();
//		configPlatforms(context);
//    	setShareContent(context,title, shareTexts, targetUrl,drawableId);
//    	mController.getConfig().setPlatformOrder(SHARE_MEDIA.SINA,
//				SHARE_MEDIA.TENCENT, SHARE_MEDIA.WEIXIN_CIRCLE,
//				SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.EMAIL,
//				SHARE_MEDIA.SMS, SHARE_MEDIA.QQ);
//    	mController.openShare(context, false);
    } 
	
    /**
	 * 友盟统计点击次数
	 * 
	 * @param id
	 *            为事件ID
	 * @param parmsKey
	 *            为当前事件的属性
	 * @param parmsV
	 *            为当前事件的取值
	 */
	public static void onEvent(Context context, String id, String parmsKey,
			String parmsV) {
		HashMap<String, String> m = new HashMap<String, String>();
		m.put(parmsKey, parmsV);
		MobclickAgent.onEvent(context, id, m);
		
	}

}
