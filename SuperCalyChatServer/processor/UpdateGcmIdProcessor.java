/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer.processor;

import SuperCalyChatServer.CcsMessage;
import SuperCalyChatServer.DAO.SuperDao;

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
        String userId = "";
        String newGcmId = "";
        
        if(msg.getPayload().containsKey(CcsMessage.SENDER_ID))
            userId = msg.getPayload().get(CcsMessage.SENDER_ID);
        if(msg.getPayload().containsKey(CcsMessage.GCM_ID))
            newGcmId = (String)msg.getPayload().get(CcsMessage.GCM_ID);
        
        SuperDao.getInstance().updateUserGcmId(userId, newGcmId);
    }
}
