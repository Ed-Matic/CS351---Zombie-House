package game;

import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * StartScreen is an implementation of Scene which holds an array of sliders used to
 * set the starting specifications of the ZombieHouse game.
 * @author Max Barnhart
 * @author Ederin Igharoro
 *
 */
public class StartScreen extends Scene
{

  protected boolean startGame = false;
  double zombieSpeed;
  double zombieSpawn;
  double zombieDecisionRate;
  double zombieSmellDistance;
  
  double playerSpeed;
  double playerHearing;
  double playerStamina;
  double playerStamRegen;
  
  Button startButton = new Button("Start");
  Slider zomSpawn = new Slider(0, .1, 0.010);
  Slider zomSpeed = new Slider(0, 5, 0.5);
  Slider zomDecisionRate = new Slider(0, 5, 2);
  Slider zomSmellDist = new Slider(0, 30, 15);
  
  Slider plSpeed = new Slider(1, 10, 2);
  Slider plHearing = new Slider(0, 50, 20);
  Slider plStamina = new Slider(0, 25, 5);
  Slider plRegen = new Slider(0, 5, 0.5);
  
  /**
   * Sets the actionEvent for the button so the game begins once it's pressed.
   * Every component is stored in a BorderPane for easy visibility/differentiation.
   * @param root the Group to which the scene is being added.
   */
  public StartScreen(Group root)
  {
    
    super(root, 1000, 1000);
    setFill(Color.DIMGRAY);
    Text titleText = new Text("Game Attributes");
    
    startButton.setOnAction(new EventHandler<ActionEvent>() 
    {
      @Override 
      public void handle(ActionEvent e)
      {
        startGame = true;
      }
    });
    
    BorderPane pane = new BorderPane();
    pane.setPadding(new Insets(20));
    pane.setTop(titleText);
    pane.setCenter(makeZombieVBox());
    pane.setRight(makePlayerVBox());
    pane.setBottom(startButton);
    root.getChildren().add(pane);
    
  }
  
  /**
   * Creates the side of the scene which allows the user to manipulate the base strengths of the player.
   * @return A new VBox holding the sliders for the player values.
   */
  private VBox makePlayerVBox()
  {
    VBox playerBox = new VBox();
    playerBox.setSpacing(20);
    playerBox.setPadding(new Insets(20));
    
    Text title = new Text("Player Attributes");
    Text pSpeed = new Text("Player Speed: ");
    Text pHearing = new Text("Player Hearing: ");
    Text pStamina = new Text("Player Stamina: ");
    Text pRegen = new Text("Player Stamina Regeneration: ");
    playerBox.getChildren().addAll(title, pSpeed, plSpeed, pHearing, plHearing, pStamina, plStamina, pRegen, plRegen);
    
    plSpeed.setShowTickMarks(true);
    plSpeed.setShowTickLabels(true);
    plSpeed.setMajorTickUnit(0.5f);
    plSpeed.setBlockIncrement(1f);
    plSpeed.setSnapToTicks(true);
    
    plHearing.setShowTickMarks(true);
    plHearing.setShowTickLabels(true);
    plHearing.setMajorTickUnit(2.5f);
    plHearing.setBlockIncrement(10f);
    
    plStamina.setShowTickMarks(true);
    plStamina.setShowTickLabels(true);
    plStamina.setMajorTickUnit(1f);
    plStamina.setBlockIncrement(5f);
    plStamina.setSnapToTicks(true);
    
    plRegen.setShowTickMarks(true);
    plRegen.setShowTickLabels(true);
    plRegen.setMajorTickUnit(.25f);
    plRegen.setBlockIncrement(1f);
    plRegen.setSnapToTicks(true);
    
    return playerBox;
  }
  
  /**
   * Creates the side of the scene which allows the user to manipulate the base strengths of the zombies.
   * @return A new VBox holding the sliders for the zombie values.
   */
  private VBox makeZombieVBox()
  {
    VBox zombieBox = new VBox();
    zombieBox.setSpacing(20);
    zombieBox.setPadding(new Insets(20));
    
    Text title = new Text("Zombie Attributes");
    title.setUnderline(true);
    Text zSpawn = new Text("Zombie Spawn Chance Per Tile: ");
    Text zSpeed = new Text("Zombie Speed: ");
    Text zDecision = new Text("Zombie Decision Time: ");
    Text zSmell = new Text("Zombie Smell Distance: ");
    zombieBox.getChildren().addAll(title, zSpawn, zomSpawn, zSpeed, zomSpeed, zDecision, zomDecisionRate, zSmell, zomSmellDist);
    
    zomSpawn.setShowTickMarks(true);
    zomSpawn.setShowTickLabels(true);
    zomSpawn.setMajorTickUnit(0.01f);
    zomSpawn.setBlockIncrement(0.01f);
    zomSpawn.setSnapToTicks(true);
    
    zomSpeed.setShowTickMarks(true);
    zomSpeed.setShowTickLabels(true);
    zomSpeed.setMajorTickUnit(0.25f);
    zomSpeed.setBlockIncrement(0.5f);
    zomSpeed.setSnapToTicks(true);
    
    zomDecisionRate.setShowTickMarks(true);
    zomDecisionRate.setShowTickLabels(true);
    zomDecisionRate.setMajorTickUnit(0.25f);
    zomDecisionRate.setBlockIncrement(0.5f);
    zomDecisionRate.setSnapToTicks(true);
    
    zomSmellDist.setShowTickMarks(true);
    zomSmellDist.setShowTickLabels(true);
    zomSmellDist.setMajorTickUnit(1f);
    zomSmellDist.setBlockIncrement(5f);
    zomSmellDist.setSnapToTicks(true);
    
    return zombieBox;
  }
  
  /**
   * Returns true if the startScreen wants the game to start.
   * @return The boolean startGame
   */
  public boolean getGameState()
  {
    return startGame;
  }
  
  /**
   * 
   * @return the slider value for player speed.
   */
  public double getPlayerSpeed()
  {
    return plSpeed.getValue();
  }
  
  /**
   * 
   * @return The slider value for player hearing.
   */
  public double getPlayerHearing()
  {
    return plHearing.getValue();
  }
  
  /**
   * 
   * @return The slider value for player stamina .
   */
  public double getPlayerStamina()
  {
    return plStamina.getValue();
  }
  
  /**
   * 
   * @return The slider value for player stamina regeneration.
   */
  public double getPlayerRegen()
  {
    return plRegen.getValue();
  }
  
  /**
   * 
   * @return The slider value for zombie speed.
   */
  public double getZombieSpeed() 
  {
    return zomSpeed.getValue();
  }
  
  /**
   * 
   * @return The slider value for zombie spawn chance on a tile.
   */
  public double getZombieSpawn() 
  {
    return zomSpawn.getValue();
  }
  
  /**
   * 
   * @return The slider value for zombie decision time frame.
   */
  public double getZombieDecision() 
  {
    return zomDecisionRate.getValue();
  }
  
  /**
   * 
   * @return The slider value for zombie smell distance.
   */
  public double getZombieSmell() 
  {
    return zomSmellDist.getValue();
  }
  
}
