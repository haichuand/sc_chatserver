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
 * Payload of DropConversationAttendee request contains:
 * senderId, conversationId, targetUserId, recipients, action
**/

/**
 * Payload of DropConversationAttendee send to recipients contains:
 * senderId, conversationId, targetUserId, action
**/
public class DropConversationAttendeeProcessor implements PayloadProcessor{
    
    @Override
    public void handleMessage(CcsMessage msg) { 
        SuperDao dao = SuperDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        List<String> recipients = new ArrayList<>();
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;

        int senderId = -1;
        int targetUserId = -1;
        String action = "";
        String conversationId = "";
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = (Integer)msg.getPayload().get(CcsMessage.SENDER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = (String)msg.getPayload().get(CcsMessage.ACTION);
        
        if(msg.getPayload().containsKey(CcsMessage.CONVERSATION_ID))
            conversationId = (String)msg.getPayload().get(CcsMessage.CONVERSATION_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.TARGET_USER_ID)) 
            targetUserId = (Integer)msg.getPayload().get(CcsMessage.TARGET_USER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.RECIPIENTS)){
            List<String> recipientsId = Arrays.asList(((String)msg.getPayload().get(CcsMessage.RECIPIENTS)).split(","));
            if(recipientsId != null) {
                for(String id: recipientsId) {
                    if(!id.equals(String.valueOf(senderId))) {
                        recipients.add(dao.getUserGcmId(Integer.valueOf(id)));
                    }
                }
            }
        }
        
        //create new payload
        Map<String, Object> newPayload = new HashMap<>();
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.ACTION, action);
        newPayload.put(CcsMessage.CONVERSATION_ID, conversationId);
        
        client.sendBroadcast(newPayload, collapseKey, timeToLive, delayWhileIdle, recipients);
        
    }
}
