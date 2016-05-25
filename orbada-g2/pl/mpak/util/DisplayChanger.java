package pl.mpak.util;

import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

public class DisplayChanger {

  private GraphicsEnvironment graphicsEnvironment;
  private GraphicsDevice graphicsDevice;
  private GraphicsDevice[] graphicsDevices;
  private boolean fullScreenSupported;
  private boolean displayChangeSupported;
  private Window window;
  private DisplayMode[] displayModes;
  private DisplayMode oryginalDisplayMode = null;
  private DisplayMode displayMode;
  private boolean undecorated;
  private int oryginalWidth;
  private int oryginalHeight;
  private boolean undecorable = true;
  private boolean exclusiveMode = true;
  
  public DisplayChanger(Window window) {
    this.window = window;
    graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    graphicsDevices = graphicsEnvironment.getScreenDevices();
    if (graphicsDevices == null) {
      System.out.println("No Graphics Devices !");
      System.exit(-1);
    }
    setGraphicsDevice(graphicsEnvironment.getDefaultScreenDevice());
  }

  public boolean isUndecorable() {
    return undecorable;
  }

  public void setUndecorable(boolean undecorable) {
    this.undecorable = undecorable;
  }

  public boolean isExclusiveMode() {
    return exclusiveMode;
  }

  public void setExclusiveMode(boolean exclusiveMode) {
    this.exclusiveMode = exclusiveMode;
  }

  public GraphicsDevice getGraphicsDevice() {
    return graphicsDevice;
  }

  public void setGraphicsDevice(GraphicsDevice graphicsDevice) {
    this.graphicsDevice = graphicsDevice;
    displayModes = this.graphicsDevice.getDisplayModes();
    fullScreenSupported = this.graphicsDevice.isFullScreenSupported();
    displayChangeSupported = this.graphicsDevice.isDisplayChangeSupported();
  }

  public GraphicsEnvironment getGraphicsEnvironment() {
    return graphicsEnvironment;
  }

  public boolean isFullScreenSupported() {
    return fullScreenSupported;
  }

  public boolean isDisplayChangeSupported() {
    return displayChangeSupported;
  }

  public DisplayMode[] getDisplayModes() {
    return displayModes;
  }

  public DisplayMode getOryginalDisplayMode() {
    return oryginalDisplayMode;
  }
  
  public DisplayMode getDisplayMode() {
    return displayMode;
  }

  public void setDisplayMode(DisplayMode displayMode) {
    this.displayMode = displayMode;
  }

  public GraphicsDevice[] getGraphicsDevices() {
    return graphicsDevices;
  }
  
  public void setDisplayMode(boolean fullScreen) {
    boolean disposed = false;
    if (fullScreen) {
      if (window.isVisible()) {
        window.dispose();
        disposed = true;
      }
      setDisplayMode(-1, -1, -1, fullScreen);
      if (disposed) {
        window.setVisible(true);
        window.repaint();
      }
    }
    else {
      if (window.isVisible()) {
        window.dispose();
        disposed = true;
      }
      restoreDisplayMode();
      if (disposed) {
        window.setVisible(true);
        window.repaint();
      }
    }
  }

  public void setDisplayMode(int width, int height, int depth) {
    setDisplayMode(width, height, depth, (graphicsDevice.getDisplayMode() == null ? 60 : graphicsDevice.getDisplayMode().getRefreshRate()), true);
  }
  
  public void setDisplayMode(int width, int height, int depth, boolean fullScreen) {
    setDisplayMode(width, height, depth, (graphicsDevice.getDisplayMode() == null ? 60 : graphicsDevice.getDisplayMode().getRefreshRate()), fullScreen);
  }
  
  /**
   * <p>Ustawia ekran na podan¹ roŸdzielczoœæ.
   * <p>Musi byæ wywo³ana przed window.setVisible(true);
   * @param width mo¿e byæ -1 wtedy bedzie tylko fullscreen
   * @param height mo¿e byæ -1 wtedy bedzie tylko fullscreen
   * @param depth
   * @param refreshRate
   */
  public void setDisplayMode(int width, int height, int depth, int refreshRate, boolean fullScreen) {
    oryginalWidth = window.getWidth();
    oryginalHeight = window.getHeight();
    if (width != -1 && height != -1) {
      window.setSize(width, height);
    }
    else {
      window.setSize(graphicsDevice.getDisplayMode().getWidth(), graphicsDevice.getDisplayMode().getHeight());
    }
    if (fullScreenSupported && fullScreen) {
      if (width != -1 && height != -1) {
        oryginalDisplayMode = graphicsDevice.getDisplayMode();
        displayMode = new DisplayMode(width, height, depth, refreshRate);
      }
      if (window instanceof Frame && !window.isVisible() && undecorable) {
        undecorated = ((Frame)window).isUndecorated();
        ((Frame)window).setUndecorated(true);
      }
      else {
        undecorated = false;
      }
      try {
        if (exclusiveMode) {
          graphicsDevice.setFullScreenWindow(window);
        }
        if (width != -1 && height != -1) {
          graphicsDevice.setDisplayMode(displayMode);
        }
      } catch (Throwable e) {
        graphicsDevice.setFullScreenWindow(null);
        oryginalDisplayMode = null;
      }
    }
  }
  
  public void restoreDisplayMode() {
    if (oryginalDisplayMode != null) {
      graphicsDevice.setDisplayMode(oryginalDisplayMode);
      if (exclusiveMode) {
        graphicsDevice.setFullScreenWindow(null);
      }
    }
    if (window instanceof Frame && !window.isVisible() && undecorable) {
      ((Frame)window).setUndecorated(undecorated);
    }
    window.setSize(oryginalWidth, oryginalHeight);
  }

}
