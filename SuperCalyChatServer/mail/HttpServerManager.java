package SuperCalyChatServer.mail;

import SuperCalyChatServer.CcsMessage;
import SuperCalyChatServer.DAO.SuperDao;
import SuperCalyChatServer.model.Conversation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages calls to http server to get user, event and conversation info
 * Also maintains userId -> name map
 * Created by haichuand on 10/15/2016.
 */
public class HttpServerManager {
    public static final String REST_URL = "http://52.25.71.19:8080/SuperCaly/rest/";
    public static final String GET_EVENT_URL = REST_URL + "event/getEvent/";
    public static final String GET_CONVERSATION_URL = REST_URL + "conversation/getConversation/";
    public static final String GET_USER_BY_ID_URL = REST_URL + "user/basicInfo/";
    public static final String GET_USER_BY_EMAIL_URL = REST_URL + "user/getUserByEmail/";
    public static final String GET_CONVERSATION_ATTENDEES_ID_URL = REST_URL + "conversation/conversationAttendeesId/";

    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "firstName";
    public static final String FCM_ID = "fcmId";
    public static final String LAST_NAME = "lastName";
    public static final String MEDIA_ID = "mediaId";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final String UID = "uId";
    public static final String EVENT_ID = "eventId";
    public static final String EVENT_TYPE = "eventType";
    public static final String TITLE = "title";
    public static final String LOCATION = "location";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String CREATOR_ID = "creatorId";
    public static final String CREATE_TIME = "createTime";
    public static final String ATTENDEES_ID = "attendeesId";
    public static final String STATUS = "status";
    public static final String CONVERSATION_ID = "conversationId";
    public static final String C_ID = "cId";

    public static final String EMAIL_SENDER = "SuperCaly Team";
    public static final String EMAIL_DOMAIN = "@supercaly.com";

    private static Map<String, String> userIdNameMap = new HashMap<>(); //map of userId -> name
    private static Map<String, String> userEmailIdMap = new HashMap<>(); //map of email -> userId
    private SuperDao dao = SuperDao.getInstance();

    public Conversation getEventConversation (String eventId) {
        Conversation conversation = new Conversation();
        try {
            JSONObject eventObj = queryServer(GET_EVENT_URL + eventId, "GET", null);
            if (eventObj == null) {
                return null;
            }
            conversation.id = eventObj.getString(CONVERSATION_ID);
            conversation.startTime = eventObj.getLong(START_TIME);
            conversation.endTime = eventObj.getLong(END_TIME);
            conversation.eventId = eventId;
            conversation.eventTitle = eventObj.getString(TITLE);
            JSONObject conversationObj = queryServer(GET_CONVERSATION_URL + conversation.id, "GET", null);
            if (conversationObj == null) {
                return null;
            }
            conversation.title = conversationObj.getString(TITLE);
            List<String> idList = new ArrayList<>();
            org.json.JSONArray idArray = conversationObj.getJSONArray(ATTENDEES_ID);
            for (int i = 0; i < idArray.length(); i++) {
                idList.add(String.valueOf(idArray.getInt(i)));
            }
            conversation.attendeesId = idList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return conversation;
    }

    public Conversation getConversation (String conversationId) {
        JSONObject conversationObj;
        Conversation conversation = new Conversation();
        try {
            conversationObj = queryServer(GET_CONVERSATION_URL + conversationId, "GET", null);
            if (conversationObj == null) {
                return null;
            }
            conversation.id = conversationObj.getString(C_ID);
            conversation.title = conversationObj.getString(TITLE);
            List<String> idList = new ArrayList<>();
            org.json.JSONArray idArray = conversationObj.getJSONArray(ATTENDEES_ID);
            for (int i = 0; i < idArray.length(); i++) {
                idList.add(String.valueOf(idArray.getInt(i)));
            }
            conversation.attendeesId = idList;
            return conversation;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getConversationTitle (String conversationId) {
        try {
            JSONObject conversationObj = queryServer(GET_CONVERSATION_URL + conversationId, "GET", null);
            if (conversationObj != null) {
                return conversationObj.getString(TITLE);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String getUserIdByEmail (String email) {
        if (userEmailIdMap.containsKey(email)) {
            return userEmailIdMap.get(email);
        }

        try {
            JSONObject userObject = queryServer(GET_USER_BY_EMAIL_URL + email, "GET", null);
            if (userObject == null) {
                return null;
            }
            String userId = String.valueOf(userObject.getInt(UID));
            userEmailIdMap.put(email, userId);
            return userId;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String getUserName (String userId) {
        if (userIdNameMap.containsKey(userId)) {
            return userIdNameMap.get(userId);
        }

        try {
            JSONObject userObject = queryServer(GET_USER_BY_ID_URL + userId, "GET", null);
            if (userObject == null) {
                return null;
            }
            String name = userObject.getString(FIRST_NAME) + " " + userObject.getString(LAST_NAME);
            if (name.trim().isEmpty()) {
                name = userObject.getString(USER_NAME);
            }
            userIdNameMap.put(userId, name);
            return name;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Get user's fcmId from http server
     * @param userId
     * @return
     */
    public String getUserFcmIdFromHttpServer(String userId) {
//        System.out.println("getUserFcmIdFromHttpServer(" + userId + ")");
        try {
            JSONObject userObject = queryServer(GET_USER_BY_ID_URL + userId, "GET", null);
            if (userObject != null) {
//                System.out.println(userObject.toString());
                return userObject.getString(CcsMessage.FCM_ID);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String getAttendeeNames (Conversation conversation) {
        if (conversation.attendeesId == null) {
            return null;
        }
        String names = "";
        for (String id : conversation.attendeesId) {
            names += getUserName(id) + ", ";
        }
        return names.substring(0, names.lastIndexOf(','));
    }

    public List<String> getConversationAttendeesFcmId (String conversationId) {
        List<String> attendeesFcmId = new ArrayList<>();
        try {
            JSONObject object = queryServer(GET_CONVERSATION_ATTENDEES_ID_URL + conversationId, "GET", null);
            if (object != null) {
                JSONArray idArray = object.getJSONArray(ATTENDEES_ID);
                for (int i = 0; i < idArray.length(); i++) {
                    attendeesFcmId.add(dao.getUserFcmId(idArray.get(i).toString()));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return attendeesFcmId;
    }

    private JSONObject queryServer(String urlString, String method, JSONObject data) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        if ("POST".equals(method) && data != null) {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(data.toString());
            writer.close();
        }

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();

            return new JSONObject(builder.toString());
        }
        System.out.println("Resonsecode: "+ responseCode + "; Message: " + connection.getResponseMessage());
        return null;
    }
}
