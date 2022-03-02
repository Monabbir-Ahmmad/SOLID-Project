package chat_app.server;

import chat_app.client.Constants;
import chat_app.interfaces.IConnection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ServerSocketInstance implements Runnable, IConnection {

    private final ArrayList<ServerSocketInstance> serverSocketInstances;

    private final Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private ServerSocketInstanceErrorListener errorListener;

    public ServerSocketInstance(Socket socket, ArrayList<ServerSocketInstance> serverSocketInstances) {
        this.serverSocketInstances = serverSocketInstances;
        this.socket = socket;
        createConnection();
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                receiveMessage();
            }

        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    @Override
    public void createConnection() {
        try {
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (dataInputStream != null) dataInputStream.close();

            if (dataOutputStream != null) dataOutputStream.close();

            if (socket != null) socket.close();


            if (errorListener != null)
                errorListener.onError(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage() throws IOException {
        byte[] fileContentBytes = null;

        String msgType = dataInputStream.readUTF();

        int userNameByteLen = dataInputStream.readInt();
        byte[] userNameBytes = new byte[userNameByteLen];

        if (userNameByteLen > 0) {
            dataInputStream.readFully(userNameBytes, 0, userNameByteLen);

            int msgBytesLength = dataInputStream.readInt();
            byte[] msgBytes = new byte[msgBytesLength];

            if (msgBytesLength > 0) dataInputStream.readFully(msgBytes, 0, msgBytesLength);

            if (msgType.equals(Constants.MESSAGE_TYPE_FILE)) {
                int fileContentLength = dataInputStream.readInt();
                if (fileContentLength > 0) {
                    fileContentBytes = new byte[fileContentLength];
                    dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
                }
            }

            broadcastMsg(msgType, new String(userNameBytes), new String(msgBytes), fileContentBytes);
        }
    }

    //Broadcast the msg to other clients except the one who sent it
    private void broadcastMsg(String messageType, String userName, String text, byte[] fileContentBytes) throws IOException {
        for (ServerSocketInstance socketInstance : serverSocketInstances) {
            if (socketInstance != this) {

                socketInstance.dataOutputStream.writeUTF(messageType);

                socketInstance.dataOutputStream.writeInt(userName.getBytes().length);
                socketInstance.dataOutputStream.write(userName.getBytes());

                socketInstance.dataOutputStream.writeInt(text.getBytes().length);
                socketInstance.dataOutputStream.write(text.getBytes());

                if (messageType.equals(Constants.MESSAGE_TYPE_FILE)) {
                    socketInstance.dataOutputStream.writeInt(fileContentBytes.length);
                    socketInstance.dataOutputStream.write(fileContentBytes);
                }
                socketInstance.dataOutputStream.flush();
            }

        }
    }

    public void setErrorListener(ServerSocketInstanceErrorListener errorListener) {
        this.errorListener = errorListener;
    }
}
