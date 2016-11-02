/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer;

import SuperCalyChatServer.DAO.SuperDao;
import static SuperCalyChatServer.SmackCcsClient.logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;

import SuperCalyChatServer.mail.MailResponseServer;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.json.simple.parser.ParseException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author xuejing
 */
public class SuperCalyChatServer {


    public static void main(String[] args) throws IOException {
        final String projectId = "670096617047";
        final String password = "AIzaSyDizGBQeIukKEftE3wgf4TWbi_UeCPeAdw";
	
        //connect();
        SmackCcsClient ccsClient = SmackCcsClient.prepareClient(projectId, password, true);
        Thread t = new Thread(new httpserver());
        t.start();

        Thread mailResponseServerThread = new Thread(new MailResponseServer());
        mailResponseServerThread.start();
        
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
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(SuperCalyChatServer.class.getName()).log(Level.SEVERE, null, ex);
            }
               
            }
 
        
        
    }  
    
}
