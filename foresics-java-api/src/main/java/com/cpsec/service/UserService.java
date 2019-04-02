package com.cpsec.service;

import org.hyperledger.fabric.sdk.User;

import com.cpsec.util.GlobalManager;


/**
 * 实现用户管理的操作，用户登记、注册、注销，主要负责与Fabric ca交互。
 * @author liang kongde 
 *
 */
public interface UserService {
	
	/**
	 * 查询用户，根据用户名称和组织名称查询用户
	 * @param name 用户名称
	 * @param orgName 组织名称
	 * @return 用户
	 */
	User getMemberUser(String name, String orgName, String type);
	
	/**
	 * 注册用户，根据用户名和组织名注册用户
	 * @param name 用户名称
	 * @param orgName 组织名称
	 * @return 用户
	 */
	User registerUser(String name, String orgName);
	
	/**
	 * 注销用户
	 * @param name
	 * @param orgName
	 */
	void revokeUser(String name, String orgName);

}
