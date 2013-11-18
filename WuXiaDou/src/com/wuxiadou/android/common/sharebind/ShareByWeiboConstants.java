package com.frameworkexample.android.common.sharebind;

interface ShareByWeiboConstants {
	
	// basic information
	// 应用的key 请到官方申请正式的appkey替换APP_KEY
    String APP_KEY = "1095511257";

    // 替换为开发者REDIRECT_URL
    String REDIRECT_URL = "http://icard.unionpaysmart.com/oauth/sina/callback";
    
    // 取消授权的回调页
    String CANCEL_REDIRECT_URL = "http://icard.unionpaysmart.com/oauth/sina/cancel";

    // 新支持scope：支持传入多个scope权限，用逗号分隔
    String SCOPE = "direct_messages_write";
    
    
    // 一些share值
	String SP_MAIN = "sharebind_values";
	String SP_ACCESS_TOKEN = "share_access_token";
	String SP_EXPIRES_IN = "share_expires_in";
	String SP_CODE = "share_code";
	String SP_REFRESH_TOKEN = "share_refresh_token";
}
