package com.frameworkexample.android.common.sharebind;

import org.ixming.android.utils.FrameworkLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 分派分享结果的类，用于外界想要单独处理某个特定的分享
 * @author YinYong
 * @version 1.0
 */
public abstract class ShareDispatcher {

	final String TAG = ShareDispatcher.class.getSimpleName();
	
	private final Context context;
	protected ShareDispatcher(Context context) {
		this.context = context;
	}
	
	private final IntentFilter filter;
	{
		filter = new IntentFilter();
		filter.addAction(ShareConstants.ACTION_SHARE_SUCCESS);
		filter.addAction(ShareConstants.ACTION_SHARE_FAILED);
	}
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			undispactch();
			String action = intent.getAction();
			int typeAndReason = intent.getIntExtra(
					ShareConstants.EXTRA_TYPE_AND_ERR_REASON,
					ShareConstants.TYPE_AND_VALUE_UNKNOWN);
			if (ShareConstants.ACTION_SHARE_SUCCESS.equals(action)) {
				//TODO 成功
				onResultOk(ShareConstants.getType(typeAndReason));
			} else if (ShareConstants.ACTION_SHARE_FAILED.equals(action)) {
				//TODO 分享出错或者取消操作
				int type = ShareConstants.getType(typeAndReason);
				int reason = ShareConstants.getErrorReason(typeAndReason);
				FrameworkLog.d(TAG, "onReceive type: " + type);
				FrameworkLog.d(TAG, "onReceive reason: " + reason);
				onResultCancel(type, reason);
			}
		}
	};
	
	/**
	 * 启动回调机制
	 * @added 1.0
	 */
	public void dispactch() {
		try {
			context.registerReceiver(receiver, filter);
		} catch (Exception e) { }
	}
	
	/**
	 * 停止回调机制
	 * @added 1.0
	 */
	public void undispactch() {
		try {
			context.unregisterReceiver(receiver);
		} catch (Exception e) { }
	}
	
	
	/**
	 * 当成功分享后触发
	 * @added 1.0
	 * 
	 * @param type one of {@link ShareConstants#TYPE_WEIBO}--
	 * {@link ShareConstants#TYPE_WEIXIN}  --
	 * {@link ShareConstants#TYPE_PENGYOUQUAN}  --
	 * {@link ShareConstants#TYPE_UNKNOWN}
	 */
	protected abstract void onResultOk(int type);
	
	/**
	 * 当分享失败或者用户取消等等情况时触发
	 * 
	 * @param type one of {@link ShareConstants#TYPE_WEIBO}--
	 * {@link ShareConstants#TYPE_WEIXIN}  --
	 * {@link ShareConstants#TYPE_PENGYOUQUAN}  --
	 * {@link ShareConstants#TYPE_UNKNOWN}
	 * @param reason one of {@link ShareConstants#VAL_ERROR_CLIENT_UNEXIST}--
	 * {@link ShareConstants#VAL_ERROR_CLIENT_UNSUPPORT}  --
	 * {@link ShareConstants#VAL_ERROR_USER_CANCELED}  --
	 * {@link ShareConstants#VAL_ERROR_UNKNOWN}
	 * @added 1.0
	 */
	protected abstract void onResultCancel(int type, int reason);
}
