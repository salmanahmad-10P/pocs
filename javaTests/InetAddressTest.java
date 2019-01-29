import java.net.InetAddress;

public class InetAddressTest {

    public static void main(String args[]) throws Exception{
        InetAddress iAddress = InetAddress.getByName("231.7.7.7");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        System.out.println("main() address is multicast = "+iAddress.isMulticastAddress()+" : hostAddress = "+hostAddress);
    }
}
