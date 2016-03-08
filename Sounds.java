package io;

public class Sounds
{
  public SoundPlayer backGroundMusic, playerWalk;
  
  public Sounds()
  {
    try
    {
      backGroundMusic = new SoundPlayer("Background_Music.wav");
      playerWalk = new SoundPlayer("footsteps.wav");
      
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
