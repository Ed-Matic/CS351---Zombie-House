package model;

import java.util.ArrayList;
import java.util.List;
import game.Xform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;


/**
 * HouseGenerator Group ZombieHouse implements JavaFX 3D. Builds
 * the walls of the house and adds the texture used to create
 * the 3D effect.
 * 
 * @author Ederin Igharoro
 * @author Max Barnhart
 */

public class HouseGenerator extends Group
{
  
  
  final double TILE_SIZE = 50;
  private int NUM_TILES = 50;
  
  public boolean visited;
  public Box north; //1
  public Box east; //2
  public Box west; //3
  public Box south;//4
  
  public ArrayList<Box> wallList = new ArrayList<Box>();
  public Box[][] wall = new Box[NUM_TILES][NUM_TILES];
  public Box[][] tempWall = new Box[NUM_TILES][NUM_TILES];
  public Box floor ;
  public Box ceeling;
  public float wallLength = 3.0f;
  private Point3D initialPos;
  public Xform wallXform = new Xform();
  public Xform wallYform = new Xform();
  public Xform floorXform = new Xform();
  public Xform ceelingXform = new Xform();
  public Xform houseXform = new Xform();
  private Cell cells;
  private int currentCell = 0;
  private int totalCells;
  private int visitedCells = 0;
  private boolean startedBuilding = false;
  private int currentNeighbor = 0;
  private List<Integer> lastCells;
  private int backingUp = 0;
  private int wallToBreak = 0;
  
  public class Cell
  {
    public Box north; //1
    public Box east; //2
    public Box west; //3
    public Box south;//4
    
    public Box[][] horizontals = new Box[NUM_TILES][NUM_TILES];
    public Box[][] verticals = new Box[NUM_TILES][NUM_TILES];
    
    public boolean visited[][] = new boolean [NUM_TILES][NUM_TILES];;
  }

  
  public HouseGenerator(int N)
  {
    this.NUM_TILES = N;
    CreateWalls();
  }

  /**
   * Creates walls in the form of a grid for each space/TILES in the house.
   * uses 2D arrays for the x-axis and the y-axis.
   */
  private void CreateWalls()
  {
    Image textureImage = new Image(getClass().getResourceAsStream("Wall.jpg"));

    PhongMaterial material = new PhongMaterial();
    material.setDiffuseMap(textureImage);
    
    initialPos = new Point3D((-NUM_TILES / 2) + wallLength / 2, 20.3f,
        (-NUM_TILES / 2) + wallLength / 2);
    
    double initialPosX = (-NUM_TILES / 2) + wallLength / 2;
    double initialPosY = 20.3f;
    double initialPosZ = (-NUM_TILES / 2) + wallLength / 2;

    Point3D myPos = initialPos;

    for (int i = 0; i < NUM_TILES; i++)
    {
      for(int j =0 ; j < NUM_TILES; j++)
      {
        int horizontalX = i;
        int verticalZ = j - 1;
        
        wall[i][j] = new Box((horizontalX - i + 1) * TILE_SIZE, TILE_SIZE * 4,TILE_SIZE);
        
        tempWall[i][j] = new Box(TILE_SIZE, TILE_SIZE * 4,TILE_SIZE *(verticalZ - j +1));
        
        wall[i][j].setMaterial(material);
        tempWall[i][j].setMaterial(material);
      }
    }
    
    // For x-Axis and y-axis
    for (int i = 0; i < NUM_TILES; i++)
    {
      for (int j = 0; j < NUM_TILES; j++)
      {
        myPos = new Point3D(
            initialPos.getX() + (j * wallLength) - wallLength / 2, 20.3f,
            initialPos.getZ() + (i * wallLength) - wallLength / 2);

        wall[i][j].setTranslateX((initialPosX + (j * wallLength) - wallLength / 2) * TILE_SIZE);
        wall[i][j].setTranslateY(initialPosY);
        wall[i][j].setTranslateZ((initialPosZ + (i * wallLength) - wallLength / 2) * TILE_SIZE);
        
        tempWall[i][j].setTranslateX((initialPosX + (j * wallLength)) * TILE_SIZE);
        tempWall[i][j].setTranslateY(initialPosY);
        tempWall[i][j].setTranslateZ((initialPosZ + (i * wallLength) - wallLength) * TILE_SIZE);
        
        wallList.add(wall[i][j]);
        wallList.add(tempWall[i][j]);

      }
    }
    CreateCell();
    
  }
  /**
   * Assigns each grid block a cell and locks the
   * walls for that certain cell. does not return
   * a value but then calls the CreateMaze
   */
  private void CreateCell()
  {
    //Assign walls & tempWall to cells
    cells = new Cell();
    for(int i = 0; i < NUM_TILES; i++)
    {
      for(int j = 0; j < NUM_TILES; j++)
      {
        cells.horizontals[i][j] = wall[i][j];
        cells.verticals[i][j] = tempWall[i][j];
      }
    }
    
    CreateMaze();
  }
  
  /**
   * Builds the maze of the game. checking if each
   * cell has been visited or if the is a current wall
   * in that position to break
   */
  private void CreateMaze()
  {
    if(visitedCells < totalCells)
    {
      if(startedBuilding)
      {
        for(int i =0; i < NUM_TILES; i++)
        {
          for(int j =0; j < NUM_TILES; j++)
          {
            if(cells.visited[i][j]==false)
            {
              BreakWall();
              visitedCells++;
            }
          }
        }
        
      }
    }
    
    FinishHouse();
  }
  
  /**
   * Breaks different walls in search cases in the method
   * then leaves the loop not to break multiple walls
   */
  private void BreakWall()
  {
    for(int i =0; i < NUM_TILES; i++)
    {
      for(int j =0; j < NUM_TILES; j++)
      {
        switch(wallToBreak)
        {
          case 1: //north
          {
            cells.horizontals[i-1][j]= null;
            break;
          }
          case 2: //east
          {
            cells.verticals[i][j+1]= null;
            break;
          }
          case 3: //west
          {
            cells.verticals[i][j-1]= null;
            break;
          }
          case 4: //south
          {
            cells.horizontals[i+1][j]= null;
            break;
          }
        }
        return;
      }
    }
  }
  
  
  /**
   * Draws the finished walls and their
   * locations on the scene
   */
  private void FinishHouse()
  {
    for (int i = 0; i < NUM_TILES; i++)
    {
      for(int j = 0; j < NUM_TILES; j++)
      {
        wallXform.getChildren().add(cells.horizontals[i][j]);
        wallYform.getChildren().add(cells.verticals[i][j]);
        wallXform.setTranslateY(-20);
        wallYform.setTranslateY(-20);
      }
    }
    
    houseXform.getChildren().addAll(wallXform, wallYform);
  }
}
