package com.frameworkexample.android.common.sharebind;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 存储/获取微博分享的一些重要的量
 * @author YinYong
 */
class ShareByWeiboPref implements ShareByWeiboConstants{

	private SharedPreferences mSharedPref;
	public ShareByWeiboPref(Context context) {
		mSharedPref = context.getSharedPreferences(
				SP_MAIN, Context.MODE_PRIVATE);
	}
	
	/**
	 * 清除缓存值，将所有缓存值置为null
	 */
	public ShareByWeiboPref clearCache() {
		mSharedPref.edit()
			.putString(SP_ACCESS_TOKEN, null)
			.putString(SP_EXPIRES_IN, null)
			.putString(SP_CODE, null)
			.putString(SP_REFRESH_TOKEN, null)
			.commit();
		return this;
	}
	
	/**
	 * 重置缓存值
	 */
	public ShareByWeiboPref resetCache(String accessToken,
			String expiresIn, String code, String refreshToken) {
		mSharedPref.edit()
			.putString(SP_ACCESS_TOKEN, accessToken)
			.putString(SP_EXPIRES_IN, expiresIn)
			.putString(SP_CODE, code)
			.putString(SP_REFRESH_TOKEN, refreshToken)
			.commit();
		return this;
	}
	
	/**
	 * 是否存在缓存值
	 */
	public boolean hasToken() {
		String token = getAccessToken();
		return null != token && token.length() > 0;
	}
	
	public ShareByWeiboPref setAccessToken(String accessToken) {
		mSharedPref.edit()
			.putString(SP_ACCESS_TOKEN, accessToken)
			.commit();
		return this;
	}
	
	public String getAccessToken() {
		return mSharedPref.getString(SP_ACCESS_TOKEN, null);
	}
	
	public ShareByWeiboPref setExpiresIn(String expiresIn) {
		mSharedPref.edit()
			.putString(SP_EXPIRES_IN, expiresIn)
			.commit();
		return this;
	}
	
	public String getExpiresIn() {
		return mSharedPref.getString(SP_EXPIRES_IN, null);
	}

	public ShareByWeiboPref setCode(String code) {
		mSharedPref.edit()
			.putString(SP_CODE, code)
			.commit();
		return this;
	}
	
	public String getCode() {
		return mSharedPref.getString(SP_CODE, null);
	}

	public ShareByWeiboPref setRefreshToken(String refreshToken) {
		mSharedPref.edit()
			.putString(SP_REFRESH_TOKEN, refreshToken)
			.commit();
		return this;
	}
	
	public String getRefreshToken() {
		return mSharedPref.getString(SP_REFRESH_TOKEN, null);
	}
	
}
