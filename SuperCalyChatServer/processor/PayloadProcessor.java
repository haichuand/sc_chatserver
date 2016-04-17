
package SuperCalyChatServer.processor;

import SuperCalyChatServer.CcsMessage;


public interface PayloadProcessor {
    
    void handleMessage(CcsMessage msg);
    
}
