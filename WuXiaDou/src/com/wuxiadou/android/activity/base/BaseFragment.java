package com.frameworkexample.android.activity.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment
implements ILocalFragment, OnClickListener{

	public Context appContext;
	public Context context;
	FragmentActivity fragmentActivity;
	private boolean mIsRootViewCreated = false;
	private View mRootView;
	protected Handler handler;
	/**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * 
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     * 
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 不推荐再重写此方法.所以加上了final<br/><br/>
	 * Not recommend that you override this method.<br/><br/>
	 * use {@link #onViewCreated(View, Bundle)} to do some thing
	 * @see {@link #onViewCreated(View, Bundle)}
	 */
	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("BaseFragment","execute onCreateView!!! ");
		// 为了实现findViewById
		mRootView = inflater.inflate(getLayoutResId(), container, false);
		// 保证RootView加载完成
		mIsRootViewCreated = true;
		return mRootView;
	}
	
	/**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	}
	
	/**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
	@Override
	public final void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		appContext = getActivity().getApplicationContext();
		handler = createActivityHandler();
		fragmentActivity = getActivity();
		
		prepareInitView(mRootView);
		initView(mRootView);
		
		initListener();
		
		prepareInitData(mRootView, savedInstanceState);
		initData(mRootView, savedInstanceState);
	}
	
	void prepareInitView(View rootView) { }
	void prepareInitData(View rootView, Bundle savedInstanceState) { }
	
	@Override
	public boolean onBackPressed() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public final <T extends View>T findViewById(int id) {
		ensureRootViewCreated();
		return (T) mRootView.findViewById(id);
	}
	
	public final View getRootView() {
		ensureRootViewCreated();
		return mRootView;
	}
	protected abstract Handler createActivityHandler();
	
	protected final void ensureRootViewCreated() {
		if (!mIsRootViewCreated) {
			throw new IllegalStateException("root view hasn't been created yet");
		}
	}
	
	@Override
	public final Context getApplicationContext() {
		return getActivity().getApplicationContext();
	}
	
	/**
	 * 给指定的View添加监听器
	 */
	@Override
	public final BaseFragment bindClickListener(View view) {
		if (null != view) {
			view.setOnClickListener(this);
		}
		return this;
	}
	
	@Override
	public final BaseFragment bindClickListener(int id) {
		bindClickListener(findViewById(id));
		return this;
	}
	
	@Override
	public BaseFragment removeClickListener(View view) {
		if (null != view) {
			view.setOnClickListener(null);
		}
		return this;
	}
	
	@Override
	public BaseFragment removeClickListener(int id) {
		bindClickListener(findViewById(id));
		return this;
	}
	
}