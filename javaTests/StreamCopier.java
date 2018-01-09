import java.io.*;
import java.net.*;

public class StreamCopier {
    public static void main(String[] args) {
        // InputStream in = System.in;
        OutputStream out = System.out;
        InputStream in = null;
        Socket s = null;
        try {
            ServerSocket ss = new ServerSocket();
            InetAddress remoteAddress = InetAddress.getByName(args[0]);
            int port = 1555;
            ss.bind(new InetSocketAddress(remoteAddress, port));
            System.out.println("now listening on "+args[0]+ " : "+port);
            s = ss.accept();
            s.setKeepAlive(true);
            s.setSoTimeout(2000);
            while (true) {
                in = s.getInputStream();
                byte[] buffer = new byte[256];
                while (true) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead == -1)
                        break;
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        } finally {
            try {
                if (s != null)
                    s.close();
                if (in != null)
                    in.close();
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }

}
