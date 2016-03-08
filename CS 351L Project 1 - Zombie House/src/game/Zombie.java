package game;

import java.util.Random;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

/**
 * Zombie Class
 * The class uses Xform and extends MovingBody in order to better refine movement for Zombies.
 * The zombie moves based on internal direction, but relies on external direction for any change of course.
 * 
 * @author Max Barnhart
 * @author Ederin Igharoro
 *
 */
public class Zombie extends MovingBody
{
  private final boolean RANDOM_WALK;
  private boolean playerDetected;
  public Xform zombieXYrotate = new Xform();
  public Xform zombieXYtranslate = new Xform();
  Random zRandom = new Random();
  
  private final double speed;
  private final double smellDistance;
  
  /**
   * Sets up the Xforms for the zombie, as well as adding the visual representation of the zombie to the Xforms. 
   * @param zombieType Type of zombie being constructed. True = random walk zombie. False = Line walk zombie.
   * @param xPos X-Coordinate of the center of the Zombie.
   * @param yPos Y-Coordinate of the center of the Zombie.
   * @param radius Radius of the Zombie.
   * @param zSpeed Speed of the zombie, in seconds.
   * @param smellDist Distance from which the zombie can smell the player.
   */
  public Zombie(boolean zombieType, double xPos, double yPos, double radius, double zSpeed, double smellDist)
  {
    super(xPos, yPos, radius);
    RANDOM_WALK = zombieType;
    playerDetected = false;
    speed = zSpeed;
    smellDistance = smellDist;
    
    
    zombieXYtranslate.setTranslateX(xPos);
    zombieXYtranslate.setTranslateZ(yPos);
    zombieXYtranslate.setTranslateY(-50);
    zombieXYtranslate.getChildren().add(zombieXYrotate);
    zombieXYrotate.getChildren().add(drawZombie());
  }
  
  /**
   * calls randomWalk() only, since A* pathfinding and Line walk
   * failed to be implemented in time.
   */
  public void move()
  {
    randomWalk();
    /*
    if (playerDetected); //A* Pathfinding
    else if (RANDOM_WALK) {
      randomWalk();//Random walk movement
    }
    else; //Line Walk movement*/
  }
  
  /**
   * randomWalk() calculates the components of force on a vector and moves the 
   * Zombie that far given his Speed/Direction.
   */
  public void randomWalk()
  {
    double yAngle = zombieXYrotate.ry.getAngle() * Math.PI / 180;
    double xAnglePercentage = Math.sin(yAngle) * Math.sin(yAngle);
    double zAnglePercentage = Math.cos(yAngle) * Math.cos(yAngle);
    
    zombieXYtranslate.t.setZ(zombieXYtranslate.t.getZ() + speed*zAnglePercentage);
    zombieXYtranslate.t.setX(zombieXYtranslate.t.getX() + speed*xAnglePercentage);
  }
  
  /**
   * Uses the random number generator to calculate a new random direction. Does not check
   * to make sure that the new angle is not the same as the last.
   */
  public void changeDirection()
  {
    float newDirection = zRandom.nextFloat()*360;
    //DEPENDING ON HOW WE IMPLEMENT COLLISION, WE'LL DO MORE DETECTION HERE
    zombieXYrotate.setRotateY(newDirection);
  }
  
  /**
   * For now, draws a cylinder with the expected radius and constant height 150.
   * Uses PhongMaterial to color the cylinder a vaguely zombie-colored hue.
   * @return The node to be rendered on screen to represent the zombie.
   */
  public Node drawZombie()
  {
    PhongMaterial material = new PhongMaterial();
    material.setDiffuseColor(Color.PALEGREEN);
    material.setSpecularColor(Color.DARKOLIVEGREEN);
    
    Cylinder zombieCylinder = new Cylinder(getRadius(), 150);
    zombieCylinder.setMaterial(material);
    return zombieCylinder;
  }

  /**
   * Returns the translation transform for the zombie.
   * @return the outermost transform which holds the rotation transform and zombie instance itself.
   */
  public Xform getXform()
  {
    return zombieXYtranslate;
  }
  
  
}
