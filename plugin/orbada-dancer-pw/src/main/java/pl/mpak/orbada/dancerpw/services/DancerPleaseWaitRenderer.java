/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.dancerpw.services;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import pl.mpak.orbada.dancerpw.OrbadaDancerPleaseWaitPlugin;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.plugins.providers.PleaseWaitRendererProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;

/**
 *
 * @author akaluza
 */
public class DancerPleaseWaitRenderer extends PleaseWaitRendererProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager("dancer-pw");

  public final static String uniqueId = "orbada-dancer-please-wait-renderer";

  private Image waitImage;
  private Rectangle renderRectangle;
  private Point mousePoint;
  private Point lastMousePoint;

  public DancerPleaseWaitRenderer() {
    waitImage = new ImageIcon(getClass().getResource("/res/dancer.gif")).getImage();
  }

  @Override
  public void beginProcess() {
  }

  @Override
  public void control() {
    lastMousePoint = mousePoint;
    if (MouseInfo.getPointerInfo() != null) {
      mousePoint = MouseInfo.getPointerInfo().getLocation();
      if (lastMousePoint != null) {
        renderRectangle = new Rectangle(
          Math.min(mousePoint.x, lastMousePoint.x),
          Math.min(mousePoint.y, lastMousePoint.y),
          (int)mousePoint.distance(lastMousePoint.x, lastMousePoint.x) +waitImage.getWidth(null),
          (int)mousePoint.distance(lastMousePoint.y, lastMousePoint.y) +waitImage.getHeight(null));
      }
      else {
        renderRectangle = new Rectangle(mousePoint.x, mousePoint.y, waitImage.getWidth(null), waitImage.getHeight(null));
      }
    }
  }

  @Override
  public void render(Graphics2D g2, PleaseWait[] waitArr) {
    if (mousePoint != null) {
      g2.drawImage(waitImage, mousePoint.x, mousePoint.y, null);
    }
  }

  @Override
  public String getDescription() {
    return stringManager.getString("pw-renderer-description");
  }

  @Override
  public String getGroupName() {
    return "OrbadaDancerPleaseWait";
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
