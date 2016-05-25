package pl.mpak.util;

import java.util.HashMap;

/**
 * @author akaluza
 * <p>Klasa drzewa, pozwala zachowaæ klucz i wartoœæ dodatkow¹.
 * S³u¿y do budowania drzewa obiektów wg dowolnego klucza.
 * <p>Lista elementów/dzieci jest map¹.
 *
 * @param <K>
 * @param <V>
 */
public class TreeNodeMap<K, V> extends HashMap<K, TreeNodeMap<K, V>> {
  private static final long serialVersionUID = 8689961036987977155L;

  public K key;
  public V value;

  public TreeNodeMap() {
  }

  public TreeNodeMap(K key) {
    this.key = key;
  }

  public TreeNodeMap(K key, V value) {
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
  
  public HashMap<K, TreeNodeMap<K, V>> add(K key, V value) {
    return put(key, new TreeNodeMap<K, V>(key, value));
  }
  
}
