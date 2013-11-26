package com.wuxiadou.android.common.view;

import org.ixming.android.utils.AndroidUtil;

import com.wuxiadou.android.R;
import com.wuxiadou.android.control.MoveLoader;
import com.wuxiadou.android.control.manager.BattleManager;
import com.wuxiadou.android.utils.ImageUtil;
import com.wuxiadou.android.utils.LogUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class BatterSelectorView_WithAnim extends RelativeLayout {

	final String TAG = BatterSelectorView_WithAnim.class.getSimpleName();
	
	private View rootView;
	private ListView own_moves_LV;
	private ListView enemy_moves_LV;
	private BattleManager manager;
	
	private Animation own_moves_Anim;
	private Animation enemy_moves_Anim;
	
	private View movesSelectorView;
	
	private WindowManager.LayoutParams windowParams;
	private WindowManager windowManager;
	private boolean movesSelectorHasAdded = false;
	
	public BatterSelectorView_WithAnim(Context context) {
		super(context);
		init();
	}

	public BatterSelectorView_WithAnim(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BatterSelectorView_WithAnim(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		rootView = inflater.inflate(R.layout.battle_move_selector_spec, this);
		movesSelectorView = inflater.inflate(R.layout.battle_singlemove_selector_horizontal, null);
		manager = new BattleManager(getContext(), new Handler());
		
		own_moves_LV = (ListView) rootView.findViewById(R.id.own_moves_lv);
		enemy_moves_LV = (ListView) rootView.findViewById(R.id.enemy_moves_lv);
		
		own_moves_LV.setAdapter(manager.getMyMovesAdapter());
		enemy_moves_LV.setAdapter(manager.getEnemyMovesAdapter());
		
		AndroidUtil.setViewVisibility(movesSelectorView, INVISIBLE);
		
		own_moves_Anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f,
        		Animation.RELATIVE_TO_PARENT, 0f,
        		Animation.RELATIVE_TO_PARENT, 0f);
		own_moves_Anim.setDuration(500l);
		own_moves_Anim.setInterpolator(new OvershootInterpolator(2f));
		own_moves_LV.setLayoutAnimation(new LayoutAnimationController(own_moves_Anim));
		
		enemy_moves_Anim = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f,
        		Animation.RELATIVE_TO_PARENT, 1.0f,
        		Animation.RELATIVE_TO_PARENT, 0f);
		enemy_moves_Anim.setDuration(500l);
		enemy_moves_Anim.setInterpolator(new OvershootInterpolator(2f));
		enemy_moves_LV.setLayoutAnimation(new LayoutAnimationController(enemy_moves_Anim));
		
		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		movesSelectorView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideMoveSelector();
			}
		});
		
		own_moves_LV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showMoveSelector(view);
			}
		});
	}

    private void showMoveSelector(View anchor) {
    	hideMoveSelector();
    	
//    	movesSelectorHasAdded = true;
//    	int[] location = new int[2];
//        anchor.getLocationOnScreen(location);
//        Rect anchorRect = new Rect(location[0], location[1], location[0]
//                + anchor.getWidth(), location[1] + anchor.getHeight());
//        
//        windowParams = new WindowManager.LayoutParams();
//		windowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
//		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL 
//		                                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//		                               | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//		windowParams.gravity = Gravity.TOP | Gravity.LEFT;
//		
//	    //得到preview左上角相对于屏幕的坐标  
//        windowParams.x = 100;  
//        windowParams.y = 100;  
//		
//		windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//		windowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
//		windowParams.alpha = 0.8f;
//
//		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  
//        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE  
//        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON  
//        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;  
//		
//		windowParams.format = PixelFormat.TRANSLUCENT;  
//		windowParams.windowAnimations = 0;  
//        windowManager.addView(movesSelectorView, windowParams);
//        
//        AndroidUtil.setViewVisibility(movesSelectorView, VISIBLE);
//        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f,
//        		Animation.RELATIVE_TO_PARENT, 0f,
//        		Animation.RELATIVE_TO_PARENT, 0f);
//        animation.setDuration(500l);
//        animation.setInterpolator(new OvershootInterpolator(2f));
//        movesSelectorView.startAnimation(animation);
    }
    
    private void hideMoveSelector() {
    	if (movesSelectorHasAdded) {
    		windowManager.removeView(movesSelectorView);
    		movesSelectorHasAdded = false;
    	}
    }
    
    @Override
    public void setVisibility(int visibility) {
    	if (visibility != getVisibility()){
    		super.setVisibility(visibility);
    		if (visibility == VISIBLE) {
    			own_moves_LV.startLayoutAnimation();
    			enemy_moves_LV.startLayoutAnimation();
    		}
    	}
    }
}
