package SuperCalyChatServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginProcessor implements PayloadProcessor {

    public static final String ACTION = "action";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String STATUS = "status";
    
    public static final int STATUS_OK = 0;
    public static final int STATUS_FAILED = 1;

    @Override
    public void handleMessage(CcsMessage msg) {
        PseudoDao dao = PseudoDao.getInstance();
        SmackCcsClient client = SmackCcsClient.getInstance();

        List<String> recipients = new ArrayList<>();
        recipients.add(msg.getFrom());

        String collapseKey = "sample";
        Long timeToLive = 10000L;
        Boolean delayWhileIdle = true;

        int status = STATUS_OK;

        String username = msg.getPayload().get(USERNAME);
        String password = msg.getPayload().get(PASSWORD);

        if (!password.equals(password)) {
            status = STATUS_FAILED;
        }

        if (status == STATUS_OK) {
            dao.addRegistration(msg.getFrom(), username);
        }

        Map<String, String> payload = new HashMap<>();
        payload.put(ACTION, ProcessorFactory.ACTION_LOGIN);
        payload.put(STATUS, String.valueOf(status));
        payload.put(USERNAME, username);

        client.sendBroadcast(payload, collapseKey, timeToLive, delayWhileIdle,
            recipients);
    }
}