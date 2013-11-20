package com.wuxiadou.android.model;

import org.ixming.utils.StringUtil;

/**
 * 技能。<br/>
 * 在游戏中，技能有名称，有基本招式组合，有最高等级，最高星级。
 * @author Yin Yong
 * @version 1.0
 */
public class Skill {

	private long id;
	/**
	 * 名字
	 */
	private String name;
	/**
	 * 技能使用时调用的基本招式组合，以字符串的方式传输即可（ID组成的字符串）
	 */
	private String moveCombination;
	/**
	 * 技能的最高等级
	 */
	private String topLevel;
	/**
	 * 技能的最高星级
	 */
	private String topGrade;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMoveCombination() {
		return moveCombination;
	}

	public void setMoveCombination(String moveCombination) {
		this.moveCombination = moveCombination;
	}

	public String getTopLevel() {
		return topLevel;
	}

	public void setTopLevel(String topLevel) {
		this.topLevel = topLevel;
	}

	public String getTopGrade() {
		return topGrade;
	}

	public void setTopGrade(String topGrade) {
		this.topGrade = topGrade;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			return true;
		}
		if (!(o instanceof Skill)) {
			return false;
		}
		Skill other = (Skill) o;
		return hashCode() == other.hashCode();
	}
	
	@Override
	public int hashCode() {
		int hash = StringUtil.isEmpty(name) ? 0 : name.hashCode();
		hash = hash * 31 + (StringUtil.isEmpty(moveCombination) ? 0 : moveCombination.hashCode());
		return hash;
	}
}
