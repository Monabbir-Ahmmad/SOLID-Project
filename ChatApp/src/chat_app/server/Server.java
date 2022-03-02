package chat_app.server;

import chat_app.interfaces.IConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements IConnection {
    private static final ArrayList<ServerSocketInstance> serverSocketInstances = new ArrayList<>();

    private final ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public void createConnection() {
        System.out.println("Server is running");

        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();

                ServerSocketInstance serverSocketInstance = new ServerSocketInstance(socket, serverSocketInstances);
                serverSocketInstances.add(serverSocketInstance);

                serverSocketInstance.setErrorListener(instance -> {
                    serverSocketInstances.remove(instance);
                    System.out.println("A client has left the chat. Total clients: " + serverSocketInstances.size());

                });

                Thread thread = new Thread(serverSocketInstance);
                thread.start();

                System.out.println("A new client has connected. Total clients: " + serverSocketInstances.size());
            }

        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    @Override
    public void closeConnection() {
        System.out.println("Server has stopped working");

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
