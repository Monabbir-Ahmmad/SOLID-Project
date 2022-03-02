package chat_app.client;

import java.io.*;
import java.net.Socket;

public class ClientController implements Runnable {
    private final String clientName;
    private final ClientConnectionController clientConnectionController;

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    private int fileID = 0;

    private MessageReceiveListener messageReceiveListener;

    public ClientController(String clientName, ClientConnectionController clientConnectionController) {
        this.clientName = clientName;
        this.clientConnectionController = clientConnectionController;
    }

    @Override
    public void run() {
        clientConnectionController.setConnectListener(() -> {
            socket = clientConnectionController.getSocket();
            dataOutputStream = clientConnectionController.getDataOutputStream();
            dataInputStream = clientConnectionController.getDataInputStream();
        });

        clientConnectionController.createConnection();

        try {
            while (socket.isConnected()) {
                receiveMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            clientConnectionController.closeConnection();
        }
    }

    public boolean sendMsg(IMessageSender messageSender) {
        try {
            messageSender.sendMessageType(dataOutputStream);
            messageSender.sendUserName(dataOutputStream, clientName);
            messageSender.sendMessage(dataOutputStream);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            clientConnectionController.closeConnection();
        }

        return false;
    }

    private void receiveMessage() throws IOException {
        ReceivedFile receivedFile = null;

        String msgType = dataInputStream.readUTF();

        int userNameByteLen = dataInputStream.readInt();
        byte[] userNameBytes = new byte[userNameByteLen];

        if (userNameByteLen > 0) dataInputStream.readFully(userNameBytes, 0, userNameByteLen);

        int msgBytesLength = dataInputStream.readInt();
        byte[] msgBytes = new byte[msgBytesLength];

        if (msgBytesLength > 0) dataInputStream.readFully(msgBytes, 0, msgBytesLength);

        if (msgType.equals(Constants.MESSAGE_TYPE_FILE)) {
            int fileContentLength = dataInputStream.readInt();
            if (fileContentLength > 0) {
                byte[] fileContentBytes = new byte[fileContentLength];
                dataInputStream.readFully(fileContentBytes, 0, fileContentLength);

                receivedFile = new ReceivedFile(fileID, new String(msgBytes), fileContentBytes);
                fileID++;
            }
        }

        if (messageReceiveListener != null) {
            messageReceiveListener.onMessageReceive(new String(userNameBytes), new String(msgBytes), msgType, receivedFile);
        }
    }

    public void setMessageReceiveListener(MessageReceiveListener listener) {
        this.messageReceiveListener = listener;
    }

}
