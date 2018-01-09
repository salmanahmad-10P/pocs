import java.net.*;
import java.io.*;

public class StreamCopierClient {

    public static void main(String[] args) {
        String remoteAddress = args[0];
        int remotePort = Integer.parseInt(args[1]);

        String statementToSend = args[2];

        Socket clientSocket = null;
        OutputStreamWriter out = null;
        try {
            clientSocket = new Socket(remoteAddress, remotePort);
            clientSocket.setKeepAlive(true); 
            out = new OutputStreamWriter(clientSocket.getOutputStream());
            while(true) {
                out.write(statementToSend, 0, statementToSend.length());
                out.flush();
                Thread.sleep(10000);
            }
        } catch (SocketException x) {
            System.out.println("SocketException = "+x.getLocalizedMessage());
        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }
}
