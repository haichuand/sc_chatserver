/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageProcessor implements PayloadProcessor{

    @Override
    public void handleMessage(CcsMessage msg) {
        PseudoDao dao = PseudoDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        List<String> recipients = new ArrayList<>();
        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;
        
        // new json payload content
        String conversationId = "";
        String message = "";
        String senderId = "";
        
        if(msg.getPayload().containsKey(CcsMessage.RECIPIENTS))
            recipients = Arrays.asList(msg.getPayload().get(CcsMessage.RECIPIENTS).split(","));
        if(msg.getPayload().containsKey(CcsMessage.CONVERSATION_ID))
            conversationId = msg.getPayload().get(CcsMessage.CONVERSATION_ID);
        if(msg.getPayload().containsKey(CcsMessage.MESSAGE))
            message = msg.getPayload().get(CcsMessage.MESSAGE);
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = msg.getPayload().get(CcsMessage.SENDER_ID);
        
        //create new payload
        Map<String, String> newPayload = new HashMap<String, String>();
        newPayload.put(CcsMessage.CONVERSATION_ID, conversationId);
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.MESSAGE, message); 
        
        client.sendBroadcast(newPayload, collapseKey, timeToLive, delayWhileIdle, recipients);
    }

}