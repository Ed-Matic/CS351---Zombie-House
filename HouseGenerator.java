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
  public int xSize = 50;
  public int ySize = 25;
  public Box[] wall = new Box[xSize * ySize];
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
  
  final double TITLE_SIZE = 10;

  
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
    Box tempWall;

    for (int i = 0; i < xSize * ySize; i++)
    {
      wall[i] = new Box();
      wall[i].setHeight(200);
      wall[i].setWidth(2);
      wall[i].setDepth(100);
      //wallXform.getChildren().add(wall[i]);
      //wallXform.setTranslateY(-20);
    }
    // For x-Axis
    for (int i = 0; i < ySize; i++)
    {
      for (int j = 0; j <= xSize; j++)
      {
        myPos = new Point3D(
            initialPos.getX() + (j * wallLength) - wallLength / 2, 20.3f,
            initialPos.getZ() + (i * wallLength) - wallLength / 2);

        wall[j].setTranslateX((initialPosX + (j * wallLength) - wallLength / 2) * TITLE_SIZE);
        wall[j].setTranslateY(initialPosY);
        wall[j].setTranslateZ((initialPosZ + (i * wallLength) - wallLength / 2) * TITLE_SIZE);
        

      }
    }

    // For y-Axis
    for (int i = 0; i <= ySize; i++)
    {
      for (int j = 0; j < xSize; j++)
      {
        
        wall[j].setTranslateX((initialPosX + (j * wallLength)) * TITLE_SIZE);
        wall[j].setTranslateY(initialPosY);
        wall[j].setTranslateZ((initialPosZ + (i * wallLength) - wallLength) * TITLE_SIZE);
        /*
         * myPos = new Vector3(initialPos.x + (j*wallLength), 20.3f,
         * initialPos.z + (i*wallLength)- wallLength); tempWall =
         * Instantiate(Wall,myPos,Quaternion.Euler(0.0f,90.0f,0.0f)) as
         * GameObject; tempWall.transform.parent = wallHolder.transform;
         */

      }
    }
    
    
    for (int i = 0; i < xSize * ySize; i++)
    {
      wallXform.getChildren().add(wall[i]);
      wallXform.setTranslateX(-400);
      wallXform.setTranslateY(-20);
    }
  }
}
