/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author anu
 */
public class httpserver implements Runnable{
        
    private static ServerSocket socket;
    public static int PORT = 8010;
    @Override
    public void run()
    {
        try {
            socket = new ServerSocket(PORT);
            System.out.println("Waiting for clients requests....");
            while(true)
            {
                Thread.sleep(100);
                System.out.println("in http while loop");
                Socket client = socket.accept();
                proccessRequest(client);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	        Date date = new Date();
	        System.out.println("Response Sent at "+ dateFormat.format(date).toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(httpserver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(httpserver.class.getName()).log(Level.SEVERE, null, ex);
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
