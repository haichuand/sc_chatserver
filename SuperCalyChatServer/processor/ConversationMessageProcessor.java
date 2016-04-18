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
 * Payload of ConversationMessage contains:
 * senderId, conversationId, recipients, action, message
**/

/**
 * Payload of ConversationMessage send to each recipient:
 * senderId, conversationId, action, message
**/

public class ConversationMessageProcessor implements PayloadProcessor{
    
    @Override
    public void handleMessage(CcsMessage msg) {
        SuperDao dao = SuperDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        List<String> recipients = new ArrayList<>();
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        
        // new json payload content
        String conversationId = "";
        String message = "";
        int senderId = -1;
        String action = "";

        if(msg.getPayload().containsKey(CcsMessage.CONVERSATION_ID))
            conversationId =(String) msg.getPayload().get(CcsMessage.CONVERSATION_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = (Integer)msg.getPayload().get(CcsMessage.SENDER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = (String)msg.getPayload().get(CcsMessage.ACTION);
        
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
        
        
        if(msg.getPayload().containsKey(CcsMessage.MESSAGE))
            message = (String)msg.getPayload().get(CcsMessage.MESSAGE);
        
        //create new payload
        Map<String, Object> newPayload = new HashMap<String, Object>();
        newPayload.put(CcsMessage.CONVERSATION_ID, conversationId);
        newPayload.put(CcsMessage.ACTION, action);
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.MESSAGE, message); 
        
        client.sendBroadcast(newPayload, collapseKey, timeToLive, delayWhileIdle, recipients);
    }
}
