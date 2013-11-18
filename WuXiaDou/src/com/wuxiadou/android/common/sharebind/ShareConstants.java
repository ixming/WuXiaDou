package com.frameworkexample.android.common.sharebind;

import android.content.Context;
import android.content.Intent;

/**
 * 定义分享模块统一的操作，比如如何进行统一通知分享成功失败的事件等。
 * @author YinYong
 * @version 1.0
 */
public class ShareConstants {

	private ShareConstants() {}
	
	public final static String URL_SHARE = "http://www.unionpaysmart.com/";
	
	//TODO all actions
	public final static String ACTION_SHARE_SUCCESS = "private.android.action.SHARE_SUCCESS";
	public final static String ACTION_SHARE_FAILED = "private.android.action.SHARE_FAILED";
	
	//TODO all extras
	/**
	 * @see {@link #getType(int)}
	 * @see {@link #getErrorReason(int)}
	 */
	public final static String EXTRA_TYPE = "extra_type";
	/**
	 * @see {@link #getType(int)}
	 * @see {@link #getErrorReason(int)}
	 */
	public final static String EXTRA_TYPE_AND_ERR_REASON = "extra_type_and_err_reason";
	
	//TODO extra values
	private final static int MASK_TYPE = 0xFF00;
	private final static int MASK_ERROR = 0x00FF;
	
	public final static int TYPE_UNKNOWN = 0x0000;
	public final static int TYPE_WEIBO = 0x0100;
	public final static int TYPE_WEIXIN = 0x0200;
	public final static int TYPE_PENGYOUQUAN = 0x0300;
	
	public final static int VAL_ERROR_UNKNOWN = 0x0000;
	public final static int VAL_ERROR_CLIENT_UNEXIST = 0x0001;
	public final static int VAL_ERROR_CLIENT_UNSUPPORT = 0x0002;
	public final static int VAL_ERROR_USER_CANCELED = 0x0003;
	
	public final static int TYPE_AND_VALUE_UNKNOWN = TYPE_UNKNOWN & VAL_ERROR_UNKNOWN;
	
	/**
	 * @see ShareConstants#TYPE_WEIBO
	 * @see ShareConstants#TYPE_WEIXIN
	 * @see ShareConstants#TYPE_PENGYOUQUAN
	 */
	public static final int getType(int val) {
		return MASK_TYPE & val;
	}
	
	/**
	 * @see ShareConstants#VAL_ERROR_USER_CANCELED
	 * @see ShareConstants#VAL_ERROR_CLIENT_UNEXIST
	 * @see ShareConstants#VAL_ERROR_CLIENT_UNSUPPORT
	 * @see ShareConstants#VAL_ERROR_UNKNOWN
	 */
	public static final int getErrorReason(int val) {
		return MASK_ERROR & val;
	}
	
	public static void broadcastSuccess(Context context, int typeAndReason) {
		if (null != context) {
			context.sendBroadcast(new Intent(ACTION_SHARE_SUCCESS)
				.putExtra(EXTRA_TYPE_AND_ERR_REASON, typeAndReason));
		}
	}
	
	public static void broadcastFailed(Context context, int typeAndReason) {
		if (null != context) {
			context.sendBroadcast(new Intent(ACTION_SHARE_FAILED)
				.putExtra(EXTRA_TYPE_AND_ERR_REASON, typeAndReason));
		}
	}
}
