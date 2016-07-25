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
 * Payload of UpdateConversationTitle request contains:
 * senderId, conversationId, title, recipients, action
**/

/**
 * Payload of DropConversationAttendee send to recipients contains:
 * senderId, conversationId, title, action
**/
public class UpdateConversationTitleProcessor implements PayloadProcessor{
    @Override
    public void handleMessage(CcsMessage msg) { 
        SuperDao dao = SuperDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        List<String> recipients = new ArrayList<>();
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;

        String senderId = "";
        String title = "";
        String action = "";
        String conversationId = "";
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = msg.getPayload().get(CcsMessage.SENDER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = msg.getPayload().get(CcsMessage.ACTION);
        
        if(msg.getPayload().containsKey(CcsMessage.CONVERSATION_ID))
            conversationId = msg.getPayload().get(CcsMessage.CONVERSATION_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.TITLE)) 
            title = msg.getPayload().get(CcsMessage.TITLE);
        
        if(msg.getPayload().containsKey(CcsMessage.RECIPIENTS)){
            List<String> recipientsId = Arrays.asList(((String)msg.getPayload().get(CcsMessage.RECIPIENTS)).split(","));
            if(recipientsId != null) {
                for(String id: recipientsId) {
                    recipients.add(dao.getUserGcmId(id));
                }
            }
        }
        
        //create new payload
        Map<String, String> newPayload = new HashMap<>();
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.ACTION, action);
        newPayload.put(CcsMessage.CONVERSATION_ID, conversationId);
        newPayload.put(CcsMessage.TITLE, title);
        
        client.sendBroadcast(newPayload, null, timeToLive, delayWhileIdle, recipients);
        
    }
    
}
