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
    private static final boolean delayWhileIdle = false;
    
    @Override
    public void handleMessage(CcsMessage msg) { 
        SuperDao dao = SuperDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        String targetUserId = "";
        List<String> recipients = new ArrayList<>();
        Long timeToLive = 10000L;

        //content of new payload
        String senderId = "";
        String action = "";
        
        if(msg.getPayload().containsKey(CcsMessage.TARGET_USER_ID)) {
            targetUserId = msg.getPayload().get(CcsMessage.TARGET_USER_ID);
            recipients.add(dao.getUserFcmId(targetUserId));
        }
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID)) {
            senderId = msg.getPayload().get(CcsMessage.SENDER_ID);
            recipients.add(dao.getUserFcmId(senderId));
        }
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = (String)msg.getPayload().get(CcsMessage.ACTION);
        
        //create new payload
        Map<String, String> newPayload = new HashMap<>(msg.getPayload());
        newPayload.put(CcsMessage.ACTION, action);
        newPayload.put(CcsMessage.SENDER_ID, senderId);

        client.sendBroadcast(newPayload, null, timeToLive, delayWhileIdle, recipients);
        
    }
}
