/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.mpak.orbada.mysql.gui.util.helpers;

/**
 *
 * @author akaluza
 */
public class EngineInfo {

  private String name;
  private String support;
  private String comment;
  private boolean transactions;
  private boolean xa;
  private boolean savepoint;

  public EngineInfo() {
  }

  public EngineInfo(String name, String support, String comment, boolean transactions, boolean xa, boolean savepoint) {
    this.name = name;
    this.support = support;
    this.comment = comment;
    this.transactions = transactions;
    this.xa = xa;
    this.savepoint = savepoint;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isSavepoint() {
    return savepoint;
  }

  public void setSavepoint(boolean savepoint) {
    this.savepoint = savepoint;
  }

  public String getSupport() {
    return support;
  }

  public void setSupport(String support) {
    this.support = support;
  }

  public boolean isTransactions() {
    return transactions;
  }

  public void setTransactions(boolean transactions) {
    this.transactions = transactions;
  }

  public boolean isXa() {
    return xa;
  }

  public void setXa(boolean xa) {
    this.xa = xa;
  }

  @Override
  public String toString() {
    return name;
  }

}
