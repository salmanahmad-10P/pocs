package com.ratwater.jbride;

import java.util.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import org.yaml.snakeyaml.Yaml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;


public class OCPick {
    private static Logger log = LoggerFactory.getLogger("OCPick");
    private static final String DEFAULT_CONFIG_FILE_NAME="ocp_env_details.yaml";
    private static final String APP_VERSION="app_version";
    private static String version="0.0";
    private static Properties appProps = null;

    public static void main(String args[]) {
        readAppProps();
        //dumpSysProperties();
        if ( (appProps != null) && (!StringUtils.isEmpty((String)appProps.get(APP_VERSION))) ) {
            version = (String)appProps.get(APP_VERSION);
        }
        log.info("main() app version = "+version);

        String currentUsersHomeDir = System.getProperty("user.home");
        String configPath = currentUsersHomeDir + File.separator + DEFAULT_CONFIG_FILE_NAME;
        log.info("main() yaml file to parse = "+configPath);
        Yaml yamlObj = new Yaml();
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
