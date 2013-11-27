package com.wuxiadou.android.activity.base;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TabHost;

import com.wuxiadou.android.utils.LogUtils;

/**
 * @author Yin Yong
 * @version 1.0
 */
public abstract class BaseFragmentTabActivity extends BaseFragmentActivity {

	final String TAG = BaseFragmentTabActivity.class.getSimpleName();
	
	private final String INSTANCESTATE_TABTAG = "tab_tag";
	
	private TabHost mTabHost;
	private FragmentManager mFragmentManager;
	private TabManager mTabManager;
	
	@Override
	void prepareInitView(View rootView) {
		super.prepareInitView(rootView);
		mFragmentManager = getSupportFragmentManager();
		mTabHost = (TabHost) findViewById(provideTabHostId());
		mTabHost.setup();
		
		mTabManager = provideMainTabBuilder().build();
		mTabHost.setOnTabChangedListener(mTabManager);
		mTabHost.setCurrentTab(0);
	}
	
	/**
	 * 提供MainTabBuilder，设置相关的设置。
	 */
	protected abstract int provideTabHostId();
	
	/**
	 * 提供MainTabBuilder，设置相关的设置。
	 */
	protected abstract MainTabBuilder provideMainTabBuilder();
	
	/**
	 * @param tag tab的标签
	 * @param labelResId tab的标签名称
	 * @param iconResId tab的图标
	 */
	protected abstract View provideTabIndicatorView(String tag, int labelResId, int iconResId) ;

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			String tabTag = savedInstanceState.getString(INSTANCESTATE_TABTAG);
			if (TextUtils.isEmpty(tabTag)) {
				mTabHost.setCurrentTab(0);
			} else {
				mTabHost.setCurrentTabByTag(tabTag);
			}
		}
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INSTANCESTATE_TABTAG, mTabHost.getCurrentTabTag());
    }
	
	protected final class MainTabBuilder {
		private int[] labels;
		private int[] tabIcons;
		private int[] viewIds;
		
		public MainTabBuilder() {
			
		}
		
		public MainTabBuilder setMainTabLabels(int...labelResIds) {
			this.labels = labelResIds;
			return this;
		}
		
		public MainTabBuilder setMainTabIcons(int...iconResIds) {
			this.tabIcons = iconResIds;
			return this;
		}
		
		public MainTabBuilder setMainTabViewIds(int...viewIds) {
			this.viewIds = viewIds;
			return this;
		}
		
		private TabManager build() {
			return new TabManager(labels, tabIcons, viewIds);
		}
	}
	
	private final class TabInfo {
		@SuppressWarnings("unused")
		public int index;
		@SuppressWarnings("unused")
		public String tag;
		public Fragment fragment;

        public TabInfo(int _index, String _tag, Fragment _fragment) {
        	index = _index;
            tag = _tag;
            fragment = _fragment;
        }
    }
	
	/**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
	private final class TabManager implements TabHost.OnTabChangeListener {
    	private final String TABTAG_PREFIX = "tab_tag_";
    	
    	private TabInfo mLastTab;
    	private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
    	
    	public TabManager(int[] labels, int[] tabIcons, int[] viewIds) {
    		int count = labels.length;
			if (null != tabIcons) {
				if (count != tabIcons.length) {
					throw new IllegalArgumentException("");
				}
			}
			
			if (null != viewIds) {
				if (count != viewIds.length) {
					throw new IllegalArgumentException("");
				}
			}
			
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			for (int i = 0; i < count; i++) {
				String tag = TABTAG_PREFIX + i;
				TabInfo tabInfo = new TabInfo(i, tag, mFragmentManager.findFragmentById(viewIds[i]));
	    		mTabHost.addTab(mTabHost.newTabSpec(tag)
						.setIndicator(provideTabIndicatorView(tag, labels[i], null != tabIcons ? tabIcons[i] : 0))
						.setContent(viewIds[i]));
	    		mTabs.put(tag, tabInfo);
	    		if (i == 0) {
	    			mLastTab = tabInfo;
	    			ft.show(tabInfo.fragment);
	    		} else {
	    			ft.hide(tabInfo.fragment);
	    		}
			}
			ft.commit();
		}
    	
		@Override
		public void onTabChanged(String tabId) {
			LogUtils.d(TAG, "onTabChanged tabId: " + tabId);
			TabInfo curTab = mTabs.get(tabId);
			if (null == curTab) {
				return ;
			}
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			if (null != mLastTab) {
				ft.hide(mLastTab.fragment);
			}
			ft.show(curTab.fragment);
			
			mLastTab = curTab;
			ft.commit();
		}
    }
}
