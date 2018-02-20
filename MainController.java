package snake.leveleditor.ui.controller;
import javafx.beans.value.ChangeListener;
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
import java.util.ArrayList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.beans.value.ObservableValue;
import javafx.stage.StageStyle;


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
      bodyVertical.setImage(img);
      bodyUpLeft.setImage(img);
      bodyUpRight.setImage(img);
      bodyDownLeft.setImage(img);
      bodyDownRight.setImage(img);
      SpriteExtractor bodyex = new SpriteExtractor(level, img, 2);
      bodyex.buildUI().showAndWait();
      level.setCoordinates(bodyex.coordinates,2);
      bodyHorizontal.setViewport(new Rectangle2D(bodyex.coordinates.get(0),bodyex.coordinates.get(1),bodyex.coordinates.get(2),bodyex.coordinates.get(3)));
      bodyVertical.setViewport(new Rectangle2D(bodyex.coordinates.get(4),bodyex.coordinates.get(5),bodyex.coordinates.get(6),bodyex.coordinates.get(7)));
      bodyUpLeft.setViewport(new Rectangle2D(bodyex.coordinates.get(8),bodyex.coordinates.get(9),bodyex.coordinates.get(10),bodyex.coordinates.get(11)));
      bodyUpRight.setViewport(new Rectangle2D(bodyex.coordinates.get(12),bodyex.coordinates.get(13),bodyex.coordinates.get(14),bodyex.coordinates.get(15)));
      bodyDownLeft.setViewport(new Rectangle2D(bodyex.coordinates.get(16),bodyex.coordinates.get(17),bodyex.coordinates.get(18),bodyex.coordinates.get(19)));
      bodyDownRight.setViewport(new Rectangle2D(bodyex.coordinates.get(20),bodyex.coordinates.get(21),bodyex.coordinates.get(22),bodyex.coordinates.get(23)));

      break;
    case "Head":
      level.setHeadPath(selectedFile.getAbsolutePath());
      head.setImage(img);
      headUp.setImage(img);
      headDown.setImage(img);
      headLeft.setImage(img);
      headRight.setImage(img);
      SpriteExtractor headex = new SpriteExtractor(level, img, 0);
      headex.buildUI().showAndWait();
      level.setCoordinates(headex.coordinates,0);
      headUp.setViewport(new Rectangle2D(headex.coordinates.get(0),headex.coordinates.get(1),headex.coordinates.get(2),headex.coordinates.get(3)));
      headDown.setViewport(new Rectangle2D(headex.coordinates.get(4),headex.coordinates.get(5),headex.coordinates.get(6),headex.coordinates.get(7)));
      headLeft.setViewport(new Rectangle2D(headex.coordinates.get(8),headex.coordinates.get(9),headex.coordinates.get(10),headex.coordinates.get(11)));
      headRight.setViewport(new Rectangle2D(headex.coordinates.get(12),headex.coordinates.get(13),headex.coordinates.get(14),headex.coordinates.get(15)));
      break;
    case "Tail":
      level.setTailPath(selectedFile.getAbsolutePath());
      tail.setImage(img);
      tailUp.setImage(img);
      tailDown.setImage(img);
      tailLeft.setImage(img);
      tailRight.setImage(img);
      SpriteExtractor tailex = new SpriteExtractor(level, img, 1);
      tailex.buildUI().showAndWait();
      level.setCoordinates(tailex.coordinates,1);
      tailUp.setViewport(new Rectangle2D(tailex.coordinates.get(0),tailex.coordinates.get(1),tailex.coordinates.get(2),tailex.coordinates.get(3)));
      tailDown.setViewport(new Rectangle2D(tailex.coordinates.get(4),tailex.coordinates.get(5),tailex.coordinates.get(6),tailex.coordinates.get(7)));
      tailLeft.setViewport(new Rectangle2D(tailex.coordinates.get(8),tailex.coordinates.get(9),tailex.coordinates.get(10),tailex.coordinates.get(11)));
      tailRight.setViewport(new Rectangle2D(tailex.coordinates.get(12),tailex.coordinates.get(13),tailex.coordinates.get(14),tailex.coordinates.get(15)));
      break;
    case "Obstacle":
      level.setObstaclePath(selectedFile.getAbsolutePath());
      obstacle.setImage(img);
      SpriteExtractor obstex = new SpriteExtractor(level, img, 4);
      obstex.buildUI().showAndWait();
      level.setCoordinates(obstex.coordinates,4);
      obstacleTile.setImage(img);
      obstacleTile.setViewport(new Rectangle2D(obstex.coordinates.get(0),obstex.coordinates.get(1),obstex.coordinates.get(2),obstex.coordinates.get(3)));
      break;
    case "Food":
      level.setFoodPath(selectedFile.getAbsolutePath());
      SpriteExtractor foodex = new SpriteExtractor(level, img, 3);
      foodex.buildUI().showAndWait();
      level.setCoordinates(foodex.coordinates,2);
      food.setImage(img);
      food.setViewport(new Rectangle2D(foodex.coordinates.get(0),foodex.coordinates.get(1),foodex.coordinates.get(2),foodex.coordinates.get(3)));
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

  @FXML TextField horizontalTiles, verticalTiles;
  @FXML VBox newLevelDialog;

  private LevelEditor level;
}

class SpriteExtractor {
  /*
    mode:
    0-head
    1-tail
    2-body
    3-food
    4-obstacle
  */
  SpriteExtractor (LevelEditor level, Image img, int mode) {
    this.level = level;
    this.mode = mode;
    this.img = img;
    switch (mode) {
    case 0:
    case 1:
      arrayOfTextField = new TextField[16];
      arrayOfImageView = new ImageView[4];
      break;
    case 2:
      arrayOfTextField = new TextField[24];
      arrayOfImageView = new ImageView[6];
      break;
    case 3:
      arrayOfTextField = new TextField[4];
      arrayOfImageView = new ImageView[1];
      break;
    case 4:
      arrayOfTextField = new TextField[4];
      arrayOfImageView = new ImageView[1];
      break;
    }
    for (int i = 0; i < arrayOfImageView.length; i++) {
      arrayOfImageView[i] = new ImageView(img);
      arrayOfImageView[i].setFitHeight(60);
      arrayOfImageView[i].setFitWidth(60);
    }
    for (int i = 0; i < arrayOfTextField.length; i++) {
      arrayOfTextField[i] = new TextField();
      arrayOfTextField[i].textProperty().addListener(new ChangeListener<String>() {
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
          verified = false;
        }
      });
    }
  }

  public Stage buildUI() {
    base = new VBox();
    base.setSpacing(10);
    base.setAlignment(Pos.BASELINE_CENTER);
    switch (mode) {
    case 0:
    case 1:
      Label top = new Label("Up", arrayOfImageView[0]);
      Label down = new Label("Down", arrayOfImageView[1]);
      Label left = new Label("Left", arrayOfImageView[2]);
      Label right = new Label("Right", arrayOfImageView[3]);
      top.setFont(Font.font("Monospaced", 12));
      down.setFont(Font.font("Monospaced", 12));
      left.setFont(Font.font("Monospaced", 12));
      right.setFont(Font.font("Monospaced", 12));
      top.prefWidthProperty().bind(right.widthProperty());
      down.prefWidthProperty().bind(right.widthProperty());
      left.prefWidthProperty().bind(right.widthProperty());
      base.getChildren().add(new HBox(10, top, arrayOfTextField[0], arrayOfTextField[1], arrayOfTextField[2], arrayOfTextField[3]));
      base.getChildren().add(new HBox(10, down, arrayOfTextField[4], arrayOfTextField[5], arrayOfTextField[6], arrayOfTextField[7]));
      base.getChildren().add(new HBox(10, left, arrayOfTextField[8], arrayOfTextField[9], arrayOfTextField[10], arrayOfTextField[11]));
      base.getChildren().add(new HBox(10, right, arrayOfTextField[12], arrayOfTextField[13], arrayOfTextField[14], arrayOfTextField[15]));
      break;
    case 2:
      Label horz = new Label("Horizontal", arrayOfImageView[0]);
      Label vert = new Label("Vertical", arrayOfImageView[1]);
      Label upleft = new Label("upleft", arrayOfImageView[2]);
      Label upright = new Label("upright", arrayOfImageView[3]);
      Label downleft = new Label("downleft", arrayOfImageView[4]);
      Label downright = new Label("downright", arrayOfImageView[5]);
      horz.setFont(Font.font("Monospaced", 12));
      vert.setFont(Font.font("Monospaced", 12));
      upleft.setFont(Font.font("Monospaced", 12));
      upright.setFont(Font.font("Monospaced", 12));
      downleft.setFont(Font.font("Monospaced", 12));
      downright.setFont(Font.font("Monospaced", 12));
      vert.prefWidthProperty().bind(horz.widthProperty());
      upleft.prefWidthProperty().bind(horz.widthProperty());
      upright.prefWidthProperty().bind(horz.widthProperty());
      downleft.prefWidthProperty().bind(horz.widthProperty());
      downright.prefWidthProperty().bind(horz.widthProperty());
      base.getChildren().add(new HBox(10, horz, arrayOfTextField[0], arrayOfTextField[1], arrayOfTextField[2], arrayOfTextField[3]));
      base.getChildren().add(new HBox(10, vert, arrayOfTextField[4], arrayOfTextField[5], arrayOfTextField[6], arrayOfTextField[7]));
      base.getChildren().add(new HBox(10, upleft, arrayOfTextField[8], arrayOfTextField[9], arrayOfTextField[10], arrayOfTextField[11]));
      base.getChildren().add(new HBox(10, upright, arrayOfTextField[12], arrayOfTextField[13], arrayOfTextField[14], arrayOfTextField[15]));
      base.getChildren().add(new HBox(10, downleft, arrayOfTextField[16], arrayOfTextField[17], arrayOfTextField[18], arrayOfTextField[19]));
      base.getChildren().add(new HBox(10, downright, arrayOfTextField[20], arrayOfTextField[21], arrayOfTextField[22], arrayOfTextField[23]));
      break;
    case 3:
      Label food = new Label("Food", arrayOfImageView[0]);
      food.setFont(Font.font("Monospaced", 12));
      base.getChildren().add(new HBox(10, food, arrayOfTextField[0], arrayOfTextField[1], arrayOfTextField[2], arrayOfTextField[3]));
      break;
    case 4:
      Label obstacle = new Label("obstacle", arrayOfImageView[0]);
      obstacle.setFont(Font.font("Monospaced", 12));
      base.getChildren().add(new HBox(10, obstacle, arrayOfTextField[0], arrayOfTextField[1], arrayOfTextField[2], arrayOfTextField[3]));
      break;
    }
    Button ok = new Button("Ok");
    Button check = new Button("check");
    check.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent ae) {
        int upperLimit = 0;
        switch (mode) {
        case 0:
        case 1:
          upperLimit = 4;
          break;
        case 2:
          upperLimit = 6;
          break;
        default:
          upperLimit = 1;
        }
        try {
          for (int i = 0; i < upperLimit; i++) {
            arrayOfImageView[i].setViewport(new Rectangle2D(Integer.parseInt(arrayOfTextField[i * 4].getText()), Integer.parseInt(arrayOfTextField[i * 4 + 1].getText()), Integer.parseInt(arrayOfTextField[i * 4 + 2].getText()), Integer.parseInt(arrayOfTextField[i * 4 + 3].getText())));
          }
          verified = true;
        } catch (Exception e) {

        }
      }
    });
    ok.setOnAction(new EventHandler<ActionEvent>() {
      public void handle(ActionEvent ae) {
        if (verified) {
          for (TextField t : arrayOfTextField) {
            coordinates.add(Integer.parseInt(t.getText()));
          }
          newStage.close();
        }
      }
    });
    base.getChildren().add(new HBox(10, check, ok));
    newStage = new Stage();
    newStage.initStyle(StageStyle.UNDECORATED);
    newStage.setScene(new Scene(base));
    return newStage;
  }
  private boolean verified = false;
  private ImageView arrayOfImageView[];
  private TextField arrayOfTextField[];
  private VBox base;
  private Image img;
  public ArrayList<Integer> coordinates = new ArrayList<Integer>();
  private LevelEditor level;
  private int mode;
  private Stage newStage;
}