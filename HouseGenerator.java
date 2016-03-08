package model;

import java.util.ArrayList;
import java.util.List;
import game.Xform;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;


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
        
        tempWall[i][j] = new Box(TILE_SIZE, TILE_SIZE * 4,TILE_SIZE);
        
        wall[i][j].setMaterial(material);
        tempWall[i][j].setMaterial(material);
      }
      //wallXform.getChildren().add(wall[i]);
      //wallXform.setTranslateY(-20);
    }
    // For x-Axis
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
    //AddFloorAndCeeling();
    //FinishHouse();
    CreateCell();
    
  }
  
  private void CreateCell()
  {
    cells = new Cell();
    for(int i = 0; i < NUM_TILES; i++)
    {
      for(int j = 0; j < NUM_TILES; j++)
      {
        cells.horizontals[i][j] = wall[i][j];
        cells.verticals[i][j] = tempWall[i][j];
      }
    }
    
    //FinishHouse();
    CreateMaze();
  }
  
  private void CreateMaze()
  {
    if(visitedCells < totalCells)
    {
      if(startedBuilding)
      {
        //GetNeighbors();
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
          }
          case 2: //east
          {
            cells.verticals[i][j+1]= null;
          }
          case 3: //west
          {
            cells.verticals[i][j-1]= null;
          }
          case 4: //south
          {
            cells.horizontals[i+1][j]= null;
          }
        }
      }
    }
  }
  
  private void GetNeighbors()
  {
    
  }
  
  private void FinishHouse()
  {
    for (int i = 0; i < NUM_TILES; i++)
    {
      for(int j = 0; j < NUM_TILES; j++)
      {
        wallXform.getChildren().add(cells.horizontals[i][j]);
        wallYform.getChildren().add(cells.verticals[i][j]);
        //wallXform.setTranslateZ(300);
        //wallYform.setTranslateZ(TITLE_SIZE);
        //wallYform.setRotateY(90);
        wallXform.setTranslateY(-20);
        wallYform.setTranslateY(-20);
      }
    }
    
    houseXform.getChildren().addAll(wallXform, wallYform);
  }
}
