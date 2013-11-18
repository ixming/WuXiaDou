package com.frameworkexample.android.common.sharebind;

import android.content.Context;
import android.util.Log;

/**
 * 默认以请求网址的方式发送分享信息
 * @author YinYong
 */
class ShareByWeiboDefAPI {
	static final String TAG = ShareByWeiboDefAPI.class.getSimpleName();
	
	// urls
	public static final String BASE_URL = "https://api.weibo.com/oauth2/authorize?";
	public static final String FAIL_REDIRECTURL = "http://api.unionpaysmart.com/oauth/sina/callback";
	
	public static final String SEND_TEXT_MESSAGE_URL = "https://api.weibo.com/2/statuses/update.json";
	public static final String SEND_IMAGE_MESSAGE_URL = "https://api.weibo.com/2/statuses/upload.json";

	public static String SERVER = "https://api.weibo.com/2/";
    public static String URL_OAUTH_TOKEN = "http://api.t.sina.com.cn/oauth/request_token";
    public static String URL_AUTHORIZE = "http://api.t.sina.com.cn/oauth/authorize";
    public static String URL_ACCESS_TOKEN = "http://api.t.sina.com.cn/oauth/access_token";
    public static String URL_AUTHENTICATION = "http://api.t.sina.com.cn/oauth/authenticate";

    public static String URL_OAUTH2_ACCESS_TOKEN = "https://api.weibo.com/oauth2/access_token";
    public static String URL_OAUTH2_ACCESS_AUTHORIZE = "https://api.weibo.com/oauth2/authorize";
    
    public ShareByWeiboDefAPI() {
    	
	}
	// ****************************************************************
	// send message to Weibo
	public void sendMsg(final Context context, String accessToken,
			final ShareBindBean bean, final ShareMsgListener listener) {
		if (null == bean) {
			if (null != listener) {
				listener.sendFailure("");
			}
			return ;
		}
		String message = "";
		// 新浪微博的特殊内容组织
		String title = bean.getTitle();
		String url = bean.getUrl();
		if (null != title && title.length() > 0) {
			message += title + ": ";
		}
		message += bean.getMessage();
		if (null != url && url.length() > 0) {
			message += " " + url;
		}
		bean.setMessage(message);
		if (bean.hasImage()) {
			sendImageMsg(context, accessToken, bean, listener);
		} else {
			sendTextMsg(context, accessToken, bean, listener);
		}
	}
	
	public void sendTextMsg(final Context context, final String accessToken,
			final ShareBindBean bean, final ShareMsgListener listener) {
		new Thread() {
			@Override
			public void run() {
				try {
					String result = ShareByWeiboUtility.newInstance()
						.setSource(ShareByWeiboConstants.APP_KEY)
						.setAccessToken(accessToken)
						.setMessage(bean.getMessage())
						.request(context, SEND_TEXT_MESSAGE_URL,
							ShareByWeiboUtility.HTTPMETHOD_POST);
					if (result != null && result.contains("\"created_at\"")) {
						listener.sendSuccess(result);
					} else {
						listener.sendFailure(result);
					}
				} catch (Exception e) {
					Log.e(TAG, "sendTextMsg Exception: " + e.getMessage());
					listener.sendFailure(e.getMessage());
				}
			}
		}.start();
	}
	
	public void sendImageMsg(final Context context, final String accessToken,
			final ShareBindBean bean, final ShareMsgListener listener) {
		new Thread() {
			@Override
			public void run() {
				try {
					String result = ShareByWeiboUtility.newInstance()
						.setSource(ShareByWeiboConstants.APP_KEY)
						.setAccessToken(accessToken)
						.setMessage(bean.getMessage())
						.setPic(bean.getImage(context))
						.request(context, SEND_IMAGE_MESSAGE_URL,
							ShareByWeiboUtility.HTTPMETHOD_POST);
					if (result != null && result.contains("\"created_at\"")) {
						listener.sendSuccess(result);
					} else {
						listener.sendFailure(result);
					}
				} catch (Exception e) {
					Log.e(TAG, "sendImageMsg Exception: " + e.getMessage());
					listener.sendFailure(e.getMessage());
				}
			}
		}.start();
	}
	
}
