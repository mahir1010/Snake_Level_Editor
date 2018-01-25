package snake.leveleditor.ui.loader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.Parent;

public class UILoader extends Application {


  public void start(Stage myStage) {
    try {
      mainWindow = FXMLLoader.load(getClass().getClassLoader().getResource("./Resources/mainUI.fxml"));
      if (mainWindow == null) {
        System.out.println("UI Not Loaded");
        return;
      }
      myStage.setScene(new Scene(mainWindow));
      myStage.setTitle("Level Editor");
      myStage.show();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Parent mainWindow;
}