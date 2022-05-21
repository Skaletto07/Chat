package ru.gb.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.chat.Command;
import ru.gb.chat.server.ChatServer;

public class ChatClient {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean afk = true;
    private final Controller controller;
    private static final Logger log = LogManager.getLogger(ChatServer.class);


    public ChatClient(Controller controller) {
        this.controller = controller;
    }

    public void openConnection() throws Exception {
        socket = new Socket("localhost", 8189);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        final Thread readThread = new Thread(() -> {
            try {
                waitAuthenticate();
                readMessage();
            } catch (IOException e) {
                log.error(e.getMessage());
            } finally {
                closeConnection();
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

    private void readMessage() throws IOException {
        while (true) {
            final String message = in.readUTF();
            log.debug("Receive message: " + message);
            controller.saveHistory();
            if (Command.isCommand(message)) {
                final Command command = Command.getCommand(message);
                final String[] params = command.parse(message);
                if (command == Command.END) {
                    controller.setAuth(false);
                    break;
                }
                if (command == Command.ERROR) {
                    Platform.runLater(() -> controller.showError(params));
                    continue;
                }
                if (command == Command.CLIENTS) {
                    controller.updateClientList(params);
                    continue;
                }
            }
            controller.addMessage(message);
        }
    }

    private void waitAuthenticate() throws IOException {
        while (true) {
            new Thread(() -> {
                try {
                    Thread.sleep(120000);
                    if (afk) {
                        closeConnection();
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }).start();
            final String msgAuth = in.readUTF();
            if (Command.isCommand(msgAuth)) {
                final Command command = Command.getCommand(msgAuth);
                final String[] params = command.parse(msgAuth);

                if (command == Command.AUTHOK) {
                    afk = false;
                    final String nick = params[0];
                   controller.loadHistory();
                    controller.addMessage("Успешная авторизация под ником " + nick);
                    controller.setAuth(true);
                    break;
                }
                if (Command.ERROR.equals(command)) {
                    Platform.runLater(() -> controller.showError(params));
                }
            }
        }
    }

    private void closeConnection() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        System.exit(0);
    }

    public void sendMessage(String message) {
        try {
            log.debug("Send message: " + message);
            out.writeUTF(message);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendMessage(Command command, String... params) {
        sendMessage(command.collectMessage(params));
    }
}