package game;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LevelRender extends Application
{
  
  final Group root = new Group();
  Xform world = new Xform();
  Xform axisGroup = new Xform();
  
  final Xform cameraXYrotate = new Xform();
  final Xform cameraXYtranslate = new Xform();
  final Xform cameraZrotate = new Xform();
  final PerspectiveCamera camera = new PerspectiveCamera(true);
  
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
  private static final double TRACK_SPEED = 0.3;
  
  private void buildCamera()
  {
    root.getChildren().add(cameraXYrotate);
    //cameraXYrotate.getChildren().add(cameraXYtranslate);
    //cameraXYtranslate.getChildren().add(cameraZrotate);
    //Testing to see...
    cameraXYtranslate.getChildren().add(cameraXYrotate);
    cameraXYrotate.getChildren().add(cameraZrotate);
    cameraZrotate.getChildren().add(camera);
    cameraZrotate.setRotateZ(180.0);

    camera.setNearClip(CAMERA_NEAR_CLIP);
    camera.setFarClip(CAMERA_FAR_CLIP);
    camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    cameraXYrotate.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    cameraXYrotate.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    
    axisGroup.setTranslateZ(1000);
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
        double modifier = 1.0;
        
        cameraXYrotate.rx.setAngle(cameraXYrotate.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);
        cameraXYrotate.ry.setAngle(cameraXYrotate.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);
      }
    });
    scene.setOnMouseExited(new EventHandler<MouseEvent>() 
    {
      
      
      @Override
      public void handle(MouseEvent me)
      {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        System.out.println(me.getSceneX());
        
        //do stuff
      }
    });
    scene.setOnMousePressed(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent me) {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        }
    });
    scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
        @Override public void handle(MouseEvent me) {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX); 
            mouseDeltaY = (mousePosY - mouseOldY); 
            
            double modifier = 1.0;
            
            if (me.isControlDown()) {
                modifier = 0.1;
            } 
            if (me.isShiftDown()) {
                modifier = 10;
            }     
            if (me.isPrimaryButtonDown()) {
                cameraXYrotate.ry.setAngle(cameraXYrotate.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);  
                cameraXYrotate.rx.setAngle(cameraXYrotate.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);  
            }
            else if (me.isSecondaryButtonDown()) {
                double z = camera.getTranslateZ();
                double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                camera.setTranslateZ(newZ);
            }
            else if (me.isMiddleButtonDown()) {
                cameraXYtranslate.t.setX(cameraXYtranslate.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);  
                cameraXYtranslate.t.setY(cameraXYtranslate.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);  
            }
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
        double xAngle = cameraXYrotate.rx.getAngle() * Math.PI / 180;
        double zAngle = cameraXYrotate.ry.getAngle() * Math.PI / 180;
        double xAnglePercentage = Math.sin(zAngle);
        double zAnglePercentage = Math.cos(zAngle);
        System.out.println("Angles: (" + xAngle + ", " + zAngle + ")");
        
        double moveBy = 5;
        
        switch (event.getCode())
        {
        case W:
          cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() + moveBy*xAnglePercentage));
          cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() + moveBy*zAnglePercentage));
          break;
        case A:
          cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() + moveBy*zAnglePercentage));
          cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() - moveBy*xAnglePercentage));
          break;
        case S:
          cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() - moveBy*xAnglePercentage));
          cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() - moveBy*zAnglePercentage));
          break;
        case D:
          cameraXYtranslate.t.setX((cameraXYtranslate.t.getX() - moveBy*zAnglePercentage));
          cameraXYtranslate.t.setZ((cameraXYtranslate.t.getZ() + moveBy*xAnglePercentage));
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
}
  
  private void generateRoom()
  {
    /*
    PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);
    
    PhongMaterial whiteMaterial = new PhongMaterial();
    whiteMaterial.setDiffuseColor(Color.ALICEBLUE);
    whiteMaterial.setSpecularColor(Color.BLACK);
    
    PhongMaterial greenMaterial = new PhongMaterial();
    greenMaterial.setDiffuseColor(Color.DARKOLIVEGREEN);
    greenMaterial.setSpecularColor(Color.LIGHTGREEN);
    
    Xform roomXform = new Xform();
    Xform floorXform = new Xform();
    Xform northWallXform = new Xform();
    Xform eastWallXform = new Xform();
    Xform southWallXform = new Xform();
    Xform westWallXform = new Xform();
    
    Box floor = new Box(1000, 1000, 1);
    floor.setMaterial(redMaterial);
    floor.setRotationAxis(Rotate.X_AXIS);
    floor.setRotate(-90);
    
    Box northWall = new Box(1000, 100, 1);
    northWall.setMaterial(whiteMaterial);
    northWall.setRotationAxis(Rotate.Y_AXIS);
    northWall.setRotate(0.0);
    
    Box eastWall = new Box(1000, 100, 1);
    eastWall.setMaterial(greenMaterial);
    eastWall.setRotationAxis(Rotate.Y_AXIS);
    eastWall.setRotate(0.0);
    
    Box southWall = new Box(1000, 100, 1);
    southWall.setMaterial(whiteMaterial);
    southWall.setRotationAxis(Rotate.Y_AXIS);
    southWall.setRotate(0.0);
    
    Box westWall = new Box(1000, 100, 1);
    westWall.setMaterial(greenMaterial);
    westWall.setRotationAxis(Rotate.Y_AXIS);
    westWall.setRotate(0.0);
    
    //more boxes
    
    floorXform.setTranslateY(100);
    northWallXform.setTranslateZ(-500);
    southWallXform.setTranslateZ(500);
    eastWallXform.setTranslateX(500);
    westWallXform.setTranslateX(-500);
    
    floorXform.getChildren().add(floor);
    northWallXform.getChildren().add(northWall);
    southWallXform.getChildren().add(southWall);
    eastWallXform.getChildren().add(eastWall);
    westWallXform.getChildren().add(westWall);
    
    roomXform.getChildren().addAll(northWallXform, southWallXform, eastWallXform, westWallXform, floorXform);
    
    world.getChildren().add(roomXform);
    */
    PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);
    
    Xform testBoxXform = new Xform();
    
    Box testBox = new Box(50, 50, 1);
    testBox.setMaterial(redMaterial);
    testBox.setRotationAxis(Rotate.X_AXIS);
    testBox.setRotate(90);
    testBoxXform.getChildren().add(testBox);
    testBoxXform.setTranslateY(-100);
    
    world.getChildren().add(testBoxXform);
    
    
    
  }
  
  
  @Override
  public void start(Stage primaryStage)
  {
    /*
    PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);
    
    PhongMaterial whiteMaterial = new PhongMaterial();
    whiteMaterial.setDiffuseColor(Color.ALICEBLUE);
    whiteMaterial.setSpecularColor(Color.BLACK);
    
    Box boxTest = new Box(100, 100, 1);
    boxTest.setMaterial(redMaterial);
    boxTest.setRotationAxis(Rotate.Y_AXIS);
    boxTest.setRotate(90);
    
    Box boxTest2 = new Box(100, 100, 1);
    boxTest2.setMaterial(whiteMaterial);
    boxTest2.setRotationAxis(Rotate.Y_AXIS);
    boxTest2.setRotate(90);
    
    Xform terrainXform = new Xform();
    Xform boxXform = new Xform();
    Xform boxXform2 = new Xform();
    boxXform.setTranslateX(50);
    boxXform2.setTranslateX(-50);
    
    
    boxXform.getChildren().add(boxTest);
    boxXform2.getChildren().add(boxTest2);
    terrainXform.getChildren().addAll(boxXform, boxXform2);
    world.getChildren().add(terrainXform);
    */
    root.getChildren().add(world);
    
    buildCamera();
    buildAxes();
    generateRoom();
    
    
    Scene scene = new Scene(root, 1000, 1000, true);
    scene.setFill(Color.GREY);
    scene.setCamera(camera);
    handleKeyboard(scene, world);
    handleMouse(scene, world);
    
    primaryStage.setTitle("JavaFX Camera and Dungeon Test");
    primaryStage.setScene(scene);
    primaryStage.show();
  }
  
  public static void main(String[] args)
  {
    launch(args);
  }

}
