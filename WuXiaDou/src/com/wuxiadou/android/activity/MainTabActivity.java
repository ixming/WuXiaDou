package com.wuxiadou.android.activity;

import com.wuxiadou.android.R;
import com.wuxiadou.android.activity.base.BaseFragmentTabActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MainTabActivity extends BaseFragmentTabActivity {

	final String TAG = MainTabActivity.class.getSimpleName();
	
	@Override
	public int getLayoutResId() {
		return R.layout.activity_maintab;
	}
	
	@Override
	protected int provideTabHostId() {
		return R.id.activity_maintab_th;
	}

	@Override
	protected MainTabBuilder provideMainTabBuilder() {
		return new MainTabBuilder()
			// 设置Tab的名称
			.setMainTabLabels(R.string.maintab_index, R.string.maintab_skills,
					R.string.maintab_maps, R.string.maintab_battles)
			// 设置Tab的图标
			.setMainTabIcons(R.drawable.activity_maintab_item_iconselector,
					R.drawable.activity_maintab_item_iconselector,
					R.drawable.activity_maintab_item_iconselector,
					R.drawable.activity_maintab_item_iconselector)
			// 设置Tab的选中后显示的Fragment
			.setMainTabViewIds(R.id.activity_maintab_index,
					R.id.activity_maintab_skills,
					R.id.activity_maintab_maps,
					R.id.activity_maintab_battles);
	}

	@Override
	protected View provideTabIndicatorView(String tag, int labelResId, int iconResId) {
		View root = getLayoutInflater().inflate(R.layout.activity_maintab_item, null);
		TextView tv = (TextView) root.findViewById(R.id.activity_maintab_item_tv);
		tv.setText(labelResId);
		tv.setBackgroundResource(iconResId);
		return root;
	}

	
	@Override
	public void initView(View view) {
		
	}

	@Override
	public void initListener() {
		
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
	}
}
