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
        final String projectId = "115711938538";
        final String password = "AIzaSyDYP8RiorJWNGwP8gSuaxoevvFQkyJH_6c";
        final String toRegId = "cqGKPB-73Ps:APA91bFQEhedJ1_KGwIBWMJFYAMMZAVkwIw8iT5FoJiuXaqij1XWglYTKtGqhhntk1snoukzLMvJQL9-s7GZP4w_j05u55IpyYgSaNnXZe6bSKBlt1iQDg_OkMbCyA-Z3r8jvEqaDYDm";
        //connect();
        SmackCcsClient ccsClient = SmackCcsClient.prepareClient(projectId, password, true);

        try {
            ccsClient.connect();
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
            SuperDao.getInstance().populateUserGcmCache();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        while(true)
        {;}
    }
    
}
