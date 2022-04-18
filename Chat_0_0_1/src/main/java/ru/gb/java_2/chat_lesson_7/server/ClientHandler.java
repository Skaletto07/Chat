package ru.gb.java_2.chat_lesson_7.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Socket socket;
    private final ChatServer server;
    private String nick;
    private final DataInputStream in;
    private final DataOutputStream out;
    private AuthService authService;

    public ClientHandler(Socket socket, ChatServer server, AuthService authService) {

        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.authService = authService;

            new Thread(() -> {
                try {
                    authenticate();
                    readMessage();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка создания подключения к клиентк", e);
        }

    }

    private void readMessage() {
        while (true) {
            try {
                final String msg = in.readUTF();
                if ("/end".equals(msg)) {
                    break;
                }
                System.out.println("Получено сообщение: " + msg);
                server.broadcast(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void authenticate() {
        long l = System.currentTimeMillis();
        while (true) {
            try {
                final String msg = in.readUTF();
                if (msg.startsWith("/auth")) {
                    final String[] s = msg.split("\\s");
                    final String login = s[1];
                    final String password = s[2];
                    final String nick = authService.getNickByLoginAndPassword(login, password);
                    if (nick != null) {
                        if (server.isNickBusy(nick)) {
                            sendMessage("Пользователь уже авторизован!");
                            continue;
                        }
                        sendMessage("/authok " + nick);
                        this.nick = nick;
                        server.broadcast("Пользователь " + nick + " Вошел в чат!");
                        server.subscribe(this);
                        break;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeConnection() {
        sendMessage("/end");
        try {
            if (in != null) {in.close();}
        } catch (IOException e) {
            throw new RuntimeException("Ошибка подключения!", e);
        }
        try {
            if (out != null) {out.close();}
        } catch (IOException e) {
            throw new RuntimeException("Ошибка подключения!", e);
        }
        try {
            if (socket != null) {
                socket.close();
                server.unsubscribe(this);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка подключения!", e);
        }
    }

    public void sendMessage(String message) {
        try {
            System.out.println("Отправляю сообщение: " + message);
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return nick;
    }
}
