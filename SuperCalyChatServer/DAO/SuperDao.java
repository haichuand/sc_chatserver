/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer.DAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This class acts as a DAO replacement. There is no
 * persistent state. As soon as you kill the server, all state will
 * be lost.
 * 
 * You have to take care of persisting messages as well as
 * recipients for proper apps!
 */
public class SuperDao {
    
    public static final Logger logger = Logger.getLogger(SuperDao.class.getName());
    
    private final static SuperDao instance = new SuperDao();
    private final static Random sRandom = new Random();
    private final String host = "http://52.25.71.19:8080/SuperCaly/rest";
    private final Set<Integer> mMessageIds = new HashSet<Integer>();
    private final Map<String, List<String>> mUserMap = new HashMap<String, List<String>>();
    private final List<String> mRegisteredUsers = new ArrayList<String>();
    private final Map<String, String> mNotificationKeyMap = new HashMap<String, String>();
    private final ConcurrentHashMap<String, String> userFcmCache = new ConcurrentHashMap<>();
    
    private SuperDao() {        
    }
    
    public static SuperDao getInstance() {
        return instance;
    }
    
    public void addRegistration(String regId, String accountName) {
        synchronized(mRegisteredUsers) {
            if (!mRegisteredUsers.contains(regId)) {
                mRegisteredUsers.add(regId);
            }
            if (accountName != null) {
                List<String> regIdList = mUserMap.get(accountName);
                if (regIdList == null) {
                    regIdList = new ArrayList<String>();
                    mUserMap.put(accountName, regIdList);
                }
                if (!regIdList.contains(regId)) {
                    regIdList.add(regId);
                }
            }
        }
    }
    
    public List<String> getAllRegistrationIds() {
        return Collections.unmodifiableList(mRegisteredUsers);
    }

    public List<String> getTokensForConversation(String conversationId) {
        return getAllRegistrationIds();
    }
    
    public List<String> getAllRegistrationIdsForAccount(String account) {
        List<String> regIds = mUserMap.get(account);
        if (regIds != null) {
           return Collections.unmodifiableList(regIds);
        }
        return null;
    }
    
    public String getNotificationKeyName(String accountName) {
        return mNotificationKeyMap.get(accountName);
    }
    
    public void storeNotificationKeyName(String accountName, String notificationKeyName) {
        mNotificationKeyMap.put(accountName, notificationKeyName);
    }
    
    public Set<String> getAccounts() {
        return Collections.unmodifiableSet(mUserMap.keySet());
    }
    
    public String getUniqueMessageId() {
        int nextRandom = sRandom.nextInt();
        while (mMessageIds.contains(nextRandom)) {
            nextRandom = sRandom.nextInt();
        }
        return Integer.toString(nextRandom);
    }
    
    public void addNewUser(String uId, String fcmId) {
        this.userFcmCache.put(uId, fcmId);
        logger.log(Level.INFO, "New user added, uId: "+ uId + ", fcmId: " + fcmId);
    }
    
    public String getUserFcmId(String uId) {
        return this.userFcmCache.get(uId);
    }
    
    public void updateUserFcmId(String uId, String newFcmId) {
        this.userFcmCache.replace(uId, newFcmId);
        logger.log(Level.INFO, "Update user fcmId, uId: "+ uId + ", new fcmId: "
                + newFcmId);
    }
    
    public void populateUserFcmCache() throws IOException, ParseException{
        String url = host + "/user/getAllUserIdAndFcmId";
        String fcmIdJson;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        
        BufferedReader in;
        in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        
        in.close();
        
        fcmIdJson = response.toString();
        logger.log(Level.INFO, "Populate the fcmId cache: "+ fcmIdJson);
        fcmIdsJsonParser(fcmIdJson, this.userFcmCache);
    }
    
    private void fcmIdsJsonParser (String fcmJson, ConcurrentHashMap<String, String> map) throws ParseException{
        JSONParser parser = new JSONParser();
        String uId;
        String fcmId;
        JSONObject obj = (JSONObject)parser.parse(fcmJson);
        if(obj.containsKey("usersFcmId")) {
            JSONArray array = (JSONArray)obj.get("usersFcmId");
            for (int i = 0; i < array.size(); i++) {
                JSONObject row = (JSONObject)array.get(i);
                Long longUId =(Long)row.get("uId");
                uId = String.valueOf(longUId.intValue());
                fcmId = (String)row.get("fcmId");
                map.put(uId, fcmId);
            }
        }
    }
}
