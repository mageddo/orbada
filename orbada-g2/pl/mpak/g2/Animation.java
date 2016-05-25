package pl.mpak.g2;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;

import pl.mpak.util.Controlable;
import pl.mpak.util.Drawable;

public class Animation implements Controlable, Drawable {

  private PictureList pictureList;
  private boolean enabled;
  private int picture;
  
  public enum Mode {NONE, LOOP, ONCE, REVERT_LOOP, REVERT_ONCE};
  private Mode mode;
  
  public Animation(PictureList pictureList, Mode mode) {
    super();
    this.pictureList = pictureList;
    enabled = true;
    picture = -1;
    this.mode = mode;
  }

  public Animation(PictureList pictureList) {
    this(pictureList, Mode.LOOP);
  }

  public Animation(PictureList pictureList, int animIndex) {
    this(pictureList, Mode.LOOP);
    picture = animIndex;
  }

  /**
   * <p>Constructor loading images and put all to the list
   * @param filePattern for example "filename%04d.png"
   * @param pictures how many pictures are loaded
   * @throws IllegalArgumentException
   * @throws InterruptedException
   * @throws MalformedURLException 
   */
  public Animation(String filePattern, int pictures) throws IllegalArgumentException, InterruptedException, MalformedURLException {
    this(new PictureList(filePattern, pictures));
  }
  
  public Animation(String fileName, int picturesX, int picturesY) throws IllegalArgumentException, InterruptedException, MalformedURLException {
    this(new PictureList(fileName, picturesX, picturesY));
  }
  
  public Animation(URL url, int picturesX, int picturesY) throws IllegalArgumentException, InterruptedException {
    this(new PictureList(url, picturesX, picturesY));
  }
  
  public void control() {
    if (enabled && mode != Mode.NONE) {
      if (++picture == pictureList.getCount()) {
        if (mode == Mode.LOOP) {
          picture = 0;
        }
        else {
          mode = Mode.NONE;
          picture = -1;
        }
      }
    }
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }
  
  /**
   * <p>Starting animation
   */
  public void start() {
    setEnabled(true);
  }
  
  public void start(Mode mode) {
    setEnabled(true);
    setMode(mode);
  }
  
  /**
   * <p>Stopping animation and set current picture to first
   */
  public void stop() {
    picture = -1;
    setEnabled(false);
  }
  
  /**
   * <p>Pause animation
   */
  public void pause() {
    setEnabled(false);
  }
  
  public boolean isStopped() {
    return picture == -1;
  }
  
  public BufferedImage getImage(int picture) {
    return pictureList.getImage(picture);
  }
  
  /**
   * <p>Getting current image
   * @return
   */
  public BufferedImage getImage() {
    return pictureList.getImage(getCurrent());
  }
  
  public int getCount() {
    return pictureList.getCount();
  }

  public void draw(Graphics g, int x, int y, ImageObserver observer) {
    g.drawImage(getImage(), x, y, observer);
  }

  public void setMode(Mode mode) {
    if (this.mode != mode || ((this.mode == Mode.ONCE || this.mode == Mode.REVERT_ONCE) && this.mode == mode)) {
      this.mode = mode;
      picture = 0;
    }
  }

  public Mode getMode() {
    return mode;
  }
  
  public int getCurrent() {
    int tpic = picture < 0 ? 0 : picture;
    return (mode == Mode.REVERT_ONCE || mode == Mode.REVERT_LOOP) ? (getCount() -tpic -1) : tpic;
  }

}
