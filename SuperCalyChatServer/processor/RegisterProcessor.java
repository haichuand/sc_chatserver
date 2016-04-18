/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer.processor;

import SuperCalyChatServer.CcsMessage;
import SuperCalyChatServer.DAO.SuperDao;

/**
 * Payload of Register contains:
 * senderId, gmcId, action
**/

public class RegisterProcessor implements PayloadProcessor{

    @Override
    public void handleMessage(CcsMessage msg) {
        //TODO: remove following two lines
        String accountName =(String)msg.getPayload().get("account");
        SuperDao.getInstance().addRegistration(msg.getFrom(), accountName);
        
        int userId = -1;
        String gcmId = "";
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            userId = (Integer)msg.getPayload().get(CcsMessage.SENDER_ID);
        if(msg.getPayload().containsKey(CcsMessage.GCM_ID))
            gcmId = (String)msg.getPayload().get(CcsMessage.GCM_ID);
        
        SuperDao.getInstance().addNewUser(userId, gcmId);
    }

}
