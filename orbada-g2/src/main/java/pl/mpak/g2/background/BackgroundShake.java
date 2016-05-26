package pl.mpak.g2.background;

import java.util.Random;

public class BackgroundShake extends BackgroundMover {

  private int noise;
  private float speed;
  private float shiftHoriz;
  private float shiftVert;
  private Random random;
  
  public BackgroundShake(int noise, float speed, BackgroundMover nextMover) {
    super(nextMover);
    this.noise = noise;
    this.speed = speed;
    random = new Random(System.currentTimeMillis());
    newShiftHoriz();
    newShiftVert();
  }
  
  public BackgroundShake(int noise, float speed) {
    this(noise, speed, null);
  }
  
  private void newShiftHoriz() {
    shiftHoriz = random.nextInt(noise *2) -noise;
  }

  private void newShiftVert() {
    shiftVert = random.nextInt(noise *2) -noise;
  }

  public void control() {
    if (enabled) {
      if (shiftHoriz > shiftX) {
        shiftX += speed;
        if (shiftX > shiftHoriz) {
          newShiftHoriz();
        }
      }
      else if (shiftHoriz < shiftX) {
        shiftX -= speed;
        if (shiftX < shiftHoriz) {
          newShiftHoriz();
        }
      }
      else {
        newShiftHoriz();
      }
      if (shiftVert > shiftY) {
        shiftY += speed;
        if (shiftY > shiftVert) {
          newShiftVert();
        }
      }
      else if (shiftVert < shiftY) {
        shiftY -= speed;
        if (shiftY < shiftVert) {
          newShiftVert();
        }
      }
      else {
        newShiftVert();
      }
      if (nextMover != null) {
        nextMover.control();
        shiftX += nextMover.getShiftX();
        shiftY += nextMover.getShiftY();
      }
    }
  }

}
