package game;

public abstract class MovingBody
{
  private double xPos;
  private double yPos;
  private double radius;
  
  public MovingBody(double xPos, double yPos, double radius)
  {
    this.xPos = xPos;
    this.yPos = yPos;
    this.radius = radius;
  }
  
  /******
   * 
   * @param other, another instance of MovingBody.
   * @return True if the radius of the bounding circle intersects the other MovingBody, false otherwise.
   * A (relatively) expensive method of checking for intersection. More expensive than bounding box
   * Used only when the boundingBox method detects an intersection. 
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
  
  /*******
   * 
   * @param other
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
  
  protected double getRadius()
  {
    return radius; 
  }
  
  protected double getXPosition()
  {
    return xPos;
  }
  
  protected double getYPosition()
  {
    return yPos;
  }
  
  public abstract void move();

}
