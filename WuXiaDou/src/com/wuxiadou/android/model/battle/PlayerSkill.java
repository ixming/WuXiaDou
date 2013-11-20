package com.wuxiadou.android.model.battle;

import com.wuxiadou.android.model.Skill;

/**
 * 除了技能的基本信息外，加入了玩家本技能当前的等级，星级和经验，加成信息等等。
 * @author Yin Yong
 * @version 1.0
 */
public class PlayerSkill extends Skill {
	/**
	 * 加成系数，用于显示；在服务器端进行计算
	 */
	private String coefficient;

	/**
	 * 技能等级，用于显示；在服务器端进行计算
	 */
	private String level;
	/**
	 * 技能经验，用于显示；在服务器端进行计算
	 */
	private String exp;
	/**
	 * 1星2星3星4星5星 技能 评分 阶段，用于显示；在服务器端进行计算
	 */
	private int grade;
	
	public String getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(String coefficient) {
		this.coefficient = coefficient;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}
}
