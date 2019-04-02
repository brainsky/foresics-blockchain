package com.cpsec.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import com.cpsec.util.FabricStore;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.Hex;

/**
 * 
 * @author Administrator
 *
 */

public class FabricUser implements User, Serializable{
	
 
	private static final long serialVersionUID = -8822655016313223171L;
	
	private transient CryptoSuite cryptoSuite;
	private String name;
    private String mspId;
    private String keyValStoreName;
    
    private Enrollment enrollment;
    
    private String enrollmentSecret;
    private String orgName;
    private String affiliation;
    private transient FabricStore keyValStore;
     
    
    public FabricUser(String name, String mspId){
    	this.name = name;
    	this.mspId = mspId;
    	setEnrollment(getMockEnrollment(MOCK_CERT));
    }
    
    public FabricUser(String name, String orgName, FabricStore store, CryptoSuite crypto){
    	
    	this.name = name;
    	this.orgName = orgName;
    	this.cryptoSuite = crypto;
    	this.keyValStoreName = toKeyValStoreName(name, orgName);
    	
    	//获取用户证书
    	String memberStr =  keyValStore.getValue(keyValStoreName);
    	if(null == memberStr){
    		saveState();
    	}else{
    		restoreState();
    	}
    }
   
	public void setEnrollment(Enrollment e) {
        this.enrollment = e;
        saveState();
    }
    
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<String> getRoles() {
		return null;
	}

	@Override
	public String getAccount() {
		return null;
	}

	@Override
	public String getAffiliation() {
		return null;
	}

	@Override
	public Enrollment getEnrollment() {
		return enrollment;
	}

	@Override
	public String getMspId() {
		return mspId;
	}
	
	public void setEnrollmentSecret(String enrollmentSecret) {
          this.enrollmentSecret = enrollmentSecret;
          saveState();
    }
	
	public String getEnrollmentSecret(){
		return enrollmentSecret;
	}
	
	public boolean isRegistered() {
	     return !StringUtils.isNotEmpty(enrollmentSecret);
	}
	
	
	public boolean isEnrolled() {
	        return this.enrollment != null;
	}
	
	/**
     * Save the state of this user to the key value store.
     */
    private void saveState() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            keyValStore.setValue(keyValStoreName, Hex.toHexString(bos.toByteArray()));
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private FabricUser restoreState() {
    	String memberStr = keyValStore.getValue(keyValStoreName);
    	if (null != memberStr) {
    		 byte[] serialized = Hex.decode(memberStr);
             ByteArrayInputStream bis = new ByteArrayInputStream(serialized);
             try {
            	 
				ObjectInputStream ois = new ObjectInputStream(bis);
				FabricUser 	state = (FabricUser)ois.readObject();
				 if (state != null) {
					 this.name = state.name;
					 this.orgName = state.orgName;
					 this.cryptoSuite = state.cryptoSuite;
					 this.enrollment = state.enrollment;
					 this.enrollmentSecret = state.enrollmentSecret;
					 this.mspId = state.mspId;
					 this.affiliation = state.affiliation;
					 return this;
				 }
			} catch (IOException e) {
				throw new RuntimeException(String.format("Could not restore state of member %s", this.name), e);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(String.format("Cound serialize Object, the keyValStoreName is %s", keyValStoreName));
			}   
    	}
    	return null;
	}

    
    public static String toKeyValStoreName(String name, String org) {
        return "user." + name + org;
    }
	
    public static boolean isStored(String name, String org, FabricStore store){
    	return store.hasValue(toKeyValStoreName(name, org));
    }
	
	public static Enrollment getMockEnrollment(String cert) {
	        return new X509Enrollment(new MockPrivateKey(), cert);
	}
	
	private static class MockPrivateKey implements PrivateKey {
        private static final long serialVersionUID = 1L;

        private MockPrivateKey() {
        }

        @Override
        public String getAlgorithm() {
            return null;
        }

        @Override
        public String getFormat() {
            return null;
        }

        @Override
        public byte[] getEncoded() {
            return new byte[0];
        }
    }
	
	private static final String MOCK_CERT = "-----BEGIN CERTIFICATE-----" +
            "MIICGjCCAcCgAwIBAgIRAPDmqtljAyXFJ06ZnQjXqbMwCgYIKoZIzj0EAwIwczEL" +
            "MAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWExFjAUBgNVBAcTDVNhbiBG" +
            "cmFuY2lzY28xGTAXBgNVBAoTEG9yZzEuZXhhbXBsZS5jb20xHDAaBgNVBAMTE2Nh" +
            "Lm9yZzEuZXhhbXBsZS5jb20wHhcNMTcwNjIyMTIwODQyWhcNMjcwNjIwMTIwODQy" +
            "WjBbMQswCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMN" +
            "U2FuIEZyYW5jaXNjbzEfMB0GA1UEAwwWQWRtaW5Ab3JnMS5leGFtcGxlLmNvbTBZ" +
            "MBMGByqGSM49AgEGCCqGSM49AwEHA0IABJve76Fj5T8Vm+FgM3p3TwcnW/npQlTL" +
            "P+fY0fImBODqQLTkBokx4YiKcQXQl4m1EM1VAbOhAlBiOfNRNL0W8aGjTTBLMA4G" +
            "A1UdDwEB/wQEAwIHgDAMBgNVHRMBAf8EAjAAMCsGA1UdIwQkMCKAIPz3drAqBWAE" +
            "CNC+nZdSr8WfZJULchyss2O1uVoP6mIWMAoGCCqGSM49BAMCA0gAMEUCIQDatF1P" +
            "L7SavLsmjbFxdeVvLnDPJuCFaAdr88oE2YuAvwIgDM4qXAcDw/AhyQblWR4F4kkU" +
            "NHvr441QC85U+V4UQWY=" +
            "-----END CERTIFICATE-----";

}
