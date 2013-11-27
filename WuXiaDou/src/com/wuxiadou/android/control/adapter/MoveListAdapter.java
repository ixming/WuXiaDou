package com.wuxiadou.android.control.adapter;

import org.ixming.android.control.adapter.AbsResLayoutAdapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.wuxiadou.android.R;
import com.wuxiadou.android.control.MoveLoader;
import com.wuxiadou.android.model.battle.BasicMove;

public class MoveListAdapter extends AbsResLayoutAdapter<BasicMove, MoveListAdapter.ViewHolder> {

	private final BasicMove EMPTY_ITEM = new BasicMove();
	private final MoveLoader mMoveLoader;
	public MoveListAdapter(Context context) {
		super(context);
		mMoveLoader = MoveLoader.getInstance();
		fillWithEmpty();
	}
	
	class ViewHolder {
		TextView name_TV;
	}

	@Override
	protected int provideLayoutResId() {
		return R.layout.moves_list_item;
	}

	@Override
	protected void bindView(ViewHolder holder, BasicMove data, int position, View view) {
		String name;
		if (EMPTY_ITEM != data) {
			name = mMoveLoader.toAppearanceString(data);
		} else {
			name = mMoveLoader.noSetBasicMoveAlias(position);
		}
		holder.name_TV.setText(name);
	}

	@Override
	protected ViewHolder newHolder(int position, View view) {
		ViewHolder holder = new ViewHolder();
		holder.name_TV = (TextView) view.findViewById(R.id.battle_moveset_item_tv);
		return holder;
	}

	/**
	 * 技能是否已经设置完成。
	 */
	public boolean isAllMoveSet() {
		for (int i = 0; i < getCount(); i++) {
			if (getItem(i) == EMPTY_ITEM) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 随机生成技能，填充该适配器。
	 */
	public void randomFill() {
		setData(mMoveLoader.randomGetBasicMoveList());
	}
	
	/**
	 * 用空对象填充该适配器，即重置技能栏。
	 */
	public void fillWithEmpty() {
		for (int i = 0; i < mMoveLoader.getBasicMoveCount(); i++) {
			appendData(EMPTY_ITEM);
		}
	}
}
