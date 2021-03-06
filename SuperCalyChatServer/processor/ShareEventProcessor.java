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
 * Payload of ShareEvent send to recipients contains:
 * creatorId, conversationId, eventId, recipients, action 
**/

/**
 * Payload of ShareEvent send to recipients contains:
 * creatorId, conversationId, eventId, action
**/

public class ShareEventProcessor implements PayloadProcessor{
    private static final boolean delayWhileIdle = false;

     @Override
    public void handleMessage(CcsMessage msg) {
        SuperDao dao = SuperDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        List<String> recipients = new ArrayList<>();
        Long timeToLive = 10000L;
        //content of new payload
        String creatorId = "";
        String action = "";
        String eventId = "";
        
        if(msg.getPayload().containsKey(CcsMessage.CREATOR_ID))
            creatorId = msg.getPayload().get(CcsMessage.CREATOR_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = msg.getPayload().get(CcsMessage.ACTION);
        
        if(msg.getPayload().containsKey(CcsMessage.EVENT_ID))
            eventId = msg.getPayload().get(CcsMessage.EVENT_ID);
        
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
        newPayload.put(CcsMessage.CREATOR_ID, creatorId);
        newPayload.put(CcsMessage.ACTION, action);
        newPayload.put(CcsMessage.EVENT_ID, eventId);

        client.sendBroadcast(newPayload, null, timeToLive, delayWhileIdle, recipients);
        
    }
}
