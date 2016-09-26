/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer.processor;

import SuperCalyChatServer.CcsMessage;
import SuperCalyChatServer.DAO.SuperDao;
import SuperCalyChatServer.SmackCcsClient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author xuejing
 */
/**
 * Payload of AddConversationAttendees request contains:
 * senderId, conversationId, userIds, recipients, action
**/

/**
 * Payload of AddConversationAttendees send to recipients contains:
 * senderId, conversationId, userIds, action
**/

public class AddConversationAttendeesProcessor implements PayloadProcessor{
    private static final boolean delayWhileIdle = false;

    @Override
    public void handleMessage(CcsMessage msg) { 
        SuperDao dao = SuperDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        List<String> recipients = new ArrayList<>();
        Long timeToLive = 10000L;

        String senderId = "";
        String userIds = "";
        String action = "";
        String conversationId = "";
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = msg.getPayload().get(CcsMessage.SENDER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = msg.getPayload().get(CcsMessage.ACTION);
        
        if(msg.getPayload().containsKey(CcsMessage.CONVERSATION_ID))
            conversationId = msg.getPayload().get(CcsMessage.CONVERSATION_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.USER_IDS)) 
            userIds = msg.getPayload().get(CcsMessage.USER_IDS);
        
        if(msg.getPayload().containsKey(CcsMessage.RECIPIENTS)){
            List<String> recipientsId = Arrays.asList(((String)msg.getPayload().get(CcsMessage.RECIPIENTS)).split(","));
            if(recipientsId != null) {
                for(String id: recipientsId) {
                    recipients.add(dao.getUserFcmId(id));
                }
            }
        }
        
        //create new payload
        Map<String, String> newPayload = new HashMap<>();
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.ACTION, action);
        newPayload.put(CcsMessage.CONVERSATION_ID, conversationId);
        newPayload.put(CcsMessage.USER_IDS, userIds);
        
        client.sendBroadcast(newPayload, null, timeToLive, delayWhileIdle, recipients);
        
    }
}
