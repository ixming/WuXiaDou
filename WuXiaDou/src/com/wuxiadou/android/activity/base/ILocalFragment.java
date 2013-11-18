package com.frameworkexample.android.activity.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * define base operations fragments should do as 
 * it recommends here
 * @author YinYong
 * @version 1.0
 */
interface ILocalFragment {

	/**
	 * define the layout res of the fragment
	 */
	int getLayoutResId();
	
	/**
	 * called before {@link #initData(View, android.os.Bundle)} while
	 * {@link Fragment#onActivityCreated(android.os.Bundle)} is running
	 * 
	 * @param view root view of the fragment
	 * 
	 * @see #findViewById(int)
	 */
	void initView(View view);
	
	/**
	 * called immediately after {@link #initView(View)} while
	 * {@link Fragment#onActivityCreated(android.os.Bundle)} is running
	 * 
	 * @param view root view of the fragment
	 * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
	 */
	void initListener() ;
	
	/**
	 * called immediately after {@link #initView(View)} while
	 * {@link Fragment#onActivityCreated(android.os.Bundle)} is running
	 * 
	 * @param view root view of the fragment
	 * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
	 */
	void initData(View view, Bundle savedInstanceState) ;
	
	/**
	 * 回退
	 * @return true if the event is handled
	 * @added 1.0
	 */
	boolean onBackPressed();
	
	/**
	 * 给指定的View添加监听器
	 */
	ILocalFragment bindClickListener(View view);
	
	/**
	 * 通过ID找到指定的View，并为之添加监听器；<br/>
	 * 
	 * 该方法着重强调此View只需添加点击事件，而不会对之进行状态或者
	 * 显示的改变。
	 */
	ILocalFragment bindClickListener(int id);
	
	ILocalFragment removeClickListener(View view);
	
	ILocalFragment removeClickListener(int id);
	
	/**
	 * just like findViewById in Activity or View subclasses
	 * @param id target id to find
	 * @return View if it exists or null
	 */
	<T extends View>T findViewById(int id);
	
	/**
	 * fragment {@link Fragment#getView()} is wrapped by inner program.
	 * <br/><br/>
	 * this method returns the pure View.
	 */
	View getRootView();
	
	/**
	 * return the application context
	 */
	Context getApplicationContext();
}
