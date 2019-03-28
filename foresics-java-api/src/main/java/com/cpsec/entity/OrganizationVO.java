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
	
	// 链码名称
	String ccName;
	
	// 链码路径
	String ccPath;
	
	// 链码名称
	String ccVersion;
	
    public String getCcPath() {
		return ccPath;
	}

	public void setCcPath(String ccPath) {
		this.ccPath = ccPath;
	}

	public String getCcVersion() {
		return ccVersion;
	}

	public void setCcVersion(String ccVersion) {
		this.ccVersion = ccVersion;
	}

	
	public String getCcName() {
		return ccName;
	}

	public void setCcName(String ccName) {
		this.ccName = ccName;
	}

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
