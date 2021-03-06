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
 * Payload of UpdateFcmId request contains:
 * senderId, newFcmId, action
**/

public class UpdateFcmIdProcessor implements PayloadProcessor{
    private static final boolean delayWhileIdle = true;

    @Override
    public void handleMessage(CcsMessage msg) { 
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        
        List<String> recipients = new ArrayList<>();
        Long timeToLive = 10000L;

        String senderId = "";
        String newFcmId = "";
        String action = "";
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = msg.getPayload().get(CcsMessage.SENDER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = (String)msg.getPayload().get(CcsMessage.ACTION);
        
        if(msg.getPayload().containsKey(CcsMessage.FCM_ID)) {
            newFcmId = (String)msg.getPayload().get(CcsMessage.FCM_ID);
            recipients.add(newFcmId);
        }
        
        SuperDao.getInstance().updateUserFcmId(senderId, newFcmId);
        
        //create new payload
        Map<String, String> newPayload = new HashMap<>();
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.ACTION, action);
        
        client.sendBroadcast(newPayload, null, timeToLive, delayWhileIdle, recipients);
        
    }
}
