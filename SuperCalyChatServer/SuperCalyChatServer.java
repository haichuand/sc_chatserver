/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer;

import SuperCalyChatServer.DAO.SuperDao;
import static SuperCalyChatServer.SmackCcsClient.logger;
import java.io.IOException;
import java.util.logging.Level;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.simple.parser.ParseException;

/**
 *
 * @author xuejing
 */
public class SuperCalyChatServer {
    
    public static void main(String[] args) {
        final String projectId = "1076145801492";
        final String password = "AIzaSyA_B4SmgfKpMbSp32jJbWrCqQ6YxGAhoMo";

        //connect();
        SmackCcsClient ccsClient = SmackCcsClient.prepareClient(projectId, password, true);

        try {
            ccsClient.getConnected();
        } 
        catch (SmackException.ConnectionException e) {
            logger.log(Level.SEVERE,e.getFailedAddresses().toString());
            logger.log(Level.SEVERE,e.getFailedAddresses().get(0).getException().getMessage());
        }
        catch (XMPPException e) {
            e.printStackTrace();
        }
        catch (SmackException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            
            SuperDao.getInstance().populateUserFcmCache();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        while(true){
            try{
               Thread.sleep(50);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
}
