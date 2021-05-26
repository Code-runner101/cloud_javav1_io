package io;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import model.Message;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    protected final Socket socket;
    protected final Server server;
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    private String nickname;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void init () throws IOException {
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void run() {
        try {
            init();
            String str = inputStream.readUTF();
            if (str.startsWith("/"))
                if (str.startsWith(Message.AUTH)){
                    String[] token = str.split("\\s");
                    String newNick = server.getAuthService()
                            .getNicknameByLoginAndPassword(token[1], token[2]);
                    if (newNick != null){
                        nickname = newNick;
                        sendMessage(Message.AUTH_OK + " " + nickname);
                        server.subscribe(this);
                    }
                }
            System.out.println("connected");
            while (true) {
                String message = inputStream.readUTF();
//                System.out.println("server:" + message);
                server.broadcast(message);
            }
        } catch (IOException e) {
            System.err.println("connection lost");
            server.kickClient(this);
        } catch (Exception e) {
            System.err.println("client couldn't auth");
        }
    }

    public void sendMessage(String message) throws IOException {
        outputStream.writeUTF(message);
        outputStream.flush();
    }


    public void close() throws IOException {
        outputStream.close();
        inputStream.close();
        socket.close();
        System.out.println("streams were closed");
    }

    public String getNickname() {
        return nickname;
    }
}
