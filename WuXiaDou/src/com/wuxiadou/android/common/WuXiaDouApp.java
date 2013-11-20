package com.wuxiadou.android.common;

import com.wuxiadou.android.control.MoveLoader;

import android.app.Application;

public class WuXiaDouApp extends Application {

	@Override
	public void onCreate() {
		//TODO 加载技能数据
		MoveLoader.init(this);
	}
	
}
