module ru.gb.java_2.chat_lesson_7 {
    requires javafx.controls;
    requires javafx.fxml;



    exports ru.gb.java_2.chat_lesson_7.client;
    opens ru.gb.java_2.chat_lesson_7.client to javafx.fxml;
}