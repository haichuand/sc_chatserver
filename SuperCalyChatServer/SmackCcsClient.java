
package SuperCalyChatServer;

import SuperCalyChatServer.processor.PayloadProcessor;
import SuperCalyChatServer.processor.ProcessorFactory;
import SuperCalyChatServer.DAO.SuperDao;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.PacketExtension;  
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.ConnectionException;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.net.ssl.SSLSocketFactory;

/**
 * Sample Smack implementation of a client for GCM Cloud Connection Server. 
 * Most of it has been taken more or less verbatim from Googles 
 * documentation: http://developer.android.com/google/gcm/ccs.html
 * <br>
 * But some additions have been made. Bigger changes are annotated like that:
 * "/// new".
 * <br>
 * Those changes have to do with parsing certain type of messages
 * as well as with sending messages to a list of recipients. The original code
 * only covers sending one message to exactly one recipient.
 */
public class SmackCcsClient {

    public static final Logger logger = Logger.getLogger(SmackCcsClient.class.getName());

    public static final String GCM_SERVER = "gcm-xmpp.googleapis.com";
    public static final int GCM_PORT = 5235;

    public static final String GCM_ELEMENT_NAME = "gcm";
    public static final String GCM_NAMESPACE = "google:mobile:data";

    static Random random = new Random();
//    XMPPTCPConnection connection;
//    ConnectionConfiguration config;
    private static volatile SmackCcsClient instance;

    /// new: some additional instance and class members
    private static SmackCcsClient sInstance = null;
    private String mApiKey = null;
    private String mProjectId = null;
    private boolean mDebuggable = false;
    
    static {
        ProviderManager.addExtensionProvider(GCM_ELEMENT_NAME,
                GCM_NAMESPACE, new PacketExtensionProvider() {

                    @Override
                    public PacketExtension parseExtension(XmlPullParser parser)
                    throws Exception {
                        String json = parser.nextText();
                        GcmPacketExtension packet = new GcmPacketExtension(json);
                        return packet;
                    }
                });
    }

    private final Deque<Channel> channels;
    
    public static SmackCcsClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("You have to prepare the client first");
        }
        return instance;
    }


    public static SmackCcsClient prepareClient(String projectId, String apiKey, boolean debuggable) {
        if (instance == null) {
            synchronized (SmackCcsClient.class) {
                if (instance == null) {
                    instance = new SmackCcsClient(projectId, apiKey, debuggable);
                }
            }
        }
        return instance;
    }

    private SmackCcsClient(String projectId, String apiKey, boolean debuggable) {
        mApiKey = apiKey;
        mProjectId = projectId;
        mDebuggable = debuggable;
        channels = new ConcurrentLinkedDeque<Channel>();
    }
    
    public void getConnected() throws XMPPException, SmackException, IOException {
        channels.addFirst(connect());
    }
    
    /**
     * XMPP Packet Extension for GCM Cloud Connection Server.
     */
    private static final class GcmPacketExtension extends DefaultPacketExtension {

        String json;

        public GcmPacketExtension(String json) {
            super(GCM_ELEMENT_NAME, GCM_NAMESPACE);
            this.json = json;
        }

        public String getJson() {
            return json;
        }

        @Override
        public String toXML() {
            return String.format("<%s xmlns=\"%s\">%s</%s>", GCM_ELEMENT_NAME,
                    GCM_NAMESPACE, json, GCM_ELEMENT_NAME);
        }

        @SuppressWarnings("unused")
        public Packet toPacket() {
            return new Message() {
                // Must override toXML() because it includes a <body>
                @Override
                public XmlStringBuilder toXML() {

                    XmlStringBuilder buf = new XmlStringBuilder();
                    buf.append("<message");
                    if (getXmlns() != null) {
                        buf.append(" xmlns=\"").append(getXmlns()).append("\"");
                    }
                    if (getLanguage() != null) {
                        buf.append(" xml:lang=\"").append(getLanguage()).append("\"");
                    }
                    if (getPacketID() != null) {
                        buf.append(" id=\"").append(getPacketID()).append("\"");
                    }
                    if (getTo() != null) {
                        buf.append(" to=\"").append(StringUtils.escapeForXML(getTo())).append("\"");
                    }
                    if (getFrom() != null) {
                        buf.append(" from=\"").append(StringUtils.escapeForXML(getFrom())).append("\"");
                    }
                    buf.append(">");
                    buf.append(GcmPacketExtension.this.toXML());
                    buf.append("</message>");
                    return buf;
                }
            };
        }
    }
    
    
    private class Channel {
        private XMPPConnection connection;
        /**
         * Indicates whether the connection is in draining state, which means that it will not accept any new downstream
         * messages.
         */
        private volatile boolean connectionDraining = false;

        /**
         * Sends a packet with contents provided.
         */
        private void send(String jsonRequest){
            Packet request = new GcmPacketExtension(jsonRequest).toPacket();
            try{
                connection.sendPacket(request);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Handles a CONNECTION_DRAINING control message.
         *
         * <p>
         * By default, it only logs a INFO message, but subclasses could
         * override it to properly handle NACKS.
         */
        public void handleConnectionDraining(Map<String, Object> jsonObject) {
            connectionDraining = true;
            logger.log(Level.INFO, "handleConnectionDraining()");
        }
    }
    
        public String getRandomMessageId() {
        return "MessageFromServer-" + Long.toString(System.currentTimeMillis())
                + Long.toString(random.nextLong());
        }

    /**
     * Sends a downstream GCM message.
     */
    public void send(String message){
        Channel channel = channels.peekFirst();
        if (channel.connectionDraining) {
            synchronized (channels) {
                channel = channels.peekFirst();
                if (channel.connectionDraining) {
                    channels.addFirst(connect());
                    channel = channels.peekFirst();
                }
            }
        }
        channel.send(message);
    }

    /// new: for sending messages to a list of recipients
    /**
     * Sends a message to multiple recipients. Kind of like the old HTTP message
     * with the list of regIds in the "registration_ids" field.
     */
    public void sendBroadcast(Map<String, String> payload, String collapseKey,
            long timeToLive, Boolean delayWhileIdle, List<String> recipients){
        Map map = createAttributeMap(null, null, payload, collapseKey,
                timeToLive, delayWhileIdle);
        for (String toRegId : recipients) {
            String messageId = getRandomMessageId();
            map.put("message_id", messageId);
            map.put("to", toRegId);
            String jsonRequest = createJsonMessage(map);
            send(jsonRequest);
        }
    }

    /// new: customized version of the standard handleIncomingDateMessage method
    /**
     * Handles an upstream data message from a device application.
     */
    public void handleIncomingDataMessage(CcsMessage msg) {
        if (msg.getPayload().get("action") != null) {
            PayloadProcessor processor = ProcessorFactory.getProcessor((String) msg.getPayload().get("action"));
            processor.handleMessage(msg);
        }
    }

    /// new: was previously part of the previous method
    /**
     *
     */
    private CcsMessage getMessage(Map<String, Object> jsonObject) {
        String from = jsonObject.get("from").toString();

        // PackageName of the application that sent this message.
        String category = jsonObject.get("category").toString();

        // unique id of this message
        String messageId = jsonObject.get("message_id").toString();

        @SuppressWarnings("unchecked")
        Map<String, String> payload = (Map<String, String>) jsonObject.get("data");

        CcsMessage msg = new CcsMessage(from, category, messageId, payload);

        return msg;
    }

    /**
     * Handles an ACK.
     *
     * <p>
     * By default, it only logs a INFO message, but subclasses could override it
     * to properly handle ACKS.
     */
    public void handleAckReceipt(Map<String, Object> jsonObject) {
        String messageId = jsonObject.get("message_id").toString();
        String from = jsonObject.get("from").toString();
        logger.log(Level.INFO, "handleAckReceipt() from: " + from + ", messageId: "
                + messageId);
    }

    /**
     * Handles a NACK.
     *
     * <p>
     * By default, it only logs a INFO message, but subclasses could override it
     * to properly handle NACKS.
     */
    public void handleNackReceipt(Map<String, Object> jsonObject) {
        String messageId = jsonObject.get("message_id").toString();
        String from = jsonObject.get("from").toString();
        String error = jsonObject.get("error").toString();
        String errorDescription = jsonObject.get("error_description").toString();
        logger.log(Level.INFO, "handleNackReceipt() from: " + from + ", messageId: "
                + messageId + ", error: " + error + ", errorDescription: " + errorDescription);
    }

    /**
     * Creates a JSON encoded GCM message.
     *
     * @param to RegistrationId of the target device (Required).
     * @param messageId Unique messageId for which CCS will send an "ack/nack"
     * (Required).
     * @param payload Message content intended for the application. (Optional).
     * @param collapseKey GCM collapse_key parameter (Optional).
     * @param timeToLive GCM time_to_live parameter (Optional).
     * @param delayWhileIdle GCM delay_while_idle parameter (Optional).
     * @return JSON encoded GCM message.
     */
    public static String createJsonMessage(String to, String messageId, Map<String, String> payload,
            String collapseKey, Long timeToLive, Boolean delayWhileIdle) {
        return createJsonMessage(createAttributeMap(to, messageId, payload,
                collapseKey, timeToLive, delayWhileIdle));
    }

    public static String createJsonMessage(Map map) {
        return JSONValue.toJSONString(map);
    }

    public static Map createAttributeMap(String to, String messageId, Map<String, String> payload,
            String collapseKey, Long timeToLive, Boolean delayWhileIdle) {
        Map<String, Object> message = new HashMap<String, Object>();
        if (to != null) {
            message.put("to", to);
        }
        if (collapseKey != null) {
            message.put("collapse_key", collapseKey);
        }
        if (timeToLive != null) {
            message.put("time_to_live", timeToLive);
        }
        if (delayWhileIdle != null && delayWhileIdle) {
            message.put("delay_while_idle", true);
        }
        if (messageId != null) {
            message.put("message_id", messageId);
        }
        message.put("data", payload);
        return message;
    }

    /**
     * Creates a JSON encoded ACK message for an upstream message received from
     * an application.
     *
     * @param to RegistrationId of the device who sent the upstream message.
     * @param messageId messageId of the upstream message to be acknowledged to
     * CCS.
     * @return JSON encoded ack.
     */
    public static String createJsonAck(String to, String messageId) {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("message_type", "ack");
        message.put("to", to);
        message.put("message_id", messageId);
        return JSONValue.toJSONString(message);
    }

    /// new: NACK added
    /**
     * Creates a JSON encoded NACK message for an upstream message received from
     * an application.
     *
     * @param to RegistrationId of the device who sent the upstream message.
     * @param messageId messageId of the upstream message to be acknowledged to
     * CCS.
     * @return JSON encoded nack.
     */
    public static String createJsonNack(String to, String messageId) {
        Map<String, Object> message = new HashMap<String, Object>();
        message.put("message_type", "nack");
        message.put("to", to);
        message.put("message_id", messageId);
        return JSONValue.toJSONString(message);
    }

    private void handleMessage(Map<String, Object> jsonMap, Channel channel) {
        // present for "ack"/"nack", null otherwise
        Object messageType = jsonMap.get("message_type");

        if (messageType == null) {
            CcsMessage msg = getMessage(jsonMap);
            // Normal upstream data message
            try {
                handleIncomingDataMessage(msg);
                // Send ACK to CCS
                String ack = createJsonAck(msg.getFrom(), msg.getMessageId());
                send(ack);
            } catch (Exception e) {
                // Send NACK to CCS
                e.printStackTrace();
                String nack = createJsonNack(msg.getFrom(), msg.getMessageId());
                send(nack);
            }
        } else if ("ack".equals(messageType.toString())) {
            // Process Ack
            handleAckReceipt(jsonMap);
        } else if ("nack".equals(messageType.toString())) {
            // Process Nack
            handleNackReceipt(jsonMap);
        } else if("control".equals(messageType.toString())) {
            //Process control message
            Object controlType = jsonMap.get("control_type");
            if("CONNECTION_DRAINING".equals(controlType.toString()))
                channel.handleConnectionDraining(jsonMap);
        } 
        else {
            logger.log(Level.WARNING, "Unrecognized message type (%s)",
                    messageType.toString());
        }
    }

    /**
     * Connects to GCM Cloud Connection Server using the supplied credentials.
     *
     * @return
     */
    public Channel connect() {
        try {
            final Channel channel = new Channel();
            ConnectionConfiguration config = new ConnectionConfiguration(GCM_SERVER, GCM_PORT);
            config.setSecurityMode(SecurityMode.enabled);
            config.setReconnectionAllowed(true);
            config.setRosterLoadedAtLogin(false);
            config.setSendPresence(false);
            config.setSocketFactory(SSLSocketFactory.getDefault());

            channel.connection = new XMPPTCPConnection(config);
            channel.connection.connect();

            channel.connection.addConnectionListener(new ConnectionListener() {
                @Override
                public void authenticated(XMPPConnection connection) {
                    logger.info(connection.toString());
                }

                @Override
                public void connected(XMPPConnection connection) {
                    logger.log(Level.INFO, "Connection connected.");
                }

                @Override
                public void reconnectionSuccessful() {
                    logger.info("Reconnecting..");
                }

                @Override
                public void reconnectionFailed(Exception e) {
                    logger.log(Level.INFO, "Reconnection failed.. ", e);
                }

                @Override
                public void reconnectingIn(int seconds) {
                    logger.log(Level.INFO, "Reconnecting in %d secs", seconds);
                }

                @Override
                public void connectionClosedOnError(Exception e) {
                    logger.log(Level.INFO, "Connection closed on error.");
                }

                @Override
                public void connectionClosed() {
                    logger.info("Connection closed.");
                }
            });

            // Handle incoming packets
            channel.connection.addPacketListener(new PacketListener() {

                @Override
                public void processPacket(Packet packet) {
                    logger.log(Level.INFO, "Received: " + packet.toXML());
                    System.out.println("new message is coming");
                    Message incomingMessage = (Message) packet;
                    GcmPacketExtension gcmPacket
                            = (GcmPacketExtension) incomingMessage.getExtension(GCM_NAMESPACE);
                    String json = gcmPacket.getJson();
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> jsonMap
                                = (Map<String, Object>) JSONValue.parseWithException(json);

                        handleMessage(jsonMap, channel);
                    } catch (ParseException e) {
                        logger.log(Level.SEVERE, "Error parsing JSON " + json, e);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Couldn't send echo.", e);
                    }
                }
            }, new PacketTypeFilter(Message.class));

            // Log all outgoing packets
            channel.connection.addPacketInterceptor(new PacketInterceptor() {
                @Override
                public void interceptPacket(Packet packet) {
                    logger.log(Level.INFO, "Sent: {0}", packet.toXML());
                }
            }, new PacketTypeFilter(Message.class));

            channel.connection.login(mProjectId + "@gcm.googleapis.com", mApiKey);
            logger.log(Level.INFO, "logged in: " + mProjectId);
            
            return channel;
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in creating channel for GCM communication", e);
            throw new RuntimeException(e);
        }
    }
    
}