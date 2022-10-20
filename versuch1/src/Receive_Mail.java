import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class Receive_Mail {
	public static void main(String[] args) throws Exception {
		fetchMail();
	}
	
	public static void fetchMail() {


		try {
			String host = "localhost";
			String user = "labrat";
			String password = "kn1lab";

			Properties prop = System.getProperties();
			Session ses = Session.getInstance(prop);

			Store store = ses.getStore("pop3");
			store.connect(host, user, password);
			Folder inbox = store.getFolder("INBOX");
			inbox.open(Folder.READ_ONLY);

			Message[] messages = inbox.getMessages();
			if (messages.length == 0){
				System.out.println("Keine Nachricht gefunden");
			}else {
				int i = 1;
				for (Message m : messages){
					System.out.println("\n-------------------------------" +
							"\n Nachricht Nummer: " + i +
							"\n Von: " + m.getFrom()[0] +
							"\n Betreff: " + m.getSubject() +
							"\n Datum: " + m.getSentDate() +
							"\n Inhalt: " + m.getContent() + "\n-------------------------------");
					i++;
				}
				inbox.close(true);
				store.close();
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}


