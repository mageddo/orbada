package pl.mpak.g2.background;

public class BackgroundCircle extends BackgroundMover {

  private int radius;
  private float degrees;
  private float speed;
  
  public BackgroundCircle(int radius, float speed, BackgroundMover nextMover) {
    super(nextMover);
    this.radius = radius;
    this.speed = speed;
    degrees = 0;
  }

  public BackgroundCircle(int radius, float speed) {
    this(radius, speed, null);
  }

  public void control() {
    if (enabled) {
      degrees += speed;
      if (degrees >= 360) {
        degrees = 0;
      }
      else if (degrees < 0) {
        degrees = 360;
      }
      shiftX = (float)Math.cos((double)degrees *Math.PI /180) *radius;
      shiftY = (float)Math.sin((double)degrees *Math.PI /180) *radius;
      if (nextMover != null) {
        nextMover.control();
        shiftX += nextMover.getShiftX();
        shiftY += nextMover.getShiftY();
      }
    }
  }
  
}
