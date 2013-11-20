package com.wuxiadou.android.model;

/**
 * 一个玩家，他拥有一些自定义或者学成的技能组合。<br/>
 * 各个技能的等级信息，历史战绩，血量。
 * @author Yin Yong
 * @version 1.0
 */
public class Player {

	private long id;
	private String name;
	// 玩家的血量
	private String blood;
	// 玩家头像
	private String avatarUrl;
	
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

	public String getBlood() {
		return blood;
	}

	public void setBlood(String blood) {
		this.blood = blood;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

}
