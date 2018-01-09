import java.io.*;
import java.net.*;

public class SocketListener {
    public static void main(String[] args) {
        byte[] inboundBuffer = new byte[512];
        try {
            int port = 1313;
            InetAddress remoteAddress = InetAddress.getByName(args[0]);
            ServerSocket ss = new ServerSocket(port, 0, remoteAddress);
            System.out.println("Now Listening on Port " + port);
            Socket client = ss.accept();
            System.out.println("Client just aquired socket");
            InputStream in = client.getInputStream();

            /*
             * Reader implementation BufferedReader fromClient = new
             * BufferedReader(new InputStreamReader(in)); String input =
             * "start"; while(input != null) { input = fromClient.readLine();
             * System.out.println(input); }
             */

            /* Byte stream implementation */
            int t = 0;
            while (true) {
                int byteRead = in.read();
                // System.out.println(t+" byteRead = "+byteRead+"
                // "+(char)byteRead);
                if (byteRead == -1) {
                    System.out.println("Client Socket Closed!");
                    break;
                }
                if (byteRead == 13) // carriage return
                {
                    printByteArrayToChar(inboundBuffer, t);
                    String line = new String(inboundBuffer, 0, t);
                    System.out.println("line = " + line);
                    int tagStart = line.lastIndexOf("TAGID:");
                    System.out.println("tagStart = " + tagStart);
                    int itemStart = line.lastIndexOf(", BARCODE:");
                    System.out.println("itemStart = " + itemStart);

                    String tagId = line.substring((tagStart + 6), (itemStart));
                    System.out.println("tagId = " + tagId);
                    String itemId = line.substring((itemStart + 10), t);
                    System.out.println("itemId = " + itemId);
                    t = 0;
                }
                if (byteRead == 10) // line feed --> don't need any of it
                {
                    t = 0;
                }
                if (byteRead == 255) // filter junk
                {
                    t = 0;
                } else {
                    inboundBuffer[t] = (byte) byteRead;
                    t++;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    private static void printByteArrayToChar(byte[] byteArray, int length) {
        System.out.println("char Stream = " + new String(byteArray, 0, length));
    }

    private static void printByteArrayToHex(byte[] byteArray, int length) {
        StringBuffer buffer = new StringBuffer();
        for (int z = 0; z < length; z++) {
            buffer.append(Integer.toHexString(byteArray[z]));
            buffer.append(' ');
        }
        System.out.println("byteStream = " + buffer.toString());
    }

}
