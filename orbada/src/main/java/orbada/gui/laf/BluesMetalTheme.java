/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package orbada.gui.laf;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/**
 *
 * @author akaluza
 */
public class BluesMetalTheme extends DefaultMetalTheme {

  // light blue colors
  private final ColorUIResource primary1 = new ColorUIResource(44, 73, 135);
  private final ColorUIResource primary2 = new ColorUIResource(85, 115, 170); 
  private final ColorUIResource primary3 = new ColorUIResource(172, 210, 248);
  private final ColorUIResource secondary1 = new ColorUIResource(110, 110, 110);
  private final ColorUIResource secondary2 = new ColorUIResource(170, 170, 170);
  private final ColorUIResource secondary3 = new ColorUIResource(220, 220, 220);

  @Override
  public String getName() {
    return "Blue";
  }

  public ColorUIResource getMenuItemSelectedBackground() {
    return getPrimary2();
  }

  public ColorUIResource getMenuItemSelectedForeground() {
    return getWhite();
  }

  public ColorUIResource getMenuSelectedBackground() {
    return getSecondary2();
  }

  protected ColorUIResource getPrimary1() {
    return primary1;
  }

  protected ColorUIResource getPrimary2() {
    return primary2;
  }

  protected ColorUIResource getPrimary3() {
    return primary3;
  }

  protected ColorUIResource getSecondary1() {
    return secondary1;
  }

  protected ColorUIResource getSecondary2() {
    return secondary2;
  }

  protected ColorUIResource getSecondary3() {
    return secondary3;
  }
}
