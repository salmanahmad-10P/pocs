import org.apache.commons.codec.binary.Base64;

public class Base64Test {

    private static String testString = "bburke:geheim";

    public static void main(String[] args) throws Exception {
        System.out.println("encoded test string = "+Base64.encodeBase64String(testString.getBytes()));
    }
}
