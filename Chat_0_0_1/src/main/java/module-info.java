module ru.gb.java_2.chat_lesson_7 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.gb.java_2.chat_lesson_7 to javafx.fxml;
    exports ru.gb.java_2.chat_lesson_7;
}