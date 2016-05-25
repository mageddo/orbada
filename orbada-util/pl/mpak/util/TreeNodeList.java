package pl.mpak.util;

import java.util.ArrayList;

/**
 * @author akaluza
 * <p>Klasa drzewa, pozwala zachowaæ klucz i wartoœæ dodatkow¹.
 * S³u¿y do budowania drzewa obiektów wg dowolnego klucza.
 * <p>Lista elementów/dzieci mo¿e byæ powtarzalna.
 *
 * @param <K>
 * @param <V>
 */
public class TreeNodeList<K, V> extends ArrayList<TreeNodeList<K, V>> {
  private static final long serialVersionUID = 8689961036987977155L;

  public K key;
  public V value;

  public TreeNodeList() {
  }

  public TreeNodeList(K key) {
    this.key = key;
  }

  public TreeNodeList(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public void setValue(V value) {
    this.value = value;
  }
  
  public boolean add(K key, V value) {
    return add(new TreeNodeList<K, V>(key, value));
  }
  
}
