/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SuperCalyChatServer;

import java.util.Map;

/**
 * Represents a message for CCS based massaging.
 */
public class CcsMessage {
    //fields in Payload Json
    public static final String SENDER_ID = "senderId";
    public static final String CONVERSATION_ID = "conversationId";
    public static final String EVENT_ID = "eventId";
    public static final String RECIPIENTS = "recipients";
    public static final String ACTION = "action";
    public static final String MESSAGE = "message";
    public static final String CREATOR_ID = "creatorId";
    public static final String MEDIA_ID = "mediaId";
    public static final String GCM_ID = "gcmId";
    public static final String TARGET_USER_ID = "targetUserId";
    public static final String TITLE = "title";   
    public static final String USER_IDS = "userIds";
    public static final String ATTACHMENTS = "attachments";
    
    /**
     * Recipient-ID.
     */
    private String mFrom;
    /**
     * Sender app's package.
     */
    private String mCategory;
    /**
     * Unique id for this message.
     */
    private String mMessageId;
    /**
     * Payload data. A String in Json format.
     */
    private Map<String, String> mPayload;

    public CcsMessage(String from, String category, String messageId, Map<String, String> payload) {
        mFrom = from;
        mCategory = category;
        mMessageId = messageId;
        mPayload = payload;
    }
    
    public String getFrom() {
        return mFrom;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getMessageId() {
        return mMessageId;
    }

    public Map<String, String> getPayload() {
        return mPayload;
    }
}
