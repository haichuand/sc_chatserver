package SuperCalyChatServer.mail;

import org.apache.commons.io.IOUtils;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * Process email responses. Listens for response email messages on port 8020.
 * Response mail messages are forwarded by mailpipe.php in /etc/postfix folder
 * 
 * Created by haichuand on 10/22/2016.
 */
public class MailResponseServer implements Runnable {
    private static final int PORT = 8020;
    @Override
    public void run() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(PORT);
            System.out.println("MailResponseServer: Waiting for clients requests....");
            while (true) {
                System.out.println("MailResponseServer loop starts...");
                Socket client = socket.accept();
//                System.out.println("MailResponseServer socket accepted...");
                InputStream in = client.getInputStream();
//                System.out.println("MailResponseServer InputStream obtained...");
                String inputString = IOUtils.toString(in, "UTF-8");
//                System.out.println(inputString);
                processEmailResponse(inputString);
                in.close();
                client.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void processEmailResponse (String emailResponse) {
        Session session = Session.getDefaultInstance(new Properties());
        InputStream inputStream = new ByteArrayInputStream(emailResponse.getBytes());
        try {
            //get header fields
            MimeMessage message = new MimeMessage(session, inputStream);
            String fromString = message.getHeader("From")[0];
            String fromEmailAddress = fromString.substring(fromString.indexOf('<') + 1, fromString.indexOf('>'));
            String toString = message.getHeader("To")[0];
            int indexOfEqualSign = toString.indexOf('=');
            if (indexOfEqualSign == -1) {
                System.out.println("Error: 'To' field does not contain id");
                return;
            }
            String toIdKey = toString.substring(toString.indexOf('<') + 1, indexOfEqualSign);
            String toIdValue = toString.substring(indexOfEqualSign + 1, toString.indexOf('@'));
            String body = "";
            if (message.isMimeType("text/plain")) {
                body = (String) message.getContent();
                System.out.println("MimeType=text/plain");
            } else if (message.isMimeType("multipart/*")) {
                Multipart parts = (Multipart) message.getContent();
                body = (String) parts.getBodyPart(0).getContent();
                System.out.println("MimeType=multipart/*");
            } else if (message.isMimeType("message/rfc822")) {
                System.out.println("MimeType=message/rfc822");
            } else {
                System.out.println("MimeType=unknown");
            }
            String replyText = com.driftt.email.EmailMessage.read(body).getReply();
            System.out.println("From=" + fromEmailAddress + "   toIdKey=" + toIdKey + "   toIdValue=" + toIdValue + "   replyText=" + replyText);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
