package com.wuxiadou.android.activity.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseFragmentActivity extends FragmentActivity 
implements ILocalActivity {
	
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
	
	@Override
	public final View getRootView() {
		return mRootView;
	}

	@Override
	public BaseFragmentActivity bindClickListener(int resId) {
		return bindClickListener(findViewById(resId));
	}
	
	@Override
	public BaseFragmentActivity bindClickListener(View view) {
		if (null != view) {
			view.setOnClickListener(this);
		}
		return this;
	}

	@Override
	public BaseFragmentActivity removeClickListener(int resId) {
		return removeClickListener(findViewById(resId));
	}

	@Override
	public BaseFragmentActivity removeClickListener(View view) {
		if (null != view) {
			view.setOnClickListener(null);
		}
		return this;
	}

	@Override
	public boolean customBack() {
		finish();
		return false;
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
