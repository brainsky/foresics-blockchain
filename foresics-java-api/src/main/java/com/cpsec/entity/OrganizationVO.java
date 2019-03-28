package com.cpsec.entity;

/**
 * 主要作为ChannelService传递共同的参数使用，把几个参数放到一个对象里面
 * @author Administrator
 *
 */
public class OrganizationVO {
	
	
	 // 组织名称
	String orgName;
	
	// 通道名称
	String channelName;
	
	// 用户名称
	String userName;

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
}
