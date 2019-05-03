package com.ratwater.jbride;

import java.util.Properties;
import java.io.File;
import org.yaml.snakeyaml.Yaml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;


public class OCPick {
    private static Logger log = LoggerFactory.getLogger("OCPick");
    private static final String DEFAULT_CONFIG_FILE_NAME="ocp_env_details.yaml";
    private static String version="1.0";

    public static void main(String args[]) {
        String currentUsersHomeDir = System.getProperty("user.home");
        String configPath = currentUsersHomeDir + File.separator + DEFAULT_CONFIG_FILE_NAME;
        log.info("main() yaml file to parse = "+configPath);
        dumpSysProperties();

        Yaml yamlObj = new Yaml();
    }

    private static void dumpSysProperties() {
        Properties pros = System.getProperties();
        pros.list(System.out); 
    }
}
