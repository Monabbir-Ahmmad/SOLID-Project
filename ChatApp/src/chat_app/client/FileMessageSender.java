package chat_app.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileMessageSender implements IMessageSender{

    private final File file;

    public FileMessageSender( File file) {
        this.file = file;
    }

    @Override
    public void sendMessageType(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(Constants.MESSAGE_TYPE_FILE);

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
        FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());

        byte[] fileContentBytes = new byte[(int) file.length()];
        fileInputStream.read(fileContentBytes);

        //Send file name
        dataOutputStream.writeInt(file.getName().getBytes().length);
        dataOutputStream.write(file.getName().getBytes());

        //Send file content
        dataOutputStream.writeInt(fileContentBytes.length);
        dataOutputStream.write(fileContentBytes);

        dataOutputStream.flush();
        fileInputStream.close();

        System.out.println("File sent");
    }
}
