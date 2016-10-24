package com.kuaibao.skuaidi.manager;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;

import java.util.Map;
import java.util.Set;

/**
 * 短信管理器
 * @author xy
 *
 */
public class SKuaidiSMSManager {
	private static final String SMS_SEND_ACTION = "SMS_SEND__ACTION";
	private static final String SMS_DELIVERED_ACTION = "SMS_DELIVERED_ACTION";
	private static final Uri SMS_URI = Uri.parse("content://sms/");
	/**
	 * 发送短信
	 * @param sendNum
	 * @param sendMessage
	 */
	public static void sendSMSMessage(String sendNum,Set<String> addr,String sendMessage,Map<String,String> splitJointStr,Context context,long id) {
		SmsManager smsManager = SmsManager.getDefault();

		/* 创建自定义Action常数的Intent(给PendingIntent参数之用) */
		Intent itSend = new Intent(SMS_SEND_ACTION);
		
		/* sentIntent参数为传送后接受的广播信息PendingIntent */
		PendingIntent mSendPI = PendingIntent.getBroadcast(context, 0,itSend,0);
		
		Intent itDeliver = new Intent(SMS_DELIVERED_ACTION);
		
		/* deliveryIntent参数为送达后接受的广播信息PendingIntent */
		PendingIntent mDeliverPI = PendingIntent.getBroadcast(context, 0,itDeliver, 0);
				
				
		// 发送短信
		if(!TextUtils.isEmpty(sendNum)){
			 if(splitJointStr!=null){
				 smsManager.sendTextMessage(sendNum, null, sendMessage+(TextUtils.isEmpty(splitJointStr.get(sendNum))?"":splitJointStr.get(sendNum)), mSendPI,mDeliverPI);
			 }else{
				 smsManager.sendTextMessage(sendNum, null, sendMessage, mSendPI,mDeliverPI);
	         }
			 ContentValues cv = new ContentValues();
			 cv.put("thread_id", id);
             cv.put("date", System.currentTimeMillis());
             if(splitJointStr!=null){
          	   cv.put("body", sendMessage+(TextUtils.isEmpty(splitJointStr.get(sendNum))?"":splitJointStr.get(sendNum)));
             }else{
          	   cv.put("body", sendMessage);
             }
             cv.put("read", 0);
             cv.put("type", 2);
             cv.put("address", sendNum);
             context.getContentResolver().insert(SMS_URI, cv);
		}else{
			  ContentValues cv = new ContentValues();
              for(String pno:addr ){
            	  if(splitJointStr!=null){
            		  smsManager.sendTextMessage(pno, null, sendMessage+(TextUtils.isEmpty(splitJointStr.get(pno))?"":splitJointStr.get(pno)), mSendPI, mDeliverPI);
            	  }else{
            		  smsManager.sendTextMessage(pno, null, sendMessage, mSendPI, mDeliverPI);
                  }
                   cv.put("thread_id", id);
                   cv.put("date", System.currentTimeMillis());
                   if(splitJointStr!=null){
                	   cv.put("body", sendMessage+(TextUtils.isEmpty(splitJointStr.get(pno))?"":splitJointStr.get(pno)));
                   }else{
                	   cv.put("body", sendMessage);
                   }
                   cv.put("read", 0);
                   cv.put("type", 2);
                   cv.put("address", pno);
                   context.getContentResolver().insert(SMS_URI, cv);
             }
		}
	}
}
