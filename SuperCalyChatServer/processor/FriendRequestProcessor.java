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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Payload of FriendRequest contains:
 * sender_id, targetUserId, action 
**/

/**
 * Payload of FriendRequest send to target user:
 * sender_id, action 
**/
public class FriendRequestProcessor implements PayloadProcessor{
    
    @Override
    public void handleMessage(CcsMessage msg) { 
        SuperDao dao = SuperDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        int targetUserId = -1;
        List<String> recipients = new ArrayList<>();
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        
        //content of new payload
        int senderId = -1;
        String action = "";
        
        if(msg.getPayload().containsKey(CcsMessage.TARGET_USER_ID))
            targetUserId = (int)msg.getPayload().get(CcsMessage.TARGET_USER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = (Integer)msg.getPayload().get(CcsMessage.SENDER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = (String)msg.getPayload().get(CcsMessage.ACTION);
        
        //create new payload
        Map<String, Object> newPayload = new HashMap<>(msg.getPayload());
        newPayload.put(CcsMessage.ACTION, action);
        newPayload.put(CcsMessage.SENDER_ID, senderId);

        client.sendBroadcast(newPayload, collapseKey, timeToLive, delayWhileIdle, recipients);
        
    }
}
