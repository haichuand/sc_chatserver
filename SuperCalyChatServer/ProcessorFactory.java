
package SuperCalyChatServer;

public class ProcessorFactory {
    
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_ECHO = "ECHO";
    public static final String ACTION_MESSAGE = "MESSAGE";

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
        }

        throw new IllegalStateException("Action " + action + " is unknown");
    }
}
