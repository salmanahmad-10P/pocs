import java.io.*;
import java.net.*;

/*
  args[0] = remote ipAddress
  args[1] = remote port
  args[2] = traffic class
  args[3] = fileName
*/
public class SocketConnection {
    public static void main(String[] args) {
        Socket aSocket = null;
        try {
            InetAddress remoteAddress = InetAddress.getByName(args[0]);
            System.out.println("isReachable = " + remoteAddress.isReachable(null, 0, 15000));
            int port = Integer.parseInt(args[1]);
            aSocket = new Socket(remoteAddress, port);
                        aSocket.setTrafficClass(Integer.parseInt(args[2]));

                        OutputStreamWriter out = new OutputStreamWriter(aSocket.getOutputStream());
            out.write(args[3], 0, args[3].length());
            System.out.println("TrafficClass = "+aSocket.getTrafficClass());
                        out.close();
        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (aSocket != null)
                    aSocket.close();
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }
}
