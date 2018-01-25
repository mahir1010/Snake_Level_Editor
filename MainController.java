package snake.leveleditor.ui.controller;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import snake.leveleditor.utils.LevelEditor;
import javafx.stage.DirectoryChooser;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.io.File;
import javafx.stage.Modality;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.ScrollPane;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.beans.property.ReadOnlyProperty;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.DragEvent;


public class MainController {
  @FXML public void initialize() {

    canvas = new GridPane();
    canvas.setGridLinesVisible(true);
    levelArea.prefWidthProperty().bind(MainWindow.widthProperty().subtract(400));
    levelArea.setContent(canvas);
    headUp.setId("headup");
    headLeft.setId("headleft");
    headRight.setId("headright");
    headDown.setId("headdown");
    tailUp.setId("tailup");
    tailLeft.setId("tailleft");
    tailRight.setId("tailright");
    tailDown.setId("taildown");
    bodyHorizontal.setId("bodyhorz");
    bodyVertical.setId("bodyvert");
    bodyUpLeft.setId("bodyul");
    bodyUpRight.setId("bodyur");
    bodyDownLeft.setId("bodyru");
    bodyDownRight.setId("bodylu");
    obstacleTile.setId("obst");
  }
  @FXML public void selectTileToDraw(MouseEvent me) {
    if (selectedTile != null) {
      selectedTile.setStyle("");
    }
    selectedTile = (ImageView)me.getSource();
    selectedTile.setStyle("-fx-effect: dropshadow(three-pass-box, white, 10, 0, 0, 0);");
  }

  @FXML public void verifyLevel() {
    String output = level.verify();
    if (output.equals("ok")) {
      createAlertDialog("Verification", "Successful");
    } else {
      String debug[] = output.split(" ");

      switch (debug[debug.length - 1]) {
      case "HNF":
        createAlertDialog("Verification Failed", "Head Not Found");
        break;
      case "IVS":
        createAlertDialog("Verification Failed", "Invalid sprite at \nX=" + debug[0] + " Y=" + debug[1]);
        break;
      case "IVT":
        createAlertDialog("Verification Failed", "Wrong type of tail at \nX=" + debug[0] + " Y=" + debug[1]);
        tailTile.setStyle("-fx-effect: dropshadow(three-pass-box, red, 5, 0.6, 0, 0);");
        break;
      case "TNF":
        createAlertDialog("Verification Failed", "Tail Not Found");
        break;
      default:
        createAlertDialog("Verification Failed", output);
      }
    }
  }

  @FXML public void buildLevel() {
    System.out.println(level.buildLevel());
  }
  @FXML public void createNewLevel() {
    try {

      level = new LevelEditor();
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/newLevelDialog.fxml"));
      controller = new NewLevelDialogController(level);
      loader.setController(controller);
      Scene newLevelScene = new Scene(loader.load());
      Stage stage = new Stage();
      stage.setTitle("New Level");
      stage.setResizable(false);
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.initOwner(newLevelScene.getWindow());
      stage.setScene(newLevelScene);
      stage.showAndWait();
      textureWidth = level.getTextureWidth();
      textureHeight = level.getTextureHeight();
      canvas.getChildren().removeAll();
      for (int i = 0; i < level.getNumberOfHorizontalTiles(); i++) {
        for (int j = 0; j < level.getNumberOfVerticalTiles(); j++) {
          ImageView view = new ImageView();
          view.setPickOnBounds(true);
          view.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
              ImageView targetTile = (ImageView)me.getSource();
              if (me.isShiftDown()) {
                targetTile.setImage(selImg);
                continuousDraw = true;
              }
              me.consume();
            }
          });
          view.setOnDragOver(new EventHandler<DragEvent>() {
            public void handle(DragEvent me) {
              System.out.println("Here");
              ImageView targetTile = (ImageView)me.getSource();
              if (continuousDraw) {
                System.out.println("Here again");
                targetTile.setImage(selImg);
              }
            }
          });
          view.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
              ImageView targetTile = (ImageView)me.getSource();
              if (selectedTile == null) {
                return;
              }
              targetTile.setStyle("");
              String substr = selectedTile.getId().substring(0, 4);
              int yCoordinate = canvas.getRowIndex(targetTile);
              int xCoordinate = canvas.getColumnIndex(targetTile);
              if (me.getButton() == MouseButton.PRIMARY) {
                targetTile.setImage(selectedTile.getImage());
                targetTile.setViewport(selectedTile.getViewport());
                if (substr.equals("head")) {
                  if (level.isHeadPlaced()) {
                    if ( headTile == targetTile)
                      return;
                    headTile.setImage(null);
                    level.removeTile(level.getHeadTileCoordinateX(), level.getHeadTileCoordinateY());
                  }
                  level.setHeadTileCoordinate(xCoordinate, yCoordinate);
                  level.setIsHeadPlaced(true);
                  headTile = targetTile;
                  switch (selectedTile.getId().substring(4)) {
                  case "up":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.HEAD_UP);
                    break;
                  case "down":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.HEAD_DOWN);
                    break;
                  case "left":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.HEAD_LEFT);
                    break;
                  case "right":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.HEAD_RIGHT);
                    break;
                  }
                } else if (substr.equals("tail")) {
                  if (level.isTailPlaced() && tailTile != targetTile) {
                    if (tailTile == targetTile)
                      return;
                    tailTile.setImage(null);
                    level.removeTile(level.getTailTileCoordinateX(), level.getTailTileCoordinateY());
                  }
                  level.setTailTileCoordinate(xCoordinate, yCoordinate);
                  level.setIsTailPlaced(true);
                  tailTile = targetTile;
                  switch (selectedTile.getId().substring(4)) {
                  case "up":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.TAIL_UP);
                    break;
                  case "down":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.TAIL_DOWN);
                    break;
                  case "left":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.TAIL_LEFT);
                    break;
                  case "right":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.TAIL_RIGHT);
                    break;
                  }
                } else if (substr.equals("body")) {
                  if (headTile == targetTile) {
                    level.setHeadTileCoordinate(-1, -1);
                    level.setIsHeadPlaced(false);
                    headTile = null;
                  }
                  if (tailTile == targetTile) {
                    level.setTailTileCoordinate(-1, -1);
                    level.setIsTailPlaced(false);
                    tailTile = null;
                  }
                  switch (selectedTile.getId().substring(4)) {
                  case "horz":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.BODY_HORIZONTAL);
                    break;
                  case "vert":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.BODY_VERTICAL);
                    break;
                  case "ul":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.BODY_UPLEFT);
                    break;
                  case "ur":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.BODY_UPRIGHT);
                    break;
                  case "ru":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.BODY_RIGHTUP);
                    break;
                  case "lu":
                    level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.BODY_LEFTUP);
                    break;
                  }

                } else if (substr.equals("obst")) {
                  level.setTile(xCoordinate, yCoordinate, LevelEditor.Sprites.OBSTACLE);
                }
              } else if (me.getButton() == MouseButton.SECONDARY) {
                targetTile.setImage(null);
                level.removeTile(xCoordinate, yCoordinate);
                if (substr.equals("head")) {
                  level.setIsHeadPlaced(false);
                  headTile = null;
                }
                if (substr.equals("tail")) {
                  level.setIsTailPlaced(false);
                  tailTile = null;
                }
              }
            }
          });
          view.setFitWidth(60);
          view.setFitHeight(60);
          canvas.add(view, i, j);
        }
        backgroundButton.setDisable(false);
        bodyButton.setDisable(false);
        headButton.setDisable(false);
        tailButton.setDisable(false);
        foodButton.setDisable(false);
        obstacleButton.setDisable(false);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }


  @FXML public void selectImage(ActionEvent ae) {
    FileChooser chooser = new FileChooser();
    String buttonName = ((Button)ae.getSource()).getText();
    chooser.setTitle(buttonName);
    File selectedFile = chooser.showOpenDialog(MainWindow.getScene().getWindow());
    if (selectedFile == null) {
      return;
    }
    Image img = new Image("file:" + selectedFile.getAbsolutePath());
    switch (buttonName) {
    case "Background":
      background.setImage(img);
      level.setBackgroundPath(selectedFile.getAbsolutePath());
      canvas.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, new BackgroundSize(100, 100, true, true, true, true))));
      break;
    case "Body":
      level.setBodyPath(selectedFile.getAbsolutePath());
      body.setImage(img);
      bodyHorizontal.setImage(img);
      bodyHorizontal.setViewport(new Rectangle2D(0, 0, textureWidth, textureHeight));
      bodyVertical.setImage(img);
      bodyVertical.setViewport(new Rectangle2D(256, 0, textureWidth, textureHeight));
      bodyUpLeft.setImage(img);
      bodyUpLeft.setViewport(new Rectangle2D(0, 256, textureWidth, textureHeight));
      bodyUpRight.setImage(img);
      bodyUpRight.setViewport(new Rectangle2D(256, 256, textureWidth, textureHeight));
      bodyDownLeft.setImage(img);
      bodyDownLeft.setViewport(new Rectangle2D(0, 512, textureWidth, textureHeight));
      bodyDownRight.setImage(img);
      bodyDownRight.setViewport(new Rectangle2D(256, 512, textureWidth, textureHeight));

      break;
    case "Head":
      level.setHeadPath(selectedFile.getAbsolutePath());
      head.setImage(img);
      headUp.setImage(img);
      headDown.setImage(img);
      headLeft.setImage(img);
      headRight.setImage(img);
      headUp.setViewport(new Rectangle2D(0, 0, textureWidth, textureHeight));
      headDown.setViewport(new Rectangle2D(256, 256, textureWidth, textureHeight));
      headRight.setViewport(new Rectangle2D(256, 0, textureWidth, textureHeight));
      headLeft.setViewport(new Rectangle2D(0, 256, textureWidth, textureHeight));
      break;
    case "Tail":
      level.setTailPath(selectedFile.getAbsolutePath());
      tail.setImage(img);
      tailUp.setImage(img);
      tailDown.setImage(img);
      tailLeft.setImage(img);
      tailRight.setImage(img);
      tailUp.setViewport(new Rectangle2D(0, 0, textureWidth, textureHeight));
      tailDown.setViewport(new Rectangle2D(256, 256, textureWidth, textureHeight));
      tailLeft.setViewport(new Rectangle2D(256, 0, textureWidth, textureHeight));
      tailRight.setViewport(new Rectangle2D(0, 256, textureWidth, textureHeight));
      break;
    case "Obstacle":
      level.setObstaclePath(selectedFile.getAbsolutePath());
      obstacle.setImage(img);
      obstacleTile.setImage(img);
      break;
    case "Food":
      level.setFoodPath(selectedFile.getAbsolutePath());
      food.setImage(img);
      break;
    }
  }

  private void createAlertDialog(String title, String message) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Attention!!");
    alert.setHeaderText(title);
    alert.setContentText(message);
    alert.showAndWait();
  }
  @FXML Button backgroundButton, bodyButton, headButton, tailButton, obstacleButton, foodButton;
  @FXML BorderPane MainWindow;
  @FXML ImageView background, body, head, tail, obstacle, obstacleTile, food, headUp, headLeft, headRight, headDown, tailUp, tailLeft, tailRight, tailDown, bodyHorizontal, bodyVertical, bodyUpLeft, bodyUpRight, bodyDownLeft, bodyDownRight;
  @FXML ScrollPane levelArea;
  private ImageView selectedTile, headTile, tailTile;
  private GridPane canvas;
  private NewLevelDialogController controller;
  private static LevelEditor level ;
  private int textureWidth, textureHeight;
  private boolean continuousDraw = false;
  private final Image selImg = new Image("file:./Resources/selected.png");
}

class NewLevelDialogController {

  NewLevelDialogController(LevelEditor instance) {
    level = instance;
  }

  @FXML public void initializeLevelEditor() {
    try {
      if (level.getGameFolder() == null) {
        noGameFolderSelectedWarning();
        return;
      }
      level.setTiles(Integer.parseInt(horizontalTiles.getText()), Integer.parseInt(verticalTiles.getText()));
      level.setTextureDimension(Integer.parseInt(textureWidth.getText()), Integer.parseInt(textureHeight.getText()));
      ((Stage)newLevelDialog.getScene().getWindow()).close();

    } catch (NumberFormatException e) {
      Alert alert = new Alert(AlertType.WARNING);
      alert.setTitle("Warning!!");
      alert.setHeaderText("Invalid Number");
      alert.setContentText("Invalid number in the textfield");
      alert.showAndWait();
    }
  }

  @FXML public void selectGameFolder() {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedDirectory = directoryChooser.showDialog(newLevelDialog.getScene().getWindow());
    if (selectedDirectory == null) {
      noGameFolderSelectedWarning();
    } else {
      level.setGameFolder(selectedDirectory.getAbsolutePath());
    }

  }

  void noGameFolderSelectedWarning() {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setTitle("Warning!!");
    alert.setHeaderText("Game Folder Path");
    alert.setContentText("Folder not selected");
    alert.showAndWait();
  }

  @FXML TextField horizontalTiles, verticalTiles, textureWidth, textureHeight;
  @FXML VBox newLevelDialog;

  private LevelEditor level;
}