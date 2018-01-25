package snake.leveleditor.utils;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

public class LevelEditor {

  public String verify() {
    int nextCoordinateX = headTileCoordinate[0];
    int nextCoordinateY = headTileCoordinate[1];
    int direction = -1;
    /*
        0-up
        1-down
        2-left
        3-right

    */
    if (!isHeadPlaced) {
      return nextCoordinateX + " " + nextCoordinateY + " HNF";
    }
    if (!isTailPlaced) {
      return nextCoordinateX + " " + nextCoordinateY + " TNF";
    }
    switch (level[nextCoordinateX][nextCoordinateY]) {
    case HEAD_UP:
      nextCoordinateY += 1;
      direction = 0;
      break;
    case HEAD_DOWN:
      nextCoordinateY -= 1;
      direction = 1;
      break;
    case HEAD_LEFT:
      nextCoordinateX += 1;
      direction = 2;
      break;
    case HEAD_RIGHT:
      nextCoordinateX -= 1;
      direction = 3;
      break;

    }

    while (!(nextCoordinateX == tailTileCoordinate[0] && nextCoordinateY == tailTileCoordinate[1])) {
      body.add(new Point(nextCoordinateX, nextCoordinateY));
      directions.add(direction);
      switch (direction) {
      case 0:
        switch (level[nextCoordinateX][nextCoordinateY]) {
        case BODY_VERTICAL:
          nextCoordinateY += 1;
          break;
        case BODY_RIGHTUP:
          nextCoordinateX -= 1;
          direction = 3;
          break;
        case BODY_LEFTUP:
          nextCoordinateX += 1;
          direction = 2;
          break;
        default:
          return nextCoordinateX + " " + nextCoordinateY + " IVS";
        }
        break;
      case 1:
        switch (level[nextCoordinateX][nextCoordinateY]) {
        case BODY_VERTICAL:
          nextCoordinateY -= 1;
          break;
        case BODY_UPLEFT:
          nextCoordinateX -= 1;
          direction = 3;
          break;
        case BODY_UPRIGHT:
          nextCoordinateX += 1;
          direction = 2;
          break;
        default:
          return nextCoordinateX + " " + nextCoordinateY + " IVS";
        }
        break;
      case 2:
        switch (level[nextCoordinateX][nextCoordinateY]) {
        case BODY_HORIZONTAL:
          nextCoordinateX += 1;
          break;
        case BODY_RIGHTUP:
          nextCoordinateY -= 1;
          direction = 1;
          break;
        case BODY_UPLEFT:
          nextCoordinateY += 1;
          direction = 0;
          break;
        default:
          return nextCoordinateX + " " + nextCoordinateY + " IVS";
        }
        break;
      case 3:
        switch (level[nextCoordinateX][nextCoordinateY]) {
        case BODY_HORIZONTAL:
          nextCoordinateX -= 1;
          break;
        case BODY_UPRIGHT:
          nextCoordinateY += 1;
          direction = 0;
          break;
        case BODY_LEFTUP:
          nextCoordinateY -= 1;
          direction = 1;
          break;
        default:
          return nextCoordinateX + " " + nextCoordinateY + " IVS";
        }
        break;
      }
    }
    switch (direction) {
    case 0:
      if (level[nextCoordinateX][nextCoordinateY] != Sprites.TAIL_UP) {
        return nextCoordinateX + " " + nextCoordinateY + " IVT";
      }
      break;
    case 1:
      if (level[nextCoordinateX][nextCoordinateY] != Sprites.TAIL_DOWN) {
        return nextCoordinateX + " " + nextCoordinateY + " IVT";
      }
      break;
    case 2:
      if (level[nextCoordinateX][nextCoordinateY] != Sprites.TAIL_LEFT) {
        return nextCoordinateX + " " + nextCoordinateY + " IVT";
      }
      break;
    case 3:
      if (level[nextCoordinateX][nextCoordinateY] != Sprites.TAIL_RIGHT) {
        return nextCoordinateX + " " + nextCoordinateY + " IVT";
      }
      break;
    }
    directions.add(direction);

    for (int i = 0; i < numberOfHorizontalTiles; i++) {
      for (int j = 0; j <  numberOfVerticalTiles; j++) {
        if (level[i][j] == Sprites.OBSTACLE) {
          obs.add(new Point(i, j));
        }
      }
    }

    isVerified = true;
    return "ok";

  }

  private String getSpriteType(int x, int y) {
    if (level[x][y] == null) {
      return null;
    }
    switch (level[x][y]) {
    case HEAD_UP:
      return "\"hu\"";
    case HEAD_DOWN:
      return "\"hd\"";
    case HEAD_LEFT:
      return "\"hd\"";
    case HEAD_RIGHT:
      return "\"hr\"";
    case TAIL_UP:
      return "\"tu\"";
    case TAIL_DOWN:
      return "\"td\"";
    case TAIL_LEFT:
      return "\"td\"";
    case TAIL_RIGHT:
      return "\"tr\"";
    case BODY_VERTICAL:
      return "\"bv\"";
    case BODY_HORIZONTAL:
      return "\"bh\"";
    case BODY_UPLEFT:
      return "\"ul\"";
    case BODY_UPRIGHT:
      return "\"ur\"";
    case BODY_RIGHTUP:
      return "\"ru\"";
    case BODY_LEFTUP:
      return "\"lu\"";
    }
    return null;
  }
  public String getDirection(int d) {
    switch (d) {
    case 0:
      return "\"u\"";
    case 1:
      return "\"d\"";
    case 2:
      return "\"l\"";
    case 3:
      return "\"r\"";
    }
    return "";
  }
  public String buildLevel() {
    if (!isVerified) {
      String output = verify();
      if (!output.equals("ok")) {
        return output;
      }
    }
    try {
      spriteDetails = new PrintWriter(new FileWriter(gameFolderPath + "/SpriteDetails.cfg"));
      levelDetails = new PrintWriter(new FileWriter(gameFolderPath + "/LevelDetails.cfg"));
    }  catch (Exception e) {
      e.printStackTrace();
    }
    levelDetails.println("background=\"images/background.png\"");
    levelDetails.println("tiles_x=" + numberOfHorizontalTiles);
    levelDetails.println("tiles_y=" + numberOfVerticalTiles);
    levelDetails.println("snake:\n{\n head={\ntype=" + getSpriteType(headTileCoordinate[0], headTileCoordinate[1]) + ";");
    levelDetails.println("x=" + headTileCoordinate[0] + ";\ny=" + headTileCoordinate[1] + ";\n};");
    levelDetails.println("body=(");
    for (int i = body.size() - 1; i >= 0; i--) {
      Point x = body.get(i);
      levelDetails.print("{type=" + getSpriteType(x.getX(), x.getY()) + "; x=" + x.getX() + ";y=" + x.getY() + "; direction=" + getDirection(directions.get(i)) + "}");
      if (i != 0) {
        levelDetails.println(",");
      }
    }
    levelDetails.println(");");
    levelDetails.println("tail={ type=" + getSpriteType(tailTileCoordinate[0], tailTileCoordinate[1]) + ";\nx=" + tailTileCoordinate[0] + ";\ny=" + tailTileCoordinate[1] + ";\n};\n};");
    levelDetails.println("obstacle=(");
    for (int i = 0; i < obs.size(); i++) {
      levelDetails.print("[" + obs.get(i).getX() + "," + obs.get(i).getY() + "]");
      if (i < obs.size() - 1) {
        levelDetails.println(",");
      }
    }
    levelDetails.println(");");
    levelDetails.flush();
    

    levelDetails.close();
    spriteDetails.close();
    body.clear();
    obs.clear();
    return "ok";
  }

  public void setGameFolder(String path) {
    gameFolderPath = path;
    try {
      Files.createDirectory(Paths.get(gameFolderPath + "/images"));
    } catch (java.nio.file.FileAlreadyExistsException ex) {
      //do nothing
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  public void setBackgroundPath(String path) {
    backgroundPath = path;
    try {
      Files.copy(Paths.get(path), Paths.get(gameFolderPath + "/images/background.png"), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void setHeadPath(String path) {
    headPath = path;
    try {
      Files.copy(Paths.get(path), Paths.get(gameFolderPath + "/images/head.png"), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void setTailPath(String path) {
    tailPath = path;
    try {
      Files.copy(Paths.get(path), Paths.get(gameFolderPath + "/images/tail.png"), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void setObstaclePath(String path) {
    obstaclePath = path;
    try {
      Files.copy(Paths.get(path), Paths.get(gameFolderPath + "/images/obstacle.png"), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void setFoodPath(String path) {
    foodPath = path;
    try {
      Files.copy(Paths.get(path), Paths.get(gameFolderPath + "/images/food.png"), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public void setBodyPath(String path) {
    bodyPath = path;
    try {
      Files.copy(Paths.get(path), Paths.get(gameFolderPath + "/images/body.png"), StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public String getGameFolder() {
    return gameFolderPath;
  }

  public void setTiles(int horizontalTiles, int verticalTiles) {
    numberOfHorizontalTiles = horizontalTiles;
    numberOfVerticalTiles = verticalTiles;
    level = new Sprites[numberOfHorizontalTiles][numberOfVerticalTiles];
  }
  public void setTextureDimension(int width, int height) {
    textureWidth = width;
    textureHeight = height;
  }
  public int getNumberOfHorizontalTiles() {
    return numberOfHorizontalTiles;
  }


  public int getNumberOfVerticalTiles() {
    return numberOfVerticalTiles;
  }

  public int getTextureWidth() {
    return textureWidth;
  }
  public int getTextureHeight() {
    return textureHeight;
  }
  public void setHeadTileCoordinate(int x, int y) {
    headTileCoordinate[0] = x;
    headTileCoordinate[1] = y;
  }

  public void setTailTileCoordinate(int x, int y) {
    tailTileCoordinate[0] = x;
    tailTileCoordinate[1] = y;
  }
  public int getHeadTileCoordinateX() {
    return headTileCoordinate[0];
  }

  public int getHeadTileCoordinateY() {
    return headTileCoordinate[1];
  }

  public int getTailTileCoordinateX() {
    return tailTileCoordinate[0];
  }

  public int getTailTileCoordinateY() {
    return tailTileCoordinate[1];
  }

  public boolean isHeadPlaced() {
    return isHeadPlaced;
  }
  public boolean isTailPlaced() {
    return isTailPlaced;
  }

  public void setIsHeadPlaced(boolean val) {
    isHeadPlaced = val;
  }


  public void setIsTailPlaced(boolean val) {
    isTailPlaced = val;
  }

  public void setTile(int x , int y, Sprites type) {
    level[x][y] = type;
  }
  public void removeTile(int x, int y) {
    level[x][y] = null;
  }
  public static enum Sprites {
    HEAD_UP(1) , HEAD_DOWN(2), HEAD_LEFT(3), HEAD_RIGHT(4), TAIL_UP(5), TAIL_DOWN(6), TAIL_LEFT(7), TAIL_RIGHT(8), BODY_HORIZONTAL(9), BODY_VERTICAL(10), BODY_UPLEFT(11), BODY_UPRIGHT(12), BODY_RIGHTUP(13), BODY_LEFTUP(14), OBSTACLE(15 );
    Sprites(int id) {
      this.id = id;
    }
    private static final Sprites[] values = Sprites.values();
    public static Sprites getSpriteById(int id) {
      for (Sprites x : values) {
        if (x.id == id) {
          return x;
        }
      }
      return null;
    }
    public int getId() {
      return id;
    }
    private int id;
  }
  private class Point {
    Point(int x, int y) {
      this.x = x;
      this.y = y;
    }
    public int getX() {
      return x;
    }
    public int getY() {
      return y;
    }
    private int x, y;
  }
  private boolean isVerified = false;
  private int headTileCoordinate[] = new int[2];
  private int tailTileCoordinate[] = new int[2];
  private boolean isHeadPlaced = false;
  private boolean isTailPlaced = false;
  private int numberOfHorizontalTiles = 16;
  private int numberOfVerticalTiles = 9;
  private int textureWidth = 256;
  private int textureHeight = 256;
  private Sprites level[][];
  private String gameFolderPath = null, backgroundPath, bodyPath, headPath, tailPath, obstaclePath, foodPath;
  private ArrayList<Point> body = new ArrayList<Point>();
  private ArrayList<Point> obs = new ArrayList<Point>();
  private ArrayList<Integer> directions = new ArrayList<Integer>();
  private PrintWriter spriteDetails, levelDetails;
}