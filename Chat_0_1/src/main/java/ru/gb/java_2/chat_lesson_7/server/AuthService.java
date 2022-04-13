package ru.gb.java_2.chat_lesson_7.server;

import java.io.Closeable;
import java.io.IOException;

public interface AuthService extends Closeable {
    String getNickByLoginAndPassword(String login, String password);

    void run();

    @Override
    void close() throws IOException;
}