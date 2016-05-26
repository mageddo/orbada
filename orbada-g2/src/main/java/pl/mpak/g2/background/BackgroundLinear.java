package pl.mpak.g2.background;

public class BackgroundLinear extends BackgroundMover {

  private float shiftHoriz;
  private float shiftVert;

  public BackgroundLinear(float shiftHoriz, float shiftVert, BackgroundMover nextMover) {
    super(nextMover);
    this.shiftHoriz = shiftHoriz;
    this.shiftVert = shiftVert;
  }

  public BackgroundLinear(float shiftHoriz, float shiftVert) {
    this(shiftHoriz, shiftVert, null);
  }

  public void control() {
    if (enabled) {
      if (shiftVert != 0) {
        shiftY += shiftVert;
        if (shiftY < 0) {
          shiftY = image.getHeight(null) -1;
        }
        else if (shiftY >= image.getHeight(null)) {
          shiftY = 0;
        }
      }
      if (shiftHoriz != 0) {
        shiftX += shiftHoriz;
        if (shiftX < 0) {
          shiftX = image.getWidth(null) -1;
        }
        else if (shiftX >= image.getWidth(null)) {
          shiftX = 0;
        }
      }
      if (nextMover != null) {
        nextMover.control();
        shiftX += nextMover.getShiftX();
        shiftY += nextMover.getShiftY();
      }
    }
  }

}
