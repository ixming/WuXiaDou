package com.wuxiadou.activity.base;


import android.content.Context;
import android.os.Bundle;
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
		fragmentActivity = getActivity();
		
		initView(mRootView);
		initData(mRootView, savedInstanceState);
		initListener();
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
	
	
	protected final void ensureRootViewCreated() {
		if (!mIsRootViewCreated) {
			throw new IllegalStateException("root view hasn't been created yet");
		}
	}
	
	@Override
	public final Context getContext() {
		return getActivity().getApplicationContext();
	}
	
	/**
	 * 给指定的View添加监听器
	 */
	@Override
	public final BaseFragment bindListener(View view) {
		if (null != view) {
			view.setOnClickListener(this);
		}
		return this;
	}
	
	@Override
	public final BaseFragment bindListener(int id) {
		bindListener(findViewById(id));
		return this;
	}
}
