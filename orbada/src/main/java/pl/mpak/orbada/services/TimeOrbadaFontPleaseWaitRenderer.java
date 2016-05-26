/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.services;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import pl.mpak.orbada.Consts;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.core.Application;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.plugins.providers.PleaseWaitRendererProvider;
import pl.mpak.util.StringManager;
import pl.mpak.util.StringManagerFactory;
import pl.mpak.util.StringUtil;

/**
 *
 * @author akaluza
 */
public class TimeOrbadaFontPleaseWaitRenderer extends PleaseWaitRendererProvider {

  private final static StringManager stringManager = StringManagerFactory.getStringManager(Consts.class);

  private final static int WAIT_X_SHIFT = 10;
  private final static int WAIT_Y_SHIFT = 5;
  public final static String uniqueId = "time-orbada-font-please-wait-renderer";

  private Rectangle renderRectangle;
  private long startTime;
  private long diffTime;
  private int left;
  private int top;

  public TimeOrbadaFontPleaseWaitRenderer() {
  }

  @Override
  public void beginProcess() {
    startTime = System.nanoTime();
  }

  @Override
  public void control() {
    diffTime = System.nanoTime() -startTime;
    calcRectangle();
  }

  private void calcRectangle() {
    int width = Application.get().getOrbadaFont().getWidth(StringUtil.formatTime(diffTime).toUpperCase());
    int height = Application.get().getOrbadaFont().getHeight(StringUtil.formatTime(diffTime).toUpperCase());
    left = component.getWidth() -WAIT_X_SHIFT -width;
    top = component.getHeight() -WAIT_Y_SHIFT -height;
    renderRectangle = new Rectangle(left -50, top -5, width +50, height +10);
  }

  @Override
  public void render(Graphics2D g2, PleaseWait[] waitArr) {
    if (renderRectangle == null) {
      calcRectangle();
    }
    Application.get().getOrbadaFont().draw(left, top, g2, StringUtil.formatTime(diffTime).toUpperCase());
  }

  @Override
  public String getDescription() {
    return stringManager.getString("TimeOrbadaFontPleaseWaitRenderer-description");
  }

  @Override
  public String getGroupName() {
    return "TimeOrbadaFontPleaseWait";
  }

  @Override
  public String getRendererId() {
    return uniqueId;
  }

  @Override
  public Rectangle getRenderBounds() {
    if (renderRectangle == null) {
      calcRectangle();
    }
    return renderRectangle;
  }

}
