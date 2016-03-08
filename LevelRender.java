package game;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LevelRender extends Application
{
  //Game loop
  MainGameLoop gameLoop;
  HouseGenerator houseGen = new HouseGenerator();
  boolean[][] floorPlan;
  
  final Group root = new Group();
  Xform world = new Xform();
  
  //Size of our tile
  final double TILE_SIZE = 50;
  final int NUM_TILES = 50;
  
  //Character movement values
  boolean goForward = false;
  boolean goBackward = false;
  boolean goLeft = false;
  boolean goRight = false;
  boolean goRun = false;
  double moveBy = 5;
  double distancePerSecond = 2*TILE_SIZE;
  double stamina = 5.0;
  double staminaRegen = 0.2;
  double charRadius = 30;
  
  //FPS fixing constants
  private long fps = 60;
  private long lastUpdate = 0;
  
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

    //Adds a PointLight at the 
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
    Xform testXform = new Xform();
    Xform testXform2 = new Xform();
    Xform crosshairXform = new Xform();
    
    crosshairXform.getChildren().addAll(testXform, testXform2);
    crosshairXform.setTranslateZ(5);
    testXform.getChildren().add(test);
    testXform2.getChildren().add(test2);
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
    /***********************
     * GOING TO BE USED WHEN I IMPLEMENT ANIMATION TIMER...
     */
    scene.setOnMouseExited(new EventHandler<MouseEvent>() 
    {
      
      
      @Override
      public void handle(MouseEvent me)
      {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        System.out.println(me.getSceneX());
        if (me.getSceneX() <= 0)
        {
          
        }
        
        //do stuff
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
    
    Xform testBoxXform = new Xform();
    
    Box testBox = new Box(30, 30, 1);
    testBox.setMaterial(redMaterial);
    testBox.setRotationAxis(Rotate.X_AXIS);
    testBox.setRotate(90);
    testBoxXform.getChildren().add(testBox);
    testBoxXform.setTranslateY(-1*TILE_SIZE);
    cameraXYtranslate.getChildren().add(testBoxXform);
    
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
        if (floorPlan[i][j] && unvisitedHorizontal[i][j])
        {
          int horizontalX = i;
          //Creating Horizontal Walls
          for (int k = i; k < NUM_TILES; k++)
          {
            unvisitedHorizontal[k][j] = false;
            if (floorPlan[k][j]) horizontalX += 1;
            else k = NUM_TILES;
          }
          if (horizontalX > i+1)
          {
            System.out.println("i, horizX: (" + i + ", " + horizontalX + ") " + j);
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
        if (floorPlan[i][j] && unvisitedVertical[i][j])
        {
          int verticalZ = j-1;
          //Creating Vertical Walls
          for (int k = j; k < NUM_TILES; k++)
          {
            unvisitedVertical[i][k] = false;
            if (floorPlan[i][k]) verticalZ += 1;
            else k = NUM_TILES;
          }
          if (verticalZ != j)
          {
            System.out.println("j, verticleZ: (" + j + ", " + verticalZ + ") " + i);
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
    System.out.println(stamina);
    //moveBy = 5;
    
    double yAngle = cameraXYrotate.ry.getAngle() * Math.PI / 180;
    double xAnglePercentage = Math.sin(yAngle) * Math.sin(yAngle);
    double zAnglePercentage = Math.cos(yAngle) * Math.cos(yAngle);
    double maths = Math.sqrt(2)/2;
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
        moveByX = moveBy*(xAnglePercentage + zAnglePercentage)*maths;
        moveByZ = moveBy*(zAnglePercentage - xAnglePercentage)*maths;
        moveCharacter(moveByX, moveByZ);
        return;
      }
      //Northeast
      if (goRight && !goLeft)
      {
        moveByX = moveBy*(xAnglePercentage - zAnglePercentage)*maths;
        moveByZ = moveBy*(zAnglePercentage + xAnglePercentage)*maths;
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
        moveByX = moveBy*(-xAnglePercentage + zAnglePercentage)*maths;
        moveByZ =  moveBy*(-zAnglePercentage - xAnglePercentage)*maths;
        moveCharacter(moveByX, moveByZ);
        return;
      }
      //Southeast
      if (goRight && !goLeft)
      {
        moveByX = moveBy*(-xAnglePercentage - zAnglePercentage)*maths;
        moveByZ = moveBy*(-zAnglePercentage + xAnglePercentage)*maths;
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
    //do stuff
    //charRadius = 30
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
        //Check for running/stamina depletion
        
        
        //Update character position
        updateCharacter((now - lastUpdate) / 1e9f);
        
        //Update zombie positions
        
        //Check for collisions
        
        lastUpdate = now;
    }
  }
  
  
  
  @Override
  public void start(Stage primaryStage)
  {
    primaryStage.setMaximized(true);
    root.getChildren().add(world);
    
    Zombie z1 = new Zombie(true, 200.0, 350.0, 20);
    Xform z1Xform = new Xform();
    z1Xform.setTranslateX(z1.getXPosition());
    z1Xform.setTranslateZ(z1.getYPosition());
    z1Xform.setTranslateY(-50);
    z1Xform.getChildren().add(z1.drawZombie());
    world.getChildren().add(z1Xform);

    

    buildCamera();
    generateRoom();
    gameLoop = new MainGameLoop();
    
    
    Scene scene = new Scene(root, 1000, 1000, true);
    scene.setFill(Color.GREY);
    scene.setCamera(camera);
    //scene.setCursor(Cursor.NONE);
    handleKeyboard(scene, world);
    handleMouse(scene, world);
    
    primaryStage.setTitle("JavaFX Camera and Dungeon Test");
    primaryStage.setScene(scene);
    primaryStage.show();
    
    gameLoop.start();
  }
  
  public static void main(String[] args)
  {
    launch(args);
  }

}
