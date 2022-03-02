package chat_app;

import chat_app.ui.ChatUI;

public class ClientStart {

    public static void main(String[] args) {
        Thread thread = new Thread(() -> new ChatUI("User"));
        thread.start();
    }
}
