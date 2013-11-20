package com.wuxiadou.android.control.adapter;

import org.ixming.android.control.adapter.AbsResLayoutAdapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.wuxiadou.android.R;
import com.wuxiadou.android.control.MoveLoader;
import com.wuxiadou.android.model.battle.BasicMove;

public class MoveSelectorAdapter extends AbsResLayoutAdapter<BasicMove, MoveSelectorAdapter.ViewHolder> {

	private final MoveLoader mMoveLoader;
	public MoveSelectorAdapter(Context context) {
		super(context);
		mMoveLoader = MoveLoader.getInstance();
		setData(mMoveLoader.getOriginalBasicMoveList());
	}

	class ViewHolder {
		TextView name_TV;
	}

	@Override
	protected int provideLayoutResId() {
		return R.layout.moves_list_item;
	}
	
	@Override
	public BasicMove getItem(int position) {
		if (position < super.getCount()) {
			return super.getItem(position);
		} else {
			return null;
		}
	}

	@Override
	protected void bindView(ViewHolder holder, BasicMove data, int position, View view) {
		String name;
		if (position < super.getCount()) {
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
	
}
