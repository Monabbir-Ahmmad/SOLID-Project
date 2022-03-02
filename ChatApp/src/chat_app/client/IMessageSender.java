package chat_app.client;

import java.io.DataOutputStream;
import java.io.IOException;

public interface IMessageSender {
    void sendMessageType(DataOutputStream dataOutputStream) throws IOException;

    void sendUserName(DataOutputStream dataOutputStream, String userName) throws IOException;

    void sendMessage(DataOutputStream dataOutputStream) throws IOException;
}
