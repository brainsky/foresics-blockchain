package com.cpsec.service;

import java.util.List;

import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;

import com.cpsec.entity.OrganizationVO;
/**
 * 实现通道服务功能，包括创建通道、安装链码、实例化链码、查询链码、调用链码、查询区块等
 * @author liang kongde
 *
 */
public interface ChannelService {
	
	/**
	 * 查询链码功能函数
	 * @param org
	 * @param functionName
	 * @param args 链码参数
	 * @return
	 */
	String queryChaincodeFunction(OrganizationVO org, String functionName, String ...args) throws Exception;
	
	/**
	 * 在通道中部署链码
	 * @param client
	 * @param channel
	 * @param ccName
	 * @param ccPath
	 * @param ccVersion
	 * @return
	 */
	ChaincodeID deployChaincode(OrganizationVO org, String chaincodeID, String ccName, 
						String ccPath, String ccVersion) throws Exception;
	
	
	/**
	 * 创建通道
	 * @param client
	 * @param channelName
	 * @param orgName
	 * @return
	 */
	Channel createChannel(OrganizationVO org, String txPath);
	
	/**
	 * 查询区块链信息
	 * @param orgName
	 * @param userName
	 * @param channelName
	 * @return BlockchainInfo
	 */
	BlockchainInfo queryBlockChainInfo(OrganizationVO org);
	
	/**
	 * 根据区块hash查询区块
	 * @param orgName
	 * @param userName
	 * @param channelName
	 * @param hash 区块哈希
	 * @return 区块信息
	 */
	BlockInfo queryBlockByHash(OrganizationVO org, String hash);
	
	/**
	 * 根据区块高度查询区块
	 * @param orgName 组织名称
	 * @param userName 用户名称
	 * @param blockNo 区块高度
	 * @return 区块
	 */
	BlockInfo queryBlockByNO(OrganizationVO org, String blockNo);
	
	/**
	 * 分页查询区块信息
	 * @param orgName
	 * @param userName
	 * @param channelName
	 * @param index 区块索引，即区块高度
	 * @param page  区块集合大小
	 * @return 区块列表
	 */
	List<BlockInfo> queryBlockList(OrganizationVO org, String index, String page);
	
	
	/**
	 * 调用链码功能
	 * @param orgName
	 * @param userName
	 * @param chaincodeID
	 * @param funcName 链码函数名称
	 * @param args 链码函数所需参数
	 * @return 交易hash
	 */
	String  invokeChaincode(OrganizationVO org,  String funcName, String ...args) throws Exception;

}
