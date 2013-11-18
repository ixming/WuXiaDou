package com.frameworkexample.android.common;

import android.view.View;

public interface CommonTitleBarContainer {
	/**
	 * @return true if there's common title bar in the root layout/view
	 * @added 1.0
	 */
	boolean hasCommonTitleBar();
	
	/**
	 * get the common title bar view
	 * @added 1.0
	 */
	View getTitleBarRootView();
	
	/**
	 * find view in the common title bar view with the specific ID
	 * @added 1.0
	 */
	<T extends View>T findTitleBarViewById(int id);
	
}
