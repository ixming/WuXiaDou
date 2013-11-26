package com.wuxiadou.android.common.view;

import com.wuxiadou.android.R;
import com.wuxiadou.android.control.MoveLoader;
import com.wuxiadou.android.utils.ImageUtil;
import com.wuxiadou.android.utils.LogUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class BatterSelectorView extends View {

	private final String TAG = BatterSelectorView.class.getSimpleName();
	
	private boolean mHasInitCaches;
	private MoveLoader mMoveLoader;
	private String[] mMoveNames;
	// bitmap caches
	private Bitmap mStateNormalBm;
	private Bitmap mStatePressedBm;
	private Bitmap mStateSelectedBm;
	private Bitmap mWiredLineBm;
	
	// view size caches
	private int mMeasuredWidth;
	private int mMeasuredHeight;
	
	private MovePoint mMovesRects[][];
	private MovePoint mSelectedMoves[];
	private int mSelectedSize;
	private TouchMovingPoint mTouchPoint = new TouchMovingPoint();
	
	private int mMoveItemSize;
	private int mMoveItemHorizotalGap;
	private int mMoveItemVerticalGap;
	
	private Matrix mMatrix = new Matrix();
	private Paint mNonTextPaint;
	private Paint mTextPaint;
	
	public BatterSelectorView(Context context) {
		super(context);
		init();
	}

	public BatterSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BatterSelectorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		if (!mHasInitCaches) {
			initCaches();
			mHasInitCaches = true;
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		
		final int moveCount = mMoveLoader.getBasicMoveCount();
		int calWidth = getPaddingLeft()
				+ mMoveItemSize * moveCount
				+ mMoveItemHorizotalGap * (moveCount - 1)
				+ getPaddingRight();
		width = Math.max(width, calWidth);
		int calHeight = getPaddingTop()
				+ mMoveItemSize * moveCount
				+ mMoveItemVerticalGap * (moveCount - 1)
				+ getPaddingBottom();
		height = Math.max(height, calHeight);
		if (width != mMeasuredWidth || height != mMeasuredHeight) {
			LayoutParams lp = getLayoutParams();
			if (null == lp) {
				lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			}
			lp.width = calWidth;
			lp.height = calHeight;
			setLayoutParams(lp);
			mMeasuredWidth = width;
			mMeasuredHeight = height;
		}
		super.onMeasure(MeasureSpec.makeMeasureSpec(width, widthMode),
				MeasureSpec.makeMeasureSpec(height, heightMode));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		drawMovesSelector(canvas);
	}

	/**
	 * 初始化一些缓存。
	 */
	private void initCaches() {
		mMoveLoader = MoveLoader.getInstance();
		mMoveNames = mMoveLoader.getOriginalBasicMoveNames();
		
		mMoveItemSize = 45;
		mMoveItemHorizotalGap = 45;
		mMoveItemVerticalGap = 45;
		
		mNonTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setTextSize(30);
		
		ImageUtil imageUtil = ImageUtil.getInstance();
		mStateNormalBm = imageUtil.getBitmapFromRes(getContext(),
				R.drawable.battle_moves_selector_item_normal);
		mStateNormalBm = Bitmap.createScaledBitmap(mStateNormalBm, mMoveItemSize, mMoveItemSize, true);
		mStatePressedBm = imageUtil.getBitmapFromRes(getContext(),
				R.drawable.battle_moves_selector_item_pressed);
		mStatePressedBm = Bitmap.createScaledBitmap(mStatePressedBm, mMoveItemSize, mMoveItemSize, true);
		mStateSelectedBm = imageUtil.getBitmapFromRes(getContext(),
				R.drawable.battle_moves_selector_item_selected);
		mStateSelectedBm = Bitmap.createScaledBitmap(mStateSelectedBm, mMoveItemSize, mMoveItemSize, true);
		
		mWiredLineBm = imageUtil.getBitmapFromRes(getContext(),
				R.drawable.battle_moves_selector_wiredline);
		
		layoutChildren();
		System.gc();
	}
	
	private void layoutChildren() {
		final int moveCount = mMoveLoader.getBasicMoveCount();
		mMovesRects = new MovePoint[moveCount][moveCount];
		mSelectedMoves = new MovePoint[moveCount];
		
		int xPaddingOffset = getPaddingLeft();
		int yPaddingOffset = getPaddingTop();
		
		for (int i = 0; i < mMovesRects.length; i++) {
			for (int j = 0; j < mMovesRects[i].length; j++) {
				MovePoint movePoint = new MovePoint();
				Rect rect = new Rect();
				rect.left = xPaddingOffset
						+ mMoveItemHorizotalGap * i
						+ mMoveItemSize * i;
				rect.right = rect.left + mMoveItemSize;
				
				rect.top = yPaddingOffset
						+ mMoveItemVerticalGap * j
						+ mMoveItemSize * j;
				rect.bottom = rect.top + mMoveItemSize;
				
				movePoint.moveIndex = i;
				movePoint.bound = rect;
				movePoint.setNormal();
				mMovesRects[i][j] = movePoint;
			}
		}
	}

	private void drawMovesSelector(Canvas canvas) {
		// 画图以及文字
		for (int i = 0; i < mMovesRects.length; i++) {
			for (int j = 0; j < mMovesRects[i].length; j++) {
				Bitmap bm;
				if (mMovesRects[i][j].isNormal()) {
					bm = mStateNormalBm;
				} else if (mMovesRects[i][j].isPressed()) {
					bm = mStatePressedBm;
				} else if (mMovesRects[i][j].isSelected()) {
					bm = mStateSelectedBm;
				} else {
					bm = mStateNormalBm;
				}
				canvas.drawBitmap(bm, 
						mMovesRects[i][j].bound.left,
						mMovesRects[i][j].bound.top,
						mNonTextPaint);
				canvas.drawText(mMoveNames[i],
						mMovesRects[i][j].bound.centerX(),
						mMovesRects[i][j].bound.bottom - (mMoveItemSize - (int)mTextPaint.measureText(mMoveNames[i])) / 2,
						mTextPaint);
			}
		}
		
		// 绘制路径
		if (mSelectedSize > 0) {
			int indexUnNull = 0;
			MovePoint lastMovePoint = null;
			for (; indexUnNull < mSelectedMoves.length && null != mSelectedMoves[indexUnNull]; indexUnNull++) {
				if (null != lastMovePoint) {
					// 绘制从lastMovePoint 到mSelectedMoves[indexUnNull]的路径
					drawLineBetweenTwoItems(canvas, lastMovePoint, mSelectedMoves[indexUnNull]);
				}
				lastMovePoint = mSelectedMoves[indexUnNull];
			}
			if (!isFullySelected()) {
				// 绘制从lastMovePoint 到手势的路径
				drawLineBetweenItemAndMotion(canvas, lastMovePoint);
			}
		}
	}
	
	private void drawLineBetweenTwoItems(Canvas canvas, MovePoint fromPoint, MovePoint toPoint) {
		drawLine(canvas, fromPoint.bound.centerX(), fromPoint.bound.centerY(),
				toPoint.bound.centerX(), toPoint.bound.centerY());
	}
	
	private void drawLineBetweenItemAndMotion(Canvas canvas, MovePoint fromPoint) {
		if (mTouchPoint.isSpecified()) {
			drawLine(canvas, fromPoint.bound.centerX(), fromPoint.bound.centerY(),
					mTouchPoint.x, mTouchPoint.y);
		}
	}
	
	private void drawLine(Canvas canvas, int fromX, int fromY, int toX, int toY) {
		float ah = (float) calDistance(fromX, fromY, toX, toY);
		int dx = toX - fromX;
		int dy = toY - fromY;
		float degree = calDegree(dx, dy);
		
		canvas.rotate(degree, fromX, fromY);
		
		mMatrix.setScale(ah / mWiredLineBm.getWidth(), 1);
		mMatrix.postTranslate(fromX, fromY - mWiredLineBm.getHeight() / 2.0f);
		canvas.drawBitmap(mWiredLineBm, mMatrix, mNonTextPaint);
		
		canvas.rotate(-degree, fromX, fromY);
	}
	
	private float calDistance(double x1, double y1, double x2, double y2) {
		return (float) Math.sqrt(Math.abs(x1 - x2) * Math.abs(x1 - x2)
				+ Math.abs(y1 - y2) * Math.abs(y1 - y2));
	}
	
	private float calDegree(int dx, int dy) {
		float degrees = 0;
		if (dx == 0) { // x轴相等 90度或270
			if (dy > 0) // 在y轴的下边 90
			{
				degrees = 90;
			}
			else if (dy < 0) // 在y轴的上边 270
			{
				degrees = 270;
			}
		} else if (dy == 0) { // y轴相等 0度或180
			if (dx > 0) // 在x轴的右边 0
			{
				degrees = 0;
			}
			else if (dx < 0) // 在x轴的左边 180
			{
				degrees = 180;
			}
		} else {
			if (dx > 0) { // 0~90 or 270~360
				if (dy > 0) // 0~90
				{
					degrees = 0 + (float) Math.toDegrees(Math.atan2(Math.abs(dy), Math.abs(dx)));
				} else if (dy < 0) // 270~360
				{
					degrees = 360 - (float) Math.toDegrees(Math.atan2(Math.abs(dy), Math.abs(dx)));
				}
			} else if (dx < 0) { // 90~270
				if (dy > 0) // 90 ~ 180
				{
					degrees = 180 - (float) Math.toDegrees(Math.atan2(Math.abs(dy), Math.abs(dx)));
				} else if (dy < 0) // 180~270
				{
					degrees = 180 + (float) Math.toDegrees(Math.atan2(Math.abs(dy), Math.abs(dx)));
				}
			}
		}
		return degrees;
	}
	
	private boolean mIsMoveSelecting = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				mIsMoveSelecting = false;
				checkMotionDown(event);
				if (mIsMoveSelecting) {
					postInvalidate();
					return true;
				}
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				if (mIsMoveSelecting) {
					doMotionMove(event);
					postInvalidate();
					return true;
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL: {
				if (mIsMoveSelecting) {
					// 
					doMotionMove(event);
					// 初始化
					initSelectedPoint();
					postInvalidate();
					mIsMoveSelecting = false;
					return true;
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}
	
	/**
	 * 初始化选中项数据
	 */
	private void initSelectedPoint() {
		for (int i = 0; i < mSelectedMoves.length && null != mSelectedMoves[i]; i++) {
			mSelectedMoves[i].setNormal();
			mSelectedMoves[i] = null;
		}
		mSelectedSize = 0;
		mTouchPoint.reset();
	}
	
	/**
	 * 是否已经达到最大选择个数
	 */
	private boolean isFullySelected() {
		return mSelectedSize == mMoveLoader.getBasicMoveCount();
	}
	
	private void checkMotionDown(MotionEvent event) {
		final int touchX = (int) event.getX();
		final int touchY = (int) event.getY();
		if (checkAppendSelectedPoint(touchX, touchY)) {
			// do something?
			mIsMoveSelecting = true;
		}
	}
	
	private void doMotionMove(MotionEvent event) {
		if (!isFullySelected()) {
			final int touchX = (int) event.getX();
			final int touchY = (int) event.getY();
			if (checkAppendSelectedPoint(touchX, touchY)) {
				
			} else {
				// 绘制线路
				mTouchPoint.set(touchX, touchY);
			}
		} else {
			mTouchPoint.reset();
		}
	}
	
	/**
	 * 当手势移动到的地方属于某个Item时，判断是否已经选中过，最终添加.
	 * @return 当
	 */
	private boolean checkAppendSelectedPoint(int touchX, int touchY) {
		MovePoint move = checkSelectedPoint(touchX, touchY);
		if (null == move) {
			return false;
		}
		if (isIncludedInSelectedArray(move)) {
			return false;
		}
		move.setPressed();
		mSelectedMoves[mSelectedSize++] = move;
		return true;
	}
	
	private boolean isIncludedInSelectedArray(MovePoint move) {
		for (int i = 0; i < mSelectedMoves.length && null != mSelectedMoves[i]; i++) {
			if (move == mSelectedMoves[i]) {
//			if (move.moveIndex == mSelectedMoves[i].moveIndex) {
				return true;
			}
		}
		return false;
	}
	
	private MovePoint checkSelectedPoint(int touchX, int touchY) {
		for (int i = 0; i < mMovesRects.length; i++) {
			for (int j = 0; j < mMovesRects[i].length; j++) {
				if (mMovesRects[i][j].bound.contains(touchX, touchY)) {
					// 如果该位置已经选中，则不能加入mSelectedMoves
					return mMovesRects[i][j];
				}
			}
		}
		return null;
	}
	
	private class MovePoint {
		private static final int STATE_NORMAL = 0;
		private static final int STATE_PRESSED = 1;
		private static final int STATE_SELECTED = 2;
		public int moveIndex;
		public Rect bound;
		private int state = STATE_NORMAL;
		
		public boolean isFirstMove() {
			return 0 == moveIndex;
		}
		
		public void setNormal() {
			state = STATE_NORMAL;
		}
		
		public void setPressed() {
			state = STATE_PRESSED;
		}

		public void setSelected() {
			state = STATE_SELECTED;
		}
		
		public boolean isNormal() {
			return state == STATE_NORMAL;
		}
		
		public boolean isPressed() {
			return state == STATE_PRESSED;
		}

		public boolean isSelected() {
			return state == STATE_SELECTED;
		}
		
		@Override
		public String toString() {
			return "moveIndex = " + moveIndex + ", bound = " + bound.toString() + ", state = " + state;
		}
	}
	
	private class TouchMovingPoint extends Point {
		private final int UNSPECIFIED = -1;
		public TouchMovingPoint() {
			reset();
		}
		public boolean isSpecified() {
			return x != UNSPECIFIED && y != UNSPECIFIED;
		}
		
		public void reset() {
			x = UNSPECIFIED;
			y = UNSPECIFIED;
		}
	}
}
