import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.Builder;
import model.Message;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    private String clientPath = "client/clientFiles";
    private String serverPath = "server/sentFiles";

    @FXML
    public ListView<String> listView;
    @FXML
    public ListView<String> listViewCloud;
    @FXML
    public Button sendIn;
    @FXML
    public Button sendOut;
    @FXML
    public TextField input;
    @FXML
    private TextField loginFiled;
    @FXML
    private PasswordField passwordField;
    @FXML
    private AnchorPane sendingPanel;
    @FXML
    private AnchorPane authPanel;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private byte[] buffer = new byte[256];
    private String nickname;
    private boolean auth;
    int count;

    public Controller() throws IOException {
    }

    public void setAuthenticated(boolean auth){
        this.auth = auth;
        sendingPanel.setVisible(auth);
        sendingPanel.setManaged(auth);
        authPanel.setVisible(!auth);
        authPanel.setManaged(!auth);

//        if (!auth){
//            nickname = "";
//        }

    }

    public void sendOn(javafx.event.ActionEvent actionEvent) throws IOException {
        String fileName = listView.getSelectionModel().getSelectedItem();
        out.writeUTF(fileName);

        long len = Files.size(Paths.get(clientPath, fileName));
        out.writeLong(len);

        try (FileInputStream fileInputStream = new FileInputStream(clientPath + "/" + fileName)) {
            int read;
            while (true) {
                read = fileInputStream.read(buffer);
                if (read == -1) {
                    break;
                }

                out.write(buffer, 0, read);
            }
        }
        out.flush();

        listView.refresh();
        listViewCloud.refresh();
    }

//Здесь попытался реализовать функцию скачивания файла с облака
    public void sendOut(javafx.event.ActionEvent actionEvent) throws IOException {
//        count++;

        String fileName = listViewCloud.getSelectionModel().getSelectedItem();
        out.writeUTF(fileName);

        long len = Files.size(Paths.get(serverPath, fileName));
        out.writeLong(len);

        try (FileInputStream fin = new FileInputStream(serverPath + fileName)) {
            int read;
            while (true) {
                read = fin.read(buffer);
                if (read == -1) {
                    break;
                }

                out.write(buffer, 0, read);
            }
        }
        out.flush();

        listView.refresh();
        listViewCloud.refresh();
    }

    private void init() throws IOException {
        socket = new Socket("localhost", 8899);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void connect() {
        try {
            init();
//            new Thread(() -> {
//                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.startsWith("/")) {
                            if (str.startsWith(Message.AUTH_OK)) {
                                nickname = str.split("\\s")[1];
                                setAuthenticated(true);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("command error");
                }
//                finally {
//                    setAuthenticated(true);
//                }
//            }).start();

//        } catch (IOException e) {
//            System.err.println("connection false");
        }


    public void tryToAuth(javafx.event.ActionEvent actionEvent) throws IOException {
        if (socket == null || socket.isClosed()) {
            connect();
        }

            String msg = String.format("%s %s %s", Message.AUTH, loginFiled.getText().trim(),
                    passwordField.getText().trim());
            try {
                out.writeUTF(msg);
                passwordField.clear();
                loginFiled.clear();
//                if (socket != null || !socket.isClosed()) {
//                setAuthenticated(true);
//                }
            }catch (Exception e){
                System.err.println("exception while auth");
            }

    }

    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);
//        count++;
//        String serverDirPath = serverPath + "/user" + count;

        try {
            List<String> clientFiles = Files.list(Paths.get(clientPath))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());

            listView.getItems().addAll(clientFiles);
            listView.refresh();

            init();

            //закончились идеи как реализовать инициализацию listView не по общей директории, а именно по пользовательской
            List<String> serverFiles = Files.list(Paths.get(serverPath + "/" + nickname))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());

            listViewCloud.getItems().addAll(serverFiles);
            listViewCloud.refresh();

            ReadHandler handler = new ReadHandler(in,
                    message -> Platform.runLater(
                            () -> input.setText(message)
                    )
            );
            Thread readThread = new Thread(handler);
            readThread.setDaemon(true);
            readThread.start();

        } catch (IOException e) {

            System.out.println("socket error server");
        }
//        setAuthenticated(true);

        }
    }

