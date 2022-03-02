package chat_app.client;

import java.io.DataOutputStream;
import java.io.IOException;

public class TextMessageSender implements IMessageSender {

    private final String  text;

    public TextMessageSender( String text) {
        this.text = text;
    }

    @Override
    public void sendMessageType(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(Constants.MESSAGE_TYPE_TEXT);

        System.out.println("Message type sent");

    }

    @Override
    public void sendUserName(DataOutputStream dataOutputStream, String userName) throws IOException {
        dataOutputStream.writeInt(userName.getBytes().length);
        dataOutputStream.write(userName.getBytes());

        System.out.println("User name sent");
    }

    @Override
    public void sendMessage(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(text.getBytes().length);
        dataOutputStream.write(text.getBytes());

        dataOutputStream.flush();

        System.out.println("Text message sent");
    }
}
