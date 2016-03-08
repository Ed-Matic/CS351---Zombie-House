package game;


/**
 * MovingBody is an abstract class which outlines some common characteristics of non-static objects in the 3D rendering world. 
 * MovingBody assumes that the object is circular, and implements collision detection with other MovingBodies. It requires
 * the implementation of the abstract method move(), which is expected to move the body based on some internal scheme.
 * @author Max Barnhart
 * @author Ederin Igharoro
 *
 */
public abstract class MovingBody
{
  private double xPos;
  private double yPos;
  private double radius;
  
  /**
   * 
   * @param xPos X-coordinate of the MovingBody's center.
   * @param yPos Y-coordinate of the MovingBody's center.
   * @param radius Radius of the MovingBody.
   */
  public MovingBody(double xPos, double yPos, double radius)
  {
    this.xPos = xPos;
    this.yPos = yPos;
    this.radius = radius;
  }
  
  /**
   * CloseIntersect is a (relatively) expensive method of checking for intersection. More expensive than bounding box
   * Used only when the boundingBox method detects an intersection.
   * @param other Another instance of MovingBody.
   * @return True if the radius of the bounding circle intersects the other MovingBody, false otherwise.
   *  
   */
  public boolean closeIntersect(MovingBody other)
  {
    double radiusOther = other.getRadius();
    double xOther = other.getXPosition();
    double yOther = other.getYPosition();
    double radiiDif1 = (radiusOther - radius) * (radiusOther - radius);
    double radiiDif2 = (radiusOther + radius) * (radiusOther + radius);
    double centerDif = (xOther - xPos) * (xOther - xPos) + (yOther - yPos) * (yOther - yPos);
    
    if (radiiDif1 <= centerDif || centerDif <= radiiDif2) return true;
    else return false;
  }
  
  /**
   * Checks to see if the bounding box of this MovingBody intersects with the bounding box of the given MovingBody.
   * @param other Another instance of MovingBody.
   * @return returns false if boundingBox fails, returns the value of closeIntersect() otherwise.
   */
  public boolean boundingBoxIntersect(MovingBody other)
  {
    double otherX = other.getXPosition()-other.getRadius();
    double otherY = other.getYPosition()-other.getRadius();
    double width = other.getRadius() * 2;
    
    if (otherX > (xPos + 2*radius)
        || (otherX + 2*width) < xPos
        || otherY < (yPos + 2*radius)
        || (otherY + 2*width) < yPos)
    {
      return false;
    }
    else return closeIntersect(other);
  }
  
  /**
   * 
   * @return The radius of the MovingBody
   */
  protected double getRadius()
  {
    return radius; 
  }
  
  /**
   * 
   * @return The X-coordinate of the MovingBody's center.
   */
  protected double getXPosition()
  {
    return xPos;
  }
  
  /**
   * 
   * @return The Y-coordinate of the MovingBody's center.
   */
  protected double getYPosition()
  {
    return yPos;
  }
  
  /**
   * Moves the MovingBody based on some internal criteria, rather than outside management.
   */
  public abstract void move();

}
