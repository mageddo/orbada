package pl.mpak.util.variant;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.Serializable;

/**
 * 
 * @author Andrzej Ka³u¿a
 * 
 * <p>Interfejs który s³u¿y do utworzenia po³¹czenia pomiêdzy Variant,
 * a dowolnym innym, ¿³o¿onym typem, obiektem javy.
 * 
 * <p>Interfejs ten pozwala utworzyæ Variant zawieraj¹cy dan¹ klas¹ i umo¿lija
 * u¿yæ funkcji Variant.write, read, compareTo i innych odwo³uj¹c siê do Variant
 * 
 * <p>Uwaga: Ka¿da klasa która implementuje ten interfejs musi siê
 * zarejestrowaæ w Variant przy pomocy.
 * <pre>
 * static {
 *   Variant.registerVariantClass(serialVersionUID, Klasa.class);
 * }
 * </pre>
 * Jeœli tego nie zrobi to Variant.read odczyta j¹ jako obiekt byte[]
 * 
 */
public interface VariantConnectable extends Serializable {

  /**
   * powinna zapisywaæ do raf dane tego obiektu
   * @param raf
   */
  public void write(DataOutput raf);
  
  /**
   * powinna odczytywaæ z raf dane tego obiektu
   * @param raf
   */
  public void read(DataInput raf);
  
  /**
   * Funkcja powinna zwracaæ uwagê na Variant.isIgnoreCase()
   * @param variant
   * @return
   */
  public int compareTo(Variant variant);
  
  /**
   * Powinna zwracaæ rozmiar (w bajtach) tego obiektu zapisywanego w pliku 
   * @return
   */
  public int getSize();
  
  /**
   * Powinna konwertowaæ typ do podanego typu Variant i zwracaæ odpowiedni obiekt
   * Domyœlnie mo¿e to byæ poprostu toString()
   * @param valueType jeden z VariantType
   * @return
   */
  public Object castTo(int valueType);
  
}
