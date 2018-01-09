package com.aimco.onesite;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

public class JSONTester {
    
    public static void main(String args[]) throws Exception {
        Map<String, String> testMap = new HashMap<String, String>();
        testMap.put("first", "azra");
        testMap.put("second", "alex");
        
        ObjectMapper jsonMapper = new ObjectMapper();
        
        StringWriter sWriter = new StringWriter();
        jsonMapper.writeValue(sWriter, testMap);
        String jsonString = sWriter.toString();
        sWriter.close();
        System.out.println("main() marshalledString = "+jsonString);
        //jsonString = "{\"incrementalPrefix\":\"Incru_U\",    \"fullPrefix\":\"U\",    \"backupFileSuffix\":\"bak\",    \"controlFileSuffix\":\"xml\",    \"priorDays\": \"-10\",    \"siteId\":\"1188988\",    \"localControlFile\": \"/mnt/grvs27/data_import/RealPage_UDS/UDS/controlFiles\",    \"localBackupFileDir\": \"/mnt/grvs27/data_import/RealPage_UDS/UDS/\",    \"ftpServer\":\"uds.realpage.com\",    \"ftpUser\":\"1188988\",    \"ftpPassword\":\"A60DQ\",    \"ftpPort\": \"21\",    \"ftpLocalDir\":\"/mnt/grvs27/data_import/RealPage_UDS/UDS/\",    \"ftpAscii\":\"false\",    \"ftpPassive\":\"true\",    \"httpProtocol\":\"http\",    \"httpHost\":\"216493-107.216493-107.com\",    \"httpPort\":\"8330\",    \"httpUsr\":\"jboss\",    \"httpPwd\":\"brms\"}";
       
        Map<String,String>  unMarshalledMap = jsonMapper.readValue(jsonString, Map.class);
        System.out.println("main() unMarshalledMap = "+unMarshalledMap);
        
    }

}
