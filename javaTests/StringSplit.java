import java.util.*;

public class StringSplit {

    public static void main(String args[]) {
        String stringToSplit = "WORK_ITEM_COMPLETER$processInstanceId$0000$workItemId$234";
        if (stringToSplit.indexOf("$") != -1) {
            String[] signalData = stringToSplit.split("\\$");
            Map<String, String> signalMap = new HashMap<String, String>();
            for(int t = 1; t< signalData.length; t++) {
                System.out.println("t = "+t+" : key = "+signalData[t]+" : value = "+signalData[t+1]);
                signalMap.put(signalData[t], signalData[t+1]);
                t++;
            }
        }else{
            System.out.println("will not split");
        }

        String content = "pInstanceId=1\nworkItemId=2";
        String pInstanceId = content.substring(12, content.indexOf("\n"));
        String workItemId = content.substring(content.indexOf("workItemId=")+11);
        System.out.println("\npInstanceId = "+pInstanceId+" : workItemid = "+workItemId);
    }

}
