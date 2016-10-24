package SuperCalyChatServer.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * Handles email for chat server
 * Created by haichuand on 10/14/2016.
 */
public class EmailHandler {
    // Sender's email ID
    private static final String FROM = "SuperCaly Team<mail@supercaly.com>";
    //sending email from localhost
    private static final String HOST = "localhost";

    private static Session session;

    static {
        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.setProperty("mail.smtp.host", HOST);
        // Get the default Session object.
        session = Session.getDefaultInstance(properties);
    }

    public void sendEmail (String to, String subject, String text, Map<String, String> headers) {
        try {
            if (to == null || to.isEmpty() || subject == null || subject.isEmpty()) {
                return;
            }
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(FROM));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Set extra headers, if any
            if (headers != null) {
                for (String key : headers.keySet()) {
                    message.setHeader(key, headers.get(key));
                }
            }

            // Now set the actual message
            message.setText(text);

            // Send message
            Transport.send(message);
            System.out.println("Email sent successfully");
        }catch (MessagingException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
