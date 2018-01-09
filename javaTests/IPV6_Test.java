import java.net.*;
import java.io.*;

/**
 *  usage :  java -DWILL_LISTEN=true -DBIND_ADDRESS=fec0::226:22ff:fed4:2121 IPV6_Test
 */
public class IPV6_Test implements Runnable {

    public static final String BIND_ADDRESS = "BIND_ADDRESS";
    public static final String WILL_LISTEN = "WILL_LISTEN";
    private static InetAddress bindAddr;
    private static int port = 3000;
    private static boolean willListen = false;

    public static void main(String[] args) throws Exception {

        String bindAddressString = "ratwater";
        if(System.getProperty(BIND_ADDRESS) != null)
            bindAddressString = System.getProperty(BIND_ADDRESS);
        if(System.getProperty(WILL_LISTEN) != null)
            willListen = Boolean.parseBoolean(System.getProperty(WILL_LISTEN));

        bindAddr = InetAddress.getByName(bindAddressString);

        if(willListen) {
            new Thread(new IPV6_Test()).start();
            Thread.sleep(3000);
        }

        Socket clientSocket = null;
        try {
            System.out.println("main() about to open socket connection on "+bindAddr.getHostAddress()+":"+port);
            clientSocket = new Socket(bindAddr, port);
        } catch(Exception x) {
            x.printStackTrace();
        }

  }

    public void run () {
        try {
            ServerSocket serverSocket = new ServerSocket(port, 0, bindAddr);
            System.out.println("run() about to listen on "+bindAddr.getHostAddress()+":"+port);

            while(true) {
                Socket socket = serverSocket.accept();
                OutputStream out = socket.getOutputStream();

                InetAddress inetAddress = socket.getInetAddress();

                System.out.println("server socket is ipv6: " + (inetAddress instanceof Inet6Address));

                socket.close();
            }
        } catch ( Exception ex ) {
            ex.printStackTrace();
        } finally {
        }
  }

}
