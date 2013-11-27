package com.wuxiadou.android.activity;

import com.wuxiadou.android.R;
import com.wuxiadou.android.activity.base.BaseFragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class SkillsFragment extends BaseFragment {

	@Override
	public int getLayoutResId() {
		return R.layout.activity_main;
	}

	@Override
	public void initView(View view) {
		view.setBackgroundColor(0xFFFFFF00);
	}

	@Override
	public void initListener() {
	}

	@Override
	public void initData(View view, Bundle savedInstanceState) {
	}

	@Override
	protected Handler createActivityHandler() {

		return null;
	}

	@Override
	public void onClick(View v) {
	}

}
