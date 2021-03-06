package ru.gb.chat.SQL;

import ru.gb.chat.server.AuthService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class JdbcApp implements AuthService {

    private static final String CONNECTION_URL = "jdbc:sqlite:javadb.db";
     private Connection connection;

    public JdbcApp() {
        run();
    }


    public static void main(String[] args) {

    }

    public  String returnNickForCngNewNick(String login) {
        String nick = null;
        try (PreparedStatement statement = connection.prepareStatement("SELECT nick FROM users WHERE login = ?");
             ResultSet rs = statement.executeQuery()) {
            statement.setString(1, login);

            while (rs.next())  {
                nick = rs.getString(1);
            }
            return nick;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    private void dropById(int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

    }

    private void selectByName(String nick) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE nick = ?");
             final ResultSet rs = statement.executeQuery()) {
            statement.setString(1, nick);
            while (rs.next()) {
                int id = rs.getInt("id");
                String nameDB = rs.getString("nick");
                String login = rs.getString("login");
                int password = rs.getInt("password");
                System.out.printf("%d - %s - %s - %d\n", id, nameDB, login, password);
            }
        }
    }

    private void select() throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
             final ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nameDB = rs.getString("nick");
                String login = rs.getString("login");
                String password = rs.getString("password");
                System.out.printf("%d - %s - %s - %s\n", id, nameDB, login, password);
            }
        }
    }


    private void insert(String nick,String login, String password) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (nick, login, password) VALUES (?, ?, ?)")) {
            statement.setString(1, nick);
            statement.setString(2, login);
            statement.setString(3, password);
            statement.executeUpdate();
        }
    }


    private void createTable() throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("" +
                " CREATE TABLE IF NOT EXISTS users (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    nick TEXT NOT NULL UNIQUE, " +
                "    login TEXT NOT NULL UNIQUE," +
                "    password TEXT NOT NULL " +
                ")")) {
            statement.executeUpdate();

        }
    }

    public boolean check(String nick) throws SQLException {
        try (final  PreparedStatement statement = connection.prepareStatement(" SELECT EXISTS(SELECT nick FROM users WHERE nick = ?);");
                ResultSet rs = statement.executeQuery() ) {

            statement.setString(1, nick);

            int result = 10;
            while (rs.next()) {
                result = Integer.parseInt(rs.getString(1));
            }

            if (result == 1) {
                return true;
            } else {
                return false;
            }
        }

    }

    public boolean checkLoginAndPassword(String nick, String password) throws SQLException {
        try (final  PreparedStatement statement = connection.prepareStatement(" SELECT EXISTS(SELECT nick FROM users WHERE nick = ? AND password = ?);");
             ResultSet rs = statement.executeQuery()) {
            statement.setString(1, nick);
            statement.setString(2, password);

            int result = 10;
            while (rs.next()) {
                result = Integer.parseInt(rs.getString(1));
            }
            if (result == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String returnNick(String login, String password)  {
        String nick = null;
        try (final  PreparedStatement statement = connection.prepareStatement(" SELECT nick FROM users WHERE login = ? AND password = ?;")) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                nick = rs.getString(1);
            }
            rs.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (nick != null) {
            return nick;
        } else {
            return null;
        }
    }

    public void changeNick(String newNick, String login) {
        try (final  PreparedStatement statement = connection.prepareStatement(" UPDATE users SET nick = ? WHERE login = ?;")) {
            statement.setString(1, newNick);
            statement.setString(2, login);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        String nick = null;
        try (final  PreparedStatement statement = connection.prepareStatement(" SELECT nick FROM users WHERE login = ? AND password = ?;")) {
            statement.setString(1, login);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                nick = rs.getString(1);
            }
            rs.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (nick != null) {
            return nick;
        } else {
            return null;
        }
    }

    @Override
    public void run() {
        try {
            connection = DriverManager.getConnection(CONNECTION_URL);
        } catch (SQLException e) {
            throw new RuntimeException("???????????? ?????????????????????? ?? ????", e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("???????????? ???????????????? ???????????????????? ?? ????" + e);
        }
    }
}