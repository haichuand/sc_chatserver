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

/**
 *
 * @author xuejing
 */
public class SuperCalyChatServer {
    
    private static ServerSocket socket;
    public static int PORT = 9000;

    public static void main(String[] args) throws IOException {
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
        
        try
        {
            socket = new ServerSocket(PORT);
            System.out.println("Waiting for clients requests....\n");
            while(true){
               Thread.sleep(50);
               Socket client = socket.accept();
               proccessRequest(client);
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            socket.close();
        }
        
        
    }

    private static void proccessRequest(Socket client) {
        String str;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            str = reader.readLine();
            if(str != null)
            {
                if(str.trim().contains("HTTP"))
                {
                    sendResponse(client);
                }
            }
        }
        catch(IOException e)
        {
            
        }
    }

    private static void sendResponse(Socket client) throws IOException {
         
        String statusCode = "200";
        String reasonPhrase = "OK";
        HashMap<String,String> headers = new HashMap<>(); 
        String message = "<p>Chat Server Running fine!</p>";
        String version = "HTTP/1.1";
        String response = "";
 
        headers.put("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT"))));
        headers.put("Server", "ChatServer");
        headers.put("Content-Type","text/html");
        headers.put("Content-Length", Integer.toString(message.getBytes().length));
        
        response += version;
        response += " " + statusCode;
        response += " " + reasonPhrase;
        response += "\r\n";
        if(headers != null)
        {
            for(Map.Entry<String, String> entry : headers.entrySet())
            {
                response += entry.getKey()+ ":" + entry.getValue() + "\r\n";
            }
        }
        response += "\r\n";
        if(message != null)
        {
            response += message;
        }
        
        try
        {
        client.getOutputStream().write(response.getBytes());
        client.getOutputStream().flush();
        }
        catch(IOException e)
        {
             e.printStackTrace();
        }
        finally
        {
            client.close();
        }
    }
    
    
}
