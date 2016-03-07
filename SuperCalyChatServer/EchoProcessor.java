
package SuperCalyChatServer;

public class EchoProcessor implements PayloadProcessor{

    @Override
    public void handleMessage(CcsMessage msg) {
        PseudoDao dao = PseudoDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        String msgId = dao.getUniqueMessageId();
        String jsonRequest = 
                SmackCcsClient.createJsonMessage(
                        msg.getFrom(), 
                        msgId, 
                        msg.getPayload(), 
                        null, 
                        null, // TTL (null -> default-TTL) 
                        false);
        client.send(jsonRequest);
    }

}