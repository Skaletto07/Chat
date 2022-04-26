module ru.gb.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ru.gb.chat to javafx.fxml;
    exports ru.gb.chat;
}