/*
 *  Copyright by Liangkongde.
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */
package com.cpsec.help;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;


public interface ConfigHelper {
	
	/**
	 * 根据组织名称和用户名称获取HFClient实例对象
	 * @param orgName 组织名称
	 * @param userName 用户名
	 * @return HFClient实例
	 */
	HFClient getClientForOrg(String orgName, String userName) throws Exception;
	
	
	/**
	 * 根据通道名称获取Fabric SDK Channel实例。
	 * @param channelName 通道名称
	 * @return Channel实例
	 */
	Channel getChannel(HFClient client, String channelName) throws Exception;
	
	/**
	 * 根据链码名称和链码路径返回链码ID，其他参数默认设置。
	 * @param ccName    链码名称
	 * @param ccPath    链码路径
	 * @param ccVersion 链码版本
	 * @return 链码ID
	 */
	ChaincodeID getChaincodeID(String ccName, String ccPath, String ccVersion);
	
}
