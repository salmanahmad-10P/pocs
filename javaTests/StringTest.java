public class StringTest {

    private static final String RED_HAT_SUFFIX = "redhat.com";

    public static void main(String args[]) {
        String fromEmail = "jbride2001@yahoo.com";
        System.out.println("indexof = "+fromEmail.indexOf(RED_HAT_SUFFIX));
        System.out.println("indexof = "+fromEmail.indexOf("redhat.com"));
    }
}

