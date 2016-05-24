/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins.providers;

import pl.mpak.orbada.plugins.IApplication;
import pl.mpak.orbada.plugins.providers.abs.IOrbadaPluginProvider;
import pl.mpak.sky.gui.swing.AutoCompleteListener;
import pl.mpak.sky.gui.swing.syntax.SyntaxTextArea;

/**
 * <p>Provider s³u¿y do obs³ugi mechanizmu auto uzupe³niania w edytorze SQL/Java<br>
 * Dla edytorów OrbadaSyntaxTextArea i OrbadaJavaSyntaxTextArea dopiero w funkcji populate mo¿na
 * sprawdziæ czy obs³ugiwana jest konkretna baza danych.
 * @author akaluza
 */
public abstract class SyntaxEditorAutoCompleteProvider implements IOrbadaPluginProvider, AutoCompleteListener {

  protected IApplication application;

  public void setApplication(IApplication application) {
    this.application = application;
  }

  public boolean isSharedProvider() {
    return false;
  }

  /**
   * <p>Funkcja powinna zwróciæ true jeœli serwis jest przeznaczony dla tego edytora.
   * @param syntaxTextArea
   * @return
   */
  public abstract boolean isForEditor(SyntaxTextArea syntaxTextArea);

}
