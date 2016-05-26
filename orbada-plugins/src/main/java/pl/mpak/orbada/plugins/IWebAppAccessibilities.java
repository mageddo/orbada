/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.plugins;

/**
 *
 * @author akaluza
 */
public interface IWebAppAccessibilities {

  public final static String SUGGESTION = "suggestion";
  public final static String PROBLEM = "problem";
  public final static String ERROR = "error";

  public String requestPost(
    String severity, String title, String os, String db, String jdbc, String java,
    String path, String callstack, String content, String workaround);
  
}
