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
    body.clear();
    obs.clear();
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
    spriteDetails.println("head:");
    spriteDetails.println("{\n\tpath=\"images/head.png\";");
    spriteDetails.println("\tup=[" + headCoordinates.get(0) + "," + headCoordinates.get(1) + "," + headCoordinates.get(2) + "," + headCoordinates.get(3) + "];");
    spriteDetails.println("\tdown=[" + headCoordinates.get(4) + "," + headCoordinates.get(5) + "," + headCoordinates.get(6) + "," + headCoordinates.get(7) + "];");
    spriteDetails.println("\tleft=[" + headCoordinates.get(8) + "," + headCoordinates.get(9) + "," + headCoordinates.get(10) + "," + headCoordinates.get(11) + "];");
    spriteDetails.println("\tright=[" + headCoordinates.get(12) + "," + headCoordinates.get(13) + "," + headCoordinates.get(14) + "," + headCoordinates.get(15) + "];");
    spriteDetails.println("};\n");

    spriteDetails.println("tail:");
    spriteDetails.println("{\n\tpath=\"images/tail.png\";");
    spriteDetails.println("\tup=[" + tailCoordinates.get(0) + "," + tailCoordinates.get(1) + "," + tailCoordinates.get(2) + "," + tailCoordinates.get(3) + "];");
    spriteDetails.println("\tdown=[" + tailCoordinates.get(4) + "," + tailCoordinates.get(5) + "," + tailCoordinates.get(6) + "," + tailCoordinates.get(7) + "];");
    spriteDetails.println("\tleft=[" + tailCoordinates.get(8) + "," + tailCoordinates.get(9) + "," + tailCoordinates.get(10) + "," + tailCoordinates.get(11) + "];");
    spriteDetails.println("\tright=[" + tailCoordinates.get(12) + "," + tailCoordinates.get(13) + "," + tailCoordinates.get(14) + "," + tailCoordinates.get(15) + "];");
    spriteDetails.println("};\n");

    spriteDetails.println("body:");
    spriteDetails.println("{\n\tpath=\"images/body.png\";");
    spriteDetails.println("\tvertical=[" + tailCoordinates.get(0) + "," + tailCoordinates.get(1) + "," + tailCoordinates.get(2) + "," + tailCoordinates.get(3) + "];");
    spriteDetails.println("\thorizontal=[" + tailCoordinates.get(4) + "," + tailCoordinates.get(5) + "," + tailCoordinates.get(6) + "," + tailCoordinates.get(7) +  "];");
    spriteDetails.println("\tupleft=[" + tailCoordinates.get(8) + "," + tailCoordinates.get(9) + "," + tailCoordinates.get(10) + "," + tailCoordinates.get(11) + "];");
    spriteDetails.println("\tupright=[" + tailCoordinates.get(12) + "," + tailCoordinates.get(13) + "," + tailCoordinates.get(14) + "," + tailCoordinates.get(15) + "];");
    spriteDetails.println("\trightup=[" + tailCoordinates.get(16) + "," + tailCoordinates.get(17) + "," + tailCoordinates.get(18) + "," + tailCoordinates.get(19) + "];");
    spriteDetails.println("\tleftup=[" + tailCoordinates.get(20) + "," + tailCoordinates.get(21) + "," + tailCoordinates.get(22) + "," + tailCoordinates.get(23) + "];");
    spriteDetails.println("};\n");

    spriteDetails.println("food:");
    spriteDetails.println("{\n\tpath=\"images/food.png\";");
    spriteDetails.println("\tobject=[" + foodCoordinates.get(0) + "," + foodCoordinates.get(1) + "," + foodCoordinates.get(2) + "," + foodCoordinates.get(3) + "];");
    spriteDetails.println("};\n");

    spriteDetails.println("obstacle:");
    spriteDetails.println("{\n\tpath=\"images/obstacle.png\";");
    spriteDetails.println("\tobject=[" + obstacleCoordinates.get(0) + "," + obstacleCoordinates.get(1) + "," + obstacleCoordinates.get(2) + "," + obstacleCoordinates.get(3) + "];");
    spriteDetails.println("};");


    levelDetails.close();
    spriteDetails.close();

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

  public int getNumberOfHorizontalTiles() {
    return numberOfHorizontalTiles;
  }


  public int getNumberOfVerticalTiles() {
    return numberOfVerticalTiles;
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
  public void setCoordinates(ArrayList<Integer> coordinates, int mode) {
    ArrayList<Integer> reference = null;
    switch (mode) {
    case 0:
      reference = headCoordinates;
      break;
    case 1:
      reference = tailCoordinates;
      break;
    case 2:
      reference = bodyCoordinates;
      break;
    case 3:
      reference = foodCoordinates;
      break;
    case 4:
      reference = obstacleCoordinates;
    }
    for (Integer x : coordinates) {
      reference.add(new Integer(x));
    }
  }
  private boolean isVerified = false;
  private int headTileCoordinate[] = new int[2];
  private int tailTileCoordinate[] = new int[2];
  private boolean isHeadPlaced = false;
  private boolean isTailPlaced = false;
  private int numberOfHorizontalTiles = 16;
  private int numberOfVerticalTiles = 9;

  private Sprites level[][];
  private String gameFolderPath = null, backgroundPath, bodyPath, headPath, tailPath, obstaclePath, foodPath;
  private ArrayList<Point> body = new ArrayList<Point>();
  private ArrayList<Point> obs = new ArrayList<Point>();
  private ArrayList<Integer> directions = new ArrayList<Integer>();
  private PrintWriter spriteDetails, levelDetails;
  private ArrayList<Integer> headCoordinates = new ArrayList<Integer>();
  private ArrayList<Integer> tailCoordinates = new ArrayList<Integer>();
  private ArrayList<Integer> bodyCoordinates = new ArrayList<Integer>();
  private ArrayList<Integer> foodCoordinates = new ArrayList<Integer>();
  private ArrayList<Integer> obstacleCoordinates = new ArrayList<Integer>();
}