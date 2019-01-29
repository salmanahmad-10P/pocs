import java.net.*;
import java.io.*;

public class URITest2 {

    private static String uriString = "ftp://ebuilder-jboss:T%40l68fB%24@ftp.aimco.com/eBuilder";

    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();
        URI uri = null;
        try {
            uri = new URI(uriString);
            System.out.println("host = "+uri.getHost());
        } catch (Exception x ) {
            x.printStackTrace();
        }
    }
}
