package com.wuxiadou.android.control.manager;

import com.wuxiadou.android.control.adapter.MoveListAdapter;
import com.wuxiadou.android.model.battle.BasicMove;
import com.wuxiadou.android.view.MoveSelectorPopWin;

import android.content.Context;
import android.os.Handler;
import android.view.View;

public class BattleManager extends BaseManager {

	private MoveListAdapter myMovesAdapter;
	private MoveListAdapter enemyMovesAdapter;
	private MoveSelectorPopWin popWin;
	private int currentPosition;
	public BattleManager(Context context, Handler handler) {
		super(context, handler);
		myMovesAdapter = new MoveListAdapter(context);
		enemyMovesAdapter = new MoveListAdapter(context);
		popWin = new MoveSelectorPopWin(context);
	}
	
	public MoveListAdapter getMyMovesAdapter() {
		return myMovesAdapter;
	}

	public MoveListAdapter getEnemyMovesAdapter() {
		return enemyMovesAdapter;
	}
	
	public void gotoSetBasicMoveInSpecPos(int position, View anchor) {
		currentPosition = position;
		popWin.setOnItemClickListener(new MoveSelectorPopWin.OnMoveItemClickListener() {
			@Override
			public void onMoveItemClick(int position, BasicMove move) {
				myMovesAdapter.update(currentPosition, move);
				myMovesAdapter.notifyDataSetChanged();
			}
		});
		popWin.showMoveSelector(anchor);
	}
	
	public boolean submitMovesSet() {
		if (!myMovesAdapter.isAllMoveSet()) {
			toastShow("请完成招式");
			return false;
		}
		enemyMovesAdapter.randomFill();
		enemyMovesAdapter.notifyDataSetChanged();
		return true;
	}

}
