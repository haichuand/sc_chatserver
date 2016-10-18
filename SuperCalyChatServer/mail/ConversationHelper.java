package SuperCalyChatServer.mail;

import SuperCalyChatServer.model.Conversation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Helper class to get conversation strings for sending email
 * Created by haichuand on 10/15/2016.
 */
public class ConversationHelper {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("M/d/yy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());
    private static final SimpleDateFormat WEEKDAY_FORMAT = new SimpleDateFormat("EEE", Locale.getDefault());

    /**
     * Reply-To header value is used for identification when user replies
     * @param conversationId
     * @return
     */
    public static String getConversationReplyTo(String conversationId) {
        return HttpServerManager.EMAIL_SENDER + " <" +
                HttpServerManager.CONVERSATION_ID + "=" + conversationId + HttpServerManager.EMAIL_DOMAIN + ">";
    }

    public static String getDateTimeString (Conversation conversation) {
        if(conversation.startTime == 0 || conversation.endTime == 0) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(conversation.startTime);
        Date dt = calendar.getTime();
        String date = DATE_FORMAT.format(dt);
        String start = TIME_FORMAT.format(dt);
        String weekDay = WEEKDAY_FORMAT.format(dt);

        calendar.setTimeInMillis(conversation.endTime);
        String end = TIME_FORMAT.format(calendar.getTime());

        return String.format("%s, %s from %s to %s", weekDay, date, start, end);
    }
}
