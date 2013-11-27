package com.wuxiadou.android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.wuxiadou.android.R;
import com.wuxiadou.android.activity.base.BaseFragment;
import com.wuxiadou.android.control.manager.BattleManager;

public class BattlesFragment extends BaseFragment {
	private ListView own_moves_LV;
	private ListView enemy_moves_LV;
	
	private Button moveset_BT;
	private BattleManager manager;
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
				manager.gotoSetBasicMoveInSpecPos(position, view);
			}
		});
	}

	@Override
	public void initData(View view, Bundle savedInstanceState) {
		manager = new BattleManager(context, handler);
		own_moves_LV.setAdapter(manager.getMyMovesAdapter());
		enemy_moves_LV.setAdapter(manager.getEnemyMovesAdapter());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.battle_moveset_submit_btn:
			manager.submitMovesSet();
			break;

		default:
			break;
		}
	}

	@Override
	protected Handler createActivityHandler() {
		return null;
	}

}
