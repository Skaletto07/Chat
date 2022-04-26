package ru.gb.chat.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class JdbcApp {
    public static void main(String[] args) {
    JdbcApp a = new JdbcApp();
//      a.connection("Bully", "AAA");



    }

    public void connection(String nick, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:javadb.db")) {

//            createTable(connection);
//            for (int i = 0; i < 5; i++) {
//                insert(connection,"nick" + i, "login" + i, "pass" + i);
//            }
           // System.out.println(check(nick));

//                createTable(connection);
//                insert(connection, nick, password);
            select(connection);
//            createTable(connection);
//            insert(connection, nick, password);
//            select(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void dropById(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

    }

    private void selectByName(Connection connection, String nick) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE nick = ?")) {
            statement.setString(1, nick);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nameDB = rs.getString("nick");
                String login = rs.getString("login");
                int password = rs.getInt("password");
                System.out.printf("%d - %s - %s - %d\n", id, nameDB, login, password);
            }
        }
    }

    private void select(Connection connection) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM users")) {
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nameDB = rs.getString("nick");
                String login = rs.getString("login");
                String password = rs.getString("password");
                System.out.printf("%d - %s - %s - %s\n", id, nameDB, login, password);
            }
        }
    }


    private void insert(Connection connection, String nick,String login, String password) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO users (nick, login, password) VALUES (?, ?, ?)")) {
            statement.setString(1, nick);
            statement.setString(2, login);
            statement.setString(3, password);
            statement.executeUpdate();
        }
    }


    private void createTable(Connection connection) throws SQLException {
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
        try (   Connection connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
                final  PreparedStatement statement = connection.prepareStatement(" SELECT EXISTS(SELECT nick FROM users WHERE nick = ?);")) {

            statement.setString(1, nick);
            ResultSet rs = statement.executeQuery();
            int result = 10;
            while (rs.next()) {
                 result = Integer.parseInt(rs.getString(1));
            }
            rs.close();
            if (result == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean checkLoginAndPassword(String nick, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
            final  PreparedStatement statement = connection.prepareStatement(" SELECT EXISTS(SELECT nick FROM users WHERE nick = ? AND password = ?);")) {
            statement.setString(1, nick);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            int result = 10;
            while (rs.next()) {
                result = Integer.parseInt(rs.getString(1));
            }
            rs.close();
            if (result == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String returnNick(String login, String password)  {
        String nick = null;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
             final  PreparedStatement statement = connection.prepareStatement(" SELECT nick FROM users WHERE login = ? AND password = ?;")) {
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
            return "Fail";
        }
    }

    public static void changeNick(String newNick, String login) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
             final  PreparedStatement statement = connection.prepareStatement(" UPDATE users SET nick = ? WHERE login = ?;")) {
            statement.setString(1, newNick);
            statement.setString(2, login);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}