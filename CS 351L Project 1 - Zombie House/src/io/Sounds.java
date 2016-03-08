package io;

/**
* Sounds implements the sound Player class. Uses
* WAV formated audio files for music and players'
* footsteps
* @author Ederin Igharoro
*/
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
