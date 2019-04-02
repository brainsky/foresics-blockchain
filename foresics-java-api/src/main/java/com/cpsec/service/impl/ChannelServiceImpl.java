package com.cpsec.service.impl;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.NetworkConfig.OrgInfo;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.stereotype.Service;

import com.cpsec.entity.OrganizationVO;
import com.cpsec.help.ConfigHelper;
import com.cpsec.help.ConfigHelperBase;
import com.cpsec.help.FabricParamsConfig;
import com.cpsec.service.ChannelService;
import com.cpsec.service.UserService;

@Service
public class ChannelServiceImpl implements ChannelService {
	
	
	private static final Log logger = LogFactory.getLog(ChannelServiceImpl.class);
	
	private final ConfigHelper configHelper;
	
	private final FabricParamsConfig fabricParams;
	
	private final UserService userService;
	
	@Autowired
	public ChannelServiceImpl(ConfigHelper helper, FabricParamsConfig fabricParams, UserService service){
		this.configHelper = helper;
		this.fabricParams = fabricParams;
		this.userService = service;
	}

	@Override
	public String queryChaincodeFunction(OrganizationVO org, String functionName, String... args) throws Exception {
		logger.info(format("execute queryChaincodeFunction orgName is %s, channel is %s, functionName is %s" ,
					org.getOrgName(), org.getChannelName(), functionName));
		
		HFClient client = configHelper.getClientForOrg(org.getOrgName(), org.getUserName());
		Channel channel = configHelper.getChannel(client, org.getChannelName());
		ChaincodeID chaincodeID = configHelper.getChaincodeID(org.getCcName(), org.getCcPath(), org.getCcVersion());
		//发送查询链码请求
		QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
		
		queryByChaincodeRequest.setArgs(args);
		queryByChaincodeRequest.setFcn(functionName);
		queryByChaincodeRequest.setChaincodeID(chaincodeID);
		
		Collection<ProposalResponse> queryProposals;
		try {
	            queryProposals = channel.queryByChaincode(queryByChaincodeRequest);
	    } catch (Exception e) {
	            throw new CompletionException(e);
	    }
		String expect = null;
		for (ProposalResponse proposalResponse : queryProposals) {
			if (!proposalResponse.isVerified() || proposalResponse.getStatus() != Status.SUCCESS) {
				logger.error(format("Fail to query proposal from peer: %s,"
						+ " the status is %s, and the messages is %s", 
						proposalResponse.getPeer().getName(),proposalResponse.getStatus(), proposalResponse.getMessage()));
			}else {
				 String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
				 if (expect != null) {
	                    assertEquals(expect, payload);
	                } else {
	                    expect = payload;
	                }
			}
		}
		return expect;
	}

	@Override
	public ChaincodeID deployChaincode(OrganizationVO org, String ccName, String ccPath,
			String ccVersion, String fcnName) throws Exception {
		logger.info(format("Execute deploy chaincode function  the chaincode name is %s, "
				+ "the Chaincode Path is %s,", ccName , ccPath ));
		ChaincodeID chaincodeID = null;
		HFClient client = configHelper.getClientForOrg(org.getOrgName(), org.getUserName());
		Channel channel = configHelper.getChannel(client, org.getChannelName());
		Collection<Orderer> orderers = channel.getOrderers();
        Collection<ProposalResponse> responses;
        Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        chaincodeID = ChaincodeID.newBuilder().setName(ccName)
                .setVersion(ccVersion)
                .setPath(ccPath).build();
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chaincodeID);
        installProposalRequest.setChaincodeSourceLocation(new File(ccPath));
        installProposalRequest.setChaincodeVersion(ccVersion);
        int numInstallProposal = 0;
        Collection<Peer> peersFromOrg = channel.getPeers();
        numInstallProposal = numInstallProposal + peersFromOrg.size();
        responses = client.sendInstallProposal(installProposalRequest, peersFromOrg);
        for (ProposalResponse response : responses) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                logger.info(format("Successful install proposal response Txid: %s from peer %s", 
                				response.getTransactionID(), response.getPeer().getName()));
                successful.add(response);
            } else {
                failed.add(response);
            }
        }
        
        if (failed.size() > 0) {
            ProposalResponse first = failed.iterator().next();
            fail("Not enough endorsers for install :" + successful.size() + ".  " + first.getMessage());
        }
        
        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(fabricParams.getProposalWaitTime());
        instantiateProposalRequest.setChaincodeID(chaincodeID);
        instantiateProposalRequest.setFcn(fcnName);
        Map<String, byte[]> tm = new HashMap<>();
        instantiateProposalRequest.setTransientMap(tm);
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File(fabricParams.getChaincode().getEndorsementPlocyFile()));
        
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        successful.clear();
        failed.clear();
        responses = channel.sendInstantiationProposal(instantiateProposalRequest);
        for (ProposalResponse response : responses) {
            if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
                successful.add(response);
                logger.info(format("Succesful instantiate proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName()));
            } else {
                failed.add(response);
            }
        }
        if (failed.size() > 0) {
            ProposalResponse first = failed.iterator().next();
            fail("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with " + first.getMessage() + ". Was verified:" + first.isVerified());
        }
        CompletableFuture<TransactionEvent> future = channel.sendTransaction(successful, orderers);
        
        TransactionEvent event = future.get(30, TimeUnit.SECONDS);
        
        assertTrue(event.isValid());   
        return chaincodeID;
	}

	@Override
	public Channel createChannel(OrganizationVO org, String txPath) throws Exception {
		logger.info(format("Excute CreateChannel function, channel name is %s, channel.tx Path is %s",org.getChannelName(), txPath));
		User peerAdmin = userService.getMemberUser(org.getUserName(), org.getOrgName(), "admin");
		HFClient client = configHelper.getClientForOrg(org.getOrgName(), org.getUserName());
		client.setUserContext(peerAdmin);
		Collection<Orderer> orderers = new LinkedList<>();
		for (String ordererName : configHelper.getNetworkConfig().getOrdererNames()) {
			Properties ordererProperties = configHelper.getNetworkConfig().getOrdererProperties(ordererName);
			ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] {5L, TimeUnit.MINUTES});
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] {8L, TimeUnit.SECONDS});
            ordererProperties.put("grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] {true});
            orderers.add(client.newOrderer(ordererName, configHelper.getGrpcByName(ordererName), ordererProperties));
            
		}
		Orderer anOrderer = orderers.iterator().next();
		orderers.remove(anOrderer);
		
		ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(txPath));
		
		Channel newChannel = client.newChannel(org.getChannelName(),anOrderer, channelConfiguration, client.getChannelConfigurationSignature(channelConfiguration, peerAdmin));
		
		boolean everyother = true;
		
		
		
		return null;
	}

	@Override
	public BlockchainInfo queryBlockChainInfo(OrganizationVO org) throws Exception {
			HFClient client = configHelper.getClientForOrg(org.getOrgName(), org.getUserName());
			
			Channel channel = configHelper.getChannel(client, org.getChannelName());
			
			return channel.queryBlockchainInfo();
		
	}

	@Override
	public BlockInfo queryBlockByHash(OrganizationVO org, String hash) throws Exception {
			HFClient client = configHelper.getClientForOrg(org.getOrgName(), org.getUserName());
		
			Channel channel = configHelper.getChannel(client, org.getChannelName());
		
			return channel.queryBlockByHash(hash.getBytes());
	}

	@Override
	public BlockInfo queryBlockByNO(OrganizationVO org, String blockNo) throws Exception {
		    HFClient client = configHelper.getClientForOrg(org.getOrgName(), org.getUserName());
		
		    Channel channel = configHelper.getChannel(client, org.getChannelName());
		
		   return channel.queryBlockByNumber(Long.parseLong(blockNo));
	}

	@Override
	public List<BlockInfo> queryBlockList(OrganizationVO org, String index, String page) throws Exception {
		return null;
	}

	@Override
	public String invokeChaincode(OrganizationVO org, String funcName, String... args) throws Exception {
		 Collection<ProposalResponse> successful = new LinkedList<>();
	     Collection<ProposalResponse> failed = new LinkedList<>();
	     
	     HFClient client = configHelper.getClientForOrg(org.getOrgName(), org.getUserName());
	     TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
	     ChaincodeID chaincodeID = configHelper.getChaincodeID(org.getCcName(),org.getCcPath(),org.getCcVersion());
	     transactionProposalRequest.setChaincodeID(chaincodeID);
	     transactionProposalRequest.setFcn(funcName);
	     transactionProposalRequest.setArgs(args);
	     transactionProposalRequest.setProposalWaitTime(fabricParams.getProposalWaitTime());
	     Channel channel =  configHelper.getChannel(client, org.getChannelName());
	     Collection<ProposalResponse> invokePropResp = channel.sendTransactionProposal(transactionProposalRequest);
	     for (ProposalResponse response : invokePropResp) {
	            if (response.getStatus() == Status.SUCCESS) {
	            	logger.info(format("Successful transaction proposal response Txid: %s from peer %s",response.getTransactionID(), response.getPeer().getName()));
	                successful.add(response);
	            } else {
	                failed.add(response);
	            }
	     }
	     Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils.getProposalConsistencySets(invokePropResp);
	     if (proposalConsistencySets.size() != 1) {
	    	 logger.error(format("Expected only one set of consistent move proposal responses but got %d", proposalConsistencySets.size()));
	     }
	     if (failed.size() > 0) {
	            ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
	            throw new ProposalException(format("Not enough endorsers for invoke(% ):%d endorser error:%s. Was verified:%b",
	                    funcName,  firstTransactionProposalResponse.getStatus().getStatus(),
	                    firstTransactionProposalResponse.getMessage(), 
	                    firstTransactionProposalResponse.isVerified()));
	     }
	     
	     return channel.sendTransaction(successful).thenApply(transactionEvent ->{
	    	  return transactionEvent.getTransactionID();
	     }).exceptionally(e -> {
	    	  if (e instanceof CompletionException && e.getCause() != null) {
	                e = e.getCause();
	          }
	    	  if (e instanceof TransactionEventException) {
	                BlockEvent.TransactionEvent te = ((TransactionEventException) e).getTransactionEvent();
	                if (te != null) {

	                    e.printStackTrace(System.err);
	                    logger.error(format("Transaction with txid %s failed. %s", te.getTransactionID(), e.getMessage()));
	                }    
	    	  }
	    	  e.printStackTrace(System.err);
	    	  return null;
	     }).get(fabricParams.getTransactionWaitTime(), TimeUnit.SECONDS);
	}

}
