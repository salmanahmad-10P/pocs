package com.ratwater.jbride;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.codehaus.plexus.util.IOUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.representer.Representer;

public class OCPick {
    private static Logger log = LoggerFactory.getLogger("OCPick");
    private static final String YAML_CONFIG_PATH_ENV = "YAML_CONFIG_PATH_ENV";
    private static final String YAML_CONFIG_PATH_ARG = "--config_path=";
    private static final String DEFAULT_CONFIG_FILE_NAME = ".ocp_env_details.yaml";
    private static final String APP_VERSION = "app_version";
    private static String version = "0.0";
    private static Properties appProps = null;
    private static String yamlConfigPath = null;

    public static void main(String args[]) {
        testOC();
        readAppProps();
        // dumpSysProperties();
        if ((appProps != null) && (!StringUtils.isEmpty((String) appProps.get(APP_VERSION)))) {
            version = (String) appProps.get(APP_VERSION);
        }
        log.info("main() app version = " + version);
        determineVariables(args);
        readAndValidateYaml();

    }
    private static void testOC() {
        
        InputStream iStream = null;
        try {
            Process p = Runtime.getRuntime().exec("oc version");
            iStream = p.getInputStream();
            String commandOutput = IOUtil.toString(iStream);
        
            log.info("testOC() commandOutput = "+commandOutput);
        } catch (IOException e) {
           throw new RuntimeException(e);
        } finally {
            if(iStream != null)
                try {
                    iStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private static void determineVariables(String args[]) {
        if (args.length > 0) {
            for (int x = 0; x < args.length; x++) {
                log.info("determineVariables() arg = " + args[x]);
                if (args[x].startsWith(YAML_CONFIG_PATH_ARG)) {
                    yamlConfigPath = args[x].substring(13);
                }
            }
        }
        if (StringUtils.isEmpty(yamlConfigPath)) {
            String temp = System.getenv(YAML_CONFIG_PATH_ENV);
            if (!StringUtils.isEmpty(temp)) {
                yamlConfigPath = System.getenv(YAML_CONFIG_PATH_ENV);
            }
        }
        if (StringUtils.isEmpty(yamlConfigPath)) {
            String currentUsersHomeDir = System.getProperty("user.home");
            yamlConfigPath = currentUsersHomeDir + File.separator + DEFAULT_CONFIG_FILE_NAME;
        }
        log.info("determineVariables() yaml file to parse = " + yamlConfigPath);
    }

    private static void readAndValidateYaml() {
        File yamlFile = new File(yamlConfigPath);
        if (!yamlFile.exists())
            throw new RuntimeException("readAndValidateYaml() the following file does not exist: " + yamlConfigPath);

        FileInputStream yamlReader = null;
        OCPENVs yamlValues = null;
        Object tempYaml;
        try {
            yamlReader = new FileInputStream(yamlFile);
            Yaml yamlObj = new Yaml();
              tempYaml = yamlObj.loadAs(yamlReader, OCPENVs.class);
              if(! (tempYaml instanceof OCPENVs))
                throw new RuntimeException("readAndValidateYaml() The following is not properly configured yaml: "+yamlConfigPath+". That yaml is of type: "+tempYaml.getClass());

            yamlValues = (OCPENVs)tempYaml;
        }catch(IOException x) {
          throw new RuntimeException(x);
        } finally {
            
        }
        StringBuilder sBuilder = new StringBuilder();
        for( OCPENV yamlObj : yamlValues.getOcpEnvs()) {
          log.info("yamlObj = "+yamlObj);
          /*
          String yamlKey = yamlEntry.getKey();
          sBuilder.append("\n"+yamlKey);
          tempYaml = yamlEntry.getValue();
          if(tempYaml instanceof String) {
              sBuilder.append(" : "+(String)tempYaml);
          }else {
              Map<String, Object> subValues = (Map<String, Object>)tempYaml;
              for(Entry<String, Object> subValue : subValues.entrySet()) {
                 String subVK = subValue.getKey();
                 String subVV = (String)subValue.getValue();
                 sBuilder.append("\n\t"+subVK+ " : "+ subVV);
              }
            }
          */
          }
        log.info(sBuilder.toString());
        

    }

    private static void readAppProps() {
        InputStream is = null;
        try {
            is = OCPick.class.getResourceAsStream("/application.properties");
            if(is != null) {
                appProps = new Properties();
                appProps.load(is);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static void dumpSysProperties() {
        Properties pros = System.getProperties();
        pros.list(System.out); 
    }
}
