/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.octopuspw.services;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import pl.mpak.orbada.octopuspw.OrbadaOctopusPleaseWaitPlugin;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.plugins.providers.PleaseWaitRendererProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class OctopusPleaseWaitRenderer extends PleaseWaitRendererProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("octopus-pw");

  private final static int WAIT_X_SHIFT = 40;
  private final static int WAIT_Y_SHIFT = 10;
  public final static String uniqueId = "orbada-octopus-please-wait-renderer";

  private Rectangle renderRectangle;
  private Image waitImage;

  public OctopusPleaseWaitRenderer() {
    waitImage = new ImageIcon(getClass().getResource("/res/octopus.gif")).getImage();
  }

  @Override
  public void control() {
  }

  @Override
  public void render(Graphics2D g2, PleaseWait[] waitArr) {
    synchronized (this) {
      int yShift = 0;
      FontMetrics fm = g2.getFontMetrics();
      for (PleaseWait wait : waitArr) {
        if (wait.getMessage() != null) {
          yShift += fm.getHeight() +4;
        }
      }
      int left = component.getWidth() -WAIT_X_SHIFT;
      int top = component.getHeight() -WAIT_Y_SHIFT -yShift;
      if (waitImage != null) {
        g2.drawImage(
          waitImage,
          left -waitImage.getWidth(null),
          top -waitImage.getHeight(null),
          null);
      }
      yShift = 0;
      for (PleaseWait wait : waitArr) {
        if (wait.getMessage() != null) {
          g2.drawString(
            wait.getMessage(),
            left -(waitImage.getWidth(null) +fm.stringWidth(wait.getMessage())) /2,
            top);
          top += fm.getHeight() +1;
        }
      }
    }
  }

  @Override
  public String getDescription() {
    return stringManager.getString("OctopusPleaseWaitRenderer-description");
  }

  @Override
  public String getGroupName() {
    return "OrbadaOctopusPleaseWait";
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
