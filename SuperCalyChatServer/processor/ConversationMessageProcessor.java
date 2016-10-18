/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer.processor;

import SuperCalyChatServer.CcsMessage;
import SuperCalyChatServer.DAO.SuperDao;
import SuperCalyChatServer.SmackCcsClient;

import java.util.*;

/**
 *
 * @author xuejing
 */


/**
 * Payload of ConversationMessage contains:
 * senderId, conversationId, recipients, action, message
**/

/**
 * Payload of ConversationMessage send to each recipient:
 * senderId, conversationId, action, message
**/

public class ConversationMessageProcessor implements PayloadProcessor{
    private static final boolean delayWhileIdle = false;

    @Override
    public void handleMessage(CcsMessage msg) {
        SuperDao dao = SuperDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        List<String> recipients = new ArrayList<>();
        Long timeToLive = 10000L;
        
        // new json payload content
        String conversationId = "";
        String message = "";
        String senderId = "";
        String action = "";
        String messageId = "";
        String attachments = "";

        if(msg.getPayload().containsKey(CcsMessage.CONVERSATION_ID))
            conversationId = msg.getPayload().get(CcsMessage.CONVERSATION_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = msg.getPayload().get(CcsMessage.SENDER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = msg.getPayload().get(CcsMessage.ACTION);

        if (msg.getPayload().containsKey(CcsMessage.MESSAGE_ID)) {
            messageId = msg.getPayload().get(CcsMessage.MESSAGE_ID);
        }

        if(msg.getPayload().containsKey(CcsMessage.RECIPIENTS)){
            List<String> recipientsId = Arrays.asList((msg.getPayload().get(CcsMessage.RECIPIENTS)).split(","));
            for(String id: recipientsId) {
                recipients.add(dao.getUserFcmId(id));
            }
        }
        
        
        if(msg.getPayload().containsKey(CcsMessage.MESSAGE))
            message = (String)msg.getPayload().get(CcsMessage.MESSAGE);
        if (msg.getPayload().containsKey(CcsMessage.ATTACHMENTS))
            attachments = msg.getPayload().get(CcsMessage.ATTACHMENTS);

        //create new payload
        Map<String, String> newPayload = new HashMap<>();
        newPayload.put(CcsMessage.CONVERSATION_ID, conversationId);
        newPayload.put(CcsMessage.ACTION, action);
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.MESSAGE, message);
        newPayload.put(CcsMessage.MESSAGE_ID, messageId);
        newPayload.put(CcsMessage.ATTACHMENTS, attachments);
        newPayload.put(CcsMessage.TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        client.sendBroadcast(newPayload, null, timeToLive, delayWhileIdle, recipients);
    }
}
