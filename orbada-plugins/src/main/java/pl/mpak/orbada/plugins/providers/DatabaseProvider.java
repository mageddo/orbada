/*
 * DatabaseProvider.java
 *
 * Created on 2007-10-25, 17:29:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import pl.mpak.orbada.plugins.IApplication;

/**
 * <p>Klasa s³u¿y do implementacji zda¿eñ i innych funkcji bazy danych
 * <p>Klasa powinna sprawdzaæ jak¹ bazê, jaki rodzaj otrzyma³a jako parametr
 * by wykonaæ dodatkowe polecenia dla w³aœciwej bazy danych.
 * @author akaluza
 */
public abstract class DatabaseProvider implements IDatabaseProvider {
  
  protected IApplication application;
  
  public void setApplication(IApplication application) {
    this.application = application;
  }
  
  public boolean isSharedProvider() {
    return true;
  }
  
}
