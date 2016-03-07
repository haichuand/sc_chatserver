/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer;

public class MessageProcessor implements PayloadProcessor{

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