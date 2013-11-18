package com.wuxiadou.activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wuxiadou.R;
import com.wuxiadou.activity.base.BaseActivity;
import com.wuxiadou.control.adapter.MovesListAdapter;
import com.wuxiadou.control.manager.BattleManager;
import com.wuxiadou.model.bean.Moves;
import com.wuxiadou.utils.CacheUtil;

public class BattleActivity extends BaseActivity {
	ListView own_moves_LV;
	ListView enemy_moves_LV;
	MovesListAdapter ownMovesAdapter;
	MovesListAdapter enemyMovesAdapter;
	BattleManager manager;
	Button moves_key_1_BT;
	Button moves_key_2_BT;
	Button moves_key_3_BT;
	Button moves_key_4_BT;
	Button moves_key_5_BT;
	LinearLayout base_moves_list_LL;
	int currentPosition;

	@Override
	public int getLayoutResId() {
		// TODO Auto-generated method stub
		return R.layout.battle_layout;
	}

	@Override
	public void initView(View view) {
		own_moves_LV = (ListView) findViewById(R.id.own_moves_lv);
		enemy_moves_LV = (ListView) findViewById(R.id.enemy_moves_lv);
		moves_key_1_BT=(Button)findViewById(R.id.moves_key_1);
		moves_key_2_BT=(Button)findViewById(R.id.moves_key_2);
		moves_key_3_BT=(Button)findViewById(R.id.moves_key_3);
		moves_key_4_BT=(Button)findViewById(R.id.moves_key_4);
		moves_key_5_BT=(Button)findViewById(R.id.moves_key_5);
		base_moves_list_LL=(LinearLayout)findViewById(R.id.base_moves_list_ll);
	}

	@Override
	public void initListener() {
		bindClickListener(moves_key_1_BT);
		bindClickListener(moves_key_2_BT);
		bindClickListener(moves_key_3_BT);
		bindClickListener(moves_key_4_BT);
		bindClickListener(moves_key_5_BT);
		own_moves_LV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				base_moves_list_LL.setVisibility(View.VISIBLE);
				currentPosition=arg2;
			}
		});
	}

	@Override
	public void initData(View view, Bundle savedInstanceState) {
		for (int i = 1; i <= 5; i++) {
			// 杀 防 极 制 闪
			// 火 金 木 土 水
			Moves moves = new Moves();
			moves.setId(i);
			moves.setKey(i);
			switch (i) {
			case 1:
				moves.setName("杀（火）");
				break;
			case 2:
				moves.setName("防（金）");
				break;
			case 3:
				moves.setName("极（木）");
				break;
			case 4:
				moves.setName("制（土）");
				break;
			case 5:
				moves.setName("闪（水）");
				break;
			}
			CacheUtil.movesCache.put(i, moves);
		}
		manager = new BattleManager(context, handler);
		ownMovesAdapter = new MovesListAdapter(context, manager.ownMovesList);
		enemyMovesAdapter = new MovesListAdapter(context,
				manager.enemyMovesList);
		own_moves_LV.setAdapter(ownMovesAdapter);
		enemy_moves_LV.setAdapter(enemyMovesAdapter);
	}

	@Override
	public int getActivityStartSource() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Handler createActivityHandler() {
		// TODO Auto-generated method stub
		return new MyHanlder();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.moves_key_1:
			manager.ownMovesList.set(currentPosition,
					CacheUtil.movesCache.get(1));
			break;
		case R.id.moves_key_2:
			manager.ownMovesList.set(currentPosition,
					CacheUtil.movesCache.get(2));
			break;
		case R.id.moves_key_3:
			manager.ownMovesList.set(currentPosition,
					CacheUtil.movesCache.get(3));
			break;
		case R.id.moves_key_4:
			manager.ownMovesList.set(currentPosition,
					CacheUtil.movesCache.get(4));
			break;
		case R.id.moves_key_5:
			manager.ownMovesList.set(currentPosition,
					CacheUtil.movesCache.get(5));
			break;
		default:
			break;
		}
		base_moves_list_LL.setVisibility(View.GONE);
		ownMovesAdapter.notifyDataSetChanged();
	}

	class MyHanlder extends Handler {
	}
}
