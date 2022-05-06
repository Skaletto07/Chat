module ru.gb.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ru.gb.chat to javafx.fxml;
    exports ru.gb.chat;
    exports ru.gb.chat.HomeWorkThreadABC5;
    opens ru.gb.chat.HomeWorkThreadABC5 to javafx.fxml;
}