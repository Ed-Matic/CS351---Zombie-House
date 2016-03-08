package game;

import io.Sounds;
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
import javafx.stage.WindowEvent;
import model.HouseGenerator;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class LevelRender extends Application
{
  // Game loop
  MainGameLoop gameLoop;

  boolean[][] floorPlan;

  final Group root = new Group();
  Xform world = new Xform();

  // Size of our tile
  final double TILE_SIZE = 100;
  final int NUM_TILES = 100;

  HouseGenerator houseGen = new HouseGenerator(NUM_TILES);
  Sounds gameSound = new Sounds();

  // Character movement values
  boolean goForward = false;
  boolean goBackward = false;
  boolean goLeft = false;
  boolean goRight = false;
  boolean goRun = false;
  double moveBy = 15;
  double charRadius = 30;

  // FPS fixing constants
  private long fps = 60;
  private long lastUpdate = 0;

  // Camera transforms
  final Xform cameraXYrotate = new Xform();
  final Xform cameraXYtranslate = new Xform();
  final Xform cameraZrotate = new Xform();
  final PerspectiveCamera camera = new PerspectiveCamera(true);
  final Xform lightXform = new Xform();
  final PointLight light = new PointLight();

  // Camera initial values
  private static final double CAMERA_INITIAL_DISTANCE = 0;
  private static final double CAMERA_INITIAL_X_ANGLE = 0;// 70;
  private static final double CAMERA_INITIAL_Y_ANGLE = 0;// 320;
  private static final double CAMERA_NEAR_CLIP = 0.1;
  private static final double CAMERA_FAR_CLIP = 10000.0;

  // Mouse values
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

    cameraXYtranslate.t.setX((NUM_TILES * TILE_SIZE / 2));
    cameraXYtranslate.t.setZ((NUM_TILES * TILE_SIZE / 2) - 100);

    // Adds a PointLight at the
    PointLight light = new PointLight();
    light.setColor(Color.WHITE);
    lightXform.getChildren().add(light);
    cameraXYrotate.getChildren().add(lightXform);
    light.getScope().add(world);

    // Sets up a reticle in the center of the screen
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

  private void handleMouse(Scene scene, final Node root)
  {
    scene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent me)
      {
        // do stuff
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = mousePosX - mouseOldX;
        mouseDeltaY = mousePosY - mouseOldY;

        double modifier = 0.75;

        double newMouseXAngle = cameraXYrotate.rx.getAngle()
            + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED;
        cameraXYrotate.ry.setAngle(cameraXYrotate.ry.getAngle()
            - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
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

        // do stuff
      }
    });
    // This might be superfluous once we figure out mouse control better.
    // Used to make sure the mouseMoved doesn't destroy our field of view
    // When the mouse reenters the scene.
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
        //gameSound.playerWalk.play();
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
          default:
            gameSound.playerWalk.stop();
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
            gameSound.playerWalk.stop();
            break;
          case A:
            goLeft = false;
            gameSound.playerWalk.stop();
            break;
          case S:
            goBackward = false;
            gameSound.playerWalk.stop();
            break;
          case D:
            goRight = false;
            gameSound.playerWalk.stop();
            break;
          default:
            gameSound.playerWalk.stop();
            break;
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
      floorPlan[i][NUM_TILES - 1] = true;
      floorPlan[NUM_TILES - 1][i] = true;
    }
    for (int i = 1; i < NUM_TILES - 1; i++)
    {
      for (int j = 1; j < NUM_TILES - 1; j++)
      {
        floorPlan[i][j] = false;
      }
    }

    return floorPlan;
  }

  private void generateRoom()
  {
    // IMAGE COMMENTED OUT BECAUSE ITS NOT ON GITHUB YET WHILE I PLAY WITH IT
    Image textureImage = new Image(
        getClass().getResourceAsStream("GameWall.jpg"));

    PhongMaterial material = new PhongMaterial();
    material.setDiffuseMap(textureImage);

    PhongMaterial testMaterial = new PhongMaterial();
    // testMaterial.setDiffuseMap(textureImage);
    testMaterial.setDiffuseColor(Color.WHITE);
    testMaterial.setSpecularColor(Color.TRANSPARENT);

    PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.BLACK);

    PhongMaterial whiteMaterial = new PhongMaterial();
    whiteMaterial.setDiffuseColor(Color.CORNFLOWERBLUE);
    whiteMaterial.setSpecularColor(Color.LIGHTBLUE);

    // material.setSpecularColor(Color.WHITE);

    // CASE FOR houseGEN
    // world.getChildren().add(houseGen.houseXform);

    Xform gridXform = new Xform();
    for (float i = 0; i < NUM_TILES; i += 1)
    {
      Xform testXform = new Xform();
      Xform testXform2 = new Xform();
      Xform testXform3 = new Xform();
      Box testBox = new Box(NUM_TILES * TILE_SIZE, 1, 1);
      Box testBox2 = new Box(1, NUM_TILES * TILE_SIZE, 1);
      Box testBox3 = new Box(NUM_TILES * TILE_SIZE, 1, NUM_TILES);
      testBox.setMaterial(redMaterial);
      testBox.setRotationAxis(Rotate.X_AXIS);
      testBox.setRotate(90);
      testXform.getChildren().add(testBox);
      testXform.setTranslateY(-2 * TILE_SIZE);
      testXform.setTranslateZ(i * TILE_SIZE);
      testXform.setTranslateX(NUM_TILES * TILE_SIZE / 2);

      testBox2.setMaterial(redMaterial);
      testBox2.setRotationAxis(Rotate.X_AXIS);
      testBox2.setRotate(90);
      testXform2.getChildren().add(testBox2);
      testXform2.setTranslateY(-2 * TILE_SIZE);
      testXform2.setTranslateX(i * TILE_SIZE);
      testXform2.setTranslateZ(NUM_TILES * TILE_SIZE / 2);

      testBox3.setMaterial(material);
      testBox3.setRotationAxis(Rotate.Y_AXIS);
      testBox3.setRotate(90);
      testXform3.getChildren().add(testBox3);
      testXform3.setTranslateY(2 * TILE_SIZE);
      testXform3.setTranslateX(i * TILE_SIZE);
      testXform3.setTranslateZ(NUM_TILES * TILE_SIZE / 2);

      gridXform.getChildren().addAll(testXform, testXform2, testXform3);
    }
    gridXform.setTranslateX(-TILE_SIZE / 2);
    gridXform.setTranslateZ(-TILE_SIZE / 2);
    world.getChildren().add(gridXform);

    Xform testBoxXform = new Xform();

    Box testBox = new Box(30, 30, 1);
    testBox.setMaterial(redMaterial);
    testBox.setRotationAxis(Rotate.X_AXIS);
    testBox.setRotate(90);
    testBoxXform.getChildren().add(testBox);
    testBoxXform.setTranslateY(-100);
    cameraXYtranslate.getChildren().add(testBoxXform);

    Xform wallsXform = new Xform();
    floorPlan = makeFloorPlan();
    for (int i = 4; i < 7; i++)
      floorPlan[22][i] = true;
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
          // Creating Horizontal Walls
          for (int k = i; k < NUM_TILES; k++)
          {
            unvisitedHorizontal[k][j] = false;
            if (floorPlan[k][j]) horizontalX += 1;
            else k = NUM_TILES;
          }
          if (horizontalX > i + 1)
          {
            System.out
                .println("i, horizX: (" + i + ", " + horizontalX + ") " + j);
            Box box = new Box((horizontalX - i + 1) * TILE_SIZE, TILE_SIZE * 4,
                TILE_SIZE);
            box.setBlendMode(BlendMode.SRC_OVER);
            box.setCullFace(CullFace.BACK);
            box.setTranslateX(i * TILE_SIZE / 2);
            box.setTranslateX(
                box.getTranslateX() + (horizontalX - i) * TILE_SIZE / 2);
            if ((horizontalX - i) / 2 % 1 > 0)
              box.setTranslateX(box.getTranslateX() + TILE_SIZE / 2);
            if (i % 2 > 0)
              box.setTranslateX(box.getTranslateX() + TILE_SIZE / 2);
            box.setTranslateZ(j * TILE_SIZE);
            box.setMaterial(material);
            wallsXform.getChildren().add(box);
          }


          
        }
        if (floorPlan[i][j] && unvisitedVertical[i][j])
        {
          int verticalZ = j - 1;
          // Creating Vertical Walls. THIS IS WAL WHERE 2 walls were drawn
          for (int k = j; k < NUM_TILES; k++)
          {
            unvisitedVertical[i][k] = false;
            if (floorPlan[i][k]) verticalZ += 1;
            else k = NUM_TILES;
          }
          if (verticalZ != j)
          {
            System.out
                .println("j, verticleZ: (" + j + ", " + verticalZ + ") " + i);
            Box box = new Box(TILE_SIZE, TILE_SIZE * 4,
                TILE_SIZE * (verticalZ - j + 1));
            box.setBlendMode(BlendMode.SRC_OVER);
            box.setCullFace(CullFace.BACK);
            box.setTranslateX(i * TILE_SIZE);
            box.setTranslateZ((verticalZ) / 2 * TILE_SIZE);
            if ((verticalZ - j) / 2 % 1 > 0)
              box.setTranslateZ(box.getTranslateZ() + TILE_SIZE / 2);
            if (j % 2 > 0)
              box.setTranslateZ(box.getTranslateZ() + TILE_SIZE / 2);
            box.setMaterial(material);
            wallsXform.getChildren().add(box);
          }
          
        }

      }
    }
    world.getChildren().add(wallsXform);
    //world.getChildren().add(houseGen.houseXform);
  }

  public void updateCharacter()
  {
    // Do stuff
    double yAngle = cameraXYrotate.ry.getAngle() * Math.PI / 180;
    double xAnglePercentage = Math.sin(yAngle) * Math.sin(yAngle);
    double zAnglePercentage = Math.cos(yAngle) * Math.cos(yAngle);
    double maths = Math.sqrt(2) / 2;
    // Keeps the sign of the sin/cos since it's needed for seeing whether to
    // add/subtract from X/Z position
    if (Math.sin(yAngle) < 0) xAnglePercentage = -xAnglePercentage;
    if (Math.cos(yAngle) < 0) zAnglePercentage = -zAnglePercentage;

    double moveByX = 0;
    double moveByZ = 0;

    if (goForward && !goBackward)
    {
      // Northwest
      if (goLeft && !goRight)
      {
        moveByX = moveBy * (xAnglePercentage + zAnglePercentage) * maths;
        moveByZ = moveBy * (zAnglePercentage - xAnglePercentage) * maths;
        moveCharacter(moveByX, moveByZ);
        return;
      }
      // Northeast
      if (goRight && !goLeft)
      {
        moveByX = moveBy * (xAnglePercentage - zAnglePercentage) * maths;
        moveByZ = moveBy * (zAnglePercentage + xAnglePercentage) * maths;
        moveCharacter(moveByX, moveByZ);
        return;
      }
      // North
      else if ((!goRight && !goLeft) || (goLeft && goRight))
      {
        moveByX = moveBy * xAnglePercentage;
        moveByZ = moveBy * zAnglePercentage;
        moveCharacter(moveByX, moveByZ);
        return;
      }
    }
    if (goBackward && !goForward)
    {
      // Southwest
      if (goLeft && !goRight)
      {
        moveByX = moveBy * (-xAnglePercentage + zAnglePercentage) * maths;
        moveByZ = moveBy * (-zAnglePercentage - xAnglePercentage) * maths;
        moveCharacter(moveByX, moveByZ);
        return;
      }
      // Southeast
      if (goRight && !goLeft)
      {
        moveByX = moveBy * (-xAnglePercentage - zAnglePercentage) * maths;
        moveByZ = moveBy * (-zAnglePercentage + xAnglePercentage) * maths;
        moveCharacter(moveByX, moveByZ);
        return;
      }
      // South
      else if ((!goRight && !goLeft) || (goRight && goLeft))
      {
        moveByX = -moveBy * xAnglePercentage;
        moveByZ = -moveBy * zAnglePercentage;
        moveCharacter(moveByX, moveByZ);
        return;
      }
      // West
    }
    if (goLeft && !goRight)
    {
      moveByX = moveBy * zAnglePercentage;
      moveByZ = -moveBy * xAnglePercentage;
      moveCharacter(moveByX, moveByZ);
      return;
    }
    // East
    if (goRight && !goLeft)
    {
      moveByX = -moveBy * zAnglePercentage;
      moveByZ = moveBy * xAnglePercentage;
      moveCharacter(moveByX, moveByZ);
      return;
    }

  }

  public void moveCharacter(double moveByX, double moveByZ)
  {
    // do stuff
    double newX = moveByX + cameraXYtranslate.t.getX();
    double newZ = moveByZ + cameraXYtranslate.t.getZ();
    // charRadius = 30
    int oldZCell = (int) Math.floor(cameraXYtranslate.t.getX() / TILE_SIZE);
    int oldXCell = (int) Math.floor(cameraXYtranslate.t.getZ() / TILE_SIZE);
    int zCell = (int) Math.floor(newZ / TILE_SIZE);
    int xCell = (int) Math.floor(newX / TILE_SIZE);
    System.out
        .println("(" + xCell + ", " + zCell + ") : " + floorPlan[xCell][zCell]);
    // if (zCell - oldZCell > 0)
    // {
    // if (xCell - oldXCell > 0)
    // {
    if (!floorPlan[xCell][zCell])
    {
      // Regular Movement
      cameraXYtranslate.t.setX(newX);
      cameraXYtranslate.t.setZ(newZ);
    }
    else if (floorPlan[oldXCell][zCell])
    {
      // Z Movement only
      cameraXYtranslate.t.setZ(newZ);
    }
    else if (floorPlan[xCell][oldZCell])
    {
      // X Movement only
      cameraXYtranslate.t.setX(newX);
    }
    // }
    // }

    // cameraXYtranslate.t.setX(newX);
    // cameraXYtranslate.t.setZ(newZ);
  }

  public class MainGameLoop extends AnimationTimer
  {
    long updateWindow = 1000 / fps;

    @Override
    public void handle(long now)
    {
      long difference = now - lastUpdate;
      lastUpdate = now;
      if (difference >= updateWindow)
      {
        // Check for running/stamina depletion

        // Update character position
        updateCharacter();

        // Update zombie positions

        // Check for collisions

      }
    }
  }

  @Override
  public void start(Stage primaryStage)
  {
    primaryStage.setMaximized(true);
    root.getChildren().add(world);

    // Zombie z1 = new Zombie(true, 200.0, 350.0, 20);
    Xform z1Xform = new Xform();
    // z1Xform.setTranslateX(z1.getXPosition());
    // z1Xform.setTranslateZ(z1.getYPosition());
    // z1Xform.setTranslateY(-50);
    // z1Xform.getChildren().add(z1.drawZombie());
    // world.getChildren().add(z1Xform);

    buildCamera();
    generateRoom();
    gameLoop = new MainGameLoop();

    Scene scene = new Scene(root, 1000, 1000, true);
    scene.setFill(Color.GREY);
    scene.setCamera(camera);
    // scene.setCursor(Cursor.NONE);
    handleKeyboard(scene, world);
    handleMouse(scene, world);

    primaryStage.setTitle("JavaFX Camera and Dungeon Test");
    primaryStage.setScene(scene);
    primaryStage.show();

    gameLoop.start();
    //gameSound.backGroundMusic.loop();

    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
    {
      @Override
      public void handle(WindowEvent event)
      {
        try
        {
          gameSound.backGroundMusic.stop();
          gameSound.playerWalk.stop();
          stop();
        }
        catch (Exception e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
  }

  public static void main(String[] args)
  {
    launch(args);
  }

}