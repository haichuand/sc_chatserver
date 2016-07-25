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
 *
 * @author xuejing
 */

/**
 * Payload of UpdateGcmId request contains:
 * senderId, gcmId, action
**/

public class UpdateGcmIdProcessor implements PayloadProcessor{
    @Override
    public void handleMessage(CcsMessage msg) { 
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        
        List<String> recipients = new ArrayList<>();
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        
        String senderId = "";
        String newGcmId = "";
        String action = "";
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = msg.getPayload().get(CcsMessage.SENDER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = (String)msg.getPayload().get(CcsMessage.ACTION);
        
        if(msg.getPayload().containsKey(CcsMessage.GCM_ID)) {
            newGcmId = (String)msg.getPayload().get(CcsMessage.GCM_ID);
            recipients.add(newGcmId);
        }
        
        SuperDao.getInstance().updateUserGcmId(senderId, newGcmId);
        
        //create new payload
        Map<String, String> newPayload = new HashMap<>();
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.ACTION, action);
        
        client.sendBroadcast(newPayload, null, timeToLive, delayWhileIdle, recipients);
        
    }
}
