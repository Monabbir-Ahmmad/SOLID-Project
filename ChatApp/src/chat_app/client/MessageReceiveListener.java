package chat_app.client;

import chat_app.client.ReceivedFile;

public interface MessageReceiveListener {
    void onMessageReceive(String senderName, String message, String messageType, ReceivedFile receivedFile);
}
