import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

public class Send_Mail {
	public static void main(String[] args) {
		sendMail();   
	}
	
	public static void sendMail() {
		try {
			// Set up Session
			Properties props = new Properties();
			props.put("localhost", "my-mail-server");
			Session session = Session.getInstance(props, null);

			//Create message
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom("labrat@localhost");
			msg.setRecipients(Message.RecipientType.TO, "labrat@localhost");
			msg.setSubject("Testbetreff");
			msg.setSentDate(new Date());
			msg.setText("Hallo labrat!\n");

			//Send message
			Transport.send(msg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
