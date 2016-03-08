package game;


import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LevelRender extends Application
{
  //Game loop
  MainGameLoop gameLoop;
  boolean isPaused;
  HouseGenerator houseGen = new HouseGenerator();
  boolean[][] floorPlan;
  
  final Group root = new Group();
  Xform world = new Xform();
  Stage stage;
  Scene scene;
  StartScreen startScene = new StartScreen(new Group());
  
  //Size of our tile
  final double TILE_SIZE = 50;
  final int NUM_TILES = 50;
  
  //Zombie Values
  ArrayList<Zombie> zombieList = new ArrayList<Zombie>();
  double zombieSpeed;
  double zombieSpawn;
  double zombieDecisionRate;
  double zombieSmellDistance;
  //Random Number generator for Zombie Spawn
  Random rand = new Random();
  
  //Character movement/hearing values
  boolean goForward = false;
  boolean goBackward = false;
  boolean goLeft = false;
  boolean goRight = false;
  boolean goRun = false;
  
  double moveBy = 5;
  double speed = 2;
  double distancePerSecond = speed*TILE_SIZE;
  double stamina = 5.0;
  double staminaRegen = 0.2;
  double hearing = 20;
  double charRadius = 30;
  
  
  //FPS fixing constants
  private long lastUpdate = 0;
  private long lastZombieUpdate = 0;
  
  //Camera transforms
  final Xform cameraXYrotate = new Xform();
  final Xform cameraXYtranslate = new Xform();
  final Xform cameraZrotate = new Xform();
  final PerspectiveCamera camera = new PerspectiveCamera(true);
  final Xform lightXform = new Xform();
  final PointLight light = new PointLight();
  
  //Camera initial values
  private static final double CAMERA_NEAR_CLIP = 0.1;
  private static final double CAMERA_FAR_CLIP = 10000.0;
  
  //Mouse values
  double mousePosX;
  double mousePosY;
  double mouseOldX;
  double mouseOldY;
  double mouseDeltaX;
  double mouseDeltaY;
  private static final double MOUSE_SPEED = 0.1;
  private static final double ROTATION_SPEED = 3.0;
  
  private void buildCamera()
  {
    root.getChildren().add(cameraXYtranslate);
    cameraXYtranslate.getChildren().add(cameraXYrotate);
    cameraXYrotate.getChildren().add(cameraZrotate);
    cameraZrotate.getChildren().add(camera);
    cameraZrotate.setRotateZ(180.0);

    camera.setNearClip(CAMERA_NEAR_CLIP);
    camera.setFarClip(CAMERA_FAR_CLIP);
    
    cameraXYtranslate.t.setX(NUM_TILES*TILE_SIZE/2);
    cameraXYtranslate.t.setZ(NUM_TILES*TILE_SIZE/2);

    //Adds a PointLight at the point of the camera so it rotates with the field of view.
    PointLight light = new PointLight();
    light.setColor(Color.WHITE);
    lightXform.getChildren().add(light);
    cameraXYrotate.getChildren().add(lightXform);
    light.getScope().add(world);
    
    
    //Sets up a reticle in the center of the screen
    PhongMaterial blackMaterial = new PhongMaterial();
    blackMaterial.setDiffuseColor(Color.BLACK);
    blackMaterial.setSpecularColor(Color.BLACK);
    
    Box test = new Box(.05, .001, .001);
    Box test2 = new Box(.001, .05, .001);
    test.setMaterial(blackMaterial);
    test2.setMaterial(blackMaterial);
    Xform crosshairXform = new Xform();
    
    crosshairXform.getChildren().addAll(test, test2);
    crosshairXform.setTranslateZ(5);
    cameraXYrotate.getChildren().add(crosshairXform);
    
  }
  

  private void handleMouse(Scene scene, final Node root) {
    scene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent me)
      {
        //do stuff
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = mousePosX - mouseOldX;
        mouseDeltaY = mousePosY - mouseOldY;
        
        double modifier = 0.75;
        
        double newMouseXAngle = cameraXYrotate.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED;
        cameraXYrotate.ry.setAngle(cameraXYrotate.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);
        if (newMouseXAngle > 90) return;
        else if (newMouseXAngle < -90) return;
        else cameraXYrotate.rx.setAngle(newMouseXAngle);
        
      }
    });
    //This might be superfluous once we figure out mouse control better.
    //Used to make sure the mouseMoved doesn't destroy our field of view
    //When the mouse reenters the scene.
    scene.setOnMouseEntered(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent me)
      {
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
      }
    });
    
}
  
  private void handleKeyboard(Scene scene, final Node root)
  {
    scene.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {   
        switch (event.getCode())
        {
        case W:
          goForward = true;
          break;
        case A:
          goLeft = true;
          break;
        case S:
          goBackward = true;
          break;
        case D:
          goRight = true;
          break;
        case SHIFT:
          goRun = true;
        }
      }  
    });
    scene.setOnKeyReleased(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {
        switch (event.getCode())
        {
        case W:
          goForward = false;
          break;
        case A:
          goLeft = false;
          break;
        case S:
          goBackward = false;
          break;
        case D:
          goRight = false;
          break;
        case SHIFT:
          goRun = false;
        }
      } 
    });
  }
  
  private boolean[][] makeFloorPlan()
  {
    boolean[][] floorPlan = new boolean[NUM_TILES][NUM_TILES];
    
    for (int i = 0; i < NUM_TILES; i++)
    {
      floorPlan[i][0] = true;
      floorPlan[0][i] = true;
      floorPlan[i][NUM_TILES-1] = true;
      floorPlan[NUM_TILES-1][i] = true;
    }
    for (int i = 1; i < NUM_TILES-1; i++)
    {
      for (int j = 1; j < NUM_TILES-1; j++)
      {
        floorPlan[i][j] = false;
      }
    }
    
    return floorPlan;
  }
  
  private void generateRoom()
  {
    //IMAGE COMMENTED OUT BECAUSE ITS NOT ON GITHUB YET WHILE I PLAY WITH IT
    //Image textureImage = new Image(getClass().getResourceAsStream("brick.jpg"));
    
    PhongMaterial testMaterial = new PhongMaterial();
    //testMaterial.setDiffuseMap(textureImage);
    testMaterial.setDiffuseColor(Color.WHITE);
    testMaterial.setSpecularColor(Color.TRANSPARENT);
    
    PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.BLACK);
    
    PhongMaterial whiteMaterial = new PhongMaterial();
    whiteMaterial.setDiffuseColor(Color.CORNFLOWERBLUE);
    whiteMaterial.setSpecularColor(Color.LIGHTBLUE);
   
    //CASE FOR houseGEN
    //world.getChildren().add(houseGen.wallXform);
    
    Xform testXform = new Xform();
    
    for (int i = 0; i < NUM_TILES; i++)
    {
      for (int j = 0; j < NUM_TILES; j++)
      {
        Box testBox = new Box(49, 1, 49);
        testBox.setMaterial(redMaterial);
        testBox.setTranslateZ(i*TILE_SIZE);
        testBox.setTranslateX(j*TILE_SIZE);
        testBox.setTranslateY(-1*TILE_SIZE);
        testXform.getChildren().add(testBox);
      }
    }
    world.getChildren().add(testXform);
    
    Xform wallsXform = new Xform();
    floorPlan = makeFloorPlan();
    for (int i = 4; i < 7; i++) floorPlan[22][i] = true;
    floorPlan[45][45] = true;
    floorPlan[45][46] = true;
    boolean[][] unvisitedHorizontal = new boolean[NUM_TILES][NUM_TILES];
    boolean[][] unvisitedVertical = new boolean[NUM_TILES][NUM_TILES];
    for (int i = 0; i < NUM_TILES; i++)
    {
      for (int j = 0; j < NUM_TILES; j++)
      {
        unvisitedHorizontal[i][j] = true;
        unvisitedVertical[i][j] = true;
      }
    }
    for (int i = 0; i < NUM_TILES; i++)
    {
      for (int j = 0; j < NUM_TILES; j++)
      {
        //Creating Horizontal Walls
        if (floorPlan[i][j] && unvisitedHorizontal[i][j])
        {
          int horizontalX = i;
          for (int k = i; k < NUM_TILES; k++)
          {
            unvisitedHorizontal[k][j] = false;
            if (floorPlan[k][j]) horizontalX += 1;
            else k = NUM_TILES;
          }
          if (horizontalX > i+1)
          {
            Box box = new Box((horizontalX-i+1)*TILE_SIZE, TILE_SIZE*4, TILE_SIZE);
            box.setBlendMode(BlendMode.SRC_OVER);
            box.setCullFace(CullFace.BACK);
            box.setTranslateX(i*TILE_SIZE/2);
            box.setTranslateX(box.getTranslateX() + (horizontalX-i)*TILE_SIZE/2);
            if ((horizontalX-i)/2 % 1 > 0) box.setTranslateX(box.getTranslateX() + TILE_SIZE/2);
            if (i % 2 > 0) box.setTranslateX(box.getTranslateX() + TILE_SIZE/2);
            box.setTranslateZ(j*TILE_SIZE);
            box.setMaterial(testMaterial);
            wallsXform.getChildren().add(box);
          }
        }
        //Creating Vertical Walls
        if (floorPlan[i][j] && unvisitedVertical[i][j])
        {
          int verticalZ = j-1;
          for (int k = j; k < NUM_TILES; k++)
          {
            unvisitedVertical[i][k] = false;
            if (floorPlan[i][k]) verticalZ += 1;
            else k = NUM_TILES;
          }
          if (verticalZ != j)
          {
            Box box = new Box(TILE_SIZE, TILE_SIZE*4, TILE_SIZE*(verticalZ-j+1));
            box.setBlendMode(BlendMode.SRC_OVER);
            box.setCullFace(CullFace.BACK);
            box.setTranslateX(i*TILE_SIZE);
            box.setTranslateZ((verticalZ+j) / 2 * TILE_SIZE);
            if ((verticalZ-j)/2 % 1 > 0) box.setTranslateZ(box.getTranslateZ() + TILE_SIZE/2);
            if (j % 2 > 0) box.setTranslateZ(box.getTranslateZ() + TILE_SIZE/2);
            box.setMaterial(testMaterial);
            wallsXform.getChildren().add(box);
          }
        }
        
      }
    }
    world.getChildren().add(wallsXform);
  }
  
  
  public void updateCharacter(float timeElapsed)
  {
    //Do stuff
    if (goRun && (stamina > timeElapsed) && (goLeft || goRight || goForward || goBackward))
    {
      moveBy = 2*distancePerSecond*(timeElapsed);
      stamina -= timeElapsed;
    }
    else {
      moveBy = distancePerSecond*(timeElapsed);
      if (stamina < 5.0) stamina += timeElapsed*staminaRegen;
    }
    //moveBy = 5;
    
    double yAngle = cameraXYrotate.ry.getAngle() * Math.PI / 180;
    double xAnglePercentage = Math.sin(yAngle) * Math.sin(yAngle);
    double zAnglePercentage = Math.cos(yAngle) * Math.cos(yAngle);
    //Keeps the sign of the sin/cos since it's needed for seeing whether to add/subtract from X/Z position
    if (Math.sin(yAngle) < 0) xAnglePercentage = -xAnglePercentage;
    if (Math.cos(yAngle) < 0) zAnglePercentage = -zAnglePercentage;
    
    double moveByX = 0;
    double moveByZ = 0;
    
    
    if (goForward && !goBackward) 
    {
      //Northwest
      if (goLeft && !goRight)
      {
        moveByX = moveBy*(xAnglePercentage + zAnglePercentage);
        moveByZ = moveBy*(zAnglePercentage - xAnglePercentage);
        moveCharacter(moveByX, moveByZ);
        return;
      }
      //Northeast
      if (goRight && !goLeft)
      {
        moveByX = moveBy*(xAnglePercentage - zAnglePercentage);
        moveByZ = moveBy*(zAnglePercentage + xAnglePercentage);
        moveCharacter(moveByX, moveByZ);
        return;
      }
      //North
      else if ((!goRight && !goLeft) || (goLeft && goRight))
      {
        moveByX = moveBy*xAnglePercentage;
        moveByZ = moveBy*zAnglePercentage;
        moveCharacter(moveByX, moveByZ);
        return;
      }
    }
    if (goBackward && !goForward)
    {
      //Southwest
      if (goLeft && !goRight)
      {
        moveByX = moveBy*(-xAnglePercentage + zAnglePercentage);
        moveByZ =  moveBy*(-zAnglePercentage - xAnglePercentage);
        moveCharacter(moveByX, moveByZ);
        return;
      }
      //Southeast
      if (goRight && !goLeft)
      {
        moveByX = moveBy*(-xAnglePercentage - zAnglePercentage);
        moveByZ = moveBy*(-zAnglePercentage + xAnglePercentage);
        moveCharacter(moveByX, moveByZ);
        return;
      }
      //South
      else if ((!goRight && !goLeft) || (goRight && goLeft))
      {
        moveByX = -moveBy*xAnglePercentage;
        moveByZ = -moveBy*zAnglePercentage;
        moveCharacter(moveByX, moveByZ);
        return;
      }
    //West  
    }
    if (goLeft && !goRight)
    {
      moveByX = moveBy*zAnglePercentage;
      moveByZ = -moveBy*xAnglePercentage;
      moveCharacter(moveByX, moveByZ);
      return;
    }
    //East
    if (goRight && !goLeft)
    {
      moveByX = -moveBy*zAnglePercentage;
      moveByZ = moveBy*xAnglePercentage;
      moveCharacter(moveByX, moveByZ);
      return;
    }

    
  }
  
  public void moveCharacter(double moveByX, double moveByZ)
  {
    int oldXCell = (int) Math.floor(cameraXYtranslate.t.getX()/TILE_SIZE);
    int oldZCell = (int) Math.floor(cameraXYtranslate.t.getZ()/TILE_SIZE);
    double moveDifX = (cameraXYtranslate.t.getX() % TILE_SIZE) + moveByX;
    /*
    if (moveDifX-15 < 0)
    {
      if (floorPlan[oldXCell-1][oldZCell] && oldXCell > 0)
      {
        cameraXYtranslate.t.setZ(cameraXYtranslate.t.getZ() + moveByZ);
      }
    }
    else if (moveDifX + 15 > TILE_SIZE)
    {
      if (floorPlan[oldXCell+1][oldZCell] && oldXCell < 49)
      {
        cameraXYtranslate.t.setZ(cameraXYtranslate.t.getZ() + moveByZ);
      }
    }
    else cameraXYtranslate.t.setZ(cameraXYtranslate.t.getZ() + moveByZ);
    */
    cameraXYtranslate.t.setZ(cameraXYtranslate.t.getZ() + moveByZ);
    cameraXYtranslate.t.setX(cameraXYtranslate.t.getX() + moveByX);
    //cameraXYtranslate.t.setZ(newZ);
  }
  

  
  
  public class MainGameLoop extends AnimationTimer
  {
    @Override
    public void handle(long now)
    {
      if (isPaused)
      {
        if (startScene.getGameState())
        {
          //Gets the initial values
          zombieSpeed = startScene.getZombieSpeed();
          zombieSpawn = startScene.getZombieSpawn();
          zombieDecisionRate = startScene.getZombieDecision();
          zombieSmellDistance = startScene.getZombieSmell();
          //PlaceHolder for generating zombies...
          for (int i = 0; i < NUM_TILES; i++)
          {
            for (int j = 0; j < NUM_TILES; j++)
            {
              //Need an additional check to make sure zombies aren't spawning within a certain
              //distance of the player
              if (!floorPlan[i][j] && rand.nextFloat() < zombieSpawn)
              {
                Zombie z1 = new Zombie(true, i*TILE_SIZE, j*TILE_SIZE, 20, zombieSpeed, zombieSmellDistance);
                Xform z1Xform = z1.getXform();
                world.getChildren().add(z1Xform);
                zombieList.add(z1);
              }
              {
                
              }
            }
          }
          
          speed = startScene.getPlayerSpeed();
          distancePerSecond = speed*TILE_SIZE;
          hearing = startScene.getPlayerHearing();
          stamina = startScene.getPlayerStamina();
          staminaRegen = startScene.getPlayerRegen();
          
          stage.setScene(scene);
          isPaused = false;
        }
      }
      else
      {
      //Check for running/stamina depletion
        
        
        //Update character position
        updateCharacter((now - lastUpdate) / 1e9f);
        
        //Update zombie positions
        if ((now - lastZombieUpdate) /1e9f >= zombieDecisionRate)
        {
          for (Zombie zombie: zombieList)
          {
            zombie.changeDirection();
          }
          lastZombieUpdate = now;
        }
        for (Zombie zombie: zombieList)
        {
          zombie.move();
        }
        
        //Check for collisions
        for (Zombie z: zombieList)
        {
          //collision detection
        }
      }
        
        lastUpdate = now;
    }
  }
  
  
  
  @Override
  public void start(Stage primaryStage)
  {
    primaryStage.setMaximized(true);
    root.getChildren().add(world);

    

    buildCamera();
    generateRoom();
    gameLoop = new MainGameLoop();
    isPaused = true;
    
    primaryStage.setTitle("JavaFX Camera and Dungeon Test");
    primaryStage.setScene(startScene);
    primaryStage.show();
    
    stage = primaryStage;
    scene = new Scene(root, startScene.getWidth(), startScene.getHeight(), true);
    scene.setFill(Color.GREY);
    scene.setCamera(camera);
    //scene.setCursor(Cursor.NONE);
    handleKeyboard(scene, world);
    handleMouse(scene, world);
    
    
    //primaryStage.setScene(scene);
    
    gameLoop.start();
  }
  
  public static void main(String[] args)
  {
    launch(args);
  }

}
