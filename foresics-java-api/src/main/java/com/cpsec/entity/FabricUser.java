package com.cpsec.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import com.cpsec.util.FabricStore;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.util.encoders.Hex;



public class FabricUser implements User{
	
    private transient CryptoSuite cryptoSuite;
	private String name;
    private String mspId;
    private String keyValStoreName;
    
    private Enrollment enrollment;
    
    private String enrollmentSecret;
    private String orgName;
    
    private transient FabricStore keyValStore;
     
    
    public FabricUser(String name, String mspId){
    	this.name = name;
    	this.mspId = mspId;
    	setEnrollment(getMockEnrollment(MOCK_CERT));
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
    void saveState() {
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
