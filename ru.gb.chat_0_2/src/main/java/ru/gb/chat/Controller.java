package ru.gb.chat;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.chat.SQL.JdbcApp;
import ru.gb.chat.server.ChatServer;

public class Controller {
    private static final Logger log = LogManager.getLogger(ChatServer.class);

    public TextField textFieldNewNick;
    @FXML
    private ListView<String> clientList;
    @FXML
    private HBox messageBox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField loginField;
    @FXML
    private HBox loginBox;
    @FXML
    private TextField textField;
    @FXML
    private TextArea textArea;

    JdbcApp jdbcApp = new JdbcApp();

    private final ChatClient client;

    public Controller() {
        client = new ChatClient(this);
        while (true) {
            try {
                client.openConnection();
                break;
            } catch (Exception e) {
                log.error(e.getMessage());
                showNotification();
            }
        }
    }

    public void btnSendClick(ActionEvent event) {
        final String message = textField.getText().trim();
        if (message.isEmpty()) {
            return;
        }
        client.sendMessage(message);
        textField.clear();
        textField.requestFocus();
    }

    public void addMessage(String message) {
        textArea.appendText(message + "\n");


    }

    public void btnAuthClick(ActionEvent actionEvent) {
        client.sendMessage(Command.AUTH, loginField.getText(), passwordField.getText());
    }

    public void setAuth(boolean success) {
        loginBox.setVisible(!success);
        messageBox.setVisible(success);
    }

    private void showNotification() {
        final Alert alert = new Alert(Alert.AlertType.ERROR,
                "Не могу подключится к серверу.\n" +
                        "Проверьте, что сервер запущен",
                new ButtonType("Попробовать еще", ButtonBar.ButtonData.OK_DONE),
                new ButtonType("Выйти", ButtonBar.ButtonData.CANCEL_CLOSE));
        alert.setTitle("Ошибка подключения");
        final Optional<ButtonType> buttonType = alert.showAndWait();
        final Boolean isExit = buttonType.map(btn -> btn.getButtonData().isCancelButton()).orElse(false);
        if (isExit) {
            System.exit(0);
        }
    }

    public void showError(String[] error) {
        final Alert alert = new Alert(Alert.AlertType.ERROR, error[0], new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
        alert.setTitle("Ошибка!");
        alert.showAndWait();
    }

    public void selectClient(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) { // /w nick1 private message
            final String message = textField.getText();
            final String nick = clientList.getSelectionModel().getSelectedItem();
            textField.setText(Command.PRIVATE_MESSAGE.collectMessage(nick, message));
            textField.requestFocus();
            textField.selectEnd();
        }
    }

    public void updateClientList(String[] params) {
        clientList.getItems().clear();
        clientList.getItems().addAll(params);
    }


    public void btnCngNick(ActionEvent actionEvent) {
        final String newNick = textFieldNewNick.getText().trim();
        final String oldNick = jdbcApp.returnNickForCngNewNick(this.loginField.getText());
        if (newNick.isEmpty()) {
            return;
        }
        jdbcApp.changeNick(newNick,oldNick);
        textFieldNewNick.clear();
        textFieldNewNick.requestFocus();
    }


    public void saveHistory() throws IOException {
        try {
            File history = new File("history_" + loginField.getText() + ".txt");
            if (!history.exists()) {
                log.debug("Создаем файл!");
                history.createNewFile();
            }
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(history, false)));
            br.write(textArea.getText());
            log.debug(textArea.getText());
            br.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void loadHistory() throws IOException {
        int posHistory = 100;
        File history = new File("history_" + loginField.getText() + ".txt");
        if (history.exists()) {

        List<String> historyList = new LinkedList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(history)));
        String text;
        while ((text = bufferedReader.readLine()) != null) {
            historyList.add(text);
        }

        if (historyList.size() > posHistory) {
            for (int i = historyList.size() - posHistory; i <= (historyList.size() - 1); i++) {
                textArea.appendText(historyList.get(i) + "\n");
            }
        } else {
            for (int i = 0; i < historyList.size(); i++) {
                textArea.appendText(historyList.get(i) + "\n");
                }
            }
        }
    }



}