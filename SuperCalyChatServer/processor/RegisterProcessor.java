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
 * Payload of Register contains:
 * senderId, fcmId, action
**/

public class RegisterProcessor implements PayloadProcessor{
    private static final boolean delayWhileIdle = false;

    @Override
    public void handleMessage(CcsMessage msg) {
        //TODO: remove following two lines
        String accountName =(String)msg.getPayload().get("account");
        SuperDao.getInstance().addRegistration(msg.getFrom(), accountName);
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        List<String> recipients = new ArrayList<>();
        Long timeToLive = 10000L;

        String senderId = "";
        String fcmId = "";
        String action = "";
        System.out.println(msg.getPayload().toString());
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID)) 
            senderId = msg.getPayload().get(CcsMessage.SENDER_ID);
        
        if(msg.getPayload().containsKey(CcsMessage.ACTION))
            action = (String)msg.getPayload().get(CcsMessage.ACTION);
        
        if(msg.getPayload().containsKey(CcsMessage.FCM_ID)) {
            fcmId = (String)msg.getPayload().get(CcsMessage.FCM_ID);
            recipients.add(fcmId);
        }
        SuperDao.getInstance().addNewUser(senderId, fcmId);
        
        //create new payload
        Map<String, String> newPayload = new HashMap<>();
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.ACTION, action);
        
        client.sendBroadcast(newPayload, null, timeToLive, delayWhileIdle, recipients);
    }

}
