package game;

import java.util.ArrayList;
import java.util.Random;

import io.Sounds;
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
import javafx.stage.WindowEvent;
import model.HouseGenerator;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * ZombieHouse Application ZombieHouse implements JavaFX 3D camera/geometry to
 * create a simple "escape the house" game. Using AnimationTimer, Mouse/Keyboard
 * handlers, the player tries to escape the house which is populated with
 * zombies. This is a first-person game in which the camera is essentially the
 * "eye" of the player.
 * 
 * Player movement is controlled by WASD in the normal fashion, with SHIFT
 * increasing speed by 2. The game assumes that JavaFX's AnimationTimer attempts
 * to update at 60 FPS as specified in its documentation. Zombie generation and
 * movement are both dependent on the classes which are a part of this package.
 * Mouse movement changes the viewing angle of the Scene/Room, although it does
 * not handle edge/off-screen movement. The Y-Axis is the UP/DOWN viewing angle
 * of the scene. The Z-AXIS is the FORWARD/BACKWARD viewing angle of the scene.
 * The X-AXis is the LEFT/RIGHT viewing angle of the scene.
 * 
 * @author Max Barnhart
 * @author Ederin Igharoro
 */
public class ZombieHouse extends Application
{
  // Game loop
  MainGameLoop gameLoop;
  boolean isPaused;
  boolean[][] floorPlan;

  final Group root = new Group();
  Xform world = new Xform();
  Stage stage;
  Scene scene;

  // Size of our tile
  final double TILE_SIZE = 50;
  final int NUM_TILES = 50;

  // Zombie Values
  ArrayList<Zombie> zombieList = new ArrayList<Zombie>();
  double zombieSpeed;
  double zombieSpawn;
  double zombieDecisionRate;
  double zombieSmellDistance;
  // Random Number generator for Zombie Spawn
  Random rand = new Random();

  // Character movement/hearing values
  boolean goForward = false;
  boolean goBackward = false;
  boolean goLeft = false;
  boolean goRight = false;
  boolean goRun = false;

  double moveBy = 5;
  double speed = 2;
  double distancePerSecond = speed * TILE_SIZE;
  double stamina = 5.0;
  double staminaRegen = 0.2;
  double hearing = 20;
  double charRadius = 30;

  // FPS fixing constants
  private long lastUpdate = 0;
  private long lastZombieUpdate = 0;

  // Camera transforms
  final Xform cameraXYrotate = new Xform();
  final Xform cameraXYtranslate = new Xform();
  final Xform cameraZrotate = new Xform();
  final PerspectiveCamera camera = new PerspectiveCamera(true);
  final Xform lightXform = new Xform();
  final PointLight light = new PointLight();

  // Camera initial values
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
  
  //Class callers
  HouseGenerator houseGen = new HouseGenerator(NUM_TILES);
  Sounds gameSound = new Sounds();
  StartScreen startScene = new StartScreen(new Group());

  /**
   * Builds the camera for the scene. Sets up a series of Transforms for easy
   * manipulation of the position of the Camera, which acts as the character in
   * the game. Sets up a PointLight so that it follows around and points at
   * wherever the camera is facing. A very-close, very-small rectangle is used
   * to display the center of the scene.
   */
  private void buildCamera()
  {
    root.getChildren().add(cameraXYtranslate);
    cameraXYtranslate.getChildren().add(cameraXYrotate);
    cameraXYrotate.getChildren().add(cameraZrotate);
    cameraZrotate.getChildren().add(camera);
    cameraZrotate.setRotateZ(180.0);

    camera.setNearClip(CAMERA_NEAR_CLIP);
    camera.setFarClip(CAMERA_FAR_CLIP);

    cameraXYtranslate.t.setX(NUM_TILES * TILE_SIZE / 2);
    cameraXYtranslate.t.setZ(NUM_TILES * TILE_SIZE / 2);

    // Adds a PointLight at the point of the camera so it rotates with the field
    // of view.
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
    Xform crosshairXform = new Xform();

    crosshairXform.getChildren().addAll(test, test2);
    crosshairXform.setTranslateZ(5);
    cameraXYrotate.getChildren().add(crosshairXform);

  }

  /**
   * handleMouse deals with the changing view of the camera based on mouse
   * inputs. UP/DOWN and LEFT/RIGHT alter the angle of viewing rather than any
   * physical distance.
   * 
   * @param scene
   *          Scene to which the EventHandlers are being attached.
   * @param root
   *          The node which the scene is attached to.
   */
  private void handleMouse(Scene scene, final Node root)
  {
    scene.setOnMouseMoved(new EventHandler<MouseEvent>()
    {
      @Override
      public void handle(MouseEvent me)
      {
        // Calculates old X/Y angles and new ones.
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
        // Checks to make sure you can't flip the camera upside down by
        // continually moving down.
        if (newMouseXAngle > 90) return;
        else if (newMouseXAngle < -90) return;
        else cameraXYrotate.rx.setAngle(newMouseXAngle);

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

  /**
   * Sets global boolean values to TRUE/FALSE based on input. AnimationTimer
   * itself handles the movement, but these EventHandlers dictates the necessity
   * of the action.
   * 
   * @param scene
   *          The scene to which the EventHandlers are being bound to.
   * @param root
   *          The Node which the screen is attached to.
   */
  private void handleKeyboard(Scene scene, final Node root)
  {
    scene.setOnKeyPressed(new EventHandler<KeyEvent>()
    {
      @Override
      public void handle(KeyEvent event)
      {
        gameSound.playerWalk.play();
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
        gameSound.playerWalk.stop();
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
          default:
            break;
        }
      }
    });
  }

  /**
   * Creates an empty floorPlan with nothing but walls surrounding it.
   * 
   * @return a 2D boolean array in which TRUE indicates a wall exists at that
   *         cell.
   */
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

  /**
   * generateRoom() takes the constants NUM_TILEs, TILE_SIZE, and creates a room
   * based on the specifications of the floorPlan[][]. Uses an algorithm which
   * follows Vertical/Horizontal walls to their end, so that clean,
   * visually-appealing walls are built.
   */
  private void generateRoom()
  {
    // IMAGE COMMENTED OUT BECAUSE ITS NOT ON GITHUB YET WHILE I PLAY WITH IT
    // Image textureImage = new
    // Image(getClass().getResourceAsStream("brick.jpg"));

    PhongMaterial testMaterial = new PhongMaterial();
    // testMaterial.setDiffuseMap(textureImage);
    testMaterial.setDiffuseColor(Color.WHITE);
    testMaterial.setSpecularColor(Color.TRANSPARENT);

    PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.BLACK);

    // CASE FOR houseGEN
    // world.getChildren().add(houseGen.wallXform);

    // Creates an array of floor tiles for the house
    Xform tileXform = new Xform();

    for (int i = 0; i < NUM_TILES; i++)
    {
      for (int j = 0; j < NUM_TILES; j++)
      {
        Box tileBox = new Box(TILE_SIZE - 1, 1, TILE_SIZE - 1);
        tileBox.setMaterial(redMaterial);
        tileBox.setTranslateZ(i * TILE_SIZE);
        tileBox.setTranslateX(j * TILE_SIZE);
        tileBox.setTranslateY(-1 * TILE_SIZE);
        tileXform.getChildren().add(tileBox);
      }
    }
    world.getChildren().add(tileXform);

    // Creates the walls for the floorPlan[][].
    Xform wallsXform = new Xform();
    floorPlan = makeFloorPlan();
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
        // Creating Horizontal Walls
        if (floorPlan[i][j] && unvisitedHorizontal[i][j])
        {
          int horizontalX = i;
          for (int k = i; k < NUM_TILES; k++)
          {
            unvisitedHorizontal[k][j] = false;
            if (floorPlan[k][j]) horizontalX += 1;
            else k = NUM_TILES;
          }
          if (horizontalX > i + 1)
          {
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
            box.setMaterial(testMaterial);
            wallsXform.getChildren().add(box);
          }
        }
        // Creating Vertical Walls
        if (floorPlan[i][j] && unvisitedVertical[i][j])
        {
          int verticalZ = j - 1;
          for (int k = j; k < NUM_TILES; k++)
          {
            unvisitedVertical[i][k] = false;
            if (floorPlan[i][k]) verticalZ += 1;
            else k = NUM_TILES;
          }
          if (verticalZ != j)
          {
            Box box = new Box(TILE_SIZE, TILE_SIZE * 4,
                TILE_SIZE * (verticalZ - j + 1));
            box.setBlendMode(BlendMode.SRC_OVER);
            box.setCullFace(CullFace.BACK);
            box.setTranslateX(i * TILE_SIZE);
            box.setTranslateZ((verticalZ + j) / 2 * TILE_SIZE);
            if ((verticalZ - j) / 2 % 1 > 0)
              box.setTranslateZ(box.getTranslateZ() + TILE_SIZE / 2);
            if (j % 2 > 0)
              box.setTranslateZ(box.getTranslateZ() + TILE_SIZE / 2);
            box.setMaterial(testMaterial);
            wallsXform.getChildren().add(box);
          }
        }

      }
    }
    world.getChildren().add(wallsXform);
    world.getChildren().add(houseGen.houseXform);
  }

  /**
   * updateCharacter() is called on each animation update. Checks to see how far
   * the character has to go based on the time elapsed since the last update so
   * that even with slow games the player moves at a constant rate. Calls a
   * second method moveCharacter(float X, float Y) because collision detection
   * was originally going in there, with this method being used to calculate the
   * change in X/Y position based on what keys were being pressed down.
   * 
   * @param timeElapsed
   *          The time, in nanoseconds, since the last animation update.
   */
  public void updateCharacter(float timeElapsed)
  {
    // Do stuff
    if (goRun && (stamina > timeElapsed)
        && (goLeft || goRight || goForward || goBackward))
    {
      moveBy = 2 * distancePerSecond * (timeElapsed);
      stamina -= timeElapsed;
    }
    else
    {
      moveBy = distancePerSecond * (timeElapsed);
      if (stamina < 5.0) stamina += timeElapsed * staminaRegen;
    }
    // moveBy = 5;

    double yAngle = cameraXYrotate.ry.getAngle() * Math.PI / 180;
    double xAnglePercentage = Math.sin(yAngle) * Math.sin(yAngle);
    double zAnglePercentage = Math.cos(yAngle) * Math.cos(yAngle);
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
        moveByX = moveBy * (xAnglePercentage + zAnglePercentage);
        moveByZ = moveBy * (zAnglePercentage - xAnglePercentage);
        moveCharacter(moveByX, moveByZ);
        return;
      }
      // Northeast
      if (goRight && !goLeft)
      {
        moveByX = moveBy * (xAnglePercentage - zAnglePercentage);
        moveByZ = moveBy * (zAnglePercentage + xAnglePercentage);
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
        moveByX = moveBy * (-xAnglePercentage + zAnglePercentage);
        moveByZ = moveBy * (-zAnglePercentage - xAnglePercentage);
        moveCharacter(moveByX, moveByZ);
        return;
      }
      // Southeast
      if (goRight && !goLeft)
      {
        moveByX = moveBy * (-xAnglePercentage - zAnglePercentage);
        moveByZ = moveBy * (-zAnglePercentage + xAnglePercentage);
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

  /**
   * Moves the character based on the given parameters.
   * 
   * @param moveByX
   *          Distance to be moved in the X direction. Cannot exceed the speed
   *          constant.
   * @param moveByZ
   *          Distance to be moved in the Z direction. Cannot exceed teh speed
   *          constant.
   */
  public void moveCharacter(double moveByX, double moveByZ)
  {
    cameraXYtranslate.t.setZ(cameraXYtranslate.t.getZ() + moveByZ);
    cameraXYtranslate.t.setX(cameraXYtranslate.t.getX() + moveByX);
  }

  /**
   * MainGameLoop is the primary handler of decisions/updates to the game state.
   * Initially, it waits for the Start Scene to finish its startup before
   * updating the positions of the Zombies and player. After that, it switches
   * to the primary scene and updating begins.
   * 
   * @author Max Barnhart
   *
   */
  public class MainGameLoop extends AnimationTimer
  {
    @Override
    public void handle(long now)
    {
      if (isPaused)
      {
        if (startScene.getGameState())
        {
          // Gets the initial values
          zombieSpeed = startScene.getZombieSpeed();
          zombieSpawn = startScene.getZombieSpawn();
          zombieDecisionRate = startScene.getZombieDecision();
          zombieSmellDistance = startScene.getZombieSmell();
          // PlaceHolder for generating zombies...
          for (int i = 0; i < NUM_TILES; i++)
          {
            for (int j = 0; j < NUM_TILES; j++)
            {
              // Need an additional check to make sure zombies aren't spawning
              // within a certain
              // distance of the player
              if (!floorPlan[i][j] && rand.nextFloat() < zombieSpawn)
              {
                Zombie z1 = new Zombie(true, i * TILE_SIZE, j * TILE_SIZE, 20,
                    zombieSpeed, zombieSmellDistance);
                Xform z1Xform = z1.getXform();
                world.getChildren().add(z1Xform);
                zombieList.add(z1);
              }
              {

              }
            }
          }

          speed = startScene.getPlayerSpeed();
          distancePerSecond = speed * TILE_SIZE;
          hearing = startScene.getPlayerHearing();
          stamina = startScene.getPlayerStamina();
          staminaRegen = startScene.getPlayerRegen();

          stage.setScene(scene);
          isPaused = false;
          gameSound.backGroundMusic.loop();
        }
      }
      else
      {

        // Update character position
        updateCharacter((now - lastUpdate) / 1e9f);

        // Update zombie positions
        if ((now - lastZombieUpdate) / 1e9f >= zombieDecisionRate)
        {
          for (Zombie zombie : zombieList)
          {
            zombie.changeDirection();
          }
          lastZombieUpdate = now;
        }
        for (Zombie zombie : zombieList)
        {
          zombie.move();
        }

        // Check for collisions
        for (Zombie z : zombieList)
        {
          // collision detection
        }
      }

      lastUpdate = now;
    }
  }

  @Override
  /**
   * Sets up the Game loop, the two different scenes used, animation loop, and
   * keyboard/mouse handlers.
   * 
   * @param primaryStage
   *          the Stage for the application
   */
  public void start(Stage primaryStage)
  {
    primaryStage.setMaximized(true);
    root.getChildren().add(world);

    buildCamera();
    generateRoom();
    gameLoop = new MainGameLoop();
    isPaused = true;

    primaryStage.setTitle("Ederin and Max's Zombie House");
    primaryStage.setScene(startScene);
    primaryStage.show();

    stage = primaryStage;
    scene = new Scene(root, startScene.getWidth(), startScene.getHeight(),
        true);
    scene.setFill(Color.GREY);
    scene.setCamera(camera);
    // scene.setCursor(Cursor.NONE);
    handleKeyboard(scene, world);
    handleMouse(scene, world);

    // primaryStage.setScene(scene);

    gameLoop.start();


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

  /**
   * Launches the application.
   * 
   * @param args
   *          Array of String arguments given to the program by the console
   *          based on user input.
   */
  public static void main(String[] args)
  {
    launch(args);
  }

}
