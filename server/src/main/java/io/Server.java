package io;

import authorization.AuthService;
import authorization.SimpleAuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;

public class Server {
    private final LinkedList <ClientHandler> clients;
    private AuthService authService;

    public Server(int port) throws SQLException, ClassNotFoundException {
        clients = new LinkedList<ClientHandler>();
        authService = new SimpleAuthService();
        try {
            ServerSocket server = new ServerSocket(port);
            System.out.println("server started");
            while (true){
                Socket socket = server.accept();
                System.out.println("client entry log");
                ClientHandler handler = new FileSender(socket, this);
            }
        } catch (Exception e){
            System.err.println("server crash");
        }
    }

    public void subscribe(ClientHandler handler){
        clients.add(handler);
        new Thread(handler).start();
    }

    public void broadcast(String message) throws IOException {
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }

    public void kickClient(ClientHandler handler){
        clients.remove(handler);
    }

    public AuthService getAuthService(){
        return authService;
    }

}
