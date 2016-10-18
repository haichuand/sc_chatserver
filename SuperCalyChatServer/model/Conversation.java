package SuperCalyChatServer.model;

import java.util.List;

/**
 * Created by haichuand on 10/14/2016.
 */
public class Conversation {

    public String id;
    public String title;
    public long startTime;
    public long endTime;
    public List<String> attendeesId;
    public String eventId;
    public String eventTitle;

}
