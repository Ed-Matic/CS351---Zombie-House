package model;

import game.Xform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.ListCell;
import javafx.scene.shape.Box;

public class HouseGenerator extends Group
{
  public class Cell
  {

  }
  
  public int xSize = 25;
  public int ySize = 25;
  public Box[][] wall = new Box[xSize][ySize];
  public float wallLength = 3.0f;
  private Point3D initialPos;
  public Xform wallXform = new Xform();
  private Cell[] cells;
  private int currentCell = 0;
  private int totalCells;
  private int visitedCells = 0;
  private boolean startBuilding = false;
  private int currentNeighbor = 0;
  private ListCell<Integer> lastCells;
  private int backingUp = 0;
  private int wallToBreak = 0;
  
  final double TITLE_SIZE = 50;

  
  public HouseGenerator()
  {
    CreateWalls();
  }

  public void CreateWalls()
  {
    initialPos = new Point3D((-xSize / 2) + wallLength / 2, 20.3f,
        (-ySize / 2) + wallLength / 2);
    
    double initialPosX = (-xSize / 2) + wallLength / 2;
    double initialPosY = 20.3f;
    double initialPosZ = (-ySize / 2) + wallLength / 2;

    Point3D myPos = initialPos;
    Box[][] tempWall = new Box[xSize][ySize];

    for (int i = 0; i < ySize; i++)
    {
      for(int j =0 ; j <xSize; j++)
      {
        wall[i][j] = new Box();
        wall[i][j].setHeight(200);
        wall[i][j].setWidth(145);
        wall[i][j].setDepth(10);
        
        tempWall[i][j] = new Box();
        tempWall[i][j].setHeight(200);
        tempWall[i][j].setWidth(145);
        tempWall[i][j].setDepth(10);
      }
      //wallXform.getChildren().add(wall[i]);
      //wallXform.setTranslateY(-20);
    }
    // For x-Axis
    for (int i = 0; i < ySize; i++)
    {
      for (int j = 0; j < xSize; j++)
      {
        myPos = new Point3D(
            initialPos.getX() + (j * wallLength) - wallLength / 2, 20.3f,
            initialPos.getZ() + (i * wallLength) - wallLength / 2);

        wall[i][j].setTranslateX((initialPosX + (j * wallLength) - wallLength / 2) * TITLE_SIZE);
        wall[i][j].setTranslateY(initialPosY);
        wall[i][j].setTranslateZ((initialPosZ + (i * wallLength) - wallLength / 2) * TITLE_SIZE);
        

      }
    }

    // For y-Axis
    /*for (int i = 0; i < ySize; i++)
    {
      for (int j = 0; j < xSize; j++)
      {
        
        tempWall[j][i].setTranslateX((initialPosX + (j * wallLength)) * TITLE_SIZE);
        tempWall[j][i].setTranslateY(initialPosY);
        tempWall[j][i].setTranslateZ((initialPosZ + (i * wallLength) - wallLength) * TITLE_SIZE);
        

      }
    }*/
    
    
    for (int i = 0; i < xSize; i++)
    {
      for(int j = 0; j < xSize; j++)
      {
        wallXform.getChildren().addAll(wall[i][j], tempWall[i][j]);
        wallXform.setTranslateX(-400);
        wallXform.setTranslateY(-20);
      }
    }
  }
}
