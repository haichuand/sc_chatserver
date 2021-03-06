/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer.processor;

import SuperCalyChatServer.CcsMessage;
import SuperCalyChatServer.DAO.SuperDao;
import SuperCalyChatServer.SmackCcsClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.parser.ParseException;

public class MessageProcessor implements PayloadProcessor{
    private static final boolean delayWhileIdle = false;

    @Override
    public void handleMessage(CcsMessage msg) {
        SuperDao dao = SuperDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();
        
        List<String> recipients = new ArrayList<>();
        Long timeToLive = 10000L;

        // new json payload content
        String conversationId = "";
        String message = "";
        String senderId = "";

        if(msg.getPayload().containsKey(CcsMessage.CONVERSATION_ID))
            conversationId = msg.getPayload().get(CcsMessage.CONVERSATION_ID);
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            senderId = msg.getPayload().get(CcsMessage.SENDER_ID);
       
        recipients = dao.getTokensForConversation(conversationId);
        
        
        if(msg.getPayload().containsKey(CcsMessage.MESSAGE))
            message = msg.getPayload().get(CcsMessage.MESSAGE);
        
        //create new payload
        Map<String, String> newPayload = new HashMap<>();
        newPayload.put(CcsMessage.CONVERSATION_ID, conversationId);
        newPayload.put(CcsMessage.SENDER_ID, senderId);
        newPayload.put(CcsMessage.MESSAGE, message); 
        
        client.sendBroadcast(newPayload, null, timeToLive, delayWhileIdle, recipients);
    }

}