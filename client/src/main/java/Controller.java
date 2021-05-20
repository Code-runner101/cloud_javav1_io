import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.Builder;

import javax.swing.*;
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

    private DataInputStream in;
    private DataOutputStream out;
    private byte[] buffer = new byte[256];
    private String nickname;
    private boolean auth;
    int count;

    public void setAuthenticated(boolean auth){
        this.auth = auth;
        sendingPanel.setVisible(auth);
        sendingPanel.setManaged(auth);
        authPanel.setVisible(!auth);
        authPanel.setManaged(!auth);

        if (!auth){
            nickname = "";
        }

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
        Socket socket = new Socket("localhost", 8899);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void initialize(URL location, ResourceBundle resources) {
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
            List<String> serverFiles = Files.list(Paths.get(serverPath))
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
        }
    }

