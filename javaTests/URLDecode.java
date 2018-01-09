import java.net.URLDecoder;

public class URLDecode {

    private static String sToDecode = "%7B%22email%22%3A%22agomez%40criticalperu.com%22%2C%22courseCommpletions%22%3A+%5B%22Red+Hat+OpenStack+7+Implementation%22%2C%22Red+Hat+OpenStack+7+FASTRAX%22%2C%22Red+Hat+CloudForms+Implementation%22%2C%22Red+Hat+CloudForms+FASTRAX%22%2C%22Red+Hat+Enterprise+Linux+7+Troubleshooting%22%2C%22Red+Hat+Ceph+for+OpenStack%22%2C%22Data+Integration+with+Red+Hat+JBoss+Data+Virtualization%22%2C%22Cloud+Management+Solution+for+Sales+Engineer%22%2C%22Red+Hat+Enterprise+Virtualization+3.1+for+Presales%22%2C%22Red+Hat+Sales+Specialist+-+Platform+%282.0%29%22%2C%22Red+Hat+Enterprise+Virtualization+FASTRAX%22%2C%22Red+Hat+Ceph+Storage+FASTRAX%22%2C%22Red+Hat+Gluster+Storage+3+Implementation%22%2C%22Red+Hat+Storage+Solution+for+Salesperson%22%2C%22Red+Hat+Network+Satellite+Sales%22%2C%22Red+Hat+Sales+Specialist+-+Platform+%282.0%29%22%2C%22RHN+Satellite+Sales%22%5D%2C%22rulesFired%22%3A+%5B%227_Red+Hat+Delivery+Specialist+-+Cloud+Management_with_2_courses%22%2C%2219_Red+Hat+Delivery+Specialist+-+Infrastructure+as+a+Service_with_2_courses%22%2C%2225_Red+Hat+Delivery+Specialist+-+Red+Hat+Ceph+for+OpenStack_with_1_course%22%2C%224_Red+Hat+Delivery+Specialist+-+Red+Hat+Enterprise+Linux+%287%29+Troubleshooting_with_1_course%22%2C%2212_Red+Hat+Delivery+Specialist+-+Storage_with_1_course%22%2C%2220_Red+Hat+Sales+Engineer+Specialist+-+Virtualization_with_2_courses%22%5D%7D";

    public static void main(String[] args) throws java.io.UnsupportedEncodingException {
        String decodedString = URLDecoder.decode(sToDecode, "UTF-8");
        System.out.println(decodedString);
    }
}
