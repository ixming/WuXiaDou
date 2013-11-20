package com.wuxiadou.android.view;

import com.wuxiadou.android.R;
import com.wuxiadou.android.control.adapter.MoveSelectorAdapter;
import com.wuxiadou.android.model.battle.BasicMove;
import com.wuxiadou.android.utils.LogUtils;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.content.Context;

/**
 * Custom popup window.
 * @author Yin Yong
 */
public class MoveSelectorPopWin extends PopupWindow {
	final String TAG = MoveSelectorPopWin.class.getSimpleName();
	
	private final int GROW_FORM_TOP_TO_BOTTOM = 0x1;
	private final int GROW_FORM_BOTTOM_TO_TOP = 0x2;
	
	private Context mContext;
	private Drawable mBackground;
	private WindowManager mWindowManager;
	private OnTouchListener mOnTouchListener;
	private OnShowListener mOnShowListener;
	private OnMoveItemClickListener mOnMoveItemClickListener;
	private int mShowAndHideType;
	private MoveSelectorAdapter mAdapter;
	private ListView mMoveListView;
	
	/**
	 * Constructor.
	 * @param context Context
	 */
	public MoveSelectorPopWin(Context context) {
		super(context);
		mContext = context;
		super.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					MoveSelectorPopWin.this.dismiss();
					return true;
				}
				if (null != mOnTouchListener) {
					mOnTouchListener.onTouch(v, event);
				}
				return false;
			}
		});

		mWindowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		
		setContentView(R.layout.battle_moves_list);
		mMoveListView = (ListView) getContentView().findViewById(R.id.battle_move_selector_lv);
		mAdapter = new MoveSelectorAdapter(mContext);
		mMoveListView.setAdapter(mAdapter);
	}
	
	protected final Context getContext() {
		return mContext;
	}
	
	protected final WindowManager getWindowManager() {
		return mWindowManager;
	}
	
	/**
	 * On pre show
	 */
	private void preShow() {
		if (null != mOnShowListener) {
			mOnShowListener.onShow();
		}

		if (mBackground == null) {
			mBackground = new ColorDrawable(Color.TRANSPARENT);
			super.setBackgroundDrawable(mBackground);
		} else {
			super.setBackgroundDrawable(mBackground);
		}
		super.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
		super.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		super.setTouchable(true);
		super.setFocusable(true);
		super.setOutsideTouchable(true);
		//super.setContentView(mRootView);
	}

	/**
	 * Set background drawable.
	 * 
	 * @param background Background drawable
	 */
	public void setBackgroundDrawable(Drawable background) {
		mBackground = background;
	}
	
	public Drawable getBackgroundDrawable() {
		return mBackground;
	}

	/**
	 * Set content view.
	 * 
	 * @param root Root view
	 */
	@Override
	public void setContentView(View root) {
		if (null == root.getLayoutParams()) {
			root.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
	                LayoutParams.WRAP_CONTENT));
		}
		root.setFocusable(true);
		root.setClickable(true);
		super.setContentView(root);
	}
	
	/**
	 * Set content view.
	 * @param layoutResID Resource id
	 */
	public void setContentView(int layoutResID) {
		LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		setContentView(inflator.inflate(layoutResID, null));
	}
	
	/**
	 * Set content view.
	 * @param layoutResID Resource id
	 */
	public void setContentView(int layoutResID, ViewGroup root) {
		LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		setContentView(inflator.inflate(layoutResID, root, false));
	}

	/**
     * Show popup window. Popup is automatically positioned.
     */
    public void showMoveSelector(View anchor) {
		preShow();
		int xPos, yPos;
		//Rect anchorRect = new Rect();
        //anchor.getGlobalVisibleRect(anchorRect);
		
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        Rect anchorRect = new Rect(location[0], location[1], location[0]
                + anchor.getWidth(), location[1] + anchor.getHeight());
        
        View rootView = getContentView();
        rootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int rootHeight = rootView.getMeasuredHeight();
        int rootWidth = rootView.getMeasuredWidth();

        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
        int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
        // 计算x轴的位置
		// automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos = anchorRect.left - (rootWidth - anchor.getWidth());
			LogUtils.i(TAG, "(anchorRect.left + rootWidth) > screenWidth");
			LogUtils.i(TAG, "anchorRect.left" + anchorRect.left);
			LogUtils.i(TAG, "rootWidth: " + rootWidth);
		} else {
			xPos = anchorRect.centerX();
//			if (anchor.getWidth() > rootWidth) {
//				xPos = anchorRect.centerX();
//				LogUtils.i(TAG, "anchor.getWidth() > rootWidth");
//				LogUtils.i(TAG, "xPos" + xPos);
//			} else {
//				xPos = anchorRect.left;
//				LogUtils.i(TAG, "anchor.getWidth()<= rootWidth");
//				LogUtils.i(TAG, "xPos" + xPos);
//			}
		}
		LogUtils.d(TAG, "showAsDropDown xPos: " + xPos);
		
		yPos = anchorRect.centerY();
		mShowAndHideType = GROW_FORM_TOP_TO_BOTTOM;
		// automatically get X coord of popup (top left)
		// 计算y轴的位置
		if ((anchorRect.top + rootHeight) > screenHeight) {
			mShowAndHideType = GROW_FORM_BOTTOM_TO_TOP;
			int posOfY = anchorRect.top - (rootHeight - anchor.getHeight() / 2);
			if (posOfY >= 0) {
				yPos = posOfY;
				LogUtils.i(TAG, "(anchorRect.top + rootHeight) > screenHeight");
				LogUtils.i(TAG, "anchorRect.top" + anchorRect.top);
				LogUtils.i(TAG, "rootHeight: " + rootHeight);
			}
		} else {
			
		}
		LogUtils.d(TAG, "showAsDropDown mShowAndHideType: " + mShowAndHideType);
		LogUtils.d(TAG, "showAsDropDown yPos: " + yPos);
		
		setAnimationStyle();
		
        // show
        super.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }
    
	 /**
     * Set animation style
     * 
     * @param screenWidth screen width
     * @param requestedX distance from left edge
     * @param onTop flag to indicate where the popup should be displayed. Set TRUE if displayed on top of anchor view
     * 		  and vice versa
     */
    private void setAnimationStyle() {
    	switch (mShowAndHideType) {
		case GROW_FORM_BOTTOM_TO_TOP:
			super.setAnimationStyle(R.style.BattleStyles_Moveselector_PopUp);
			break;
		case GROW_FORM_TOP_TO_BOTTOM:
		default:
			super.setAnimationStyle(R.style.BattleStyles_Moveselector_PopDown);
			break;
		}
    }
	
	/**
     * Listener that is called when this popup window is dismissed.
     */
    public interface OnShowListener {
        /**
         * Called when this popup window is dismissed.
         */
        public void onShow();
    }
    
    public void setOnShowListener(OnShowListener listener) {
    	mOnShowListener = listener;
    }
    
    /**
     * Listener that is Called when one of this move list is clicked.
     */
    public interface OnMoveItemClickListener {
        /**
         * Called when one of this move list is clicked.
         */
        public void onMoveItemClick(int position, BasicMove move);
    }
    
    public void setOnItemClickListener(OnMoveItemClickListener listener) {
    	mOnMoveItemClickListener = listener;
    	if (null != mOnMoveItemClickListener) {
    		mMoveListView.setOnItemClickListener(new ListView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mOnMoveItemClickListener.onMoveItemClick(position, mAdapter.getItem(position));
					MoveSelectorPopWin.this.dismiss();
				}
			});
    	} else {
    		mMoveListView.setOnItemClickListener(null);
    	}
    }
}