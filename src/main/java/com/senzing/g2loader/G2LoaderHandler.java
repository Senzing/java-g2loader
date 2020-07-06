package com.senzing.g2loader;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonObjectBuilder;


import com.senzing.g2.engine.Result;
import com.senzing.g2.engine.G2Engine;
import com.senzing.g2.engine.G2JNI;
import com.senzing.g2.engine.G2Config;
import com.senzing.g2.engine.G2ConfigJNI;
import com.senzing.g2.engine.G2ConfigMgr;
import com.senzing.g2.engine.G2ConfigMgrJNI;

public class G2LoaderHandler {
	  protected G2Engine g2Engine;
	  protected String configData;
	  protected boolean verboseLogging = false;

	  static final String moduleName = "g2loader";

	  /**
	   * Default constructor.
	   */
	  public G2LoaderHandler() {
	  }

	  /**
	   * Initializes the service. It reads the information from the ini file and
	   * sets up G2 using that data.
	   * 
	   * @param iniFile
	   * 
	   * @throws Exception
	   */
	  public void init(String iniFile, boolean verboseLogging) throws Exception {
	    configData = null;
	    this.verboseLogging = verboseLogging;
	    try {
	      configData = getG2IniDataAsJson(iniFile);
	    } catch (IOException | RuntimeException e) {
	      throw new Exception(e);
	    }
	    
	    initConfig();
	    
	    g2Engine = new G2JNI();
	    int result = g2Engine.initV2(moduleName, configData, verboseLogging);
	    if (result != 0) {
	      StringBuilder errorMessage = new StringBuilder("G2 engine failed to initalize with error: ");
	      errorMessage.append(g2ErrorMessage(g2Engine));
	      throw new Exception(errorMessage.toString());
	    }
	  }
	  
	  protected void initConfig() throws Exception {
	    	int result = 0;
	    	
		    G2Config g2Config = new G2ConfigJNI();
		    result = g2Config.initV2(moduleName, configData, verboseLogging);
		    if (result != 0) {
			      StringBuilder errorMessage = new StringBuilder("G2Config failed to initalize with error: ");
			      errorMessage.append(g2ErrorMessage(g2Config));
			      throw new Exception(errorMessage.toString());
		    }

		    G2ConfigMgr g2ConfigMgr = new G2ConfigMgrJNI();
		    result = g2ConfigMgr.initV2(moduleName, configData, verboseLogging);
		    if (result != 0) {
			      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to initalize with error: ");
			      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
			      throw new Exception(errorMessage.toString());
		    }


		    Result<Long> configID = new Result<Long>();
		    result = g2ConfigMgr.getDefaultConfigID(configID);
		    if (result != 0) {
			      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to getDefaultConfigID with error: ");
			      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
			      throw new Exception(errorMessage.toString());
			    }

		    if(configID.getValue() == 0) {
		    	long configHandle = g2Config.create();
		    	StringBuffer configStringBuffer = new StringBuffer();
		    	if (configHandle == 0 || 0 != g2Config.save(configHandle, configStringBuffer)) {
				      StringBuilder errorMessage = new StringBuilder("G2Config failed to save with error: ");
				      errorMessage.append(g2ErrorMessage(g2Config));
				      throw new Exception(errorMessage.toString());
			    }
		    	if (0 != g2ConfigMgr.addConfig(configStringBuffer.toString(), "Initial Configuration", configID)) {
				      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to addConfig with error: ");
				      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
				      throw new Exception(errorMessage.toString());
			    }
		    	if (0 != g2ConfigMgr.setDefaultConfigID(configID.getValue())) {
				      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to setDefaultConfigID with error: ");
				      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
				      throw new Exception(errorMessage.toString());
			    }
		    }
		 
	  }

	  protected void purgeRepository() throws Exception {
		    G2ConfigMgr g2ConfigMgr = new G2ConfigMgrJNI();
		    int result = g2ConfigMgr.initV2(moduleName, configData, verboseLogging);
		    if (result != 0) {
			      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to initalize with error: ");
			      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
			      throw new Exception(errorMessage.toString());
		    }


		    Result<Long> configID = new Result<Long>();
		    result = g2ConfigMgr.getDefaultConfigID(configID);
		    if (result != 0) {
			      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to getDefaultConfigID with error: ");
			      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
			      throw new Exception(errorMessage.toString());
			    }

		    if(configID.getValue() == 0) {
		    	throw new Exception("No existing configuration");
		    }

		  g2Engine.purgeRepository();
		  g2Engine.reinitV2(configID.getValue());
	  }
	  
	  protected void addRecord(String record) throws Exception {
		  JsonObject jsonRecord = Json.createReader(new StringReader(record)).readObject();
		  
		  JsonValue value = (JsonValue)jsonRecord.get("RECORD_ID");
		  if (value == null || value.getValueType() != JsonValue.ValueType.STRING)
	    	  throw new Exception("RECORD_ID not populated in JSON record");
		  String recordID = ((JsonString)value).getString();

		  value = jsonRecord.get("DATA_SOURCE");
		  if (value == null || value.getValueType() != JsonValue.ValueType.STRING)
	    	  throw new Exception("DATA_SOURCE not populated in JSON record");
		  String dataSource = ((JsonString)value).getString();

		  
	      if (0 != g2Engine.addRecord(dataSource, recordID, record, null)) {
		      StringBuilder errorMessage = new StringBuilder("G2Engine failed to addRecord with error: ");
		      errorMessage.append(g2ErrorMessage(g2Engine));
		      throw new Exception(errorMessage.toString());
	      }
	  }
	  
	  protected void addDataSource(String dataSource) throws Exception {
	    	int result = 0;
	    	
		    G2Config g2Config = new G2ConfigJNI();
		    result = g2Config.initV2(moduleName, configData, verboseLogging);
		    if (result != 0) {
			      StringBuilder errorMessage = new StringBuilder("G2Config failed to initalize with error: ");
			      errorMessage.append(g2ErrorMessage(g2Config));
			      throw new Exception(errorMessage.toString());
		    }

		    G2ConfigMgr g2ConfigMgr = new G2ConfigMgrJNI();
		    result = g2ConfigMgr.initV2(moduleName, configData, verboseLogging);
		    if (result != 0) {
			      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to initalize with error: ");
			      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
			      throw new Exception(errorMessage.toString());
		    }


		    Result<Long> configID = new Result<Long>();
		    result = g2ConfigMgr.getDefaultConfigID(configID);
		    if (result != 0) {
			      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to getDefaultConfigID with error: ");
			      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
			      throw new Exception(errorMessage.toString());
			    }

		    if(configID.getValue() == 0) {
		    	throw new Exception("No existing configuration");
		    }

		    StringBuffer response = new StringBuffer();

		    result = g2ConfigMgr.getConfig(configID.getValue(), response);
		    if (result != 0) {
			      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to getConfig with error: ");
			      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
			      throw new Exception(errorMessage.toString());
			}
		    
		    long configHandle = g2Config.load(response.toString());
		    if (configHandle == 0) {
			      StringBuilder errorMessage = new StringBuilder("G2Config failed to load with error: ");
			      errorMessage.append(g2ErrorMessage(g2Config));
			      throw new Exception(errorMessage.toString());
			}
		    
		    String cmd = "{\"DSRC_CODE\": \"" + dataSource + "\"}";
		    response = new StringBuffer();

		    result = g2Config.addDataSourceV2(configHandle, cmd, response);

		    cmd = "{\"ETYPE_CODE\": \"" + dataSource + "\",\"ECLASS_CODE\": \"ACTOR\"}";
		    response = new StringBuffer();

		    result = g2Config.addEntityTypeV2(configHandle, cmd, response);

		    if(result==0) {
			    response = new StringBuffer();
		    	if (configHandle == 0 || 0 != g2Config.save(configHandle, response)) {
				      StringBuilder errorMessage = new StringBuilder("G2Config failed to save with error: ");
				      errorMessage.append(g2ErrorMessage(g2Config));
				      throw new Exception(errorMessage.toString());
			    }
		    	if (0 != g2ConfigMgr.addConfig(response.toString(), "Initial Configuration", configID)) {
				      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to addConfig with error: ");
				      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
				      throw new Exception(errorMessage.toString());
			    }
		    	if (0 != g2ConfigMgr.setDefaultConfigID(configID.getValue())) {
				      StringBuilder errorMessage = new StringBuilder("G2ConfigMgr failed to setDefaultConfigID with error: ");
				      errorMessage.append(g2ErrorMessage(g2ConfigMgr));
				      throw new Exception(errorMessage.toString());
			    }
		    	
		    	g2Engine.reinitV2(configID.getValue());
		    }
    		 
	  }

	  

	  /**
	   * Cleans up and frees resources after processing.
	   */
	  public void cleanUp() {
	    if (g2Engine != null) {
	      g2Engine.destroy();
	    }
	  }

	  static protected String g2ErrorMessage(G2Engine g2) {
		    return g2.getLastExceptionCode() + ", " + g2.getLastException();
		  }
	  static protected String g2ErrorMessage(G2Config g2) {
		    return g2.getLastExceptionCode() + ", " + g2.getLastException();
		  }
	  static protected String g2ErrorMessage(G2ConfigMgr g2) {
		    return g2.getLastExceptionCode() + ", " + g2.getLastException();
		  }

	  protected static String getG2IniDataAsJson(String iniFile) throws IOException {
	    Pattern  iniSection  = Pattern.compile( "\\s*\\[([^]]*)\\]\\s*" );
	    Pattern  iniKeyValue = Pattern.compile( "\\s*([^=]*)=(.*)" );
	    JsonObjectBuilder rootObject = Json.createObjectBuilder();
	    try (Scanner scanner = new Scanner(new File(iniFile))) {
	      JsonObjectBuilder currentSection = null;
	      String currentGroup = null;
	      while (scanner.hasNextLine()) {
	        String line = scanner.nextLine().trim();
	        if (line.startsWith("#")) {
	          continue;
	        }
	        Matcher matcher = iniSection.matcher(line);
	        if (matcher.matches()) {
	          if (currentGroup != null) {
	            rootObject.add(currentGroup, currentSection.build());
	          }
	          currentGroup = matcher.group(1);
	          currentSection = Json.createObjectBuilder();
//	          rootObject.add(matcher.group(1), currentSection);
	        } else if (currentSection != null) {
	          matcher = iniKeyValue.matcher(line);
	          if (matcher.matches()) {
	            currentSection.add(matcher.group(1), matcher.group(2));
	          }
	        }
	      }
	      if (currentGroup != null) {
	        rootObject.add(currentGroup, currentSection.build());
	      }
	    }
	    return rootObject.build().toString();
	  }

}
