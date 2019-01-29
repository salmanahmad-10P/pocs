import java.net.*;
import java.io.*;

public class URITest {

    private static String urlString = "file:///rdm_journal/jboss/serviceRepository/index.conf";

    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();
        URL url = null;
        try {
            url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader( url.openStream() ));
            String line = null;

            while ( ( line = reader.readLine() ) != null ) { // while loop begins here
                builder.append( line );
                builder.append( "\n" );
            }

            reader.close();
            System.out.println("line = "+builder.toString());
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to read " + url.toExternalForm() );
        }

    }
}
