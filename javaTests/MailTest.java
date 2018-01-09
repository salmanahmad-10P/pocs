import java.util.Properties;
import java.util.Date;
import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;

public class MailTest {
	public static void main(String args[]) {
		Properties props = System.getProperties();
		//props.put("mail.smtp.host", "168.175.3.105");
		props.put("mail.smtp.host", "localhost");
		Session session = Session.getDefaultInstance(props, null);
		InternetAddress[] recipients = new InternetAddress[2];
		MimeMessage msg = null;
		try {
			recipients[0] = new InternetAddress("jbride2000@yahoo.com");
			recipients[1] = new InternetAddress("jbride@rfidgs.com");

			msg = new MimeMessage(session);
			msg.addRecipients(RecipientType.TO, recipients);
			msg.setFrom(new InternetAddress("jeff.bride@wachovia.com"));
			msg.setSubject("Test Subject");
			msg.setSentDate(new Date());
			msg.setText("Sample Body");
			Transport.send(msg);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
