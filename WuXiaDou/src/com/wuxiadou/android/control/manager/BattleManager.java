package com.wuxiadou.android.control.manager;

import java.util.ArrayList;
import java.util.List;

import com.wuxiadou.android.model.battle.BasicMove;

import android.content.Context;
import android.os.Handler;

public class BattleManager extends BaseManager {
	public List<BasicMove> ownMovesList = new ArrayList<BasicMove>();
	public List<BasicMove> enemyMovesList = new ArrayList<BasicMove>();

	public BattleManager(Context context, Handler handler) {
		super(context, handler);
		for (int i = 0; i < 5; i++) {
			ownMovesList.add(null);
			enemyMovesList.add(null);
		}
	}

}
