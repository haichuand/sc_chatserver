
package SuperCalyChatServer.processor;

import SuperCalyChatServer.processor.EchoProcessor;
import SuperCalyChatServer.processor.MessageProcessor;
import SuperCalyChatServer.processor.RegisterProcessor;

public class ProcessorFactory {
    
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_ECHO = "ECHO";
    public static final String ACTION_MESSAGE = "MESSAGE";
    public static final String ACTION_SHARE_EVENT = "SHARE_EVENT";
    public static final String ACTION_UPDATE_EVENT = "UPDATE_EVENT";
    public static final String ACTION_START_CONVERSATION= "START_CONVERSATION";
    public static final String ACTION_CONVERSATION_MESSAGE = "CONVERSATION_MESSAGE";
    public static final String ACTION_FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String ACTION_DROP_CONVERSATION_ATTENDEES = "DROP_CONVERSATION_ATTENDEES";
    public static final String ACTION_UPDATE_GCM_ID = "UPDATE_GCM_ID";
    public static final String ACTION_LEAVE_CONVERSATION = "LEAVE_CONVERSATION";
    public static final String ACTION_ADD_CONVERSATION_ATTENDEES = "ADD_CONVERSATION_ATTENDEES";
    public static final String ACTION_UPDATE_CONVERSATION_TITLE = "UPDATE_CONVERSATION_TITLE";

    public static PayloadProcessor getProcessor(String action) {
        if (action == null) {
            throw new IllegalStateException("action must not be null");
        }
        
        switch (action) {
            case ACTION_LOGIN:
                return new LoginProcessor();
            case ACTION_REGISTER:
                return new RegisterProcessor();
            case ACTION_ECHO:
                return new EchoProcessor();
            case ACTION_MESSAGE:
                return new MessageProcessor();
            case ACTION_SHARE_EVENT:
                return new ShareEventProcessor();
            case ACTION_UPDATE_EVENT:
                return new UpdateEventProcessor();
            case ACTION_START_CONVERSATION:
                return new StartConversationProcessor();
            case ACTION_CONVERSATION_MESSAGE:
                return new ConversationMessageProcessor();
            case ACTION_FRIEND_REQUEST:
                return new ShareEventProcessor();
            case ACTION_DROP_CONVERSATION_ATTENDEES:
                return new DropConversationAttendeesProcessor();
            case ACTION_UPDATE_GCM_ID:
                return new UpdateGcmIdProcessor();
            case ACTION_LEAVE_CONVERSATION:
                return new LeaveConversationProcessor();
            case ACTION_ADD_CONVERSATION_ATTENDEES:
                return new AddConversationAttendeesProcessor();
            case ACTION_UPDATE_CONVERSATION_TITLE:
                return new UpdateConversationTitleProcessor();
        }

        throw new IllegalStateException("Action " + action + " is unknown");
    }
}
