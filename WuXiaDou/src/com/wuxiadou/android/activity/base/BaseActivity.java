package com.frameworkexample.android.activity.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 基本的Activity，它规定了代码的一些格式，需要相应的遵循，使得结构相对清晰。
 * @author Yin Yong
 */
public abstract class BaseActivity extends Activity
implements ILocalActivity, OnClickListener {

	private View mRootView;
	protected Context context;
	protected Context appContext;
	protected Handler handler;
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		context = this;
		appContext = getApplicationContext();
		handler = createActivityHandler();
		
		mRootView = LayoutInflater.from(this).inflate(getLayoutResId(), null);
		setContentView(mRootView);
		
		prepareInitView(mRootView);
		initView(mRootView);
		initListener();
		prepareInitData(mRootView, savedInstanceState);
		initData(mRootView, savedInstanceState);
	}

	void prepareInitView(View rootView) {  };
	
	void prepareInitData(View rootView, Bundle savedInstanceState) {  };
	
	/**
	 * 创建一个本Activity的Handler对象，此方法在onCreate()中调用，且
	 * 在initView及initData之前。
	 * @added 1.0
	 */
	protected abstract Handler createActivityHandler();
	
	@Override
	public final View getRootView() {
		return mRootView;
	}
	
	@Override
	public BaseActivity bindClickListener(int resId) {
		bindClickListener(findViewById(resId));
		return this;
	}
	
	@Override
	public BaseActivity bindClickListener(View view) {
		if (null != view) {
			view.setOnClickListener(this);
		}
		return this;
	}
	
	@Override
	public BaseActivity removeClickListener(int resId) {
		removeClickListener(findViewById(resId));
		return this;
	}
	
	@Override
	public BaseActivity removeClickListener(View view) {
		if (null != view) {
			view.setOnClickListener(null);
		}
		return this;
	}
	
	/**
	 * 如果Activity中存在点击的View，你可以联合bindClickListener使用。
	 */
	public abstract void onClick(View v);
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (customBack()) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * <p>
	 * base基类中已经有了默认的实现，如果有特殊的需要，请重写此方法；
	 * 该默认实现不保证符合所有的特殊情况。
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public boolean customBack() {
		finish();
		return false;
		
//		switch (getActivityStartSource()) {
//		case ActivitySwitcher.SOURCE_FROM_MAIN:
//			finish();
//			ActivitySwitcher.switchFromSubBack2Main(this);
//			return true;
//		case ActivitySwitcher.SOURCE_FROM_SUB:
//			finish();
//			ActivitySwitcher.switchFromSubBack2Sub(this);
//			return true;
//		case ActivitySwitcher.SOURCE_FROM_UNKNOWN:
//		default:
//			finish();
//			return false;
//		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
}