package com.wuxiadou.android.activity;

import com.wuxiadou.android.R;
import com.wuxiadou.android.activity.base.BaseActivity;
import com.wuxiadou.android.control.adapter.MoveListAdapter;
import com.wuxiadou.android.control.manager.BattleManager;
import com.wuxiadou.android.model.battle.BasicMove;
import com.wuxiadou.android.view.MoveSelectorPopWin;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class BattleActivity extends BaseActivity {
	ListView own_moves_LV;
	ListView enemy_moves_LV;
	Button moveset_BT;
	MoveListAdapter ownMovesAdapter;
	MoveListAdapter enemyMovesAdapter;
	BattleManager manager;
	int currentPosition;

	@Override
	public int getLayoutResId() {
		return R.layout.battle_layout;
	}

	@Override
	public void initView(View view) {
		own_moves_LV = (ListView) findViewById(R.id.own_moves_lv);
		enemy_moves_LV = (ListView) findViewById(R.id.enemy_moves_lv);
		moveset_BT = (Button) findViewById(R.id.battle_moveset_submit_btn);
	}

	@Override
	public void initListener() {
		bindClickListener(moveset_BT);
		own_moves_LV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				currentPosition = position;
				
				MoveSelectorPopWin popWin = new MoveSelectorPopWin(context);
				popWin.setOnItemClickListener(new MoveSelectorPopWin.OnMoveItemClickListener() {
					@Override
					public void onMoveItemClick(int position, BasicMove move) {
						ownMovesAdapter.update(currentPosition, move);
						ownMovesAdapter.notifyDataSetChanged();
					}
				});
				popWin.showMoveSelector(view);
			}
		});
	}

	@Override
	public void initData(View view, Bundle savedInstanceState) {
		manager = new BattleManager(context, handler);
		ownMovesAdapter = new MoveListAdapter(context);
		enemyMovesAdapter = new MoveListAdapter(context);
		own_moves_LV.setAdapter(ownMovesAdapter);
		enemy_moves_LV.setAdapter(enemyMovesAdapter);
	}

	@Override
	protected Handler createActivityHandler() {
		return new MyHanlder();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.battle_moveset_submit_btn:
			if (ownMovesAdapter.isAllSet()) {
				enemyMovesAdapter.randomFill();
				enemyMovesAdapter.notifyDataSetChanged();
			} else {
				manager.toastShow("请完成招式");
			}
			break;

		default:
			break;
		}
	}

	class MyHanlder extends Handler {
	}
}
