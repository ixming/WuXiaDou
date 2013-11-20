package com.wuxiadou.android.model;

import java.util.List;

import com.wuxiadou.android.model.battle.PlayerSkill;

/**
 * 玩家信息
 * @author Yin Yong
 * @version 1.0
 */
public class PlayerDetailInfo {
	// 玩家自己使用的技能，没有学习过相应技能之前的“自定义技能”
	private List<PlayerSkill> selfSkills;
	// 玩家学习的技能，该技能有具体的名称
	private List<PlayerSkill> learnedSkills;
	
	public List<PlayerSkill> getSelfSkills() {
		return selfSkills;
	}

	public void setSelfSkills(List<PlayerSkill> selfSkills) {
		this.selfSkills = selfSkills;
	}

	public List<PlayerSkill> getLearnedSkills() {
		return learnedSkills;
	}

	public void setLearnedSkills(List<PlayerSkill> learnedSkills) {
		this.learnedSkills = learnedSkills;
	}
	
}
