package ru.gb.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.chat.Command;
import ru.gb.chat.Controller;
import ru.gb.chat.SQL.JdbcApp;

public class ClientHandler {
    private final Socket socket;
    private final ChatServer server;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final AuthService authService;
    private final JdbcApp jdbcApp = new JdbcApp();
    private final ExecutorService clientHandlerES = Executors.newSingleThreadExecutor();

    private static final Logger log = LogManager.getLogger(ChatServer.class);


    private String nick;

    public ClientHandler(Socket socket, ChatServer server, AuthService authService, ExecutorService executorService) {
        try {
            this.nick = "";
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.authService = authService;

            executorService.submit(() -> {
                try {
                    authenticate();
                    readMessages();
                } finally {
                    closeConnection();
                }
            });
//            new Thread(() -> {
//
//            }).start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            clientHandlerES.shutdown();
        }

    }

    private void closeConnection() {
        sendMessage(Command.END);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        try {
            if (socket != null) {
                server.unsubscribe(this);
                socket.close();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void authenticate() {
        while (true) {
            try {
                final String str = in.readUTF();
                if (Command.isCommand(str)) {
                    final Command command = Command.getCommand(str);
                    final String[] params = command.parse(str);
                    if (command == Command.AUTH) {
                        final String login = params[0];
                        final String password = params[1];
                        final String nick = jdbcApp.returnNick(login,password);

                        if (nick != null) {
                            if (server.isNickBusy(nick)) {
                                sendMessage(Command.ERROR, "Пользователь уже авторизован");
                                continue;
                            }
                            sendMessage(Command.AUTHOK, nick);
                            this.nick = nick;
                            server.broadcast("Пользователь " + nick + " зашел в чат");
                            server.subscribe(this);
                            break;
                        } else {
                            sendMessage(Command.ERROR, "Неверные логин и пароль");
                        }
                    }
                }
            } catch (IOException  e) {
                log.error(e.getMessage());
            }

        }
    }

    public void sendMessage(Command command, String... params) {
        sendMessage(command.collectMessage(params));
    }

    public void sendMessage(String message) {
        try {
            log.debug("SERVER: Send message to " + nick);
            out.writeUTF(message);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void readMessages() {
        try {
            while (true) {
                final String msg = in.readUTF();
                log.debug("Receive message: " + msg);
                if (Command.isCommand(msg)) {
                    final Command command = Command.getCommand(msg);
                    final String[] params = command.parse(msg);
                    if (command == Command.END) {
                        break;
                    }
                    if (command == Command.PRIVATE_MESSAGE) {
                        server.sendMessageToClient(this, params[0], params[1]);
                        continue;
                    }
                }
                server.broadcast(nick + ": " + msg);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }

    public String getNick() {
        return nick;
    }
}