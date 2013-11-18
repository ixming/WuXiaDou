package com.wuxiadou.control.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.wuxiadou.utils.ToastUtil;
import com.wuxiadou.utils.Utils;

public class BaseManager {
	Context context;
	Context appContext;
	Handler handler;
	ProgressDialog progressDialog=null;
	public BaseManager(Context context ,Handler handler)
	{
		this.context=context;
		this.appContext=context.getApplicationContext();
		this.handler=handler;
	}
	
	
	protected void checkAndHideProgressDialog() {
		if (null == progressDialog) {
			return ;
		}
		Runnable run = new Runnable() {
			@Override
			public void run() {
				if (null != progressDialog) {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					progressDialog = null;
				}
			}
		};
		if (isMainThread()) {
			run.run();
		} else {
			if (null != handler) {
				handler.post(run);
			}
		}
	}
	
	public void toastShow(final String arg) {
		if(!Utils.isNotNull(arg))
		{
			return ;
		}
		if (isMainThread()) {
			ToastUtil.showToast(appContext, arg);
		} else {
			handler.post(new Runnable() {
				@Override
				public void run() {
					ToastUtil.showToast(appContext, arg);
				}
			});
		}
	}
	
	public void toastShow(final int resId) {
		String msg = appContext.getString(resId);
		toastShow(msg);
	}
	
	protected final boolean isMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}
	/**
	 * 全局刷新用户数据
	 */
}
