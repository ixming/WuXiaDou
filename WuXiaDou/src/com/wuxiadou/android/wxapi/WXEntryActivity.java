package com.frameworkexample.android.wxapi;

import org.ixming.android.common.view.ViewUtils;
import org.ixming.android.utils.FrameworkLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.frameworkexample.android.common.sharebind.ShareBindBean;
import com.frameworkexample.android.common.sharebind.ShareBindWeixinObject;
import com.frameworkexample.android.common.sharebind.ShareConstants;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * 如果客户端需要使用WX的分享，需要将本类移动到<package-name>.wxapi包下；
 * （如果将本类放入框架中，则需要继承本类，此时可以不重写任何方法）。
 * <p>
 * 还需要在AndroidManifest.xml中加入如下配置。
 * <pre>
 * &lt;activity
 * 	android:name="<package-name>.wxapi.WXEntryActivity(或其子类)"
 * 	android:exported="true"
 * 	android:launchMode="singleTask"
 * 	android:theme="@android:style/Theme.Translucent.NoTitleBar"/&gt;
 * <pre>
 * </p>
 * @author Yin Yong
 * @version 1.0
 */
public class WXEntryActivity extends Activity 
implements IWXAPIEventHandler{

	final String TAG = WXEntryActivity.class.getSimpleName();
	
	public static void shareByWeixinActivity(Activity from,
			ShareBindBean bean) {
		if (null == from) {
			return ;
		}
		from.startActivity(
			new Intent(from, WXEntryActivity.class)
				.putExtra(ShareBindBean.EXTRA_SHARE_BIND, bean)
				.putExtra(ShareConstants.EXTRA_TYPE, ShareConstants.TYPE_WEIXIN));
	}
	
	public static void shareByPengyouquanActivity(Activity from,
			ShareBindBean bean) {
		if (null == from) {
			return ;
		}
		from.startActivity(
			new Intent(from, WXEntryActivity.class)
				.putExtra(ShareBindBean.EXTRA_SHARE_BIND, bean)
				.putExtra(ShareConstants.EXTRA_TYPE, ShareConstants.TYPE_PENGYOUQUAN));
	}
	
	
	private int type;
	private ShareBindWeixinObject shareBindWeixinObject;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置一个空的布局
		setContentView(ViewUtils.newTransparentRelativeLayout(this));
		
		// 接收几个参数
		shareBindWeixinObject = new ShareBindWeixinObject(this);
		if (shareBindWeixinObject.handleIntent(getIntent(), this)) {
			// 如果返回TRUE，说明已经调用过onResp，所以此处直接return即可
			return ;
		}
		
		Intent intent = getIntent();
		type = intent.getIntExtra(ShareConstants.EXTRA_TYPE,
				ShareConstants.TYPE_UNKNOWN);
		if (type == ShareConstants.TYPE_UNKNOWN) {
			FrameworkLog.d(TAG, "onCreate type is undefined!");
			resultCancel(ShareConstants.VAL_ERROR_UNKNOWN);
			return ;
		}
		ShareBindBean bean = intent.getParcelableExtra(ShareBindBean.EXTRA_SHARE_BIND);
		if (null == bean) {
			FrameworkLog.d(TAG, "onCreate bean is null!");
			resultCancel(ShareConstants.VAL_ERROR_UNKNOWN);
			return ;
		}
		
		if (!shareBindWeixinObject.isWXAppInstalled()) {
			FrameworkLog.d(TAG, "onCreate wx is not Installed!");
			resultCancel(ShareConstants.VAL_ERROR_CLIENT_UNEXIST);
			return ;
		}
		
		if (!shareBindWeixinObject.isSupportWXSharing()) {
			FrameworkLog.d(TAG, "onCreate wx is unsupported!");
			resultCancel(ShareConstants.VAL_ERROR_CLIENT_UNSUPPORT);
			return ;
		}
		switch (type) {
		case ShareConstants.TYPE_PENGYOUQUAN:
			if (!shareBindWeixinObject.isSupportTimeline()) {
				FrameworkLog.d(TAG, "onCreate wx pengyouquan is unsupported!");
				resultCancel(ShareConstants.VAL_ERROR_CLIENT_UNSUPPORT);
				return ;
			}
			shareBindWeixinObject.setTimeLineScene();
			break;
		case ShareConstants.TYPE_WEIXIN:
		default:
			shareBindWeixinObject.setSessionScene();
			break;
		}
		
		if (!shareBindWeixinObject.sendMsgWithAppIcon(this, bean)) {
			resultCancel(ShareConstants.VAL_ERROR_UNKNOWN);
			return;
		}
		
		finish();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		if (shareBindWeixinObject.handleIntent(intent, this)) { }
	}
	
	private void resultOk() {
		ShareConstants.broadcastSuccess(this, type);
		finish();
	}

	private void resultCancel(int reason) {
		ShareConstants.broadcastFailed(this, type | reason);
		finish();
	}

	@Override
	public void onReq(BaseReq req) { }

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "WXAPI---onResp resp.errCode: " + resp.errCode);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			resultOk();
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			resultCancel(ShareConstants.VAL_ERROR_USER_CANCELED);
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
		default:
			// 没有权限
			resultCancel(ShareConstants.VAL_ERROR_UNKNOWN);
			break;
		}
	}
	
}