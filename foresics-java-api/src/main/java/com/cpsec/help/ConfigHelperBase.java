package com.cpsec.help;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cpsec.service.UserService;
import com.cpsec.util.GlobalManager;
import com.cpsec.util.UserManager;


@Component
public class ConfigHelperBase implements ConfigHelper {
	
	
	private static NetworkConfig networkConfig;

	private static  FabricParamsConfig paramsConfig;
	
	private static Map<String, String> clientCerts;
	
	private static Map<String, String> clientKey;
	
	private final UserService userService;
	
	static{
		bootstrap();
	}
	
	@Autowired
	public ConfigHelperBase(FabricParamsConfig paramsConfig, UserService userService) {
		this.paramsConfig = paramsConfig;
		this.userService = userService;
	}
	

	@Override
	public HFClient getClientForOrg(String orgName, String userName) throws Exception {
		HFClient client = HFClient.createNewInstance();
		client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		User user = null;
		if("admin".equals(userName)){
			//获取节点管理员证书
			user = userService.getMemberUser(userName, orgName, "admin");
		}else{
			user = userService.getMemberUser(userName, orgName, "public");
		}
		client.setUserContext(user);
		return client;
}

	@Override
	public Channel getChannel(HFClient client, String channelName) throws Exception {
		Channel newChannel = client.loadChannelFromConfig(channelName, networkConfig);
		if (newChannel == null) {
            throw new RuntimeException("Channel " + channelName + " is not defined in the config file!");
        }
		return newChannel.initialize();
	}

	@Override
	public ChaincodeID getChaincodeID(String ccName, String ccPath, String ccVersion) {
		return  ChaincodeID.newBuilder()
				.setName(ccName)
				.setVersion(ccVersion)
				.setPath(ccPath).build();
	}
	/**
	 * 启动方法，用于加载节点网络连接配置文件，以及加载各个节点的客户端证书
	 */
	public static void bootstrap(){
		if(networkConfig == null){
			try {
				
				networkConfig = NetworkConfig.fromYamlFile(new File(paramsConfig.getNetworkFile()));
				networkConfig.getOrdererNames().forEach(ordererName ->{
					try {
						Properties ordererProperties = networkConfig.getOrdererProperties(ordererName);
						ordererProperties.setProperty(GlobalManager.CLIENT_CERT, getClientCertByName(ordererName));
						ordererProperties.setProperty(GlobalManager.CLIENT_KEY, getClientKeyByName(ordererName));
						networkConfig.setOrdererProperties(ordererName, ordererProperties);
					} catch (InvalidArgumentException e) {
						throw new RuntimeException(e);
					}
				});
				
				networkConfig.getPeerNames().forEach(peerName -> {
					try {
						Properties peerProperties = networkConfig.getPeerProperties(peerName);
						peerProperties.setProperty(GlobalManager.CLIENT_CERT, getClientCertByName(peerName));
						peerProperties.setProperty(GlobalManager.CLIENT_KEY, getClientKeyByName(peerName));
						networkConfig.setPeerProperties(peerName, peerProperties);
					} catch (InvalidArgumentException e) {
						throw new RuntimeException(e);
					}
				});
				
				
			} catch (InvalidArgumentException e) {
				throw new RuntimeException(format("Load NetworkConfig file Error, due to %s", e));
			} catch (NetworkConfigurationException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(format("Can not read network file, please chekc file path %s", paramsConfig.getNetworkFile()));
			}
		}
	}
	/**
	 * 根据节点域名获取客户端证书
	 * @param domainName 节点域名
	 * @return 客户端证书
	 */
	public static String getClientCertByName(String domainName){
		//TODO
		return "";
	}
	
	/**
	 * 根据节点域名获取客户端密钥
	 * @param domainName 节点域名
	 * @return 客户端密钥
	 */
	public static String getClientKeyByName(String domainName){
		//TODO 
		return "";
	}
	
	public NetworkConfig getNetworkConfig(){
		return this.networkConfig;
	}
	
	
}
