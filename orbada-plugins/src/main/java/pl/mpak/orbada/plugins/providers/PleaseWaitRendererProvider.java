/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JComponent;
import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.PleaseWait;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;

/**
 *
 * @author akaluza
 */
public abstract class PleaseWaitRendererProvider implements IOrbadaPluginProvider {

  protected IApplication application;
  protected JComponent component;

  public void setApplication(IApplication application) {
    this.application = application;
  }

  public void setComponent(JComponent component) {
    this.component = component;
  }

  public boolean isSharedProvider() {
    return true;
  }

  public void beginProcess() {

  }
  
  public void endProcess() {

  }

  /**
   * <p>Funkcja jest wywo³ywana raz na 100 milisekund, s³u¿y do np p³ynnej animacji.
   * <p>Jej wywo³anie nie oznacza, ¿e bêdzie wywo³ana funkcja render
   */
  public abstract void control();

  /**
   * <p>Wywo³ywane jest wtedy gdy wymagane jest odrysowanie procesu oczekiwania
   * @param g2
   * @param waitArr - lista oczekiwañ, nigdy null
   */
  public abstract void render(Graphics2D g2, PleaseWait[] waitArr);

  /**
   * <p>Powinna zwróciæ unikalny identyfikator rysowacza w celu ustalenia który ma dzia³aæ
   * @return
   */
  public abstract String getRendererId();

  public abstract Rectangle getRenderBounds();

}
