package game;

import java.util.Random;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

public class Zombie extends MovingBody
{
  private final boolean RANDOM_WALK;
  private boolean playerDetected;
  public Xform zombieXYrotate = new Xform();
  public Xform zombieXYtranslate = new Xform();
  Random zRandom = new Random();
  
  private final double speed;
  private final double smellDistance;
  
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
  
  @Override
  public void move()
  {
    if (playerDetected); //A* Pathfinding
    else if (RANDOM_WALK) {
      randomWalk();//Random walk movement
    }
    else; //Line Walk movement
  }
  
  public void randomWalk()
  {
    double yAngle = zombieXYrotate.ry.getAngle() * Math.PI / 180;
    double xAnglePercentage = Math.sin(yAngle) * Math.sin(yAngle);
    double zAnglePercentage = Math.cos(yAngle) * Math.cos(yAngle);
    
    zombieXYtranslate.t.setZ(zombieXYtranslate.t.getZ() + speed*zAnglePercentage);
    zombieXYtranslate.t.setX(zombieXYtranslate.t.getX() + speed*xAnglePercentage);
  }
  
  public void changeDirection()
  {
    float newDirection = zRandom.nextFloat()*360;
    //DEPENDING ON HOW WE IMPLEMENT COLLISION, WE'LL DO MORE DETECTION HERE
    zombieXYrotate.setRotateY(newDirection);
  }
  
  public void randomMove()
  {
    
  }
  
  public Node drawZombie()
  {
    PhongMaterial material = new PhongMaterial();
    material.setDiffuseColor(Color.PALEGREEN);
    material.setSpecularColor(Color.DARKOLIVEGREEN);
    
    Cylinder zombieCylinder = new Cylinder(getRadius(), 150);
    zombieCylinder.setMaterial(material);
    return zombieCylinder;
  }

  public Xform getXform()
  {
    return zombieXYtranslate;
  }
  
  
}
