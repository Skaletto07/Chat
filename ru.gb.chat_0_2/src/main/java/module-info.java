module ru.gb.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.logging.log4j;


    opens ru.gb.chat to javafx.fxml;
    exports ru.gb.chat;
    exports ru.gb.chat.HomeWorkThreadABC5;
    opens ru.gb.chat.HomeWorkThreadABC5 to javafx.fxml;
}