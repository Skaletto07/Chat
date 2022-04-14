package ru.gb.java_2.chat_lesson_7.server;

import java.io.Closeable;

public interface AuthService extends Closeable {

    String getNickByLoginAndPassword(String login, String password);

    void start();

    void close();


}
