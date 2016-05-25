package pl.mpak.g2.background;

import java.awt.Image;

import pl.mpak.util.Controlable;

public abstract class BackgroundMover implements Controlable {

  protected float shiftX;
  protected float shiftY;
  protected Image image; 
  protected boolean enabled;
  protected BackgroundMover nextMover;
  
  public BackgroundMover(BackgroundMover nextMover) {
    super();
    shiftX = 0;
    shiftY = 0;
    enabled = true;
    this.nextMover = nextMover;
  }

  public int getShiftX() {
    return (int)shiftX;
  }
  
  public void setShiftX(float shiftX) {
    this.shiftX = shiftX;
  }

  public int getShiftY() {
    return (int)shiftY;
  }
  
  public void setShiftY(float shiftY) {
    this.shiftY = shiftY;
  }

  public void setImage(Image image) {
    this.image = image;
    if (nextMover != null) {
      nextMover.setImage(this.image);
    }
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

}
