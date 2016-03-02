package game;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

public class Zombie extends MovingBody
{
  private final boolean RANDOM_WALK;
  private boolean playerDetected;
  protected Xform zombieXform = new Xform();
  
  public Zombie(boolean zombieType, double xPos, double yPos, double radius)
  {
    super(xPos, yPos, radius);
    RANDOM_WALK = zombieType;
    playerDetected = false;
    zombieXform.setTranslateX(xPos);
    zombieXform.setTranslateZ(yPos);
    zombieXform.setTranslateY(-50);
  }
  
  @Override
  public void move()
  {
    if (playerDetected); //A* Pathfinding
    else if (RANDOM_WALK); //Random walk movement
    else; //Line Walk movement
  }
  
  //METHODS TO BE ADDED?
  //drawZombie() 
  
  public Node drawZombie()
  {
    PhongMaterial material = new PhongMaterial();
    material.setDiffuseColor(Color.PALEGREEN);
    material.setSpecularColor(Color.DARKOLIVEGREEN);
    
    Cylinder zombieCylinder = new Cylinder(getRadius(), 150);
    zombieCylinder.setMaterial(material);
    return zombieCylinder;
  }
  
  
}
