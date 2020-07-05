package com.senzing.g2loader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.senzing.g2loader.config.AppConfiguration;
import com.senzing.g2loader.config.ConfigKeys;
import com.senzing.g2loader.config.CommandOptions;

public class G2Loader {

	private static Map<String, Object> configValues;

	public static void main(String[] args) {
	    configValues = new HashMap<>();
	    try {
	      processConfiguration();
	      processArguments(args);

	      validateCommandLineParams();

	      G2LoaderHandler handler = new G2LoaderHandler();
	      handler.init(configValues.get(CommandOptions.INI_FILE).toString(), configValues.containsKey(CommandOptions.VERBOSE));
	      
	      if (configValues.containsKey(CommandOptions.PURGE)) {
	    	  System.out.println("Purging repository");
	    	  handler.purgeRepository();
	      }
	      Object value = configValues.get(CommandOptions.DATA_SOURCE);
	      if (null != value){
	    	  System.out.println("Adding dataSource");
	    	  handler.addDataSource(value.toString());
	      }
	      
	      value = configValues.get(CommandOptions.DATA_FILE);
	      if (null != value){
	    	  System.out.println("Loading dataFile");
	    	  loadFile(value.toString());
	      }

	      
	      
	      handler.cleanUp();
	      
	    } catch (org.apache.commons.cli.UnrecognizedOptionException e) {
	    	System.err.println("ERROR: " + e.getMessage());
	    	helpMessage();
	    	System.exit(-1);
	    } catch (Exception e) {
	      e.printStackTrace();
	      System.exit(-1);
	    }

	}
	
	private static void loadFile(String dataFile) throws Exception {

	}

	private static void processConfiguration() {
	    try {
	      AppConfiguration config = new AppConfiguration();
	      configValues.put(CommandOptions.INI_FILE, config.getConfigValue(ConfigKeys.G2_INI_FILE));
	      configValues.put(CommandOptions.VERBOSE, config.getConfigValue(ConfigKeys.G2_VERBOSE));
	    } catch (IOException e) {
	      //System.out.println("Configuration file not found. Expecting command line arguments.");
	    }
	  }

	  private static void processArguments(String[] args) throws ParseException {
	    Options options = new Options();

	    // Add options.
	    options.addOption(CommandOptions.INI_FILE, true, "Path to the G2 ini file");
	    options.addOption(CommandOptions.DATA_FILE, true, "CSV or JSON data file to load");
	    options.addOption(CommandOptions.DATA_SOURCE, true, "Data source for file");
	    options.addOption(CommandOptions.PURGE, false, "Purge the repository");
	    options.addOption(CommandOptions.VERBOSE, false, "Debug");


	    CommandLineParser parser = new DefaultParser();
	    CommandLine commandLine = parser.parse(options, args);

	    addCommandLineValue(commandLine, CommandOptions.INI_FILE);
	    addCommandLineValue(commandLine, CommandOptions.DATA_FILE);
	    addCommandLineValue(commandLine, CommandOptions.DATA_SOURCE);
	    addCommandLineValue(commandLine, CommandOptions.PURGE);
	    addCommandLineValue(commandLine, CommandOptions.VERBOSE);
	  }

	  private static void addCommandLineValue(CommandLine commandLine, String key) {
	    boolean hasOption = commandLine.hasOption(key);
	    if (hasOption) {
	    	configValues.put(key, commandLine.getOptionValue(key));
	    }
	  }

	  private static void validateCommandLineParams() {
	    List<String> unsetParameters = new ArrayList<>();
	    checkParameter(unsetParameters, CommandOptions.INI_FILE);
	    //checkParameter(unsetParameters, CommandOptions.DATA_FILE);

	    if (!unsetParameters.isEmpty()) {
	      System.out.println("No configuration found for parameters: " + String.join(", ", unsetParameters));
	      helpMessage();
	      System.out.println("Failed to start!!!");
	      System.exit(-1);
	    }
	  }

	  private static void checkParameter(List<String> parameters, String key) {
	    Object value = configValues.get(key);
	    if (value == null || value.toString().isEmpty()) {
	      parameters.add(key);
	    }
	  }

	  private static void helpMessage() {
	    System.out.println("Set the configuration in the g2loader.properties or add command line parameters.");
	    System.out.println("Command line usage: java -jar g2loader.jar -iniFile <path to ini file> \\");
	    System.out.println("                                          [-dataFile <path to data file>] \\");
	    System.out.println("                                          [-dataSource <name of data source>] \\");
	    System.out.println("                                          [-purge]  ");
	    System.out.println("                                          [-debug]");
	    System.out.println("");
	  }
}
