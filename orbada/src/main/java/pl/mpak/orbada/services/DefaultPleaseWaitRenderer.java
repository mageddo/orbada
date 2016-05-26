/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.services;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.plugins.providers.PleaseWaitRendererProvider;
import pl.mpak.sky.gui.swing.ImageManager;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DefaultPleaseWaitRenderer extends PleaseWaitRendererProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private final static int WAIT_X_SHIFT = 10;
  private final static int WAIT_Y_SHIFT = 10;
  public final static String uniqueId = "orbada-default-please-wait-renderer";

  private int waitAniPic;
  private ArrayList<Image> waitImageList;
  private Image waitBackground;
  private Rectangle renderRectangle;

  public DefaultPleaseWaitRenderer() {
    waitImageList = new ArrayList<Image>();
    for (int i=0; i<12; i++) {
      waitImageList.add(ImageManager.getImage(String.format("/res/wait/wait%02d.png", new Object[] {i})).getImage());
    }
    waitBackground = ImageManager.getImage("/res/wait/waitbg.png").getImage();
    //waitBackground = ImageManager.getImage("/res/wait/juggernaut.gif").getImage();
  }

  @Override
  public void control() {
    if (++waitAniPic >= 12) {
      waitAniPic = 0;
    }
    if (renderRectangle == null) {
      int left = component.getWidth() -WAIT_X_SHIFT -30 -waitBackground.getWidth(null);
      int top = component.getHeight() -WAIT_Y_SHIFT -30 -waitBackground.getHeight(null);
      renderRectangle = new Rectangle(left, top, component.getWidth() -left, component.getHeight() -top);
    }
  }

  @Override
  public void render(Graphics2D g2, PleaseWait[] waitArr) {
    FontMetrics fm = g2.getFontMetrics();
    int left = component.getWidth() -WAIT_X_SHIFT;
    int top = component.getHeight() -WAIT_Y_SHIFT;
    g2.drawImage(
      waitBackground,
      left -waitBackground.getWidth(null),
      top -waitBackground.getHeight(null),
      null);
    int picWidth = 0;
    for (PleaseWait wait : waitArr) {
      if (wait.getImage() != null) {
        picWidth += wait.getImage().getWidth(null);
      }
    }
    int picPos = 0;
    for (PleaseWait wait : waitArr) {
      if (wait.getImage() != null) {
        g2.drawImage(
          wait.getImage(),
          left -(waitBackground.getWidth(null) +picWidth) /2 +picPos,
          top -(waitBackground.getHeight(null) +wait.getImage().getHeight(null)) /2,
          null);
        picPos += wait.getImage().getWidth(null);
      }
    }
    Image image = waitImageList.get(waitAniPic);
    g2.drawImage(
      image,
      left -(waitBackground.getWidth(null) +image.getWidth(null)) /2,
      top -(waitBackground.getHeight(null) +image.getHeight(null)) /2,
      null);
    int textPos = 0;
    for (PleaseWait wait : waitArr) {
      if (wait.getMessage() != null) {
        g2.drawString(
          wait.getMessage(),
          left -(waitBackground.getWidth(null) +fm.stringWidth(wait.getMessage())) /2,
          top -(waitBackground.getHeight(null) +textPos) /2 +20);
        textPos += fm.getHeight() +4;
      }
    }
  }

  @Override
  public String getDescription() {
    return stringManager.getString("DefaultPleaseWaitRenderer-default-display");
  }

  @Override
  public String getGroupName() {
    return "OrbadaDefaultPleaseWait";
  }

  @Override
  public String getRendererId() {
    return uniqueId;
  }

  @Override
  public Rectangle getRenderBounds() {
    return renderRectangle;
  }

}
