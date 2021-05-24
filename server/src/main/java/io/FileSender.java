package io;

import authorization.AuthService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSender extends ClientHandler {

    protected String serverPath = "server/sentFiles";
    protected String clientPath = "client/clientFiles";
    private byte buffer[];
//    private static int count;

    public FileSender(Socket socket, Server server) throws IOException {
        super(socket, server);
        buffer = new byte[256];
//        count++;

        serverPath = serverPath + "/" + getNickname();
        if (!Files.exists(Paths.get(serverPath))) {
            Files.createDirectory(Paths.get(serverPath));
        }
    }

        @Override
        public void run () {

            try {

                init();

                while (true) {

                    String fileName = inputStream.readUTF();
                    System.out.println("file name: " + fileName);
                    long fileLength = inputStream.readLong();
                    System.out.println("file length: " + fileLength);
                    String serverFilePath = serverPath + "/" + fileName;
                    String clientFilePath = clientPath + "/" + fileName;

                    try (FileOutputStream fileOutputStream = new FileOutputStream(serverFilePath)) {
                        for (int i = 0; i < (fileLength + 255) / 256; i++) {
                            int read = inputStream.read(buffer);
                            fileOutputStream.write(buffer, 0, read);
                        }
                    } catch (Exception e){
                        outputStream.writeUTF("Error");
                        outputStream.flush();
                    }
                        outputStream.writeUTF("OK");
                        outputStream.flush();
                        System.out.println("file was successfully uploaded on the cloud");

                    try (FileOutputStream fileOutputStream = new FileOutputStream(clientFilePath)) {
                        for (int i = 0; i < (fileLength + 255) / 256; i++) {
                            int read = inputStream.read(buffer);
                            fileOutputStream.write(buffer, 0, read);
                        }
                    } catch (Exception e) {
                        outputStream.writeUTF("Error");
                        outputStream.flush();
                    }
                    outputStream.writeUTF("OK");
                    outputStream.flush();
                    System.out.println("file was successfully uploaded from the cloud");
                }
            } catch (Exception e) {
                System.err.println("connection lost");
                try {
                    close();
                } catch (Exception t) {
                    System.err.println("exception caused by closing");
                }
            }
        }
    }
