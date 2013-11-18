package com.frameworkexample.android.common.sharebind;

import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;

import android.app.Activity;
import android.util.Log;

class ShareByWeiboSDKAPI {
	
	final String TAG = ShareByWeiboSDKAPI.class.getSimpleName();
	
	
	public ShareByWeiboSDKAPI() {

	}

	// ****************************************************************
	// send message to Weibo
	public void sendMsg(final Activity context, final IWeiboAPI weiboAPI,
			final ShareBindBean bean) {
		if (null == bean) {
			return;
		}
		if (bean.hasImage()) {
			sendImageMsg(context, weiboAPI, bean);
		} else {
//			String message = "";
//			// 新浪微博的特殊内容组织
//			String title = bean.getTitle();
//			String url = bean.getUrl();
//			if (null != title && title.length() > 0) {
//				message += title + ": ";
//			}
//			message += bean.getMessage();
//			if (null != url && url.length() > 0) {
//				message += " " + url;
//			}
//			bean.setMessage(message);
			sendTextMsg(context, weiboAPI, bean);
		}
	}

	private void sendTextMsg(final Activity context, final IWeiboAPI weiboAPI,
			final ShareBindBean bean) {
		new Thread() {
			@Override
			public void run() {
				try {
					// 1. 初始化微博的分享消息
			        // 用户可以分享文本、图片、网页、音乐、视频中的一种
					WeiboMessage weiboMessage = new WeiboMessage();
					TextObject textObject = new TextObject();
					textObject.title = bean.getTitle();
			        textObject.text = bean.getMessage();
			        textObject.description = bean.getMessage();
			        
			        weiboMessage.mediaObject = textObject;
			        // 2. 初始化从第三方到微博的消息请求
			        SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
			        // 用transaction唯一标识一个请求
			        req.transaction = String.valueOf(System.currentTimeMillis());
			        req.message = weiboMessage;
			        
			        // 3. 发送请求消息到微博，唤起微博分享界面
			        weiboAPI.sendRequest(context, req);
				} catch (Exception e) { 
					Log.e(TAG, "sendTextMsg Exception: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void sendImageMsg(final Activity context, final IWeiboAPI weiboAPI,
			final ShareBindBean bean) {
		if (weiboAPI.getWeiboAppSupportAPI() >= 10351) {
			
		} else {
			sendTextMsg(context, weiboAPI, bean);
		}
		new Thread() {
			@Override
			public void run() {
				try {
					// 1. 初始化微博的分享消息
			        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//					// 1. 初始化微博的分享消息
//			        // 用户可以分享文本、图片、网页、音乐、视频中的一种
//					WeiboMessage weiboMessage = new WeiboMessage();
					
//					WebpageObject mediaObject = new WebpageObject();
//			        mediaObject.identify = Util.generateId();// 创建一个唯一的ID
//			        mediaObject.title = bean.getTitle();
//			        mediaObject.description = bean.getMessage();
//					
//			        mediaObject.setThumbImage(bean.getImage(context, 2L * 1024L * 1024L));
//			        mediaObject.actionUrl = bean.getUrl();
			        
			        TextObject textObject = new TextObject();
					textObject.title = bean.getTitle();
			        textObject.text = bean.getMessage();
			        textObject.description = bean.getMessage();
			        weiboMessage.textObject = textObject;
			        
					ImageObject mediaObject = new ImageObject();
			        mediaObject.setImageObject(bean.getImage(context, 2L * 1024L * 1024L));
			        mediaObject.description = bean.getMessage();
			        
			        weiboMessage.imageObject = mediaObject;
			        // 2. 初始化从第三方到微博的消息请求
			        SendMultiMessageToWeiboRequest req = new SendMultiMessageToWeiboRequest();
			        // 用transaction唯一标识一个请求
			        req.transaction = String.valueOf(System.currentTimeMillis());
			        req.multiMessage = weiboMessage;
			        
			        // 3. 发送请求消息到微博，唤起微博分享界面
			        weiboAPI.sendRequest(context, req);
				} catch (Exception e) { 
					Log.e(TAG, "sendImageMsg Exception: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}
}
