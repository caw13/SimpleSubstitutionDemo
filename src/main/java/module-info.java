module edu.ccsu.gencyber {
  requires javafx.controls;
  requires javafx.fxml;
requires javafx.graphics;
requires javafx.base;

  opens edu.ccsu.gencyber to javafx.fxml;

  exports edu.ccsu.gencyber;
  exports edu.ccsu.gencyber.cryptography;
}
