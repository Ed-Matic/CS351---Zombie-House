package game;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
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
  
  final Group root = new Group();
  Xform world = new Xform();
  Xform axisGroup = new Xform();
  
  //Size of our tile
  final long TILE_SIZE = 50;
  
  //Character movement values
  boolean goForward = false;
  boolean goBackward = false;
  boolean goLeft = false;
  boolean goRight = false;
  boolean goRun = false;
  double moveBy = 5;
  
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
  private static final double CAMERA_INITIAL_DISTANCE = 0;
  private static final double CAMERA_INITIAL_X_ANGLE = 0;//70;
  private static final double CAMERA_INITIAL_Y_ANGLE = 0;//320;
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
    camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    cameraXYrotate.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    cameraXYrotate.rx.setAngle(CAMERA_INITIAL_X_ANGLE);

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
        }
      } 
    });
  }
  
  private void buildAxes() {
    System.out.println("buildAxes()");
    final PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);

    final PhongMaterial greenMaterial = new PhongMaterial();
    greenMaterial.setDiffuseColor(Color.DARKGREEN);
    greenMaterial.setSpecularColor(Color.GREEN);

    final PhongMaterial blueMaterial = new PhongMaterial();
    blueMaterial.setDiffuseColor(Color.DARKBLUE);
    blueMaterial.setSpecularColor(Color.BLUE);

    final Box xAxis = new Box(250, 1, 1);
    final Box yAxis = new Box(1, 250, 1);
    final Box zAxis = new Box(1, 1, 250);

    xAxis.setMaterial(redMaterial);
    yAxis.setMaterial(greenMaterial);
    zAxis.setMaterial(blueMaterial);

    axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
    axisGroup.setVisible(true);
    world.getChildren().addAll(axisGroup);
    
    axisGroup.setTranslateZ(1000);
}
  
  private void generateRoom()
  {
    //IMAGE COMMENTED OUT BECAUSE ITS NOT ON GITHUB YET WHILE I PLAY WITH IT
    //Image textureImage = new Image(getClass().getResourceAsStream("brick.jpg"));
    
    PhongMaterial testMaterial = new PhongMaterial();
    //testMaterial.setDiffuseMap(textureImage);
    testMaterial.setDiffuseColor(Color.WHITE);
    testMaterial.setSpecularColor(Color.BLACK);
    
    PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.BLACK);
    
    PhongMaterial whiteMaterial = new PhongMaterial();
    whiteMaterial.setDiffuseColor(Color.CORNFLOWERBLUE);
    whiteMaterial.setSpecularColor(Color.LIGHTBLUE);
    
    PhongMaterial greenMaterial = new PhongMaterial();
    greenMaterial.setDiffuseColor(Color.DARKOLIVEGREEN);
    greenMaterial.setSpecularColor(Color.LIGHTGREEN);
    
    Xform roomXform = new Xform();
    Xform floorXform = new Xform();
    Xform northWallXform = new Xform();
    Xform eastWallXform = new Xform();
    Xform southWallXform = new Xform();
    Xform westWallXform = new Xform();
    
    Box floor = new Box(TILE_SIZE*20, TILE_SIZE*20, TILE_SIZE);
    floor.setMaterial(redMaterial);
    floor.setRotationAxis(Rotate.X_AXIS);
    floor.setRotate(-90);
    
    Box northWall = new Box(TILE_SIZE*20, TILE_SIZE*4, TILE_SIZE);
    northWall.setMaterial(testMaterial);
    
    Box eastWall = new Box(TILE_SIZE*20, TILE_SIZE*4, TILE_SIZE);
    eastWall.setMaterial(testMaterial);
    eastWallXform.setRotationAxis(Rotate.Y_AXIS);
    eastWallXform.setRotate(90);
    
    Box southWall = new Box(TILE_SIZE*20, TILE_SIZE*4, TILE_SIZE);
    southWall.setMaterial(testMaterial);
    
    Box westWall = new Box(TILE_SIZE*20, TILE_SIZE*4, TILE_SIZE);
    westWall.setMaterial(testMaterial);
    westWallXform.setRotationAxis(Rotate.Y_AXIS);
    westWallXform.setRotate(90);
    
    //more boxes
    
    floorXform.setTranslateY(TILE_SIZE*2);
    northWallXform.setTranslateZ(-TILE_SIZE*10);
    southWallXform.setTranslateZ(TILE_SIZE*10);
    eastWallXform.setTranslateX(TILE_SIZE*10);
    westWallXform.setTranslateX(-TILE_SIZE*10);
    
    floorXform.getChildren().add(floor);
    northWallXform.getChildren().add(northWall);
    southWallXform.getChildren().add(southWall);
    eastWallXform.getChildren().add(eastWall);
    westWallXform.getChildren().add(westWall);
    
    roomXform.getChildren().addAll(northWallXform, southWallXform, eastWallXform, westWallXform, floorXform);
    
    world.getChildren().add(roomXform);
    
    //CASE FOR houseGEN
    world.getChildren().add(houseGen.wallXform);
    
    for (int i = -1000; i < 1000; i += TILE_SIZE)
    {
      Xform testXform = new Xform();
      Xform testXform2 = new Xform();
      Box testBox = new Box(2000, 1, 1);
      Box testBox2 = new Box(1, 2000, 1);
      testBox.setMaterial(redMaterial);
      testBox.setRotationAxis(Rotate.X_AXIS);
      testBox.setRotate(90);
      testXform.getChildren().add(testBox);
      testXform.setTranslateY(-100);
      testXform.setTranslateZ(i);
      testBox2.setMaterial(redMaterial);
      testBox2.setRotationAxis(Rotate.X_AXIS);
      testBox2.setRotate(90);
      testXform2.getChildren().add(testBox2);
      testXform2.setTranslateY(-100);
      testXform2.setTranslateX(i);
      world.getChildren().addAll(testXform, testXform2);
    }
    
    Xform testBoxXform = new Xform();
    
    Box testBox = new Box(50, 50, 1);
    testBox.setMaterial(redMaterial);
    testBox.setRotationAxis(Rotate.X_AXIS);
    testBox.setRotate(90);
    testBoxXform.getChildren().add(testBox);
    testBoxXform.setTranslateY(-100);
    
    world.getChildren().add(testBoxXform);
  }
  
  public void updateCharacter()
  {
    //Do stuff
    double yAngle = cameraXYrotate.ry.getAngle() * Math.PI / 180;
    double xAnglePercentage = Math.sin(yAngle) * Math.sin(yAngle);
    double zAnglePercentage = Math.cos(yAngle) * Math.cos(yAngle);
    double maths = Math.sqrt(2)/2;
        //Keeps the sign of the sin/cos since it's needed for seeing whether to add/subtract from X/Z position
    if (Math.sin(yAngle) < 0) xAnglePercentage = -xAnglePercentage;
    if (Math.cos(yAngle) < 0) zAnglePercentage = -zAnglePercentage;
    
    
    if (goForward && !goBackward) 
    {
      //Northwest
      if (goLeft && !goRight)
      {
        
        cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() + moveBy*(xAnglePercentage + zAnglePercentage)*maths));
        cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() + moveBy*(zAnglePercentage - xAnglePercentage)*maths));
        return;
      }
      //Northeast
      if (goRight && !goLeft)
      {
        cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() + moveBy*(xAnglePercentage - zAnglePercentage)*maths));
        cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() + moveBy*(zAnglePercentage + xAnglePercentage)*maths));
        return;
      }
      //North
      else if ((!goRight && !goLeft) || (goLeft && goRight))
      {
        System.out.println("(" + xAnglePercentage + ", " + zAnglePercentage + ")");
        cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() + moveBy*xAnglePercentage));
        cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() + moveBy*zAnglePercentage));
        
        return;
      }
    }
    if (goBackward && !goForward)
    {
      //Southwest
      if (goLeft && !goRight)
      {
        cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() + moveBy*(-xAnglePercentage + zAnglePercentage)*maths));
        cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() + moveBy*(-zAnglePercentage - xAnglePercentage)*maths));
        return;
      }
      //Southeast
      if (goRight && !goLeft)
      {
        cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() + moveBy*(-xAnglePercentage - zAnglePercentage)*maths));
        cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() + moveBy*(-zAnglePercentage + xAnglePercentage)*maths));
        return;
      }
      //South
      else if ((!goRight && !goLeft) || (goRight && goLeft))
      {
        cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() - moveBy*xAnglePercentage));
        cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() - moveBy*zAnglePercentage));
        return;
      }
    //West  
    }
    if (goLeft && !goRight)
    {
      cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() + moveBy*zAnglePercentage));
      cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() - moveBy*xAnglePercentage));
      return;
    }
    //East
    if (goRight && !goLeft)
    {
      cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() - moveBy*zAnglePercentage));
      cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() + moveBy*xAnglePercentage));
      return;
    }

    
  }
  

  
  
  public class MainGameLoop extends AnimationTimer
  {
    long updateWindow = 1000/fps;
    @Override
    public void handle(long now)
    {
      long difference = now - lastUpdate;
      lastUpdate = now;
      if (difference >= updateWindow)
      {
        //Check for running/stamina depletion
        
        //Update character position
        updateCharacter();
        
        //Update zombie positions
        
        //Check for collisions
      }
    }
  }
  
  
  
  @Override
  public void start(Stage primaryStage)
  {
    root.getChildren().add(world);
    
    Zombie z1 = new Zombie(true, 200.0, 350.0, 20);
    Xform z1Xform = new Xform();
    z1Xform.setTranslateX(z1.getXPosition());
    z1Xform.setTranslateZ(z1.getYPosition());
    z1Xform.setTranslateY(-50);
    z1Xform.getChildren().add(z1.drawZombie());
    world.getChildren().add(z1Xform);

    

    buildCamera();
    buildAxes();
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
