package com.cpsec.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.security.CryptoSuite;

import com.cpsec.entity.FabricUser;

/**
 * A local file-based key value store.
 */
public class FabricStore {
	
	 private String file;
	 private Log logger = LogFactory.getLog(FabricStore.class);
	 
	 private final Map<String, FabricUser> members = new HashMap<>();
	 
	 private CryptoSuite cryptoSuite;
	 
	 public FabricStore(File file){
		 this.file = file.getAbsolutePath();
	 }
	 
	 public String getValue(String name) {
	        Properties properties = loadProperties();
	        return properties.getProperty(name);
	 }
	 
	 private Properties loadProperties() {
	        Properties properties = new Properties();
	        try (InputStream input = new FileInputStream(file)) {
	            properties.load(input);
	            input.close();
	        } catch (FileNotFoundException e) {
	            logger.info(String.format("Could not find the file \"%s\"", file));
	        } catch (IOException e) {
	            logger.warn(String.format("Could not load keyvalue store from file \"%s\", reason:%s",
	                    file, e.getMessage()));
	        }

	        return properties;
	 }
	 
	 /**
	  * Set the value associated with name.
	  * @param name  The name of the parameter
	  * @param value Value for the parameter
	  */
	 public void setValue(String name, String value) {
	        Properties properties = loadProperties();
	        try (
	                OutputStream output = new FileOutputStream(file)
	        ) {
	            properties.setProperty(name, value);
	            properties.store(output, "");
	            output.close();

	        } catch (IOException e) {
	            logger.warn(String.format("Could not save the keyvalue store, reason:%s", e.getMessage()));
	        }
	 }
	 
	 
	 public FabricUser getMember(String name, String org) {
		 return null;
	 }
}
