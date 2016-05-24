/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.oracle.dba.gui;

import java.awt.Color;
import java.util.Random;

/**
 *
 * @author akaluza
 */
public class GuiUtil {
  
  private static Random random1 = new Random(new Random().nextLong());
  private static Random random2 = new Random(new Random().nextLong());
  private static Random random3 = new Random(new Random().nextLong());

  public static Color randomColor() {
    return new Color(random1.nextInt(150) +50, random2.nextInt(150) +50, random3.nextInt(150) +50);
  }
  
}
