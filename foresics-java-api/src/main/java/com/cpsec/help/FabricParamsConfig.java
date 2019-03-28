package com.cpsec.help;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="fabric")
public class FabricParamsConfig {
	
	private String networkFile;
	
	private int proposalWaitTime;
	
	private int transactionWaitTime;
	
	private String sslProvider;
	
	private String negotiationType;
	
	private Chaincode chaincode;
	
	public class Chaincode{
		
		private String chaincodeVersion;
		
		private String chaincodePath;
		
		private String endorsementPlocyFile;

		public String getChaincodeVersion() {
			return chaincodeVersion;
		}

		public void setChaincodeVersion(String chaincodeVersion) {
			this.chaincodeVersion = chaincodeVersion;
		}

		public String getChaincodePath() {
			return chaincodePath;
		}

		public void setChaincodePath(String chaincodePath) {
			this.chaincodePath = chaincodePath;
		}

		public String getEndorsementPlocyFile() {
			return endorsementPlocyFile;
		}

		public void setEndorsementPlocyFile(String endorsementPlocyFile) {
			this.endorsementPlocyFile = endorsementPlocyFile;
		}
		
	}

	public String getNetworkFile() {
		return networkFile;
	}

	public void setNetworkFile(String networkFile) {
		this.networkFile = networkFile;
	}

	public int getProposalWaitTime() {
		return proposalWaitTime;
	}

	public void setProposalWaitTime(int proposalWaitTime) {
		this.proposalWaitTime = proposalWaitTime;
	}

	public int getTransactionWaitTime() {
		return transactionWaitTime;
	}

	public void setTransactionWaitTime(int transactionWaitTime) {
		this.transactionWaitTime = transactionWaitTime;
	}

	public String getSslProvider() {
		return sslProvider;
	}

	public void setSslProvider(String sslProvider) {
		this.sslProvider = sslProvider;
	}

	public String getNegotiationType() {
		return negotiationType;
	}

	public void setNegotiationType(String negotiationType) {
		this.negotiationType = negotiationType;
	}

	public Chaincode getChaincode() {
		return chaincode;
	}

	public void setChaincode(Chaincode chaincode) {
		this.chaincode = chaincode;
	}
	
	

}
