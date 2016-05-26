/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

import java.math.BigInteger;

/**
 * <p>Uniwersalny generator Orbada
 * @author akaluza
 */
public interface IGenerator {
  
  public String getName();
  
  /**
   * <p>Wywo³anie powoduje pobranie nowej wartoœci
   * @return
   */
  public BigInteger getNextValue() throws GeneratorException;
  
  /**
   * <p>Zwraca uprzednio wygenerowan¹ getNextValue() wartoœæ
   * @return
   */
  public BigInteger getCurrValue() throws GeneratorException;
  
  public BigInteger getMinValue();
  
  public BigInteger getMaxValue();
  
  /**
   * <p>Zwraca wartoœæ która bêdzie zwiêkszaæ/zmniejszaæ wartoœæ generatora
   * @return
   */
  public BigInteger getIncrement();
  
  public boolean getCycle();
  
}
