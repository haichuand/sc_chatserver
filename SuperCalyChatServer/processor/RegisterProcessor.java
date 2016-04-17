/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer.processor;

import SuperCalyChatServer.CcsMessage;
import SuperCalyChatServer.DAO.SuperDao;

public class RegisterProcessor implements PayloadProcessor{

    @Override
    public void handleMessage(CcsMessage msg) {
        String accountName = msg.getPayload().get("account");
        SuperDao.getInstance().addRegistration(msg.getFrom(), accountName);
    }

}
