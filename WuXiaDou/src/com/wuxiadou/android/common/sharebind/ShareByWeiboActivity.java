package com.frameworkexample.android.common.sharebind;

import org.ixming.android.common.view.ViewUtils;
import org.ixming.android.utils.FrameworkLog;
import org.ixming.android.utils.ToastUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.sso.SsoHandler;
import com.weibo.sdk.android.util.AccessTokenKeeper;

public class ShareByWeiboActivity extends Activity
implements IWeiboHandler.Response{

	final String TAG = ShareByWeiboActivity.class.getSimpleName();
	
	public static void shareByWeiboActivity(Activity from,
			ShareBindBean bean) {
		if (null == from) {
			return ;
		}
		from.startActivity(
			new Intent(from, ShareByWeiboActivity.class)
				.putExtra(ShareBindBean.EXTRA_SHARE_BIND, bean)
				.putExtra(ShareConstants.EXTRA_TYPE, ShareConstants.TYPE_WEIBO));
	}
	
	private Handler handler;
	
	private ShareBindBean bean;
	// 
	private ShareByWeiboPref mShareByWeiboPref;
	
	/** 微博API接口类，提供认证等功能  */
	private Weibo mWeibo;
	
	private IWeiboAPI mWeiboAPI;
	
	/** 注意：SsoHandler 仅当sdk支持sso时有效 */
    private SsoHandler mSsoHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(ViewUtils.newTransparentRelativeLayout(this));
		
		// 微博分享API
		mWeiboAPI = WeiboSDK.createWeiboAPI(this, ShareByWeiboConstants.APP_KEY, false);
		FrameworkLog.d(TAG, "onCreate");
		if (mWeiboAPI.responseListener(getIntent(), this)) {
			return ;
		}
		
		bean = getIntent().getParcelableExtra(ShareBindBean.EXTRA_SHARE_BIND);
		if (null == bean) {
			FrameworkLog.d(TAG, "onCreate bean is null!");
			resultCancel(ShareConstants.VAL_ERROR_UNKNOWN);
			return ;
		}
		
		handler = new Handler();
		
		mWeibo = Weibo.getInstance(ShareByWeiboConstants.APP_KEY,
				ShareByWeiboConstants.REDIRECT_URL,
				ShareByWeiboConstants.SCOPE);
		
		mShareByWeiboPref = new ShareByWeiboPref(getApplicationContext());
		
		doProcess();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		FrameworkLog.d(TAG, "onNewIntent");
		super.onNewIntent(intent);
		setIntent(intent);
		mWeiboAPI.responseListener(intent, this);
	}
	
	private void doProcess() {
		if (mShareByWeiboPref.hasToken()) {
			FrameworkLog.d(TAG, "doProcess has token");
			// 如果存在TOKEN
			// 尝试直接分享
			gotoShare();
		} else {
			FrameworkLog.d(TAG, "doProcess no token==> anthorize");
			anthorize();
		}
	}
	
	/**
	 * 授权
	 */
	private void anthorize() {
		if (mWeiboAPI.isWeiboAppInstalled()) {
			FrameworkLog.d(TAG, "anthorize 已安装客户端");
			if (mWeiboAPI.isWeiboAppSupportAPI()) {
				FrameworkLog.d(TAG, "anthorize 客户端支持分享");
				anthorizeByClient();
			} else {
				FrameworkLog.d(TAG, "anthorize 客户端不支持分享");
				anthorizeByWeb();
			}
		} else {
			FrameworkLog.d(TAG, "anthorize 未安装客户端");
			// 如果没有安装客户端，则
			anthorizeByWeb();
		}
	}
	
	private void anthorizeByWeb() {
		mWeibo.anthorize(this, new WeiboAuthListenerImpl());
	}
	
	private void anthorizeByClient() {
		mSsoHandler = new SsoHandler(ShareByWeiboActivity.this, mWeibo);
        mSsoHandler.authorize(new WeiboAuthListenerImpl(), null);
	}
	
	/**
	 * 保证在调用前已经确保存在相关的参数信息，即可以进行分享了
	 */
	private void gotoShare() {
		if (mWeiboAPI.isWeiboAppInstalled() && mWeiboAPI.isWeiboAppSupportAPI()) {
			mWeiboAPI.registerApp();
			FrameworkLog.d(TAG, "gotoShare ===> client");
			// 如果安装并支持
			ShareByWeiboSDKAPI sdkAPI = new ShareByWeiboSDKAPI();
			sdkAPI.sendMsg(this, mWeiboAPI, bean);
			//finish();
		} else {
			FrameworkLog.d(TAG, "gotoShare ===> default");
			gotoShareByDefAPI();
		}
	}
	
	private void gotoShareByDefAPI() {
		ShareByWeiboDefAPI defApi = new ShareByWeiboDefAPI();
		defApi.sendMsg(this, mShareByWeiboPref.getAccessToken(), bean,
				new ShareMsgListener() {
			@Override
			public void sendSuccess(String result) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						resultOk();
					}
				});
			}
			
			@Override
			public void sendFailure(String errorMsg) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						// 清除缓存->>此处大多数原因是TOKEN过期
						mShareByWeiboPref.clearCache();
						// 这是分享失败的情况
						anthorize();
					}
				});
			}
		});
	}
	
	private void resultOk() {
		ShareConstants.broadcastSuccess(this, ShareConstants.TYPE_WEIBO);
		finish();
	}
	
	private void resultCancel(int reason) {
		ShareConstants.broadcastFailed(this, ShareConstants.TYPE_WEIBO | reason);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		FrameworkLog.d(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		
		// SSO 授权回调
        // 重要：发起 SSO 登陆的Activity必须重写onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
	}
	
	private class WeiboAuthListenerImpl implements WeiboAuthListener{
		/**
		 * 认证结束后将调用此方法
		 * @param values - Key-value string pairs extracted from the response.
		 * 从responsetext中获取的键值对，键值包括"code"或"access_token"、"expires_in"、“refresh_token”等
		 */
		@Override
		public void onComplete(Bundle values) {
			FrameworkLog.d(TAG, "WeiboAuthListener onComplete");
			ToastUtil.showToast(ShareByWeiboActivity.this, "认证成功");
			String token = values.getString("access_token");
            String expires_in = values.getString("expires_in");
            String code = values.getString("code");
            String refresh_token = values.getString("refresh_token");
            mShareByWeiboPref.setAccessToken(token);
            mShareByWeiboPref.setExpiresIn(expires_in);
            mShareByWeiboPref.setCode(code);
            mShareByWeiboPref.setRefreshToken(refresh_token);
            Oauth2AccessToken accessToken = new Oauth2AccessToken(token, expires_in);
            AccessTokenKeeper.keepAccessToken(ShareByWeiboActivity.this, accessToken);
            // 授权成功，分享操作
            gotoShare();
		}
		
		/**
		 * Oauth2.0认证过程中，如果认证窗口被关闭或认证取消时调用
		 */
		@Override
		public void onCancel() {
			ToastUtil.showToast(ShareByWeiboActivity.this, "认证取消");
			resultCancel(ShareConstants.VAL_ERROR_USER_CANCELED);
		}
		
		/**
		 * 当认证过程中捕获到WeiboException时调用
		 */
		@Override
		public void onWeiboException(WeiboException e) {
			FrameworkLog.w(TAG, "认证出错 ： onWeiboException: " + e.getMessage());
			// e.getStatusCode();
			resultCancel(ShareConstants.VAL_ERROR_UNKNOWN);
		}
		
		/**
		 * Oauth2.0认证过程中，当认证对话框中的webview接收数据出现错误时调用此方法
		 */
		@Override
		public void onError(WeiboDialogError e) {
			FrameworkLog.w(TAG, "认证出错 ： onError: " + e.getMessage());
			resultCancel(ShareConstants.VAL_ERROR_UNKNOWN);
		}
		
	}

	@Override
	public void onResponse(BaseResponse resp) {
		FrameworkLog.w(TAG, "onResponse errMsg: " + resp.errMsg);
		switch (resp.errCode) {
		case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_OK:
			resultOk();
			break;
		case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_CANCEL:
			resultCancel(ShareConstants.VAL_ERROR_USER_CANCELED);
			break;
		case com.sina.weibo.sdk.constant.Constants.ErrorCode.ERR_FAIL:
		default:
			resultCancel(ShareConstants.VAL_ERROR_UNKNOWN);
			break;
		}
	}
}
