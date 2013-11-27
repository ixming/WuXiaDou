package com.wuxiadou.android.activity;

import org.ixming.android.utils.AndroidUtil;

import com.wuxiadou.android.R;
import com.wuxiadou.android.activity.base.BaseActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class MainActivity extends BaseActivity {

	private View btn1;
	private View btn2;
	private View btn3;
	
	
	private View view1;
	private View view2;
	private View view3;
	@Override
	public int getLayoutResId() {
		return R.layout.activity_main;
	}

	@Override
	public void initView(View view) {
		btn1 = findViewById(R.id.btn1);
		btn2 = findViewById(R.id.btn2);
		btn3 = findViewById(R.id.btn3);
		
		view1 = findViewById(R.id.view1);
		view2 = findViewById(R.id.view2);
		view3 = findViewById(R.id.view3);
	}

	@Override
	public void initListener() {
		bindClickListener(btn1);
		bindClickListener(btn2);
		bindClickListener(btn3);
	}

	@Override
	public void initData(View view, Bundle savedInstanceState) {
		
	}

	@Override
	public Handler createActivityHandler() {
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn1:
			AndroidUtil.setViewVisibility(view1, View.VISIBLE);
			
			AndroidUtil.setViewVisibility(view2, View.GONE);
			AndroidUtil.setViewVisibility(view3, View.GONE);
			break;
		case R.id.btn2:
			AndroidUtil.setViewVisibility(view2, View.VISIBLE);
			
			AndroidUtil.setViewVisibility(view1, View.GONE);
			AndroidUtil.setViewVisibility(view3, View.GONE);
			break;
		case R.id.btn3:
			AndroidUtil.setViewVisibility(view3, View.VISIBLE);
			
			AndroidUtil.setViewVisibility(view1, View.GONE);
			AndroidUtil.setViewVisibility(view2, View.GONE);
			break;

		default:
			break;
		}
	}

}
