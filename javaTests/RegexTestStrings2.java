import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexTestStrings2 {

    public static final String ACCENTURE="accenture";
    public static final String REDHAT_HYPHENED="red-hat";
    public static final String REDHAT="redhat";

    public static String course1="MWS-SED-MWM-5957-AST-EN_US";
    public static String course2="DCI-S-PLT-ASM-RHL-KO";
    public static String course3="MWS-ADD-JAFU-CHP4-ASM-EN_US";

    public static String[] companyArray = new String[] {
                                            "TECNOLOGIA ESPECIALIZADA ASOCIADA DE MEXICO, S.A. DE C.V.",
                                            "SYONE SBS SOFTWARE - TECNOLOGIA E SERVIÇOS DE INFORMÁTICA, LDA",
                                            "RED HAT (INTERNAL USE ONLY)",
                                            "Axiz (Pty) Ltd",                       // uid=mokgethi.makgopa-axizworkgroup.c,cn=users,cn=accounts,dc=opentlc,dc=com      axiz
                                            "Red Hat",
                                            "Carahsoft Technology Corporation",
                                            "LIS-Linuxland GmbH",                   // uid=j.fluk-linux-ag.com,cn=users,cn=accounts,dc=opentlc,dc=com                   lis-linuxland
                                            "Tecnologia Y Gerencia Del Peru Sac",   // uid=marellano12,cn=users,cn=accounts,dc=opentlc,dc=com,                          tecnologia-y-genercia-del-peru
                                            "CVM, Inc."
                                        };

    public static void main(String[] args) {
        //testCourse();
        testCompany();
    }

    private static void testCompany() {
        StringBuilder sBuilder = new StringBuilder("\ncompanyString = ");
        for(String origCompanyName : companyArray) {
            sBuilder.append("\n\t"+origCompanyName);
            sBuilder.append(" : ");
            sBuilder.append("\t\t\t\t"+transformCompany(origCompanyName));
        }
	System.out.println(sBuilder.toString());
    }

    private static String transformCompany(String orig) {
        String tString = orig.toLowerCase();
        tString = tString.replaceAll("[ ]{2,}", " "); // Not sure
        tString = tString.replaceAll(" - ", "-"); // eliminate spaces before and after dashes
        tString = tString.replaceAll(" ", "-"); //  replace spaces with dash
        //tString = tString.replaceAll("[^\\p{ASCII}]", ""); //  eliminate all non-ascii characters
        tString = tString.replaceAll("[^0-9a-z-]", ""); // eliminate all characters with exception alpha numeric and dash

        if(tString.indexOf(REDHAT_HYPHENED) > -1 )
            tString = REDHAT;

        if(tString.indexOf(ACCENTURE) > -1)
            tString = ACCENTURE;
        else if(tString.indexOf(REDHAT) > -1)
            tString = REDHAT;

        return tString;
    }

    private static void testCourse() {
        String cleanString = course3.replaceAll("(JA$|EN_US$|KO$)","");
        cleanString = cleanString.replaceAll("-$","");
        System.out.println("\n");
	System.out.println("courseStringg = "+cleanString);
    }

}
